package controller;

import java.io.*;
import java.sql.SQLException;

import javafx.fxml.FXML;
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
import model.Model;
import model.User;

public class SignupController {
    @FXML
    private TextField username;
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField password;
    @FXML
    private Button createUser;
    @FXML
    private Button select;
    @FXML
    private Button close;
    @FXML
    private Label status;
    @FXML
    private ImageView image;

    private Stage stage;
    private Stage parentStage;
    private Model model;
    private String fileName;

    public SignupController(Stage parentStage, Model model) {
        this.stage = new Stage();
        this.parentStage = parentStage;
        this.model = model;
        this.fileName="src/main/image/photo.jpg";
    }

    @FXML
    public void initialize() throws FileNotFoundException {
        InputStream fileInputStream;
        fileInputStream = new FileInputStream(this.fileName);
        image.setImage(new Image(fileInputStream));
        createUser.setOnAction(event -> {
            if (!username.getText().isEmpty() && !password.getText().isEmpty() && !firstName.getText().isEmpty() && !lastName.getText().isEmpty()) {
                User user;
                try {
                    if (!model.getUserDao().isExist(username.getText())) {
                        user = model.getUserDao().createUser(username.getText(), password.getText(),firstName.getText(),lastName.getText(),this.fileName);
                        if (user != null) {
                            status.setText("Created " + user.getUsername());
                            status.setTextFill(Color.GREEN);
                        } else {
                            status.setText("Cannot create user");
                            status.setTextFill(Color.RED);
                        }
                    } else {
                        status.setText("Username is exist");
                        status.setTextFill(Color.RED);
                    }
                } catch (SQLException e) {
                    status.setText(e.getMessage());
                    status.setTextFill(Color.RED);
                }

            } else {
                status.setText("Empty username or password");
                status.setTextFill(Color.RED);
            }
        });
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
                this.fileName=selectedFile.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        close.setOnAction(event -> {
            stage.close();
            parentStage.show();
        });
    }

    public void showStage(Pane root) {
        Scene scene = new Scene(root, 400, 600);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Sign up");
        stage.show();
    }
}
