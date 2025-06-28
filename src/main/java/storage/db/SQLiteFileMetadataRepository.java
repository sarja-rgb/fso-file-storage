package storage.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import storage.FileObject;
import util.SqlUtil;

/**
 * SQLiteFileMetadataRepository is a concrete implementation of the
 * FileMetadataRepository interface that stores and manages file metadata
 * using a SQLite database.
 */
public class SQLiteFileMetadataRepository implements FileMetadataRepository {
    private static final Logger logger = LogManager.getLogger(SQLiteFileMetadataRepository.class);
    private final Connection connection;
    private Function<ResultSet,FileObject> rowMapper;

    /**
     * Constructs a new SQLiteFileMetadataRepository with a given SQLite connection.
     *
     * @param connection the SQLite JDBC connection
     */
    public SQLiteFileMetadataRepository(Connection connection) {
        this.connection = connection;
        init();
    }

    private void init(){
        createTableIfNotExists(); // Ensure the metadata table exists
        rowMapper = this::mapRow;
    }
    /**
     * Creates the file_metadata table if it doesn't already exist.
     * The table stores filename, last modified timestamp, and checksum.
     * @return 
     */
    private void createTableIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(SqlUtil.FILE_METADATA_SQL_SCHEMA);
        } catch (SQLException ex) {
            logger.error("Failed to create database schema table , error: {}",ex.getMessage());
            throw new RuntimeException("Failed to create table", ex);
        }
    }

    /**
     * Inserts a new file metadata record, or updates it if the file already exists.
     * Uses SQLite's ON CONFLICT clause to perform upsert operation.
     *
     * @param file the FileObject containing file metadata
     */
    @Override
    public void saveOrUpdate(FileObject file) {
        try (PreparedStatement stmt = connection.prepareStatement(SqlUtil.FILE_METADATA_SAVE_UPDATE_SQL)) {
            prepareSaveOrUpdateStatement(file, stmt);
            stmt.executeUpdate();
            logger.info("File object record saved in DB {}",file);
        } catch (SQLException ex) {
            logger.error("Failed to save or update database record , error: {}",ex.getMessage());
            throw new RuntimeException("Failed to save or update file", ex);
        }
    }

        @Override
    public void saveOrUpdateFiles(List<FileObject> files) {
        try (PreparedStatement stmt = connection.prepareStatement(SqlUtil.FILE_METADATA_SAVE_UPDATE_SQL)) {
            for (FileObject file : files) {
                prepareSaveOrUpdateStatement(file, stmt);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException ex) {
            logger.error("Failed to save or update files batch, error: {}",ex.getMessage());
            throw new RuntimeException("Failed to save or update files batch", ex);
        }
    }  

    /**
     * Retrieves a file metadata entry by its name.
     *
     * @param name the name of the file
     * @return the FileObject if found; otherwise null
     */
    @Override
    public FileObject findByName(String name) {
        String sql = "SELECT * FROM file_metadata WHERE file_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
               return this.rowMapper.apply(rs);
            }
            return null;
        } catch (SQLException ex) {
            logger.error("Failed to find file by namem, error: {}",ex.getMessage());
            throw new RuntimeException("Failed to find file by name", ex);
        }
    }

    /**
     * Deletes a file metadata entry by its name.
     *
     * @param name the name of the file to delete
     */
    @Override
    public void delete(String name) {
        String sql = "DELETE FROM file_metadata WHERE file_name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            logger.error("Failed to delete file. error: {}",ex.getMessage());
            throw new RuntimeException("Failed to delete file", ex);
        }
    }

    /**
     * Retrieves all file metadata entries from the database.
     *
     * @return a list of FileObject instances representing all stored files
     */
    @Override
    public List<FileObject> findAll() {
        String sql = "SELECT * FROM file_metadata";
        List<FileObject> files = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                FileObject fileObject = this.rowMapper.apply(rs);
                if(fileObject != null){
                   files.add(fileObject);
                }
            }
            return files;
        } catch (SQLException ex) {
            logger.error("Failed to find all file. error: {}",ex.getMessage());
            throw new RuntimeException("Failed to retrieve all files", ex);
        }
    }

    /**
     * Checks whether a file metadata entry exists by name.
     *
     * @param name the name of the file
     * @return true if the file exists; false otherwise
     */
    @Override
    public boolean exists(String name) {
        String sql = "SELECT 1 FROM file_metadata WHERE file_name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Returns true if a record is found
        } catch (SQLException ex) {
            logger.error("Failed to check existence. error: {}",ex.getMessage());
            throw new RuntimeException("Failed to check existence", ex);
        }
    }

    private FileObject mapRow(ResultSet resultSet) {
        try {

            Date modifiedDate  =  resultSet.getDate("last_modified_date");
            return FileObject.builder()
                    .setFileName(resultSet.getString("file_name"))
                    .setFilePath(resultSet.getString("file_path"))
                    .setFileSize(resultSet.getLong("file_size"))
                    .setCheckSum(resultSet.getString("checksum"))
                    .setVersion(resultSet.getString("version"))
                    .setBucketName(resultSet.getString("bucket"))
                    .setLastModifiedDate(new java.util.Date(modifiedDate.getTime()))
                    .build();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void prepareSaveOrUpdateStatement(FileObject fileObject, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, fileObject.getFileName());
        stmt.setString(2, fileObject.getFilePath());
        stmt.setLong(3, fileObject.getFileSize());
        stmt.setDate(4, new Date(fileObject.getLastModifiedDate().getTime()));
        stmt.setString(5,fileObject.getChecksum());
        stmt.setString(6,fileObject.getBucketName());
        stmt.setString(7,fileObject.getVersion() != null? fileObject.getVersion() : "1");
    }

}
