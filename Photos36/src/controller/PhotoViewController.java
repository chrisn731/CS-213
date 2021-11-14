package controller;

/**
 * @author Michael Nelli
 * @author Christopher Naporlee
 */
import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import app.Assets;
import app.Scenes;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
		private Node root;
		
		private void init(PhotoViewController pvc, Photo p, Node root) {
			parentController = pvc;
			photo = p;
			this.root = root;
			imageview.setImage(new Image("file:" + p.getPath(), true));
			labelCaption.setText(p.getCaption());
			
			imageview.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
				public void handle(MouseEvent click) {
					updateParentDisplay();
				}
			});
			
			imageview.hoverProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue) {
					imageview.setStyle("-fx-cursor: hand;"
							         + "-fx-border-color: black");
				}
			});
		}
		
		private Photo getPhoto() {
			return photo;
		}
		
		private void updateParentDisplay() {
			parentController.setMainDisplay(this);
		}
		
		private Node getRoot() {
			return root;
		}
	}
	
	public static class TagController { 
		@FXML
		private ComboBox<String> comboTagNames;
		
		@FXML
		private TextField textboxTagValues;
		
		@FXML
		private Button buttonApply;
		
		@FXML
		private Button buttonDone;
		
		private Stage stage;
		private User user;
		private Photo photo;
		
		private void init(Stage s, User u, Photo p) {
			stage = s;
			user = u;
			photo = p;
			comboTagNames.getItems().addAll(u.getTagNames());
		}
		
		@FXML
		private void applyTags() {
			String tag = comboTagNames.getValue();
			if (!tag.isBlank() && !user.getTagNames().contains(tag)) {
				comboTagNames.getItems().add(comboTagNames.getValue());
				user.getTagNames().add(tag);
			}
			photo.addTagPair(tag, textboxTagValues.getText());
		}
		
		@FXML
		private void doDone() {
			stage.close();
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
	@FXML private Button buttonAddTag;
	@FXML private Button buttonDeleteTag;
	@FXML private Button buttonAddPhoto;
	@FXML private TextField textboxSearch;
	@FXML private DatePicker datepicker;
	@FXML private ComboBox<String> comboSearchFilter;
	@FXML private AnchorPane paneMainDisplay;
	
	private User user;
	private Album album;
	private PhotoPaneController selectedController;
	private boolean displayEnabled;
	private ArrayList<PhotoPaneController> photoPanes = new ArrayList<>();
	
	public void init(User u, Album a) {
		this.user = u;
		this.album = a;
		disableDisplay();
		comboSearchFilter.setItems(
			FXCollections.observableArrayList(
					"Sort by: None",
					"Sort by: Date", 
					"Sort by: Tags"
			)
		);
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
	
	private void disableDisplay() {
		paneMainDisplay.setVisible(false);
		displayEnabled = false;
	}
	
	private void enableDisplay() {
		if (!displayEnabled) {
			paneMainDisplay.setVisible(true);
			displayEnabled = true;
		}
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
		FXMLLoader loader = loadAsset(Assets.PHOTO_PANE_PHOTO_VIEW);
		PhotoPaneController ppc = (PhotoPaneController) loader.getController();
		Node root = loader.getRoot();
		ppc.init(this, p, root);
		photoList.getChildren().add(root);
		photoPanes.add(ppc);
		if (selectedController == null)
			setMainDisplay(ppc);
	}
	
	private void setMainDisplay(Photo p) {
		enableDisplay();
		mainDisplay.setImage(new Image("file:" + p.getPath(), true));
		labelImageCaption.setText(p.getCaption());
		labelImageDate.setText(p.getDateAsString());
		
		ObservableList<String> tags = listviewTags.getItems();
		if (tags == null) {
				tags = FXCollections.observableArrayList();
				listviewTags.setItems(tags);
		} else {
			tags.clear();
		}
		
		for (Iterator<String> i = p.getTagKeys(); i.hasNext();) {
			String key = i.next();
			String listItem = key + ": ";
			for (Iterator<String> j = p.getTagValues(key); j.hasNext();) {
				listItem += j.next() + (j.hasNext() ? ", " : "");
			}
			tags.add(listItem);
		}
	}
	
	private void setMainDisplay(PhotoPaneController p) {
		setMainDisplay(p.getPhoto());
		selectedController = p;
	}
	
	@FXML 
	private void addTags() {
		Stage stage = new Stage();
		FXMLLoader loader = loadAsset(Assets.TAGS);
		TagController tc = loader.getController();
		tc.init(stage, user, selectedController.getPhoto());
		stage.setScene(new Scene(loader.getRoot()));
		stage.setTitle("");
		stage.setResizable(false);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
		setMainDisplay(selectedController);
	}
	
	@FXML
	private void closeAlbum() {
		AlbumViewController avc = (AlbumViewController) switchScene(Scenes.ALBUM_VIEW);
		avc.init(user);
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
		ssc.init(album);
		stage.setScene(new Scene(loader.getRoot()));
		stage.setTitle(album.getName() + "'s SlideShow");
		stage.setResizable(false);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.show();
	}
	
	@FXML
	private void deletePhoto() {
		boolean approval = showPopup(
			"Delete Album", null, 
			"Are you sure you want to delete this photo?", 
			AlertType.CONFIRMATION
		);
		if (!approval)
			return;
		processPhotoDeletion();
	}
	
	private void processPhotoDeletion() {
		photoList.getChildren().remove(selectedController.getRoot());
		album.removePhoto(selectedController.getPhoto());
		
		int photoIndex = photoPanes.indexOf(selectedController);
		photoPanes.remove(selectedController);
		if (photoPanes.size() == 0) {
			disableDisplay();
		} else {
			if (photoIndex >= photoPanes.size())
				photoIndex -= 1;
			setMainDisplay(photoPanes.get(photoIndex));
		}
	}

	private Optional<String> promptPhotoMovement(boolean copy) {
		ArrayList<String> userAlbumNames = new ArrayList<>();
		for (Album a : user.getAlbums())
			if (a.getName() != album.getName())
				userAlbumNames.add(a.getName());
		ChoiceDialog<String> cd = new ChoiceDialog<>(null, userAlbumNames);
		cd.setHeaderText(
			(copy ? "Copy" : "Move") + 
			" to which album?"
		);
		cd.setContentText(
			"Select album to " +
			(copy ? "copy" : "move") +
			"to: "
		);
		return cd.showAndWait();
	}
	
	@FXML
	private void movePhoto() { 
		Optional<String> result = promptPhotoMovement(false);
		if (result.isPresent()) {
			user.getAlbum(result.get()).addPhoto(selectedController.getPhoto());
			processPhotoDeletion();
		}
	}
	
	@FXML
	private void copyPhoto() {
		Optional<String> result = promptPhotoMovement(true);
		if (result.isPresent())
			user.getAlbum(result.get()).addPhoto(selectedController.getPhoto());
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
