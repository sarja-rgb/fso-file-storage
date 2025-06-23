package util;

public class SqlUtil {
    private SqlUtil(){}
    public static final String SQLITE_DB_STRING = "jdbc:sqlite:cloud_store.db";
    public static final String FILE_METADATA_SQL_SCHEMA =  """
                        CREATE TABLE IF NOT EXISTS file_metadata (
                            file_name TEXT PRIMARY KEY,
                            last_modified_date INTEGER NOT NULL,
                            checksum TEXT NOT NULL,
                            file_path TEXT,
                            file_size INTEGER,
                            bucket TEXT NOT NULL,
                            version TEXT NOT NULL DEFAULT '1'
                        );
               """;
   
    public static final String FILE_METADATA_SAVE_UPDATE_SQL= """
                            INSERT INTO file_metadata (file_name, file_path, file_size, last_modified_date, checksum, bucket, version)
                            VALUES (?, ?, ?, ?, ?, ?, ?)
                            ON CONFLICT(file_name) DO UPDATE SET
                            last_modified_date = excluded.last_modified_date,
                            checksum = excluded.checksum,
                            version = excluded.version;
                        """;
}