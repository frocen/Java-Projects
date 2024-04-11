package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.CheckList;
import model.Model;
import model.Workspace;
import model.WorkspaceModel;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class TaskController {

    @FXML
    private Button Cancel;

    @FXML
    private Button Ok;

    @FXML
    private HBox actionItemsBox;

    @FXML
    private Hyperlink addLink;

    @FXML
    private Label checkListLabel;

    @FXML
    private Label status;

    @FXML
    private Hyperlink checkListLink;

    @FXML
    private Hyperlink deleteCheckListLink;

    @FXML
    private Hyperlink deleteDue;

    @FXML
    private TextArea description;

    @FXML
    private Label dueLabel;

    @FXML
    private HBox editDuePlace;

    @FXML
    private HBox progressBox;

    @FXML
    private TextField taskName;


    private WorkspaceModel workspaceModel;
    private int columnID;
    private Stage stage;
    private Model model;
    private String sentence;
    private int taskID;
    private int projectID;
    private DatePicker datePicker = new DatePicker();
    private CheckBox checkBox = new CheckBox("Mark as completed");
    private Label percentage = new Label();
    private ProgressBar progressBar = new ProgressBar();
    private VBox container;
    private Hyperlink addItem = new Hyperlink("Add an item");

    public TaskController(WorkspaceModel workspaceModel, int columnID, Model model, String sentence, int taskID, int projectID) {
        this.workspaceModel = workspaceModel;
        this.columnID = columnID;
        this.stage = new Stage();
        this.model = model;
        this.sentence = sentence;
        this.taskID = taskID;
        this.projectID = projectID;
    }

    @FXML
    public void initialize() {
        //due date part
        if (taskID == 0) {
            addLink.setText("Add due date");
        } else if (!this.workspaceModel.getCurrentWorkspace().getBoards().get(projectID).getColumns().get(columnID).getTasks().get(taskID).haveDueDate()) {
            addLink.setText("Add due date");
        } else {
            dueLabel.setText("Due date");
            deleteDue.setText("Delete");
            datePicker = new DatePicker(this.workspaceModel.getCurrentWorkspace().getBoards().get(projectID).getColumns().get(columnID).getTasks().get(taskID).getDueDate());
            editDuePlace.getChildren().addAll(datePicker, checkBox);
            editDuePlace.setMargin(checkBox, new Insets(0, 0, 0, 15));
        }
        addLink.setOnAction(event -> {
            addLink.setText("");
            addLink.setVisible(false);
            dueLabel.setText("Due date");
            dueLabel.setVisible(true);
            deleteDue.setText("Delete");
            deleteDue.setVisible(true);
            editDuePlace.getChildren().addAll(datePicker, checkBox);
            editDuePlace.setMargin(checkBox, new Insets(0, 0, 0, 15));
        });
        deleteDue.setOnAction(event -> {
            deleteDue.setText("");
            deleteDue.setVisible(false);
            dueLabel.setText("");
            dueLabel.setVisible(false);
            addLink.setText("Add due date");
            addLink.setVisible(true);
            editDuePlace.getChildren().removeAll(checkBox, datePicker);
            datePicker.setValue(null);
            checkBox.setSelected(false);
        });
        //checkList part
        if (taskID == 0) {
        } else if (!this.workspaceModel.getCurrentWorkspace().getBoards().get(projectID).getColumns().get(columnID).getTasks().get(taskID).haveCheckList()) {
            checkListLink.setText("Add checklist");
        } else {
            checkListLabel.setText("Checklist");
            deleteCheckListLink.setText("Delete");
            progressBox.getChildren().addAll(percentage, progressBar);
            progressBox.setMargin(progressBar, new Insets(0, 0, 0, 15));
            progressBox.setMargin(percentage, new Insets(0, 0, 0, 15));
            refreshScreen();
        }
        checkListLink.setOnAction(event -> {
            checkListLink.setText("");
            checkListLink.setVisible(false);
            checkListLabel.setText("Checklist");
            checkListLabel.setVisible(true);
            deleteCheckListLink.setText("Delete");
            deleteCheckListLink.setVisible(true);
            progressBox.getChildren().addAll(percentage, progressBar);
            progressBox.setMargin(progressBar, new Insets(0, 0, 0, 15));
            progressBox.setMargin(percentage, new Insets(0, 0, 0, 15));
            refreshScreen();
        });
        deleteCheckListLink.setOnAction(event -> {
            deleteCheckListLink.setText("");
            deleteCheckListLink.setVisible(false);
            checkListLabel.setText("");
            checkListLabel.setVisible(false);
            checkListLink.setText("Add checklist");
            checkListLink.setVisible(true);
            progressBox.getChildren().removeAll(percentage, progressBar);
            actionItemsBox.getChildren().removeAll(container);
            HashMap<Integer, CheckList> actionItems = this.workspaceModel.getCurrentWorkspace().getBoards().get(projectID).getColumns().get(columnID).getTasks().get(taskID).getActionItems();
            try {
                for (Integer keyOfItem : actionItems.keySet()) {
                    this.workspaceModel.getWorkspaceDao().deleteActionItems(keyOfItem, taskID);
                }
                //get all work space from database
                Workspace changedWorkspace = this.workspaceModel.getWorkspaceDao().getWorkspace(this.workspaceModel.getCurrentWorkspace().getUser());
                //set to current work space
                this.workspaceModel.setCurrentWorkspace(changedWorkspace);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Cancel.setOnAction(event -> {
            backToWorkspace();
        });

        checkBox.setOnAction(event -> {
            try {
                int completed;
                if (checkBox.isSelected()) {
                    completed = 1;
                } else {
                    completed = 0;
                }
                if (taskID!=0) {
                    if (this.workspaceModel.getCurrentWorkspace().getBoards().get(projectID).getColumns().get(columnID).getTasks().get(taskID).getActionItems().size()!=0) {
                        HashMap<Integer, CheckList> actionItems = this.workspaceModel.getCurrentWorkspace().getBoards().get(projectID).getColumns().get(columnID).getTasks().get(taskID).getActionItems();
                        for (Integer keyOfItem : actionItems.keySet()) {
                            //change in database
                            this.workspaceModel.getWorkspaceDao().setActionItemStatus(taskID, keyOfItem, completed);
                        }


                        //get all new work space from database
                        Workspace changedWorkspace = this.workspaceModel.getWorkspaceDao().getWorkspace(this.workspaceModel.getCurrentWorkspace().getUser());
                        //set to current work space
                        this.workspaceModel.setCurrentWorkspace(changedWorkspace);
                        //change interface
                        refreshScreen();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        if (this.taskID == 0) {
            Ok.setOnAction(event -> {
                if (!taskName.getText().isEmpty() && !description.getText().isEmpty() && taskName.getText().length() <= 20 && description.getText().length() <= 200) {
                    try {
                        this.workspaceModel.getWorkspaceDao().addTask(taskName.getText(), description.getText(), this.columnID, datePicker.getValue());
                        //get all work space from database
                        Workspace changedWorkspace = this.workspaceModel.getWorkspaceDao().getWorkspace(this.workspaceModel.getCurrentWorkspace().getUser());
                        //set to current work space
                        this.workspaceModel.setCurrentWorkspace(changedWorkspace);
                        //change interface
                        backToWorkspace();
                    } catch (SQLException e) {
                        status.setText(e.getMessage());
                        status.setTextFill(Color.RED);
                    }
                } else {
                    status.setText("Task name(within 20 chars) and description(within 200 chars) can not be empty");
                    status.setTextFill(Color.RED);
                }
            });
        } else {
            taskName.setText(workspaceModel.getCurrentWorkspace().getBoards().get(this.projectID).getColumns().get(this.columnID).getTasks().get(this.taskID).getTaskName());
            description.setText(workspaceModel.getCurrentWorkspace().getBoards().get(this.projectID).getColumns().get(this.columnID).getTasks().get(this.taskID).getDescription());
            Ok.setOnAction(event -> {

                if (!taskName.getText().isEmpty() && !description.getText().isEmpty() && taskName.getText().length() <= 20 && description.getText().length() <= 200) {
                    try {
                        this.workspaceModel.getWorkspaceDao().editTask(taskName.getText(), description.getText(), this.columnID, datePicker.getValue(), this.taskID);
                        //get all work space from database
                        Workspace changedWorkspace = this.workspaceModel.getWorkspaceDao().getWorkspace(this.workspaceModel.getCurrentWorkspace().getUser());
                        //set to current work space
                        this.workspaceModel.setCurrentWorkspace(changedWorkspace);
                        //change interface
                        backToWorkspace();
                    } catch (SQLException e) {
                        status.setText(e.getMessage());
                        status.setTextFill(Color.RED);
                    }
                } else {
                    status.setText("Task name(within 20 chars) and description(within 200 chars) can not be empty");
                    status.setTextFill(Color.RED);
                }
            });
        }
    }


    public void refreshScreen() {
        //clean action item box
        actionItemsBox.getChildren().removeAll(container);
        //set progress bar and label
        if (this.taskID==0||this.workspaceModel.getCurrentWorkspace().getBoards().get(projectID).getColumns().get(columnID).getTasks().get(taskID).getActionItems().isEmpty()) {
            percentage.setText("%0.0");
            progressBar.setProgress(0.0);
        } else {
            percentage.setText("%" + this.workspaceModel.getCurrentWorkspace().getBoards().get(projectID).getColumns().get(columnID).getTasks().get(taskID).completePercentage());
            progressBar.setProgress(this.workspaceModel.getCurrentWorkspace().getBoards().get(projectID).getColumns().get(columnID).getTasks().get(taskID).completePercentage() / 100);
        }
        container = new VBox();
        //container contain all the action items and a add link
        if (taskID!=0) {
            HashMap<Integer, CheckList> actionItems = this.workspaceModel.getCurrentWorkspace().getBoards().get(projectID).getColumns().get(columnID).getTasks().get(taskID).getActionItems();
            for (Integer keyOfItem : actionItems.keySet()) {
                HBox rows = new HBox();
                CheckBox item = new CheckBox(actionItems.get(keyOfItem).getActionItem());
                boolean itemStatus = false;
                if (actionItems.get(keyOfItem).getStatus() == 1) {
                    itemStatus = true;
                }
                //set item check box
                item.setSelected(itemStatus);
                Hyperlink editItem = new Hyperlink("Edit");
                Hyperlink deleteItem = new Hyperlink("Delete");
                rows.getChildren().addAll(item, editItem, deleteItem);
                rows.setMargin(editItem, new Insets(0, 0, 0, 150));
                rows.setMargin(deleteItem, new Insets(0, 0, 0, 15));
                item.setOnAction(event -> {
                    try {
                        int completed;
                        if (item.isSelected()) {
                            completed = 1;
                        } else {
                            completed = 0;
                        }
                        //change in database
                        this.workspaceModel.getWorkspaceDao().setActionItemStatus(taskID, keyOfItem, completed);
                        //get all work space from database
                        Workspace changedWorkspace = this.workspaceModel.getWorkspaceDao().getWorkspace(this.workspaceModel.getCurrentWorkspace().getUser());
                        //set to current work space
                        this.workspaceModel.setCurrentWorkspace(changedWorkspace);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    //change interface
                    refreshScreen();
                });
                container.getChildren().addAll(rows);
                //set actions
                editItem.setOnAction(event -> {
                    //generate dialog
                    Dialog<String> dialog = new Dialog<>();
                    dialog.setTitle("edit item");
                    TextField name = new TextField();
                    name.setPromptText("change item");
                    name.setLayoutX(20);
                    name.setLayoutY(20);
                    Label status = new Label();
                    Pane pane = new Pane();
                    pane.getChildren().add(name);
                    dialog.getDialogPane().setContent(pane);
                    ButtonType submitButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                    dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);
                    pane.getChildren().add(status);
                    //get information from dialog
                    dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == submitButtonType) {
                            //empty error
                            if (name.getText().isEmpty() || name.getText().length() > 20) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error Dialog");
                                alert.setHeaderText("item must have a name and less than 20 chars");
                                alert.setContentText("Input a name");
                                alert.showAndWait();
                            }
                            return name.getText();
                        }
                        return null;
                    });
                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(itemName -> {
                        try {
                            if (!name.getText().isEmpty()) {
                                int completed;
                                if (item.isSelected()) {
                                    completed = 1;
                                } else {
                                    completed = 0;
                                }
                                //change in database
                                this.workspaceModel.getWorkspaceDao().editActionItem(itemName, taskID, keyOfItem, completed);
                                //get all work space from database
                                Workspace changedWorkspace = this.workspaceModel.getWorkspaceDao().getWorkspace(this.workspaceModel.getCurrentWorkspace().getUser());
                                //set to current work space
                                this.workspaceModel.setCurrentWorkspace(changedWorkspace);
                                //change interface
                                refreshScreen();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                });
                deleteItem.setOnAction(event -> {
                    try {
                        //change in database
                        this.workspaceModel.getWorkspaceDao().deleteActionItems(keyOfItem, taskID);
                        //get all work space from database
                        Workspace changedWorkspace = this.workspaceModel.getWorkspaceDao().getWorkspace(this.workspaceModel.getCurrentWorkspace().getUser());
                        //set to current work space
                        this.workspaceModel.setCurrentWorkspace(changedWorkspace);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    refreshScreen();
                });
            }
        }
        container.getChildren().addAll(addItem);
        addItem.setOnAction(event -> {
            //generate dialog
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("New item");
            TextField name = new TextField();
            name.setPromptText("New item");
            name.setLayoutX(20);
            name.setLayoutY(20);
            Label status = new Label();
            Pane pane = new Pane();
            pane.getChildren().add(name);
            dialog.getDialogPane().setContent(pane);
            ButtonType submitButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);
            pane.getChildren().add(status);
            //get information from dialog
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == submitButtonType) {
                    //empty error
                    if (name.getText().isEmpty() || name.getText().length() > 20) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error Dialog");
                        alert.setHeaderText("item must have a name and less than 20 chars");
                        alert.setContentText("Input a name");
                        alert.showAndWait();
                    }
                    return name.getText();
                }
                return null;
            });
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(itemName -> {
                try {
                    if (!name.getText().isEmpty()) {
                        //create in database
                        this.workspaceModel.getWorkspaceDao().addActionItem(itemName, taskID);
                        //get all work space from database
                        Workspace changedWorkspace = this.workspaceModel.getWorkspaceDao().getWorkspace(this.workspaceModel.getCurrentWorkspace().getUser());
                        //set to current work space
                        this.workspaceModel.setCurrentWorkspace(changedWorkspace);
                        //change interface
                        refreshScreen();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            refreshScreen();
        });
        actionItemsBox.getChildren().addAll(container);
    }

    public void showStage(Pane root) {
        Scene scene = new Scene(root, 610, 430);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Add/edit new task");
        stage.show();
    }

    public void backToWorkspace() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/WorkSpaceView.fxml"));

            // Customize controller instance
            Callback<Class<?>, Object> controllerFactory = param -> {
                return new WorkSpaceController(this.workspaceModel, this.model, this.sentence);
            };

            loader.setControllerFactory(controllerFactory);
            Pane root = loader.load();

            WorkSpaceController workSpaceController = loader.getController();
            workSpaceController.showStage(root);

            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.close();
    }
}
