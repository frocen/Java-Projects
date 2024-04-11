package dao;
import java.sql.*;


public class Database {
	private static final String DB_URL = "jdbc:sqlite:Assignment2.db";

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DB_URL);
	}
}
