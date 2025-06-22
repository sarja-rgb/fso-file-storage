package util;

public class SqlUtil {
    private SqlUtil(){}
    
    public static final String FILE_METADATA_SQL_SCHEMA =  """
                        CREATE TABLE IF NOT EXISTS file_metadata (
                            file_name TEXT PRIMARY KEY,
                            last_modified_date INTEGER NOT NULL,
                            checksum TEXT NOT NULL,
                            file_path TEXT,
                            file_size INTEGER,
                            version INTEGER DEFAULT 1,
                            bucket TEXT NOT NULL
                        );
               """;
}
