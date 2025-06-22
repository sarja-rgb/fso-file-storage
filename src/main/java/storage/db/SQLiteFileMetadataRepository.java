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

import storage.FileObject;

/**
 * SQLiteFileMetadataRepository is a concrete implementation of the
 * FileMetadataRepository interface that stores and manages file metadata
 * using a SQLite database.
 */
public class SQLiteFileMetadataRepository implements FileMetadataRepository {
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
        String sql = """
                        CREATE TABLE IF NOT EXISTS file_metadata (
                            file_name TEXT PRIMARY KEY,
                            last_modified_date INTEGER NOT NULL,
                            checksum TEXT NOT NULL,
                            file_path TEXT,
                            file_size INTEGER,
                            version TEXT,
                            bucket TEXT
                        );
               """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create table", e);
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
        String sql = """
            INSERT INTO file_metadata (file_name, file_path, file_size, last_modified_date, checksum, version, bucket)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(file_name) DO UPDATE SET
              last_modified_date = excluded.last_modified_date,
              checksum = excluded.checksum;
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, file.getFileName());
            stmt.setString(2, file.getFilePath());
            stmt.setLong(3, file.getFileSize());
            stmt.setString(5,file.getChecksum());
            stmt.setString(6,file.getVersion());
            stmt.setString(7,file.getBucketName());
            if(file.getLastModifiedDate() != null){
               Date sqlDate = new Date(file.getLastModifiedDate().getTime());
               stmt.setDate(4, sqlDate);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save or update file", e);
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
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find file by name", e);
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
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete file", e);
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
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve all files", e);
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
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check existence", e);
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

}
