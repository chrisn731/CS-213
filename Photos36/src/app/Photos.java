package app;

import java.io.FileNotFoundException;
import java.io.IOException;

import controller.AlbumViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Admin;
import model.User;

public class Photos extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			Admin.loadFromDisk();
		} catch (FileNotFoundException e) {
			System.out.println("File not there");
		}
	
		/* START ALBUM TESTING: Launch into Album View */
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/AlbumView.fxml"));
		AnchorPane root = (AnchorPane)loader.load();
		AlbumViewController avc = loader.getController();
		avc.init(primaryStage, new User(null));
		/* END ALBUM TESTING */
		
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
