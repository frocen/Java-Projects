package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import model.User;

public class UserDaoImpl implements UserDao {
    private final String TABLE_NAME = "users";

    public UserDaoImpl() {
    }

    @Override
    public void setup() throws SQLException {
        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement();) {
            String sql = "CREATE TABLE IF NOT EXISTS users (\n" +
                    "username VARCHAR(10) NOT NULL,\n" +
                    "password VARCHAR(8) NOT NULL,\n" +
                    "firstName VARCHAR(10),\n" +
                    "lastName VARCHAR(10),\n" +
                    "photo VARCHAR(100),\n" +
                    "PRIMARY KEY (username));\n" +
                    "CREATE TABLE IF NOT EXISTS workSpace (\n" +
                    "username VARCHAR(10) NOT NULL,\n" +
                    "boardID INT NOT NULL,\n" +
                    "PRIMARY KEY (boardID),\n" +
                    "foreign key (username) \n" +
                    "references users);\n" +
                    "CREATE TABLE IF NOT EXISTS project (\n" +
                    "projectName VARCHAR(20),\n" +
                    "boardID INT,\n" +
                    "projectID INT,\n" +
                    "status INT,\n" +
                    "PRIMARY KEY (projectID),\n" +
                    "foreign key (boardID) \n" +
                    "references workSpace);\n" +
                    "CREATE TABLE IF NOT EXISTS column (\n" +
                    "columnName VARCHAR(20),\n" +
                    "columnID INT,\n" +
                    "projectID INT,\n" +
                    "PRIMARY KEY (columnID),\n" +
                    "foreign key (projectID) \n" +
                    "references project);\n" +
                    "CREATE TABLE IF NOT EXISTS task (\n" +
                    "taskName VARCHAR(20),\n" +
                    "columnID INT,\n" +
                    "taskID INT,\n" +
                    "description VARCHAR(200),\n" +
                    "due DATE,\n" +
                    "PRIMARY KEY (taskID),\n" +
                    "foreign key (columnID) \n" +
                    "references column);\n" +
            "CREATE TABLE IF NOT EXISTS checkList (\n" +
                    "actionName VARCHAR(20),\n" +
                    "actionItemID INT,\n" +
                    "taskID INT,\n" +
                    "status INT,\n" +
                    "PRIMARY KEY (actionItemID),\n" +
                    "foreign key (taskID) \n" +
                    "references task);";
            stmt.executeUpdate(sql);
        }
    }

    @Override
    public User getUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE username = ? AND password = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setFirstName(rs.getString("firstName"));
                    user.setLastName(rs.getString("lastName"));
                    user.setPhoto(rs.getString("photo"));
                    return user;
                }
                return null;
            }
        }
    }

    @Override
    public User createUser(String username, String password, String firstName, String lastName, String photo) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);
            stmt.setString(5, photo);

            stmt.executeUpdate();
            return new User(username, password, firstName, lastName, photo);
        }
    }

    @Override
    public boolean isExist(String username) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
                return false;
            }
        }
    }

    @Override
    public void updateProfile(String username, String firstName, String lastName, String photo) throws SQLException {
        String sql = "UPDATE users SET firstName = ?, lastName = ?, photo = ? WHERE username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, photo);
            stmt.setString(4, username);
            stmt.executeUpdate();
        }
    }

    //hard code quote database
    @Override
    public String getQuote() {
        ArrayList<String> quotes = new ArrayList<>(Arrays.asList("“Pursue what catches your heart, not what catches your eyes. ― Roy T. Bennett”"
                , "“Do not fear failure but rather fear not trying.” ― Roy T. Bennett"
                , "“Be brave to stand for what you believe in even if you stand alone.” ― Roy T. Bennett"
                , "“You only live once, but if you do it right, once is enough.” ― Mae West"));
        // create instance of Random class
        Random rand = new Random();
        // Generate random integers in range 0 to 3
        int rand_int = rand.nextInt(4);
        return quotes.get(rand_int);
    }


}
