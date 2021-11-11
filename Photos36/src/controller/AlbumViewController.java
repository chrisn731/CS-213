package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import app.Assets;
import app.Scenes;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Album;
import model.Photo;
import model.User;

public class AlbumViewController extends SceneController {

	public static class AlbumPaneController {
		@FXML private Text labelAlbumName;
		@FXML private Text labelPhotoCount;
		@FXML private Text labelDates;
		@FXML private Button buttonEdit;
		@FXML private Button buttonDelete;
		@FXML private Pane paneAlbum;
		@FXML private AnchorPane root;
		
		private Album album;
		private AlbumViewController parentController;
		private final double HOVER_OPACITY = .4;
		
		public void init(Album a, AlbumViewController avc) {
			album = a;
			parentController = avc;
			labelAlbumName.setText(album.getName());
			
			playScaleAnimation();
			
			for (Node n : paneAlbum.getChildren()) {
				n.setVisible(false);
			}
			paneAlbum.hoverProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue) {
					paneAlbum.setStyle("-fx-background-color: rgba(125,125,125," + HOVER_OPACITY + ");"
							         + "-fx-cursor: hand;"
							         + "-fx-border-color: black");
				} else {
					paneAlbum.setStyle("-fx-background-color: rgba(125,125,125," + 1 + ")");
				}
				for (Node n : paneAlbum.getChildren()) {
					n.setVisible(newValue);
				}
				labelPhotoCount.setText(Integer.toString(album.getPhotoCount()) + " Photos");
			});
			
			paneAlbum.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
				public void handle(MouseEvent click) {
					if (click.getClickCount() == 1) {
						parentController.requestAlbumOpen(album);
					}
				}
			});
			labelDates.setText("");
		}
		
		@FXML
		private void editAlbumName() {
			String newName;
			if ((newName = parentController.editAlbumName(album.getName())) != null) {
				labelAlbumName.setText(newName);
				album.setName(newName);
			}
		}
		
		@FXML
		private void deleteAlbum() {
			parentController.deleteAlbum(this, root);
		}
		
		private void playScaleAnimation() {
			paneAlbum.setScaleX(.05);
			paneAlbum.setScaleY(.05);
			ScaleTransition st = new ScaleTransition();
			st.setNode(paneAlbum);
			st.setDuration(Duration.millis(200));
			st.setByX(.95);
			st.setByY(.95);
			st.setCycleCount(1);
			st.setAutoReverse(true);
			st.play();
		}
		
		public Album getAlbum() {
			return album;
		}
		
		public void setVisible(boolean b) {
			root.setVisible(b);
		}
	}
	
	@FXML private MenuItem buttonNewAlbum;
	@FXML private MenuItem buttonNewAlbumFromSearch;
	@FXML private MenuItem buttonQuit;
	@FXML private MenuItem buttonDelete;
	@FXML private MenuItem buttunLogout;
	@FXML private TilePane albumList;
	@FXML private ScrollPane scrollpane;
	@FXML private ComboBox<String> comboFilter;
	@FXML private TextField textboxSearch;
	
	//private Stage stage;
	private User user;
	private ArrayList<AlbumPaneController> albumPanes;
	//private final int MAX_NAME_LENGTH = 20;
	
	public void init(Stage s, User u) {
		//this.stage = s;
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
		
		for (Album a : u.getAlbums()) {
			FXMLLoader loader = loadAsset(Assets.ALBUM_PANE);
			AlbumPaneController albumPaneController = loader.getController();
			albumPaneController.init(a, this);
			albumList.getChildren().add(loader.getRoot());
			albumPanes.add(albumPaneController);
		}
	}
	
	public void requestAlbumOpen(Album a) {
		PhotoViewController pvc = (PhotoViewController) switchScene(Scenes.PHOTO_VIEW);
		pvc.init(user, a);
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
		String name = getUserInput("New Album", null, "Enter name: ", null, true);
		if (name == null || duplicateNameFound(name))
			return;
		FXMLLoader loader = loadAsset(Assets.ALBUM_PANE);
		AlbumPaneController albumPaneController = loader.getController();
		Album album = new Album(name);
		user.addAlbum(album);
		albumPaneController.init(album, this);
		albumList.getChildren().add(loader.getRoot());
		albumPanes.add(albumPaneController);
		scrollpane.setVvalue(1);
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
	
	public String editAlbumName(String oldName) {
		String newName = getUserInput("Edit Name", null, "Edit name: ", oldName, false);
		if (newName == null || duplicateNameFound(newName))
			return null;
		return newName;
	}
	
	public void deleteAlbum(AlbumPaneController apc, Node root) {
		if (!showPopup("Delete Album", null, "Are you sure you want to delete " + apc.getAlbum().getName() + "?", AlertType.CONFIRMATION))
			return;
		user.removeAlbum(apc.getAlbum());
		albumList.getChildren().remove(root);
		albumPanes.remove(apc);
	}
	
	public void setVval() {
		scrollpane.setVvalue(1);
	}
}
