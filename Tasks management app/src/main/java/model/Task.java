package model;

import java.time.LocalDate;
import java.util.HashMap;

public class Task {
    private String taskName;
    private String description;
    private LocalDate dueDate;
    private HashMap<Integer, CheckList> actionItems;

    public Task(String taskName, String description,LocalDate dueDate,HashMap<Integer, CheckList> actionItems) {
        this.taskName = taskName;
        this.description = description;
        this.dueDate=dueDate;
        this.actionItems=actionItems;
    }

    public HashMap<Integer, CheckList> getActionItems() {
        return actionItems;
    }

    public void setActionItems(HashMap<Integer, CheckList> actionItems) {
        this.actionItems = actionItems;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean haveDueDate(){
        if (getDueDate()==null){
            return false;
        }else {
            return true;
        }
    }
    public boolean haveCheckList(){
        if (getActionItems().size()==0){
            return false;
        }else {
            return true;
        }
    }
    public int completeNumber(){
        int result=0;
        for (Integer keyOfItem : actionItems.keySet()) {
            if(actionItems.get(keyOfItem).getStatus()==1){
                result+=1;
            }
        }
        return result;
    }
    public double completePercentage(){
            return 100*completeNumber()/actionItems.size();
    }
    public boolean isComplete(){
        if (completeNumber()==actionItems.size()){
            return true;
        }else {
            return false;
        }
    }
}
