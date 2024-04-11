package dao;

import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;

public class WorkspaceImpl implements WorkspaceDao {
    private final String TABLE_NAME = "workSpace";

    public WorkspaceImpl() {
    }

    @Override
    public Workspace getWorkspace(User user) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("boardID");
                    sql = "SELECT * FROM project WHERE boardID = ?";
                    PreparedStatement findProject = connection.prepareStatement(sql);
                    findProject.setInt(1, id);
                    HashMap<Integer, Project> boards = getProject(id);
                    return new Workspace(user, boards, id);
                }
            }
        }
        return null;
    }

    @Override
    public void createWorkspace(String username) throws SQLException {
        if (!isExist(username)) {
            String sql = "INSERT INTO " + TABLE_NAME + " VALUES (?, ?)";
            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setInt(2, getID(TABLE_NAME, "boardID"));
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public boolean isExist(String username) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
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
    public void createProject(String projectName, int status, int boardID) throws SQLException {
        String tableName = "project";
        String sql = "INSERT INTO " + tableName + " VALUES (?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, projectName);
            stmt.setInt(2, boardID);
            stmt.setInt(3, getID(tableName, "projectID"));
            stmt.setInt(4, status);
            stmt.executeUpdate();
        }
    }

    @Override
    public void changeProjectName(String projectName, int projectID, int boardID) throws SQLException {
        String sql = "UPDATE project SET projectName = ? where boardID = ? and projectID = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, projectName);
            stmt.setInt(2, boardID);
            stmt.setInt(3, projectID);
            stmt.executeUpdate();
        }
    }

    @Override
    public HashMap<Integer, Project> getProject(int boardID) throws SQLException {
        String tableName = "project";
        String sql = "SELECT * FROM " + tableName + " WHERE boardID = ?";
        HashMap<Integer, Project> boards = new HashMap();
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, boardID);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int projectID = rs.getInt("projectID");
                    String projectName = rs.getString("projectName");
                    int status = rs.getInt("status");
                    boards.put(projectID, new Project(projectName, status, getColumn(projectID)));
                }
                return boards;
            }
        }
    }


    @Override
    public HashMap<Integer, Column> getColumn(int projectID) throws SQLException {
        HashMap<Integer, Column> columns = new HashMap();
        String tableName = "column";
        String sql = "SELECT * FROM " + tableName + " WHERE projectID = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, projectID);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int columnID = rs.getInt("columnID");
                    String columnName = rs.getString("columnName");
                    columns.put(columnID, new Column(columnName, getTask(columnID)));
                }
                return columns;
            }
        }
    }

    @Override
    public HashMap<Integer, Task> getTask(int columnID) throws SQLException {
        HashMap<Integer, Task> tasks = new HashMap();
        String tableName = "task";
        String sql = "SELECT * FROM " + tableName + " WHERE columnID = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, columnID);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int taskID = rs.getInt("taskID");
                    String taskName = rs.getString("taskName");
                    String description = rs.getString("description");
                    LocalDate dueDate;
                    if (rs.getDate("due") == null) {
                        dueDate = null;
                    } else {
                        dueDate = rs.getDate("due").toLocalDate();
                    }
                    tasks.put(taskID, new Task(taskName, description, dueDate, getActionItems(taskID)));
                }
                return tasks;
            }
        }
    }

    @Override
    public void addTask(String taskName, String description, int columnID, LocalDate date) throws SQLException {
        String tableName = "task";
        java.sql.Date time = null;
        if (date == null) {
            time = null;
        } else {
            time = java.sql.Date.valueOf(date);
        }
        String sql = "INSERT INTO " + tableName + " VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, taskName);
            stmt.setInt(2, columnID);
            stmt.setInt(3, getID(tableName, "taskID"));
            stmt.setString(4, description);
            stmt.setDate(5, time);
            stmt.executeUpdate();
        }
    }

    @Override
    public void editTask(String taskName, String description, int columnID, LocalDate date, int taskID) throws SQLException {
        String sql = "UPDATE task SET taskName = ?, description = ?, due = ? where columnID = ? and taskID = ?";
        java.sql.Date time = null;
        if (date == null) {
            time = null;
        } else {
            time = java.sql.Date.valueOf(date);
        }
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, taskName);
            stmt.setString(2, description);
            stmt.setDate(3, time);
            stmt.setInt(4, columnID);
            stmt.setInt(5, taskID);
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteTask(int taskID, int columnID, HashMap<Integer, CheckList> actionItems) throws SQLException {
        for (Integer keyOfItems : actionItems.keySet()) {
            deleteActionItems(keyOfItems, taskID);
        }
        String tableName = "task";
        String sql = "DELETE FROM " + tableName + " WHERE taskID = ? and columnID=?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, taskID);
            stmt.setInt(2, columnID);
            stmt.executeUpdate();
        }
    }

    @Override
    public void switchTaskCard(int selectedTaskID, int selectedColumnID, String selectedTaskName, String selectedDescription, LocalDate selectedDate, int droppedTaskID, int droppedColumnID, String droppedTaskName, String droppedDescription, LocalDate droppedDate) throws SQLException {
        java.sql.Date selectedTime = null;
        if (selectedDate == null) {
            selectedTime = null;
        } else {
            selectedTime = java.sql.Date.valueOf(selectedDate);
        }
        java.sql.Date droppedTime = null;
        if (droppedDate == null) {
            droppedTime = null;
        } else {
            droppedTime = java.sql.Date.valueOf(droppedDate);
        }
        String sql = "UPDATE task SET taskName = ?, description = ?, due= ? where columnID = ? and taskID = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, droppedTaskName);
            stmt.setString(2, droppedDescription);
            stmt.setDate(3, droppedTime);
            stmt.setInt(4, selectedColumnID);
            stmt.setInt(5, selectedTaskID);
            stmt.executeUpdate();
            String sql1 = "UPDATE task SET taskName = ?, description = ?, due= ? where columnID = ? and taskID = ?";
            PreparedStatement stmt1 = connection.prepareStatement(sql1);
            stmt1.setString(1, selectedTaskName);
            stmt1.setString(2, selectedDescription);
            stmt1.setDate(3, selectedTime);
            stmt1.setInt(4, droppedColumnID);
            stmt1.setInt(5, droppedTaskID);
            stmt1.executeUpdate();
            String sql2 = "UPDATE checkList SET taskID = 0 where taskID = ?";
            PreparedStatement stmt2 = connection.prepareStatement(sql2);
            stmt2.setInt(1, selectedTaskID);
            stmt2.executeUpdate();
            String sql3 = "UPDATE checkList SET taskID = ? where taskID = ?";
            PreparedStatement stmt3 = connection.prepareStatement(sql3);
            stmt3.setInt(1, selectedTaskID);
            stmt3.setInt(2, droppedTaskID);
            stmt3.executeUpdate();
            String sql4 = "UPDATE checkList SET taskID = ? where taskID = 0";
            PreparedStatement stmt4 = connection.prepareStatement(sql4);
            stmt4.setInt(1, droppedTaskID);
            stmt4.executeUpdate();
        }
    }

    @Override
    public void moveToColumn(int selectedTaskID, int droppedColumnID) throws SQLException {
        String sql = "UPDATE task SET columnID = ? where taskID = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, droppedColumnID);
            stmt.setInt(2, selectedTaskID);
            stmt.executeUpdate();
        }
    }

    @Override
    public HashMap<Integer, CheckList> getActionItems(int taskID) throws SQLException {
        HashMap<Integer, CheckList> checkList = new HashMap();
        String tableName = "checkList";
        String sql = "SELECT * FROM " + tableName + " WHERE taskID = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, taskID);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int actionItemID = rs.getInt("actionItemID");
                    String actionName = rs.getString("actionName");
                    int status = rs.getInt("status");
                    checkList.put(actionItemID, new CheckList(actionName, status));
                }
                return checkList;
            }
        }
    }

    @Override
    public void addActionItem(String actionName, int taskID) throws SQLException {
        String tableName = "checkList";
        String sql = "INSERT INTO " + tableName + " VALUES (?, ?, ?, 0)";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, actionName);
            stmt.setInt(2, getID(tableName, "actionItemID"));
            stmt.setInt(3, taskID);
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteActionItems(int actionItemID, int taskID) throws SQLException {
        String tableName = "checkList";
        String sql = "DELETE FROM " + tableName + " WHERE actionItemID = ? and taskID=?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, actionItemID);
            stmt.setInt(2, taskID);
            stmt.executeUpdate();
        }
    }

    @Override
    public void editActionItem(String actionName, int taskID, int actionItemID, int status) throws SQLException {
        String sql = "UPDATE checkList SET actionName = ? where taskID = ? and actionItemID = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, actionName);
            stmt.setInt(2, taskID);
            stmt.setInt(3, actionItemID);
            stmt.executeUpdate();
        }
    }

    @Override
    public void setActionItemStatus(int taskID, int actionItemID, int status) throws SQLException {
        String sql = "UPDATE checkList SET status = ? where taskID = ? and actionItemID = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, status);
            stmt.setInt(2, taskID);
            stmt.setInt(3, actionItemID);
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteProject(int projectID, HashMap<Integer, Column> columns) throws SQLException {
        for (Integer keyOfColumn : columns.keySet()) {
            deleteColumn(keyOfColumn, projectID, columns.get(keyOfColumn).getTasks());
        }
        String tableName = "project";
        String sql = "DELETE FROM " + tableName + " WHERE projectID = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, projectID);
            stmt.executeUpdate();
        }
    }

    @Override
    public Boolean haveDefault(int boardID) throws SQLException {
        String sql1 = "SELECT * FROM project WHERE boardID = ? and status=1";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql1)) {
            stmt.setInt(1, boardID);
            try (ResultSet rs = stmt.executeQuery()) {
                //if there is no status value 1
                if (!rs.next()) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }


    @Override
    public void setProjectDefault(int projectID, int boardID) throws SQLException {
        //if there is no status value 1
        if (!haveDefault(boardID)) {
            String sql1 = "SELECT * FROM project WHERE boardID = ? and status = 0";
            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt1 = connection.prepareStatement(sql1)) {
                stmt1.setInt(1, boardID);
                try (ResultSet rs1 = stmt1.executeQuery()) {
                    //get the first projectID
                    if (rs1.next()) {
                        int newDefaultProjectID = rs1.getInt("projectID");
                        String sql4 = "UPDATE project SET status = 1 WHERE boardID = ? and projectID = ?";
                        PreparedStatement stmt2 = connection.prepareStatement(sql4);
                        stmt2.setInt(1, boardID);
                        stmt2.setInt(2, newDefaultProjectID);
                        stmt2.executeUpdate();
                    }
                }
            }
        } else {
            String sql = "UPDATE project SET status = 0 where boardID = ?";
            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt3 = connection.prepareStatement(sql)) {
                stmt3.setInt(1, boardID);
                stmt3.executeUpdate();
                String sql2 = "UPDATE project SET status = 1 where boardID = ? and projectID=?";
                PreparedStatement stmt4 = connection.prepareStatement(sql2);
                stmt4.setInt(1, boardID);
                stmt4.setInt(2, projectID);
                stmt4.executeUpdate();
            }
        }
    }

    @Override
    public void unSetProjectDefault(int projectID, int boardID) throws SQLException {
        String sql = "UPDATE project SET status = 0 where boardID = ? and projectID = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, boardID);
            stmt.setInt(2, projectID);
            stmt.executeUpdate();
        }
    }

    @Override
    public int getStatus(int projectID, int boardID) throws SQLException {
        String sql = "SELECT status FROM project where boardID = ? and projectID = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, boardID);
            stmt.setInt(2, projectID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("status");
                }
                return rs.getInt("status");
            }
        }
    }

    @Override
    public void addColumn(String columnName, int projectID) throws SQLException {
        String tableName = "column";
        String sql = "INSERT INTO " + tableName + " VALUES (?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, columnName);
            stmt.setInt(2, getID(tableName, "columnID"));
            stmt.setInt(3, projectID);
            stmt.executeUpdate();
        }
    }

    @Override
    public void changeColumnName(String columnName, int columnID, int projectID) throws SQLException {
        String sql = "UPDATE column SET columnName = ? where columnID = ? and projectID = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, columnName);
            stmt.setInt(2, columnID);
            stmt.setInt(3, projectID);
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteColumn(int columnID, int projectID, HashMap<Integer, Task> tasks) throws SQLException {
        for (Integer keyOfTasks : tasks.keySet()) {
            deleteTask(keyOfTasks, columnID, tasks.get(keyOfTasks).getActionItems());
        }
        String tableName = "column";
        String sql = "DELETE FROM " + tableName + " WHERE projectID = ? and columnID=?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, projectID);
            stmt.setInt(2, columnID);
            stmt.executeUpdate();
        }
    }


    public int getID(String NAME, String columnLabel) throws SQLException {
        String sql = "SELECT * FROM " + NAME;
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                int maxID = 0;
                while (rs.next()) {
                    int id = rs.getInt(columnLabel);
                    if (id > maxID) {
                        maxID = id;
                    }
                }
                return maxID + 1;
            }
        }
    }
}
