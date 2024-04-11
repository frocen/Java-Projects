package model;

import java.util.HashMap;

public class Workspace {
    private User user;
    private HashMap<Integer, Project> boards = new HashMap();
    private int boardID;

    public Workspace(User user, HashMap<Integer, Project> boards, int boardID) {
        this.user = user;
        this.boards = boards;
        this.boardID=boardID;
    }

    public int getBoardID() {
        return boardID;
    }

    public void setBoardID(int boardID) {
        this.boardID = boardID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public HashMap<Integer, Project> getBoards() {
        return boards;
    }

    public void setBoards(HashMap<Integer, Project> boards) {
        this.boards = boards;
    }

}
