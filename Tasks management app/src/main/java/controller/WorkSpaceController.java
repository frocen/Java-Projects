package controller;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.*;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;


public class WorkSpaceController {
    @FXML
    private Menu projectMenu;

    @FXML
    private MenuItem deleteOption;

    @FXML
    private MenuItem setDefault;

    @FXML
    private MenuItem unsetDefault;

    @FXML
    private ImageView image;

    @FXML
    private Button logout;

    @FXML
    private Label name;

    @FXML
    private Button profile;

    @FXML
    private Label quote;

    @FXML
    private TabPane tabPane;

    @FXML
    private MenuItem addColumn;

    @FXML
    private MenuItem newProjectName;

    private Stage stage;
    private WorkspaceModel workspaceModel;
    private Model model;
    private String sentence;

    public WorkSpaceController(WorkspaceModel workspaceModel, Model model, String sentence) {
        this.stage = new Stage();
        this.workspaceModel = workspaceModel;
        this.model = model;
        this.sentence = sentence;
    }

    //setup in initialize and write each button.
    @FXML
    public void initialize() throws FileNotFoundException, SQLException {
        //set information and image
        quote.setText(this.sentence);
        name.setText(this.workspaceModel.getCurrentWorkspace().getUser().getFirstName()
                + " " + this.workspaceModel.getCurrentWorkspace().getUser().getLastName());
        InputStream fileInputStream;
        fileInputStream = new FileInputStream(this.workspaceModel.getCurrentWorkspace().getUser().getPhoto());
        image.setImage(new Image(fileInputStream));
        //is no project, disable project menu bar
        if (!this.workspaceModel.getCurrentWorkspace().getBoards().isEmpty()) {
            resetProjectTab();
        } else {
            projectMenu.setDisable(true);
        }
        setDefault.setOnAction(event -> {
            //get selected tab
            int orderNumber = tabPane.getSelectionModel().getSelectedIndex();
            // get back up project
            HashMap<Integer, Project> projects = this.workspaceModel.getCurrentWorkspace().getBoards();
            int i = 0;
            for (Integer projectID : projects.keySet()) {
                //use if and i to get specific projectID
                if (i == orderNumber) {
                    try {
                        //set in database
                        this.workspaceModel.getWorkspaceDao().setProjectDefault(projectID, this.workspaceModel.getCurrentWorkspace().getBoardID());
                        //get all workspace from database
                        Workspace changedWorkspace = this.workspaceModel.getWorkspaceDao().getWorkspace(this.workspaceModel.getCurrentWorkspace().getUser());
                        //set to current work space
                        this.workspaceModel.setCurrentWorkspace(changedWorkspace);
                        //change interface
                        resetProjectTab();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                i++;
            }
        });
        unsetDefault.setOnAction(event -> {
//get selected tab
            int orderNumber = tabPane.getSelectionModel().getSelectedIndex();
            // get back up project
            HashMap<Integer, Project> projects = this.workspaceModel.getCurrentWorkspace().getBoards();
            int i = 0;
            for (Integer projectID : projects.keySet()) {
                //use if and i to get specific projectID
                if (i == orderNumber) {
                    try {
                        //set in database
                        this.workspaceModel.getWorkspaceDao().unSetProjectDefault(projectID, this.workspaceModel.getCurrentWorkspace().getBoardID());
                        this.workspaceModel.getWorkspaceDao().setProjectDefault(projectID, this.workspaceModel.getCurrentWorkspace().getBoardID());
                        //get all workspace from database
                        Workspace changedWorkspace = this.workspaceModel.getWorkspaceDao().getWorkspace(this.workspaceModel.getCurrentWorkspace().getUser());
                        //set to current work space
                        this.workspaceModel.setCurrentWorkspace(changedWorkspace);
                        //change interface
                        resetProjectTab();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                i++;
            }
        });
        deleteOption.setOnAction(event -> {
            //get selected tab
            int orderNumber = tabPane.getSelectionModel().getSelectedIndex();
            //delete tab in interface
            tabPane.getTabs().remove(orderNumber);
            // get back up project
            HashMap<Integer, Project> projects = this.workspaceModel.getCurrentWorkspace().getBoards();
            int i = 0;
            for (Integer projectID : projects.keySet()) {
                //use if and i to get specific projectID
                if (i == orderNumber) {
                    try {
                        //delete in database
                        this.workspaceModel.getWorkspaceDao().deleteProject(projectID, projects.get(projectID).getColumns());
                        //get all work space from database
                        Workspace changedWorkspace = this.workspaceModel.getWorkspaceDao().getWorkspace(this.workspaceModel.getCurrentWorkspace().getUser());
                        //set to current work space
                        this.workspaceModel.setCurrentWorkspace(changedWorkspace);
                        if (!this.workspaceModel.getWorkspaceDao().haveDefault(this.workspaceModel.getCurrentWorkspace().getBoardID())) {
                            this.workspaceModel.getWorkspaceDao().setProjectDefault(projectID, this.workspaceModel.getCurrentWorkspace().getBoardID());
                        }
                        //user may delete the last project,if so set the project menu bar disable
                        if (changedWorkspace.getBoards().isEmpty()) {
                            projectMenu.setDisable(true);
                        }
                        resetProjectTab();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                i++;
            }
        });
        newProjectName.setOnAction(event -> {
            //get selected tab
            int orderNumber = tabPane.getSelectionModel().getSelectedIndex();
            HashMap<Integer, Project> projects = this.workspaceModel.getCurrentWorkspace().getBoards();
            int i = 0;
            int projectID = 0;
            for (Integer ID : projects.keySet()) {
                //use if and i to get specific projectID
                if (i == orderNumber) {
                    projectID = ID;
                }
                i++;
            }
//generate dialog
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("New project name");
            TextField name = new TextField();
            name.setPromptText("New project name");
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
                        alert.setHeaderText("Project must have a name and less than 20 chars");
                        alert.setContentText("Input a name");
                        alert.showAndWait();
                    }
                    return name.getText();
                }
                return null;
            });
            Optional<String> result = dialog.showAndWait();
            int finalProjectID = projectID;
            result.ifPresent(projectName -> {
                try {
                    if (!name.getText().isEmpty()) {
                        //change in database
                        this.workspaceModel.getWorkspaceDao().changeProjectName(projectName, finalProjectID, this.workspaceModel.getCurrentWorkspace().getBoardID());
                        //get all work space from database
                        Workspace changedWorkspace = this.workspaceModel.getWorkspaceDao().getWorkspace(this.workspaceModel.getCurrentWorkspace().getUser());
                        //set to current work space
                        this.workspaceModel.setCurrentWorkspace(changedWorkspace);
                        //change interface
                        resetProjectTab();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        });
        profile.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProfileEditorView.fxml"));

                // Customize controller instance
                Callback<Class<?>, Object> controllerFactory = param -> {
                    return new ProfileEditorController(this.workspaceModel, this.model, this.sentence);
                };

                loader.setControllerFactory(controllerFactory);
                GridPane root = loader.load();

                ProfileEditorController profileEditorController = loader.getController();
                profileEditorController.showStage(root);
                stage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        addColumn.setOnAction(event -> {
            //get selected tab
            int orderNumber = tabPane.getSelectionModel().getSelectedIndex();
            HashMap<Integer, Project> projects = this.workspaceModel.getCurrentWorkspace().getBoards();
            int i = 0;
            int projectID = 0;
            for (Integer ID : projects.keySet()) {
                //use if and i to get specific projectID
                if (i == orderNumber) {
                    projectID = ID;
                }
                i++;
            }
            //generate dialog
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Add column");
            TextField name = new TextField();
            name.setPromptText("New column");
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
                        alert.setHeaderText("column must have a name and less than 20 chars");
                        alert.setContentText("Input a name");
                        alert.showAndWait();
                    }
                    return name.getText();
                }
                return null;
            });
            Optional<String> result = dialog.showAndWait();
            int finalProjectID = projectID;
            result.ifPresent(columnName -> {
                try {
                    if (!name.getText().isEmpty() && name.getText().length() <= 20) {
                        //create in database
                        this.workspaceModel.getWorkspaceDao().addColumn(columnName, finalProjectID);
                        //get all work space from database
                        Workspace changedWorkspace = this.workspaceModel.getWorkspaceDao().getWorkspace(this.workspaceModel.getCurrentWorkspace().getUser());
                        //set to current work space
                        this.workspaceModel.setCurrentWorkspace(changedWorkspace);
                        //change interface
                        resetProjectTab();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        });
        logout.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));

                // Customize controller instance
                Callback<Class<?>, Object> controllerFactory = param -> {
                    return new LoginController(new Stage(), model);
                };

                loader.setControllerFactory(controllerFactory);

                GridPane root = loader.load();

                LoginController loginController = loader.getController();
                loginController.showStage(root);
                stage.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    public void newProjectDialog(ActionEvent event) {
        //generate dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Create project");
        TextField name = new TextField();
        name.setPromptText("New project");
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
                    alert.setHeaderText("project must have a name and less than 20 chars");
                    alert.setContentText("Input a name");
                    alert.showAndWait();
                }
                return name.getText();
            }
            return null;
        });
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(projectName -> {
            try {
                if (!name.getText().isEmpty()) {
                    //create in database
                    this.workspaceModel.getWorkspaceDao().createProject(projectName, 0, this.workspaceModel.getCurrentWorkspace().getBoardID());
                    //get all work space from database
                    Workspace changedWorkspace = this.workspaceModel.getWorkspaceDao().getWorkspace(this.workspaceModel.getCurrentWorkspace().getUser());
                    //set to current work space
                    this.workspaceModel.setCurrentWorkspace(changedWorkspace);
                    if (!this.workspaceModel.getWorkspaceDao().haveDefault(this.workspaceModel.getCurrentWorkspace().getBoardID())) {
                        for (Integer projectID : this.workspaceModel.getCurrentWorkspace().getBoards().keySet()) {
                            this.workspaceModel.getWorkspaceDao().setProjectDefault(projectID, this.workspaceModel.getCurrentWorkspace().getBoardID());
                        }
                    }
                    //change interface
                    resetProjectTab();
                    projectMenu.setDisable(false);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void newTaskDialog(ActionEvent event, int columnID, int projectID) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TaskView.fxml"));

            // Customize controller instance
            Callback<Class<?>, Object> controllerFactory = param -> {
                return new TaskController(this.workspaceModel, columnID, this.model, this.sentence, 0, projectID);
            };

            loader.setControllerFactory(controllerFactory);
            Pane root = loader.load();

            TaskController taskController = loader.getController();
            taskController.showStage(root);
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetProjectTab() throws SQLException {
        //get all work space from database
        Workspace changedWorkspace = this.workspaceModel.getWorkspaceDao().getWorkspace(this.workspaceModel.getCurrentWorkspace().getUser());
        //set to current work space
        this.workspaceModel.setCurrentWorkspace(changedWorkspace);
        //is no project, disable project menu bar
        if (!this.workspaceModel.getCurrentWorkspace().getBoards().isEmpty()) {
            tabPane.getTabs().clear();
            HashMap<Integer, Project> projects = this.workspaceModel.getCurrentWorkspace().getBoards();
            int selectedIndex = 0;
            //key is projectID
            for (Integer key : projects.keySet()) {
                //create a tab
                Tab tab = new Tab();
                tab.setText(projects.get(key).getProjectName());
                HashMap<Integer, Column> columns = projects.get(key).getColumns();
                ScrollPane scrollPane = new ScrollPane();
                Pane pane = new Pane();

                int progressBarX = 0;
                int columnNameLabelX = 5;
                int menuButtonX = 200;
                //keyOfColumn is columnID
                for (Integer keyOfColumn : columns.keySet()) {
                    //add a bar
                    ProgressBar progressBar = new ProgressBar();
                    progressBar.setProgress(0.0);
                    progressBar.setPrefWidth(300);
                    progressBar.setPrefHeight(40);
                    progressBar.setLayoutX(progressBarX);
                    progressBar.setLayoutY(0);
                    //add column name
                    Label columnNameLabel = new Label();
                    columnNameLabel.setText(columns.get(keyOfColumn).getColumnName());
                    columnNameLabel.setLayoutX(columnNameLabelX);
                    columnNameLabel.setLayoutY(12);
                    columnNameLabel.setOnDragOver(new EventHandler<DragEvent>() {
                        public void handle(DragEvent event) {
                            /* accept it only if it is not dragged from the same node
                             * and if it has a string data */
                            if (event.getGestureSource() != columnNameLabel &&
                                    event.getDragboard().hasString()) {
                                /* allow for both copying and moving, whatever user chooses */
                                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                            }
                            event.consume();
                        }
                    });
                    columnNameLabel.setOnDragDropped(new EventHandler<DragEvent>() {
                        public void handle(DragEvent event) {
                            /* data dropped */
                            /* if there is a string data on dragboard, read it and use it */
                            Dragboard db = event.getDragboard();
                            boolean success = false;
                            if (db.hasString()) {
                                int selectedTaskID = Integer.parseInt(db.getString().split(",")[0]);
                                try {
                                    //change in database and refresh windows
                                    workspaceModel.getWorkspaceDao().moveToColumn(selectedTaskID, keyOfColumn);
                                    resetProjectTab();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                success = true;
                            }
                            /* let the source know whether the string was successfully
                             * transferred and used */
                            event.setDropCompleted(success);
                            event.consume();
                        }
                    });
                    MenuButton menuButton = new MenuButton("Select");
                    menuButton.setLayoutX(menuButtonX);
                    menuButton.setLayoutY(7);
                    //set MenuItems
                    MenuItem rename = new MenuItem("Rename");
                    MenuItem delete = new MenuItem("Delete");
                    menuButton.getItems().addAll(rename, delete);
                    rename.setOnAction(event -> {
                        newColumnName(event, keyOfColumn, key);
                    });
                    delete.setOnAction(event -> {
                        deleteColumnList(event, keyOfColumn, key, columns.get(keyOfColumn).getTasks());
                    });
                    MenuItem addTask = new MenuItem("Add task");
                    menuButton.getItems().add(addTask);
                    addTask.setOnAction(event -> {
                        newTaskDialog(event, keyOfColumn, key);
                    });
                    pane.getChildren().addAll(progressBar, columnNameLabel, menuButton);
                    //add task card here
                    int taskCardY = 45;
                    HashMap<Integer, Task> tasks = columns.get(keyOfColumn).getTasks();
                    //keyOfTask is taskID
                    for (Integer keyOfTask : tasks.keySet()) {
                        Pane taskCard = new Pane();
                        taskCard.setStyle("-fx-background-color: white;");
                        taskCard.setLayoutX(progressBar.getLayoutX());
                        taskCard.setLayoutY(taskCardY);
                        taskCard.setPrefHeight(120);
                        taskCard.setPrefWidth(300);
                        Hyperlink edit = new Hyperlink("Edit");
                        edit.setLayoutX(200);
                        edit.setLayoutY(10);
                        edit.setOnAction(event -> {
                            editTask(event, keyOfColumn, keyOfTask, key);
                        });
                        Hyperlink deleteTask = new Hyperlink("Delete");
                        deleteTask.setLayoutX(240);
                        deleteTask.setLayoutY(10);
                        deleteTask.setOnAction(event -> {
                            deleteTaskCard(event, keyOfColumn, keyOfTask, tasks.get(keyOfTask).getActionItems());
                        });
                        Label dueDate = new Label();
                        dueDate.setLayoutY(100);
                        String date;
                        if (tasks.get(keyOfTask).getDueDate() == null) {
                            date = null;
                        } else {
                            date = "Before end of " + tasks.get(keyOfTask).getDueDate().toString();
                        }
                        dueDate.setText(date);
                        Label items = new Label();
                        items.setLayoutX(200);
                        items.setLayoutY(100);
                        if (!tasks.get(keyOfTask).getActionItems().isEmpty()) {
                            items.setText(tasks.get(keyOfTask).completeNumber() + "/" + tasks.get(keyOfTask).getActionItems().size());
                            if (tasks.get(keyOfTask).getDueDate() != null) {
                                if (tasks.get(keyOfTask).isComplete()) {
                                    items.setTextFill(Color.GREEN);
                                    dueDate.setTextFill(Color.GREEN);
                                } else if (LocalDate.now().isEqual(tasks.get(keyOfTask).getDueDate())) {
                                    dueDate.setTextFill(Color.YELLOW);
                                } else if (LocalDate.now().isAfter(tasks.get(keyOfTask).getDueDate())) {
                                    dueDate.setTextFill(Color.RED);
                                }
                            }
                        }
                        Label taskName = new Label();
                        taskName.setText(tasks.get(keyOfTask).getTaskName());
                        taskName.setLayoutY(50);
                        taskCard.getChildren().addAll(edit, deleteTask, taskName, dueDate, items);
                        taskName.setOnDragDetected(new EventHandler<MouseEvent>() {
                            public void handle(MouseEvent event) {
                                /* drag was detected, start drag-and-drop gesture*/
                                /* allow any transfer mode */
                                Dragboard db = taskName.startDragAndDrop(TransferMode.ANY);
                                /* put a string on dragboard */
                                ClipboardContent content = new ClipboardContent();
                                String date;
                                if (tasks.get(keyOfTask).getDueDate() == null) {
                                    date = "null";
                                } else {
                                    date = tasks.get(keyOfTask).getDueDate().toString();
                                }
                                content.putString(keyOfTask + "," + keyOfColumn + "," + tasks.get(keyOfTask).getTaskName() + "," + tasks.get(keyOfTask).getDescription() + "," + date);
                                db.setContent(content);
                                event.consume();
                            }
                        });
                        taskName.setOnDragOver(new EventHandler<DragEvent>() {
                            public void handle(DragEvent event) {
                                /* accept it only if it is not dragged from the same node
                                 * and if it has a string data */
                                if (event.getGestureSource() != taskName &&
                                        event.getDragboard().hasString()) {
                                    /* allow for both copying and moving, whatever user chooses */
                                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                                }

                                event.consume();
                            }
                        });
                        taskName.setOnDragDropped(new EventHandler<DragEvent>() {
                            public void handle(DragEvent event) {
                                /* data dropped */
                                /* if there is a string data on dragboard, read it and use it */
                                Dragboard db = event.getDragboard();
                                boolean success = false;
                                if (db.hasString()) {
                                    int selectedTaskID = Integer.parseInt(db.getString().split(",")[0]);
                                    int selectedColumnID = Integer.parseInt(db.getString().split(",")[1]);
                                    String selectedTaskName = db.getString().split(",")[2];
                                    String selectedDescription = db.getString().split(",")[3];
                                    LocalDate date;
                                    if (!db.getString().split(",")[4].equals("null")) {
                                        date = LocalDate.parse(db.getString().split(",")[4]);
                                    } else {
                                        date = null;
                                    }
                                    try {
                                        //change in database and refresh windows
                                        workspaceModel.getWorkspaceDao().switchTaskCard(selectedTaskID, selectedColumnID
                                                , selectedTaskName, selectedDescription, date, keyOfTask, keyOfColumn
                                                , tasks.get(keyOfTask).getTaskName(), tasks.get(keyOfTask).getDescription()
                                                , tasks.get(keyOfTask).getDueDate());
                                        resetProjectTab();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    success = true;
                                }
                                /* let the source know whether the string was successfully
                                 * transferred and used */
                                event.setDropCompleted(success);
                                event.consume();
                            }
                        });

                        pane.getChildren().add(taskCard);
                        taskCardY += 125;
                    }
                    progressBarX += 325;
                    columnNameLabelX += 325;
                    menuButtonX += 325;
                }

                scrollPane.setContent(pane);
                tab.setContent(scrollPane);
                //create end
                //select the default project
                if (this.workspaceModel.getWorkspaceDao().getStatus(key, this.workspaceModel.getCurrentWorkspace().getBoardID()) == 1) {
                    tab.setText(projects.get(key).getProjectName() + "(*)");
                    tabPane.getTabs().add(tab);
                    tabPane.getSelectionModel().select(selectedIndex);
                    continue;
                }
                tabPane.getTabs().add(tab);
                selectedIndex++;
            }
        } else {
            projectMenu.setDisable(true);
        }
    }

    public void newColumnName(ActionEvent event, int columnID, int projectID) {
        //generate dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("New column name");
        TextField name = new TextField();
        name.setPromptText("New column name");
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
                    alert.setHeaderText("Column must have a name and less than 20 chars");
                    alert.setContentText("Input a name");
                    alert.showAndWait();
                }
                return name.getText();
            }
            return null;
        });
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(columnName -> {
            try {
                if (!name.getText().isEmpty()) {
                    //change in database
                    this.workspaceModel.getWorkspaceDao().changeColumnName(columnName, columnID, projectID);
                    //get all work space from database
                    Workspace changedWorkspace = this.workspaceModel.getWorkspaceDao().getWorkspace(this.workspaceModel.getCurrentWorkspace().getUser());
                    //set to current work space
                    this.workspaceModel.setCurrentWorkspace(changedWorkspace);
                    //change interface
                    resetProjectTab();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void deleteColumnList(ActionEvent event, int columnID, int projectID, HashMap<Integer, Task> tasks) {
        try {
            //delete in database
            this.workspaceModel.getWorkspaceDao().deleteColumn(columnID, projectID, tasks);
            //get all work space from database
            Workspace changedWorkspace = this.workspaceModel.getWorkspaceDao().getWorkspace(this.workspaceModel.getCurrentWorkspace().getUser());
            //set to current work space
            this.workspaceModel.setCurrentWorkspace(changedWorkspace);
            resetProjectTab();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editTask(ActionEvent event, int columnID, int taskID, int projectID) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TaskView.fxml"));

            // Customize controller instance
            Callback<Class<?>, Object> controllerFactory = param -> {
                return new TaskController(this.workspaceModel, columnID, this.model, this.sentence, taskID, projectID);
            };

            loader.setControllerFactory(controllerFactory);
            Pane root = loader.load();

            TaskController taskController = loader.getController();
            taskController.showStage(root);
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteTaskCard(ActionEvent event, int columnID, int taskID, HashMap<Integer, CheckList> actionItems) {
        try {
            //delete in database
            this.workspaceModel.getWorkspaceDao().deleteTask(taskID, columnID, actionItems);
            //get all work space from database
            Workspace changedWorkspace = this.workspaceModel.getWorkspaceDao().getWorkspace(this.workspaceModel.getCurrentWorkspace().getUser());
            //set to current work space
            this.workspaceModel.setCurrentWorkspace(changedWorkspace);
            resetProjectTab();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showStage(Pane root) {
        Scene scene = new Scene(root, 1000, 400);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Welcome to board");
        stage.show();
    }
}
