package controller;

/**
 * @author Michael Nelli - mrn73
 * @author Christopher Naporlee - cmn134
 */
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import app.Assets;
import app.Scenes;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import model.Album;
import model.Photo;
import model.User;

/**
 * Main controller of the Album View.
 */
public class AlbumViewController extends SceneController {

	/**
	 * Main controller of each Album in the Album View.
	 */
	public static class AlbumPaneController {
		/**
		 * Text that displays the album's name.
		 */
		@FXML private Text labelAlbumName;
		
		/**
		 * Text that displays the album's photo count.
		 */
		@FXML private Text labelPhotoCount;
		
		/**
		 * Text that displays the album's date range.
		 */
		@FXML private Text labelDates;
		
		/**
		 * Pane that holds the album's image and text fields.
		 */
		@FXML private Pane paneAlbum;
		
		/**
		 * Root pane that holds all of the album's viewable contents.
		 */
		@FXML private AnchorPane root;
		
		/**
		 * Imageview that holds the first image of the album.
		 */
		@FXML private ImageView imageview;
		
		/**
		 * Holds the imageview so that it stays centered in the album pane.
		 */
		@FXML private HBox imageViewContainer;
		
		/**
		 * The actual album model that this object represents.
		 */
		private Album album;
		
		/**
		 * The AlbumViewController that this object is a part of.
		 */
		private AlbumViewController parentController;
		
		/**
		 * The opacity of the imageview when the cursor is hovering over the album pane.
		 */
		private final double HOVER_OPACITY = .15;
		
		/**
		 * Initializes an album pane.
		 * @param a  album that this object is connected to
		 * @param avc  AlbumViewController that this object is controlled by
		 */
		public void init(Album a, AlbumViewController avc) {
			album = a;
			parentController = avc;
			labelAlbumName.setText(album.getName());
			
			playScaleAnimation();
			
			for (Node n : paneAlbum.getChildren()) {
				if (!(n instanceof HBox))
					n.setVisible(false);
			}
			
			paneAlbum.hoverProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue) {
					imageViewContainer.setOpacity(HOVER_OPACITY);
				} else {
					imageViewContainer.setOpacity(1);
				}
				for (Node n : paneAlbum.getChildren()) {
					if (!(n instanceof HBox))
						n.setVisible(newValue);
				}
				String suffix = album.getPhotoCount() == 1 ? " Photo" : " Photos";
				labelPhotoCount.setText(Integer.toString(album.getPhotoCount()) + suffix);
			});
			
			paneAlbum.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
				public void handle(MouseEvent click) {
					if (click.getClickCount() == 1) {
						parentController.requestAlbumOpen(album);
					}
				}
			});
			
			setDateRange();
			setCoverImage();
		}
		
		/**
		 * Sets the imageview to be the first image of the stored album object.
		 */
		private void setCoverImage() {
			//imageViewContainer.setStyle("-fx-border-color: black;");
			if (album.getPhotoCount() > 0) {
				Image tmp = new Image("file:" + album.getPhotos().get(0).getPath());
				double width = tmp.getWidth();
				double height = tmp.getHeight();
				Rectangle2D viewport;
				if (width > height) 
					viewport = new Rectangle2D((width - height) / 2, 0, height, height);
				else
					viewport = new Rectangle2D(0, (height-width) / 2, width, width);
				Image i = new Image("file:" + album.getPhotos().get(0).getPath(), true);
				imageview.setImage(i);
				imageview.setViewport(viewport);
			} else {
				Image i = new Image(
						getClass().getResource("/assets/empty_album_placeholder.png").toString(), 
						true
				);
				imageview.setImage(i);
				imageview.setScaleX(.75);
				imageview.setScaleY(.75);
				imageview.setOpacity(.15);
				imageViewContainer.setStyle("-fx-background-color: #c7c7c7;");
			}
		}
		
		/**
		 * Sets the date range of the album.
		 */
		private void setDateRange() {
			if (album.getMinDateAsString() == null || album.getMaxDateAsString() == null)
				labelDates.setText("");
			else 
				labelDates.setText(album.getMinDateAsString() + " - " + album.getMaxDateAsString());
		}
		
		/**
		 * Changes the album name in both the view and in the stored album object.
		 */
		@FXML
		private void editAlbumName() {
			String newName;
			if ((newName = parentController.editAlbumName(album.getName())) != null) {
				labelAlbumName.setText(newName);
				album.setName(newName);
			}
		}
		
		/**
		 * Removes this object from the Album View's list of albums.
		 */
		@FXML
		private void deleteAlbum() {
			parentController.deleteAlbum(this, root);
		}
		
		/**
		 * Plays a scaling animation of the album pane.
		 */
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
		
		/**
		 * Gets the stored album object.
		 * @return the stored album
		 */
		public Album getAlbum() {
			return album;
		}
	}
	
	/**
	 * Controller of a photo that is created from a search.
	 */
	public static class PhotoPaneController {
		/**
		 * Displays the photo that is stored in this object
		 */
		@FXML private ImageView imageview;
		
		/**
		 * Label that contains that caption of the photo stored in this object
		 */
		@FXML private Label labelCaption;
		
		/**
		 * The actual photo object that this object represents
		 */
		private Photo photo;
		
		/**
		 * Initializes a photo pane
		 * @param p Photo that will be displayed in the pane
		 */
		private void init(Photo p) {
			photo = p;
			imageview.setImage(new Image("file:" + p.getPath(), 300, 300, true, false, true));
			labelCaption.setText(p.getCaption());
		}
		
		/**
		 * Gets the photo that this object represents
		 * @return photo stored in this object
		 */
		private Photo getPhoto() {
			return photo;
		}
	}
	
	/**
	 * Menu button that creates a new album from search results.
	 */
	@FXML private MenuItem buttonNewAlbumFromSearch;
	
	/**
	 * Menu button that closes the program.
	 */
	@FXML private MenuItem buttonQuit;
	
	/**
	 * The list of all albums that are within a user. Albums are replaced by photos.
	 * during a search
	 */
	@FXML private TilePane viewList;
	
	/**
	 * Stores the list of albums contained in the tilepane so that the list can grow.
	 */
	@FXML private ScrollPane scrollpane;
	
	/**
	 * Lets the user select which search method they want to use.
	 */
	@FXML private ComboBox<String> comboFilter;
	
	/**
	 * Responsible for holding searches related to tags.
	 */
	@FXML private TextField textboxSearch;
	
	/**
	 * Contains the DatePickers.
	 */
	@FXML private AnchorPane paneDateRange;
	
	/**
	 * The start date of the date range we are searching for.
	 */
	@FXML private DatePicker datePickerStartDate;
	
	/**
	 * The end date of the date range we are searching for.
	 */
	@FXML private DatePicker datePickerEndDate;
	
	/**
	 * Label that appears when no results are found.
	 */
	@FXML private Label labelNoResults;
	
	/**
	 * Button that clears the DatePicker fields.
	 */
	@FXML private Button buttonClearDates;
	
	/**
	 * The user that we are logged into.
	 */
	private User user;
	
	/**
	 * The list of controllers that each correspond to a unique album stored in the user.
	 */
	private ArrayList<AlbumPaneController> albumPaneControllers;
	
	/**
	 * The list of controllers that each correspond to a unique photo stored in the user
	 * that matches a lookup within a search field.
	 */
	private ArrayList<PhotoPaneController> photoPaneControllers;
	
	/**
	 * Holds the viewable photo panes that will be displayed in the list.
	 */
	private ObservableList<Node> photoPanes;
	
	/**
	 * Holds the viewable album panes that will be displayed in the list.
	 */
	private ObservableList<Node> albumPanes;
	
	/**
	 * Switches the list to show either photos or albums.
	 */
	private boolean showPhotos = true;
	
	/**
	 * Initializes a new Album View.
	 * @param u User that this Album View will reference
	 */
	public void init(User u) {
		this.user = u;
		albumPaneControllers = new ArrayList<AlbumPaneController>();
		photoPaneControllers = new ArrayList<PhotoPaneController>();
		albumPanes = FXCollections.observableArrayList();
		photoPanes = FXCollections.observableArrayList();
		
		comboFilter.setItems(FXCollections.observableArrayList("Sort by: Date", "Sort by: Tags"));
		comboFilter.getSelectionModel().select(1);
		
		setListeners();
		setListStateToPhotos(false);
		
		for (Album a : u.getAlbums()) {
			FXMLLoader loader = loadAsset(Assets.ALBUM_PANE);
			AlbumPaneController albumPaneController = loader.getController();
			albumPaneController.init(a, this);
			albumPanes.add(loader.getRoot());
			albumPaneControllers.add(albumPaneController);
		}
		
		s.setTitle(u.getUserName() + "'s Albums");
		comboFilter.requestFocus();
		buttonNewAlbumFromSearch.setDisable(true);
	}
	
	/**
	 * Sets the listeners of the search fields.
	 */
	private void setListeners() {
		textboxSearch.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.trim().equals(""))
				setListStateToPhotos(false);
		});
		
		datePickerStartDate.valueProperty().addListener((observable, oldValue, newValue) -> {
			LocalDate endDate = datePickerEndDate.getValue();
			if (newValue == null || endDate == null)
				return;
			if (newValue.compareTo(endDate) > 0) {
				showPopup("Error", null, "Start date must be less than or equal to the end date.", AlertType.WARNING);
				datePickerStartDate.setValue(null);
			} else { 
				searchAlbumsByDate();
			}
		});
		
		datePickerEndDate.valueProperty().addListener((observable, oldValue, newValue) -> {
			LocalDate startDate = datePickerStartDate.getValue();
			if (newValue == null || startDate == null)
				return;
			if (newValue.compareTo(startDate) < 0) {
				showPopup("Error", null, "End date must be greater than or equal to the start date.", AlertType.WARNING);
				datePickerEndDate.setValue(null);
			} else {
				searchAlbumsByDate();
			}
		});
	}
	
	/**
	 * Changes which observable list the tilepane reads from.
	 * @param showPhotos  boolean to determine if we show photos or albums in the list
	 */
	private void setListStateToPhotos(boolean showPhotos) {
		labelNoResults.setVisible(false);
		
		if (this.showPhotos == showPhotos)
			return;
		
		if (showPhotos) {
			Bindings.unbindContentBidirectional(viewList.getChildren(), albumPanes);
			Bindings.bindContentBidirectional(viewList.getChildren(), photoPanes);
		} else {
			buttonNewAlbumFromSearch.setDisable(true);
			Bindings.unbindContentBidirectional(viewList.getChildren(), photoPanes);
			Bindings.bindContentBidirectional(viewList.getChildren(), albumPanes);
		}
		this.showPhotos = showPhotos;
	}
	
	/**
	 * Uses the selected field of the search filter to determine which input fields to display.
	 */
	@FXML
	private void updateSearchBar() {
		boolean isDate = comboFilter.getValue().equals("Sort by: Date");
		if (isDate) {
			datePickerStartDate.setValue(null);
			datePickerEndDate.setValue(null);
		} else {
			textboxSearch.clear();
		}
		paneDateRange.setVisible(isDate);
		textboxSearch.setVisible(!isDate);
		setListStateToPhotos(false);
	}
	
	/**
	 * Creates an album from the search results.
	 */
	@FXML
	private void createAlbumFromSearch() {
		ArrayList<Photo> photos = new ArrayList<>();
		for (PhotoPaneController ppc : photoPaneControllers) {
			photos.add(ppc.getPhoto());
		}
		createAlbumInteral(
			getUserInput("New Album", null, "Enter name: ", null, true),
			photos.iterator()
		);
	}
	
	/**
	 * Switches the scene to the Login View
	 */
	@FXML
	private void logout() {
		LoginViewController lvc = (LoginViewController) switchScene(Scenes.LOGIN);
		lvc.init(s);
	}
	
	/**
	 * Closes the program.
	 */
	@FXML
	private void doQuit() {
		Platform.exit();
	}

	/**
	 * Creates a basic, empty album.
	 */
	@FXML
	private void createAlbum() {
		createAlbumInteral(
			getUserInput("New Album", null, "Enter name: ", null, true),
			Collections.emptyIterator()
		);
	}
	
	/**
	 * Handles the internal process of creating a new album.
	 * Displays an error message if no name is given in the pop-up dialog or a duplicate name is found.
	 * @param name Name of the Album
	 * @param photosToAdd Photos to add into the album
	 * @return the created AlbumPaneController
	 */
	private AlbumPaneController createAlbumInteral(String name, Iterator<Photo> photosToAdd) {
		if (name == null || duplicateNameFound(name))
			return null;
		FXMLLoader loader = loadAsset(Assets.ALBUM_PANE);
		AlbumPaneController albumPaneController = loader.getController();
		Album album = new Album(name);
		user.addAlbum(album);
		while (photosToAdd.hasNext())
			album.addPhoto(photosToAdd.next());
		albumPaneController.init(album, this);
		albumPanes.add(loader.getRoot());
		albumPaneControllers.add(albumPaneController);
		setListStateToPhotos(false);
		scrollpane.setVvalue(1);
		return albumPaneController;
	}
	
	/**
	 * Parses a tag search
	 * @return a Map that contains the tag-value pairs (NOTE: the value of the tag is the key, so it is stored
	 * as value-tag)
	 */
	private HashMap<String, String> parseTagSearch() {
		String input = textboxSearch.getText();
		HashMap<String, String> valueTagPairs = new HashMap<String, String>();
		String splitter = null;
		
		if (!input.contains("="))
			return null;
		
		if (input.toLowerCase().contains(" and ")) {
			int index = input.toLowerCase().indexOf(" and ");
			splitter = input.substring(index, index + " and ".length());
		} else if (input.toLowerCase().contains(" or ")) {
			int index = input.toLowerCase().indexOf(" or ");
			splitter = input.substring(index, index + " or ".length());
		}
		
		if (splitter != null) {
			// First tag-value pair
			String firstTagValuePair = input.substring(0, input.indexOf(splitter));
			if (!firstTagValuePair.contains("="))
				return null;
			int equalsIndex = firstTagValuePair.indexOf("=");
			valueTagPairs.put(firstTagValuePair.substring(equalsIndex + 1), firstTagValuePair.substring(0, equalsIndex));
			
			//Second tag-value pair
			String secondTagValuePair = input.substring(input.indexOf(splitter)).replace(splitter, "");
			if (!secondTagValuePair.contains("="))
				return null;
			equalsIndex = secondTagValuePair.indexOf("=");
			valueTagPairs.put(secondTagValuePair.substring(equalsIndex).replace("=", ""), secondTagValuePair.substring(0, equalsIndex));
		} else {
			valueTagPairs.put(input.substring(input.indexOf('=')).replace("=", ""), input.substring(0, input.indexOf('=')));
		}
		return valueTagPairs;
	}
	
	/**
	 * Searches through all albums within the user by a given tag-value pair and displays all matches.
	 * Displays an error if the input could not be parsed.
	 */
	@FXML
	private void searchAlbumsByTag() {
		HashMap<String, String> valueTagPairs = parseTagSearch();
		if (valueTagPairs == null) {
			showPopup("Error", null, "Invalid search input.", AlertType.WARNING);
			return;
		}
		
		photoPanes.clear();
		photoPaneControllers.clear();
		setListStateToPhotos(true);
		
		String firstTag = null;
		String firstVal = null;
		String secondTag = null;
		String secondVal = null;
		boolean containsAND = textboxSearch.getText().toLowerCase().contains(" and ");
		boolean containsOR = textboxSearch.getText().toLowerCase().contains(" or ");
		Iterator<String> i = valueTagPairs.keySet().iterator();
		if (valueTagPairs.size() < 2) {
			firstVal = i.next();
			firstTag = valueTagPairs.get(firstVal);
		} else {
			firstVal = i.next();
			firstTag = valueTagPairs.get(firstVal);
			secondVal = i.next();
			secondTag = valueTagPairs.get(secondVal);
		}
		
		ArrayList<Photo> visited = new ArrayList<>();
		for (Album a : user.getAlbums()) {
			for (Photo p : a.getPhotos()) {
				if (containsAND && p.tagPairExists(firstTag, firstVal) && p.tagPairExists(secondTag, secondVal) && !visited.contains(p)) {
					loadPhoto(p, visited);
				} else if (containsOR && ((p.tagPairExists(firstTag, firstVal) || p.tagPairExists(secondTag, secondVal)) && !visited.contains(p))) {
					loadPhoto(p, visited);
				} else if (!containsOR && !containsAND && p.tagPairExists(firstTag, firstVal) && !visited.contains(p)) {
					loadPhoto(p, visited);
				}
			}
		}
		if (visited.isEmpty()) {
			labelNoResults.setVisible(true);
		}
		buttonNewAlbumFromSearch.setDisable(photoPanes.isEmpty());
	}
	
	/**
	 * Searches through all albums within the user by date range. 
	 */
	public void searchAlbumsByDate() {
		photoPanes.clear();
		photoPaneControllers.clear();
		setListStateToPhotos(true);
		
		ArrayList<Photo> visited = new ArrayList<>();
		for (AlbumPaneController apc : albumPaneControllers) {
			for (Photo p : apc.getAlbum().getPhotos()) {
				LocalDate photoDate = p.getLocalDate();
				if (photoDate.compareTo(datePickerStartDate.getValue()) >= 0 && 
						photoDate.compareTo(datePickerEndDate.getValue()) <= 0 &&
						!visited.contains(p)) {
					FXMLLoader loader = loadAsset(Assets.PHOTO_PANE_ALBUM_VIEW);
					PhotoPaneController ppc = loader.getController();
					ppc.init(p);
					photoPanes.add(loader.getRoot());
					photoPaneControllers.add(ppc);
					visited.add(p);
				}
			}
		}
		if (visited.isEmpty()) {
			labelNoResults.setVisible(true);
		}
		buttonNewAlbumFromSearch.setDisable(photoPanes.isEmpty());
	}
	
	/**
	 * Clears the date fields
	 */
	@FXML
	private void clearDates() {
		datePickerStartDate.setValue(null);
		datePickerEndDate.setValue(null);
		setListStateToPhotos(false);
	}
	
	/**
	 * Loads the photo into view and creates a corresponding controller object. Adds the photo the list
	 * of visited photos to prevent duplicates in the view.
	 * @param p  the photo to load
	 * @param visited  the list of photos that have been previously loaded
	 */
	private void loadPhoto(Photo p, ArrayList<Photo> visited) {
		FXMLLoader loader = loadAsset(Assets.PHOTO_PANE_ALBUM_VIEW);
		PhotoPaneController ppc = loader.getController();
		ppc.init(p);
		photoPanes.add(loader.getRoot());
		photoPaneControllers.add(ppc);
		visited.add(p);
	}
	
	/**
	 * Checks whether a name is already used by an album within the user
	 * @param name  we want to check for
	 * @return  whether or not the name is in use
	 */
	private boolean duplicateNameFound(String name) {
		for (AlbumPaneController apc : albumPaneControllers) {
			if (apc.getAlbum().getName().equals(name)) {
				showPopup("Error", null, "This album name already exists", AlertType.WARNING);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Switches to the photo view a selected album.
	 * @param a  the selected album
	 */
	public void requestAlbumOpen(Album a) {
		PhotoViewController pvc = (PhotoViewController) switchScene(Scenes.PHOTO_VIEW);
		pvc.init(user, a);
	}
	
	/**
	 * Edits the name of a given album
	 * @param oldName  the previous name of the album prior to changing
	 * @return  the new name of the album, if the name is permitted; otherwise null
	 */
	public String editAlbumName(String oldName) {
		String newName = getUserInput("Edit Name", null, "Edit name: ", oldName, false);
		if (newName == null || duplicateNameFound(newName))
			return null;
		return newName;
	}
	
	/**
	 * Deletes the selected album.
	 * @param apc  controller attached to the album
	 * @param root  root node of the pane that is stored in the list
	 */
	public void deleteAlbum(AlbumPaneController apc, Node root) {
		boolean approval = showPopup(
				"Delete Album", null, 
				"Are you sure you want to delete " + apc.getAlbum().getName() + "?", 
				AlertType.CONFIRMATION
		);
		if (!approval)
			return;
		user.removeAlbum(apc.getAlbum());
		albumPanes.remove(root);
		albumPaneControllers.remove(apc);
	}
}
