package app;

import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Admin;

public class Photos extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			Admin.loadFromDisk();
		} catch (FileNotFoundException e) {
			System.out.println("File not there");
		}
	
		
		AnchorPane ap = (AnchorPane) new AnchorPane();
		Scene scene = new Scene(ap, 900, 600);
		
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
