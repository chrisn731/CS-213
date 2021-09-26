/* 
 * Michael Nelli
 * Chris Naporlee 
 */

package songlib.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import songlib.view.SongLibController;

public class SongLib extends Application {

	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/songlib/view/songlib.fxml"));
		GridPane root = (GridPane)loader.load();
		Scene scene = new Scene(root, 410, 500);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Song Library");
		primaryStage.setResizable(false);
		primaryStage.show();
		
		SongLibController sbc = loader.getController();
		sbc.init();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
