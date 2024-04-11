package model;

public class CheckList {
    private String actionItem;
    private int status;

    public CheckList(String actionItem, int status) {
        this.actionItem = actionItem;
        this.status=status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getActionItem() {
        return actionItem;
    }

    public void setActionItem(String actionItem) {
        this.actionItem = actionItem;
    }
}
