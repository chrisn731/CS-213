package app;

/**
 * @author Christopher Naporlee - cmn134
 * @author Michael Nelli - mrn73
 */

import java.io.IOException;

import controller.LoginViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Admin;

/**
 * Entry and launchpad class of the application.
 */
public class Photos extends Application {

	@Override
	/**
	 * Launches the application. Does necessary loading data from disk and then puts
	 * the user into the login view.
	 */
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
	/**
	 * Application tear down routine. Called when the user presses 'Quit' at any point
	 * or presses the 'X' on the window.
	 */
	public void stop() {
		try {
			Admin.syncToDisk();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Application entry point
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
