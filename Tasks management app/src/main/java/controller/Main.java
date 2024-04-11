package controller;

import java.io.IOException;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.fxml.FXMLLoader;

import model.Model;

public class Main extends Application {
	private Model model;

	@Override
	public void init() {
		model = new Model();
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			model.setup();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
			
			// Customize controller instance
			Callback<Class<?>, Object> controllerFactory = param -> {
				return new LoginController(primaryStage, model);
			};

			loader.setControllerFactory(controllerFactory);

			GridPane root = loader.load();

			LoginController loginController = loader.getController();
			loginController.showStage(root);
		} catch (IOException | RuntimeException | SQLException e) {
			Scene scene = new Scene(new Label(e.getMessage()), 200, 100);
			primaryStage.setTitle("Error");
			primaryStage.setScene(scene);
			primaryStage.show();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
