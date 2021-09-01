package store;

import exceptions.LoadSaveException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String url = "jdbc:derby:derbyDB;create=true";
    private static final String user = "";
    private static final String password = "";
    private final Connection connection;
    private static DBConnection unique = null;

    private DBConnection() throws LoadSaveException {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new LoadSaveException("Connection failed", e);
        }
    }

    public static DBConnection instance() throws LoadSaveException {
        if (unique == null)
            return unique = new DBConnection();
        return unique;
    }

    public static Connection getConnection() throws LoadSaveException {
        return instance().getConn();
    }

    public Connection getConn() {
        return connection;
    }

    public void createTable(String query) throws SQLException {
        try (Statement statement = getConn().createStatement()) {
            statement.execute(query);
        }
    }

    public void close() {
        try {
            getConn().close();
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException ignored) { }
    }
}
