package other;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DbConnection {

    private static Connection connection;
    private static String url;
    private static String username;
    private static String password;

    public static synchronized void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public static synchronized Connection getConnection(String url, String username, String password) throws SQLException {
        DbConnection.url = url;
        DbConnection.username = username;
        DbConnection.password = password;

        if (connection == null || connection.isClosed()) {
            setConnection(url, username, password);
        }
        return connection;
    }

    public static synchronized Connection getConnection() throws SQLException {
        return getConnection(url, username, password);
    }

    private static void setConnection(String url, String username, String password) throws SQLException {
        connection = DriverManager.getConnection(url, username, password);
    }
}