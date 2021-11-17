package controller;
/**
 * @author: Michael Nelli - mrn73
 * @author: Christopher Naporlee - cmn134
 */
import java.io.IOException;
import java.util.Optional;

import app.Loadable;
import app.Scenes;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Base class for all scenes.
 */
public abstract class SceneController {
	
	/**
	 * The stage of the scene.
	 */
	protected Stage s;
	
	/**
	 * The max length of a textbox entry.
	 */
	protected final int MAX_ENTRY_LENGTH = 20;
	
	/**
	 * Switches the current scene of the stage.
	 * @param scene  to switch to
	 * @return  the controller of the loaded scene
	 */
	protected SceneController switchScene(Scenes scene) {
		FXMLLoader loader = loadAsset(scene);
		SceneController sceneController = loader.getController();
		sceneController.s = s;	
		Scene newScene = new Scene(loader.getRoot());
		s.setScene(newScene);
		return sceneController;
	}
	
	/**
	 * Loads all objects that implement Loadable.
	 * @param asset  to be loaded
	 * @return  the loader object of the asset
	 */
	protected FXMLLoader loadAsset(Loadable asset) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(asset.getPath()));
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return loader;
	}
	
	/**
	 * Creates and displays a TextInputDialog box where the user can enter input.
	 * @param title  of the TextInputDialog
	 * @param header  of the TextInputDialog
	 * @param content  of the TextInputDialog
	 * @param prompText  of the TextField
	 * @param disableOK Determines whether we should disable the OK button
	 * @return String within TextField. If empty, returns null.
	 */
	protected String getUserInput(String title, String header, String content, String prompText, boolean disableOK) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.initOwner(s);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(content);
		Node buttonOK = dialog.getDialogPane().lookupButton(ButtonType.OK);
		
		TextField editor = dialog.getEditor();
		editor.setText(prompText);
		buttonOK.setDisable(disableOK);
		
		/* Prevents the user from clicking OK without having a proper album name */
		editor.textProperty().addListener((observable, oldValue, newValue) -> {
		    if (newValue.length() > MAX_ENTRY_LENGTH)
		    	editor.setText(newValue = oldValue);
		    buttonOK.setDisable(newValue.isBlank());
		});
		
		/* Making it here means our album has at least 1 letter in it. Clicking cancel will return null. */
		Optional<String> ret = dialog.showAndWait();
		return ret.isPresent() ? ret.get() : null;
	}
	
	/**
	 * Creates and displays an Alert message when there is an error or a warning.
	 * @param title  of the Alert pop-up
	 * @param header  of the Alert pop-up
	 * @param context  of the Alert pop-up
	 * @param t  the type of Alert pane shown
	 * @return  whether or not the user confirmed the pop-up
	 */
	protected boolean showPopup(String title, String header, String context, AlertType t) {
		Alert alert = new Alert(t);
		alert.initOwner(s);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(context);
		Optional<ButtonType> res = alert.showAndWait();
		return res.isPresent() ? res.get().equals(ButtonType.OK) : false;
	}
}
