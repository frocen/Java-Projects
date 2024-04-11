package model;

import java.util.HashMap;

public class Column{
    private String columnName;
    private HashMap<Integer, Task> tasks;

    public Column(String columnName, HashMap<Integer, Task> tasks) {
        this.columnName = columnName;
        this.tasks = tasks;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public void setTasks(HashMap<Integer, Task> tasks) {
        this.tasks = tasks;
    }
}
