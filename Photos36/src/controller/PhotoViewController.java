package controller;

import java.io.File;
import app.Assets;
import app.Scenes;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Album;
import model.Photo;
import model.User;

public class PhotoViewController extends SceneController {
	
	public static class PhotoPaneController {
		@FXML
		private ImageView imageview;
		
		@FXML
		private Label labelCaption;
		
		private PhotoViewController parentController;
		private Photo photo;
		
		public void init(PhotoViewController pvc, Photo p) {
			parentController = pvc;
			photo = p;
			imageview.setImage(new Image("file:" + p.getPath(), true));
			labelCaption.setText(p.getCaption());
			
			imageview.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
				public void handle(MouseEvent click) {
					if (click.getClickCount() == 1) {
						parentController.setMainDisplay(photo);
					}
				}
			});
			
			imageview.hoverProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue) {
					imageview.setStyle("-fx-cursor: hand;"
							         + "-fx-border-color: black");
				}
			});
		}
	}
	
	@FXML private MenuItem buttonNewPhoto;
	@FXML private MenuItem buttonCloseAlbum;
	@FXML private MenuItem buttonQuit;
	@FXML private MenuItem buttonCopyPhoto;
	@FXML private MenuItem buttonMovePhoto;
	@FXML private MenuItem buttonStartSlideshow;
	@FXML private MenuItem buttonLogout;
	@FXML private ScrollPane scrollpane;
	@FXML private TilePane photoList;
	@FXML private ImageView mainDisplay;
	@FXML private Label labelImageCaption;
	@FXML private Label labelImageDate;
	@FXML private Button buttonDelete;
	@FXML private ListView<String> listviewTags;
	@FXML private Button buttonAddToList;
	@FXML private Button buttonDeleteFromList;
	@FXML private TextField textboxSearch;
	@FXML private DatePicker datepicker;
	@FXML private ComboBox<String> comboSearchFilter;
	
	private User user;
	private Album album;
	
	public void init(User u, Album a) {
		this.user = u;
		this.album = a;
		comboSearchFilter.setItems(FXCollections.observableArrayList("Sort by: None", "Sort by: Date", "Sort by: Tags"));
		s.setTitle("Viewing " + u.getUserName() + "'s photos from album '" + album.getName() + "'");
		for (Photo p : a.getPhotos()) {
			addPhotoToView(p);
		}
		changeButtonStates();
	}
	
	private void changeButtonStates() {
		boolean shouldButtonDisarm = album.getPhotoCount() <= 0;
		buttonStartSlideshow.setDisable(shouldButtonDisarm);
		buttonCopyPhoto.setDisable(shouldButtonDisarm);
		buttonMovePhoto.setDisable(shouldButtonDisarm);
	}
	
	@FXML
	private void addPhoto() {
		File file = invokeFileChooser();
		if (file == null)
			return;
		Photo photo = new Photo(file);
		album.addPhoto(photo);
		addPhotoToView(photo);
		changeButtonStates();
	}
	
	private void addPhotoToView(Photo p) {
		FXMLLoader loader = loadAsset(Assets.PHOTO_PANE);
		PhotoPaneController ppc = (PhotoPaneController) loader.getController();
		ppc.init(this, p);
		photoList.getChildren().add(loader.getRoot());
	}
	
	public void setMainDisplay(Photo p) {
		mainDisplay.setImage(new Image("file:" + p.getPath(), true));
		labelImageCaption.setText(p.getCaption());
	}
	
	@FXML
	private void closeAlbum() {
		AlbumViewController avc = (AlbumViewController) switchScene(Scenes.ALBUM_VIEW);
		avc.init(s, user);
	}
	
	private File invokeFileChooser() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose picture to add");
		fileChooser.getExtensionFilters().addAll(
			new ExtensionFilter(
					"Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"
			)
		);
		return fileChooser.showOpenDialog(s);
	}
	
	@FXML
	private void startSlideShow() {
		Stage stage = new Stage();
		FXMLLoader loader = loadAsset(Assets.SLIDESHOW);
		SlideShowViewController ssc = loader.getController();
		Scene slideScene = new Scene(loader.getRoot());
		ssc.init(album);
		stage.setScene(slideScene);
		stage.setTitle(album.getName() + "'s SlideShow");
		stage.setResizable(false);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.show();
	}
	
	@FXML 
	private void doQuit() {
		Platform.exit();
	}
	
	@FXML
	private void doLogout() {
		LoginViewController lvc = (LoginViewController) switchScene(Scenes.LOGIN);
		lvc.init();
	}
}
