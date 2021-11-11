package app;

import java.io.IOException;

import controller.LoginViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Admin;

public class Photos extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Admin.loadFromDisk();
	
		FXMLLoader loader = new FXMLLoader();
		AnchorPane root;
		loader.setLocation(getClass().getResource("/view/LoginView.fxml"));
		root = (AnchorPane)loader.load();
		LoginViewController lvc = loader.getController();
		lvc.init(primaryStage);
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Photos Library");
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	@Override
	public void stop() {
		try {
			Admin.syncToDisk();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
