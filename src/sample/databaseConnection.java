package sample;

import java.sql.*;

//method to set up the database connection
public class databaseConnection {
    public Connection conn;

    public static Connection connect() {
        //gets the filename of where the database is held
        String fileName = "C:\\Users\\Annie\\serverClientProject\\src\\sample\\serverData.db";
        // SQLite connection string
        String url = "jdbc:sqlite:" + fileName;
        Connection conn = null;
        try {
            //creates the connection
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //returns the databaseConnection
        return conn;
    }
}
