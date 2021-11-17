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
import javafx.scene.CacheHint;
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

/**
 * Main controller of the Photo View.
 */
public class PhotoViewController extends SceneController {
	
	/**
	 * Controller of each photo pane in the Photo View.
	 */
	public static class PhotoPaneController {
		/**
		 * Stores the photo's image.
		 */
		@FXML private ImageView imageview;
		
		/**
		 * Label that holds the caption of the stored photo object.
		 */
		@FXML private Label labelCaption;
		
		/**
		 * The main controller that this object is a part of.
		 */
		private PhotoViewController parentController;
		
		/**
		 * The actual photo model that this object represents.
		 */
		private Photo photo;
		
		/**
		 * The root node of the asset.
		 */
		private Node root;
		
		/**
		 * The preview image that is displayed in the list.
		 */
		private Image thumbnailImage;
		
		/**
		 * The image that is displayed in the main display area
		 */
		private Image displayImage;
		
		/**
		 * Initializes a photo pane.
		 * @param pvc  the parent controller of this object
		 * @param p  the photo that this object represents
		 * @param root  the root of the photo pane displayed in the list
		 */
		private void init(PhotoViewController pvc, Photo p, Node root) {
			parentController = pvc;
			photo = p;
			this.root = root;
			imageview.setCache(true);
			imageview.setCacheHint(CacheHint.SPEED);
			thumbnailImage = new Image("file:" + p.getPath(), 175.0, 175.0, true, false, true);
			displayImage = new Image("file:" + p.getPath(), 500, 500, true, true, true);
			imageview.setImage(thumbnailImage);
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
		
		/**
		 * Gets the photo that this object represents.
		 * @return
		 */
		private Photo getPhoto() {
			return photo;
		}
		
		/**
		 * Calls the Photo View's controller to update its main display area to this object.
		 */
		private void updateParentDisplay() {
			parentController.setMainDisplay(this);
		}
		
		/**
		 * Gets the root of the photo pane.
		 * @return
		 */
		private Node getRoot() {
			return root;
		}
		
		/**
		 * Get the image of the main display
		 * @return
		 */
		private Image getImage() {
			return displayImage;
		}
	}
	
	/**
	 * Controller for the tag popup screen.
	 */
	public static class TagController { 
		/**
		 * Holds a list of all previously entered tags. Also editable to add new ones.
		 */
		@FXML private ComboBox<String> comboTagNames;
		
		/**
		 * Text field where the user can enter the value of a selected tag
		 */
		@FXML private TextField textboxTagValues;
		
		/**
		 * Button to apply the entered tag-value pair.
		 */
		@FXML private Button buttonApply;
		
		/**
		 * Button to close the window.
		 */
		@FXML private Button buttonDone;
		
		/**
		 * Label that states whether a tag-value was successfully added or not.
		 */
		@FXML private Label labelApplyFeedback;
		
		/**
		 * The stage of the scene.
		 */
		private Stage stage;
		
		/**
		 * The user we are currently signed into.
		 */
		private User user;
		
		/**
		 * The photo we are adding tags to.
		 */
		private Photo photo;
		
		/**
		 * Initializes the pop-up window.
		 * @param s  the stage of the scene.
		 * @param u  the user we are signed into
		 * @param p  the photo we are adding tags to
		 */
		private void init(Stage s, User u, Photo p) {
			stage = s;
			user = u;
			photo = p;
			comboTagNames.getItems().addAll(u.getTagNames());
		}
		
		/**
		 * Applies the tag-value pair to the photo.
		 */
		@FXML
		private void applyTags() {
			String tag = comboTagNames.getValue();
			String val = textboxTagValues.getText();
			if (!tag.isBlank() && !user.getTagNames().contains(tag)) {
				comboTagNames.getItems().add(comboTagNames.getValue());
				user.getTagNames().add(tag);
			}
			if (!tag.isBlank() && !val.isBlank()) {
				String text = photo.tagPairExists(tag, val) ? "Tag already applied!" : "Tag applied: " + tag + "=" + val;
				photo.addTagPair(tag, val);
				labelApplyFeedback.setText(text);
			}
		}
		
		/**
		 * Closes the pop-up window.
		 */
		@FXML
		private void doDone() {
			applyTags();
			stage.close();
		}
	}
	
	//@FXML private MenuItem buttonNewPhoto;
	//@FXML private MenuItem buttonCloseAlbum;
	//@FXML private MenuItem buttonQuit;
	
	/**
	 * Menu button that copies selected photo to another album.
	 */
	@FXML private MenuItem buttonCopyPhoto;
	
	/**
	 * Menu button that moves the selected photo into another album. 
	 */
	@FXML private MenuItem buttonMovePhoto;
	
	/**
	 * Menu button that starts the slide-show view.
	 */
	@FXML private MenuItem buttonStartSlideshow;
	
	//@FXML private MenuItem buttonLogout;
	
	/**
	 * Contains the list of photos so that the list grows.
	 */
	@FXML private ScrollPane scrollpane;
	
	/**
	 * Contains the photo panes.
	 */
	@FXML private TilePane photoList;
	
	/**
	 * Stores the image in the main display area.
	 */
	@FXML private ImageView mainDisplay;
	
	/**
	 * Label that displays the caption of the photo in the main display area.
	 */
	@FXML private Label labelImageCaption;
	
	/**
	 * Label that displays the date of the photo in the main display area.
	 */
	@FXML private Label labelImageDate;
	
	//@FXML private Button buttonDelete;
	//@FXML private Button buttonEditCaption;
	
	/**
	 * List of all tags that belong the photo shown in the main display area.
	 */
	@FXML private ListView<String> listviewTags;
	
	//@FXML private Button buttonAddTag;
	//@FXML private Button buttonDeleteTag;
	//@FXML private Button buttonAddPhoto;
	//@FXML private TextField textboxSearch;
	//@FXML private DatePicker datepicker;
	
	/**
	 * Search filter that hides or shows certain search fields depending on the selected index.
	 */
	@FXML private ComboBox<String> comboSearchFilter;
	
	/**
	 * The main display area.
	 */
	@FXML private AnchorPane paneMainDisplay;
	
	/**
	 * The user we are logged into.
	 */
	private User user;
	
	/**
	 * The album that we are inside of.
	 */
	private Album album;
	
	/**
	 * The main controller of the Photo View.
	 */
	private PhotoPaneController selectedController;
	
	/**
	 * Says whether or not the main display area should be visible or not.
	 */
	private boolean displayEnabled;
	
	/**
	 * Contains all the controllers for all visible photo panes in the tilepane.
	 */
	private ArrayList<PhotoPaneController> photoPanes = new ArrayList<>();
	
	/**
	 * Initializes the Photo View.
	 * @param u  the user we are logged into.
	 * @param a  the album we are inside of.
	 */
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
		scrollpane.setFitToWidth(true);
		changeButtonStates();
	}
	
	/**
	 * Enables or disables certain buttons depending on photo count.
	 */
	private void changeButtonStates() {
		boolean shouldButtonDisarm = album.getPhotoCount() <= 0;
		buttonStartSlideshow.setDisable(shouldButtonDisarm);
		buttonCopyPhoto.setDisable(shouldButtonDisarm);
		buttonMovePhoto.setDisable(shouldButtonDisarm);
	}
	
	/**
	 * Hides the main display.
	 */
	private void disableDisplay() {
		paneMainDisplay.setVisible(false);
		displayEnabled = false;
	}
	
	/**
	 * Shows the main display.
	 */
	private void enableDisplay() {
		if (!displayEnabled) {
			paneMainDisplay.setVisible(true);
			displayEnabled = true;
		}
	}
	
	/**
	 * Adds a photo to the album we are in.
	 */
	@FXML
	private void addPhoto() {
		File file = invokeFileChooser();
		if (file == null)
			return;
		
		Photo photo = null;
		for (Album a : user.getAlbums()) {
			if (!a.equals(album)) {
				photo = a.getPhotoByFile(file.toString());
				if (photo != null)
					break;
			}
		}
		
		if (photo == null) {
			System.out.println("made a new photo!");
			photo = new Photo(file);
		}
			
		if (album.getPhotoByFile(file.toString()) == null) {
			album.addPhoto(photo);
			addPhotoToView(photo);
		}
		
		changeButtonStates();
	}
	
	/**
	 * Creates a new Photo Pane Controller and adds the pane to the view.
	 * @param p  the photo to add
	 */
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
	
	/**
	 * Sets the main display area to display all attributes of a selected photo.
	 * @param ppc  the controller of the selected photo pane
	 */
	private void setMainDisplay(PhotoPaneController ppc) {
		selectedController = ppc;
		Photo p = ppc.getPhoto();
		
		enableDisplay();
		mainDisplay.setImage(ppc.getImage());
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
	
	/**
	 * Edits the caption of a selected photo.
	 */
	@FXML
	private void editCaption() {
		String newCaption = getUserInput("Edit Name", null, "Edit name: ", labelImageCaption.getText(), false);
		if (newCaption == null)
			return;
		selectedController.getPhoto().setCaption(newCaption);
		selectedController.labelCaption.setText(newCaption);
		labelImageCaption.setText(newCaption);
	}
	
	/**
	 * Opens the Tag View to add new tags to the selected photo.
	 */
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
	
	/**
	 * Removes a tag from the list of tags of a selected photo.
	 */
	@FXML
	private void removeTag() {
		ArrayList<String> tagVals = new ArrayList<>();
		int selectedIndex = listviewTags.getSelectionModel().getSelectedIndex();
		if (selectedIndex < 0)
			return;
		
		String selectedLine = listviewTags.getSelectionModel().getSelectedItem();
		String tagKey = selectedLine.substring(0, selectedLine.indexOf(':'));
		Iterator<String> i = selectedController.getPhoto().getTagValues(tagKey);
		while (i.hasNext()) {
			tagVals.add(i.next());
		}
		
		ChoiceDialog<String> cd = new ChoiceDialog<>(null, tagVals);
		cd.setHeaderText("Tag value removal from '" + tagKey + "'");
		cd.setContentText("Select which tag value to remove: ");
		Optional<String> res = cd.showAndWait();
		
		if (res.isEmpty())
			return;
		selectedController.getPhoto().removeTagPair(tagKey, res.get());
		setMainDisplay(selectedController);
		return;
	}
	
	/**
	 * Closes the album we are currently in by switching back to the Album View.
	 */
	@FXML
	private void closeAlbum() {
		AlbumViewController avc = (AlbumViewController) switchScene(Scenes.ALBUM_VIEW);
		avc.init(user);
	}
	
	/**
	 * Prompts the user to select a file with certain image extensions from their file browser.
	 * @return  the selected file
	 */
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
	
	/**
	 * Creates a new window that displays the slide-show of all images of the opened album.
	 */
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
	
	/**
	 * Prompts the user if they want to delete the selected photo;
	 */
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
	
	/**
	 * Deletes the selected photo and sets the main display to the next image in the list.
	 */
	private void processPhotoDeletion() {
		photoList.getChildren().remove(selectedController.getRoot());
		album.removePhoto(selectedController.getPhoto());
		
		int photoIndex = photoPanes.indexOf(selectedController);
		photoPanes.remove(selectedController);
		if (photoPanes.size() == 0) {
			disableDisplay();
			selectedController = null;
			changeButtonStates();
		} else {
			if (photoIndex >= photoPanes.size())
				photoIndex -= 1;
			setMainDisplay(photoPanes.get(photoIndex));
		}
	}

	/**
	 * Asks the user which album they want to move to; the user may choose from a list of albums where the photo
	 * will be moved to, where all options provided are those that do not already contain the selected photo.
	 * Displays an error if the photo exists in all other albums.
	 * @param copy  whether not the photo will be copied
	 * @return   name of the album we want to move to.
	 */
	private Optional<String> promptPhotoMovement(boolean copy) {
		ArrayList<String> userAlbumNames = new ArrayList<>();
		for (Album a : user.getAlbums()) {
			String path = selectedController.getPhoto().getPath();
			if (a.getName() != album.getName() && a.getPhotoByFile(path) == null)
				userAlbumNames.add(a.getName());
		}
		
		if (userAlbumNames.size() == 0) {
			showPopup(
				(copy ? "Copy" : "Move") + " Error",
				"", 
				"This photo already exists in every album!",
				AlertType.WARNING
			);
			return Optional.empty();
		}
		
		ChoiceDialog<String> cd = new ChoiceDialog<>(null, userAlbumNames);
		cd.setHeaderText(
			(copy ? "Copy" : "Move") + 
			" to which album?"
		);
		cd.setContentText(
			"Select an album to " +
			(copy ? "copy" : "move") +
			" to: "
		);
		return cd.showAndWait();
	}
	
	/**
	 * Moves the photo from the current album to a different one, deleting it from the current one.
	 */
	@FXML
	private void movePhoto() { 
		Optional<String> result = promptPhotoMovement(false);
		if (result.isPresent()) {
			user.getAlbum(result.get()).addPhoto(selectedController.getPhoto());
			processPhotoDeletion();
		}
	}
	
	/**
	 * Copies the photo into a different album. It is not deleted from the current album. 
	 */
	@FXML
	private void copyPhoto() {
		Optional<String> result = promptPhotoMovement(true);
		if (result.isPresent())
			user.getAlbum(result.get()).addPhoto(selectedController.getPhoto());
	}
	
	/**
	 * Closes the program.
	 */
	@FXML 
	private void doQuit() {
		Platform.exit();
	}
	
	/**
	 * Logs out of the user and switches to the Login View scene.
	 */
	@FXML
	private void doLogout() {
		LoginViewController lvc = (LoginViewController) switchScene(Scenes.LOGIN);
		lvc.init();
	}
}
