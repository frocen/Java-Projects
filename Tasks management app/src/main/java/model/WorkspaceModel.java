package model;


import dao.WorkspaceDao;
import dao.WorkspaceImpl;

public class WorkspaceModel {
    private WorkspaceDao workspaceDao;
    private Workspace currentWorkspace;

    public WorkspaceModel() {
        workspaceDao = new WorkspaceImpl();
    }

    public WorkspaceDao getWorkspaceDao() {
        return workspaceDao;
    }

    public Workspace getCurrentWorkspace() {
        return this.currentWorkspace;
    }

    public void setCurrentWorkspace(Workspace workspace) {
        currentWorkspace = workspace;
    }
}
