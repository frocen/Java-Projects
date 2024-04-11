package controller;

import dao.UserDao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Model;
import model.User;
import model.WorkspaceModel;

import java.io.*;
import java.sql.SQLException;

public class ProfileEditorController {

    @FXML
    private Button Cancel;

    @FXML
    private Button Ok;

    @FXML
    private ImageView image;

    @FXML
    private Label status;

    @FXML
    private Button select;

    @FXML
    private TextField newFirstName;

    @FXML
    private TextField newLastname;

    @FXML
    private Label userName;
    private Stage stage;
    private WorkspaceModel workspaceModel;
    private String fileName;
    private Model model;
    private String sentence;

    public ProfileEditorController(WorkspaceModel workspaceModel, Model model, String sentence) {
        this.stage = new Stage();
        this.workspaceModel = workspaceModel;
        this.fileName = this.workspaceModel.getCurrentWorkspace().getUser().getPhoto();
        this.model=model;
        this.sentence=sentence;
    }

    @FXML
    public void initialize() throws FileNotFoundException {
        userName.setText(this.workspaceModel.getCurrentWorkspace().getUser().getUsername());
        InputStream fileInputStream;
        fileInputStream = new FileInputStream(this.fileName);
        image.setImage(new Image(fileInputStream));

        select.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();

            // Set extension filter
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("*.png", "*.jpg", "*.jpeg");

            fileChooser.getExtensionFilters().add(extFilter);

            // Show a file open dialog
            File selectedFile = fileChooser.showOpenDialog(stage);

            InputStream fileInputStream1;

            try {
                fileInputStream1 = new FileInputStream(selectedFile);
                image.setImage(new Image(fileInputStream1));
                this.fileName = selectedFile.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Cancel.setOnAction(event -> {
            backToWorkspace();
        });

        Ok.setOnAction(event -> {
            if (!newFirstName.getText().isEmpty() && !newLastname.getText().isEmpty()) {
                try {
                    String username=this.model.getCurrentUser().getUsername();
                    this.model.getUserDao().updateProfile(username,newFirstName.getText(),newLastname.getText(),this.fileName);
                    User user=this.model.getCurrentUser();
                    user.setFirstName(newFirstName.getText());
                    user.setLastName(newLastname.getText());
                    user.setPhoto(this.fileName);
                    this.model.setCurrentUser(user);
                    backToWorkspace();
                } catch (SQLException e) {
                    status.setText(e.getMessage());
                    status.setTextFill(Color.RED);
                }
            } else {
                status.setText("Empty first name or last name");
                status.setTextFill(Color.RED);
            }
        });
    }

    public void showStage(Pane root) {
        Scene scene = new Scene(root, 590, 300);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Edit profile");
        stage.show();
    }

    public void backToWorkspace() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/WorkSpaceView.fxml"));

            // Customize controller instance
            Callback<Class<?>, Object> controllerFactory = param -> {
                return new WorkSpaceController(this.workspaceModel ,this.model , this.sentence);
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
