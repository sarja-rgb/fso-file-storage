package storage.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import util.SqlUtil;

/**
 * SqlConnectionManager is a simple singleton-style class for managing
 * a single SQLite database connection across the application.
 */
public class SqlConnectionManager {
    // Holds the singleton instance of the database connection
    private static Connection connection;
   
    private SqlConnectionManager(){}
    /**
     * Returns a shared SQLite Connection instance.
     * If no connection has been established yet, it initializes one
     * using the connection string defined in SqlUtil.SQLITE_DB_STRING.
     *
     * @return a valid SQLite JDBC Connection
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        // Initialize the connection only once (singleton behavior)
        if (connection == null) {
            connection = DriverManager.getConnection(SqlUtil.SQLITE_DB_STRING);
        }
        return connection;
    }
}
