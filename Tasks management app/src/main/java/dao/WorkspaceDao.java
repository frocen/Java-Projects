package dao;

import model.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;

public interface WorkspaceDao {
    Workspace getWorkspace(User user) throws SQLException;

    void createWorkspace(String username) throws SQLException;

    boolean isExist(String username) throws SQLException;

    void createProject(String name, int status, int id) throws SQLException;

    void changeProjectName(String projectName, int projectID, int boardID) throws SQLException;

    HashMap<Integer, Project> getProject(int boardID) throws SQLException;

    HashMap<Integer, Column> getColumn(int projectID) throws SQLException;

    void deleteProject(int projectID, HashMap<Integer, Column> columns) throws SQLException;

    Boolean haveDefault(int boardID) throws SQLException;

    void setProjectDefault(int projectID, int boardID) throws SQLException;

    void unSetProjectDefault(int projectID, int boardID) throws SQLException;

    int getStatus(int projectID, int boardID) throws SQLException;

    void addColumn(String columnName, int projectID) throws SQLException;

    void changeColumnName(String columnName, int columnID, int projectID) throws SQLException;

    void deleteColumn(int columnID, int projectID, HashMap<Integer, Task> tasks) throws SQLException;

    HashMap<Integer, Task> getTask(int columnID) throws SQLException;

    void addTask(String taskName, String description, int columnID, LocalDate date) throws SQLException;

    void editTask(String taskName, String description, int columnID, LocalDate date, int taskID) throws SQLException;

    void deleteTask(int taskID, int columnID, HashMap<Integer, CheckList> actionItems) throws SQLException;

    void switchTaskCard(int selectedTaskID, int selectedColumnID, String selectedTaskName, String selectedDescription
            , LocalDate selectedDate, int droppedTaskID, int droppedColumnID, String droppedTaskName
            , String droppedDescription, LocalDate droppedDate) throws SQLException;

    void moveToColumn(int selectedColumnID, int droppedColumnID) throws SQLException;

    HashMap<Integer, CheckList> getActionItems(int taskID) throws SQLException;

    void addActionItem(String actionName, int taskID) throws SQLException;

    void deleteActionItems(int actionItemID, int taskID) throws SQLException;

    void editActionItem(String actionName, int taskID, int actionItemID, int status) throws SQLException;

    void setActionItemStatus(int taskID, int actionItemID, int status) throws SQLException;
}
