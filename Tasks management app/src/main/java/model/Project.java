package model;

import java.util.HashMap;

public class Project{
    private String projectName;
    private int status;
    private HashMap<Integer,Column> columns;

    public Project(String projectName, int status,HashMap<Integer, Column> columns) {
        this.projectName = projectName;
        this.columns = columns;
        this.status=status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public HashMap<Integer, Column> getColumns() {
        return columns;
    }

    public void setColumns(HashMap<Integer, Column> columns) {
        this.columns = columns;
    }
}
