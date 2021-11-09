package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import model.Album;
import model.Photo;
import model.User;

public class AlbumViewController {

	@FXML private MenuItem buttonNewAlbum;
	@FXML private MenuItem buttonNewAlbumFromSearch;
	@FXML private MenuItem buttonQuit;
	@FXML private MenuItem buttonDelete;
	@FXML private MenuItem buttunLogout;
	@FXML private TilePane albumList;
	@FXML private ScrollPane scrollpane;
	@FXML private ComboBox<String> comboFilter;
	@FXML private TextField textboxSearch;
	
	private Stage stage;
	private User user;
	private ArrayList<AlbumPaneController> albumPanes;
	private final int MAX_NAME_LENGTH = 20;
	
	public void init(Stage s, User u) {
		this.stage = s;
		this.user = u;
		albumPanes = new ArrayList<AlbumPaneController>();
		comboFilter.setItems(FXCollections.observableArrayList("Sort by: None", "Sort by: Date", "Sort by: Tags"));
		
		buttonNewAlbumFromSearch.setDisable(true);
		textboxSearch.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.trim().equals("")) {
		    	buttonNewAlbumFromSearch.setDisable(true);
		    } else {
		    	buttonNewAlbumFromSearch.setDisable(false);
		    	searchAlbums(newValue);
		    }
		});
		
		//scrollpane.vvalueProperty().bind(albumList.heightProperty());
	}
	
	@FXML
	private void menuSelect(ActionEvent e) {
		MenuItem item = (MenuItem) e.getSource();
		if (item == buttonQuit) {
			setVval();
		}
	}
	
	@FXML
	private void createAlbumFromSearch(ActionEvent e) {
		 
	}
	
	@FXML
	private void logout(ActionEvent e) {
		/* TODO: go back to login screen */
	}
	
	private void searchAlbums(String name) {
		for (AlbumPaneController apc : albumPanes) {
			if (!apc.getAlbum().getName().contains(name)) {
				apc.setVisible(false);
			}
		}
	}

	@FXML
	private void createAlbum() {
		String name = confirmAction("New Album", null, "Enter name: ", null, true);
		if (name == null || duplicateNameFound(name))
			return;

		/* Create an albumPane from an FXML file to represent an album in the tilepane*/
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/albumBox.fxml"));
		AnchorPane albumPane;
		try {
			albumPane = (AnchorPane)loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		AlbumPaneController albumPaneController = loader.getController();
		Album album = new Album(name);
		album.addPhoto(new Photo("hi"));
		album.addPhoto(new Photo("hey"));
		user.addAlbum(album);
		albumPaneController.init(album, this);
		albumList.getChildren().add(albumPane);
		albumPanes.add(albumPaneController);
		//scrollpane.setVvalue(1);
	}
	
	private boolean duplicateNameFound(String name) {
		for (AlbumPaneController apc : albumPanes) {
			if (apc.getAlbum().getName().equals(name)) {
				showPopup("Error", null, "This album name already exists", AlertType.WARNING);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Creates and displays a TextInputDialog box where the user adds or edits the name of an album.
	 * <p>
	 * All albums must have at least 1 character (not including spaces) to be valid.
	 * @param title  of the TextInputDialog
	 * @param header  of the TextInputDialog
	 * @param content  of the TextInputDialog
	 * @param prompText  of the TextField
	 * @param disableOK
	 * @return String within TextField. If empty, returns null.
	 */
	public String confirmAction(String title, String header, String content, String prompText, boolean disableOK) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.initOwner(stage);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(content);
		Node buttonOK = dialog.getDialogPane().lookupButton(ButtonType.OK);
		
		TextField editor = dialog.getEditor();
		editor.setText(prompText);
		buttonOK.setDisable(disableOK);
		
		/* Prevents the user from clicking OK without having a proper album name */
		editor.textProperty().addListener((observable, oldValue, newValue) -> {
		    if (newValue.length() > MAX_NAME_LENGTH)
		    	editor.setText(newValue = oldValue);
		    buttonOK.setDisable(newValue.trim().equals(""));
		});
		
		/* Making it here means our album has at least 1 letter in it. Clicking cancel will return null. */
		Optional<String> ret = dialog.showAndWait();
		if (ret.isPresent())
			return ret.get(); 
		return null;
	}
	
	/**
	 * Creates and displays an Alert message when there is an error.
	 * @param title  of the Alert pop-up
	 * @param header  of the Alert pop-up
	 * @param context  of the Alert pop-up
	 */
	private boolean showPopup(String title, String header, String context, AlertType t) {
		Alert alert = new Alert(t);
		alert.initOwner(stage);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(context);
		alert.showAndWait();
		if (alert.getResult().equals(ButtonType.OK))
			return true;
		return false;
	}
	
	public String editAlbumName(String oldName) {
		String newName = confirmAction("Edit Name", null, "Edit name: ", oldName, false);
		if (newName == null || duplicateNameFound(newName))
			return null;
		return newName;
	}
	
	public void deleteAlbum(AlbumPaneController apc, Node root) {
		//double vVal = scrollpane.getVvalue();
		if (!showPopup("Delete Album", null, "Are you sure you want to delete " + apc.getAlbum().getName() + "?", AlertType.CONFIRMATION))
			return;
		user.removeAlbum(apc.getAlbum());
		albumList.getChildren().remove(root);
		albumPanes.remove(apc);
	}
	
	public void setVval() {
		scrollpane.setVvalue(1);
	}
	
	private void easteregg() {
		/*Hidden easteregg -- don't forget to add back javafx.media to vm
		if (name.equals("helicopters")) {
			File mediaFile = new File("C:\\Users\\mikes\\Documents\\Java Projects\\Software Methodology\\Photos36\\src\\assets\\ALL_IS_SEE_IS_HELICOPTERS.mp3");
			Media m = new Media(mediaFile.toURI().toString());
			MediaPlayer player = new MediaPlayer(m);
			player.play();
		}
		*/
	}
}
