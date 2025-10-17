package config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class for managing the database connection.
 * It reads configuration from a properties file and provides a static method
 * to get a connection to the PostgreSQL database.
 */
public class DatabaseConnection {

    private static final Properties properties = new Properties();

    // Static block to load the database configuration when the class is initialized.
    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find db.properties");
                // In a real application, you might want to throw an exception here.
            } else {
                properties.load(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private DatabaseConnection() {}

    /**
     * Establishes and returns a connection to the database.
     * It uses the properties loaded from the config.properties file.
     *
     * @return A Connection object to the database.
     * @throws SQLException if a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password")
        );
    }
}
