package controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import app.Assets;
import app.Scenes;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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
		@FXML private ImageView imageview;
		@FXML private HBox imageViewContainer;
		
		private Album album;
		private AlbumViewController parentController;
		private final double HOVER_OPACITY = .1;
		
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
					paneAlbum.setStyle("-fx-cursor: hand;"
							         + "-fx-border-color: black;");
					imageViewContainer.setOpacity(HOVER_OPACITY);
				} else {
					imageViewContainer.setStyle("-fx-background-color: black;"
							                  + "-fx-border-color: black;");
					imageViewContainer.setOpacity(1);
				}
				for (Node n : paneAlbum.getChildren()) {
					if (!(n instanceof HBox))
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
			
			if (album.getPhotoCount() > 0) {
				imageview.setImage(new Image("file:" + album.getPhotos().get(0).getPath()));
			} else {
				paneAlbum.setStyle("-fx-background-color: rgba(125,125,125,1)");
			}
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
	}
	
	public static class PhotoPaneController {
		@FXML
		private ImageView imageview;
		
		@FXML
		private Label labelCaption;
		
		private Photo photo;
		
		private void init(Photo p) {
			photo = p;
			imageview.setImage(new Image("file:" + p.getPath(), true));
			labelCaption.setText(p.getCaption());
		}
	}
	
	@FXML private MenuItem buttonNewAlbum;
	@FXML private MenuItem buttonNewAlbumFromSearch;
	@FXML private MenuItem buttonQuit;
	@FXML private MenuItem buttonDelete;
	@FXML private MenuItem buttonLogout;
	@FXML private TilePane albumList;
	@FXML private ScrollPane scrollpane;
	@FXML private ComboBox<String> comboFilter;
	@FXML private TextField textboxSearch;
	@FXML private AnchorPane paneDateRange;
	@FXML private DatePicker datePickerStartDate;
	@FXML private DatePicker datePickerEndDate;
	
	private User user;
	private ArrayList<AlbumPaneController> albumPaneControllers;
	private ObservableList<Node> photoPanes;
	private ObservableList<Node> albumPanes;
	private boolean showPhotos = true;
	
	public void init(User u) {
		this.user = u;
		albumPaneControllers = new ArrayList<AlbumPaneController>();
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
		
		s.setTitle("Viewing " + u.getUserName() + "'s Albums");
		comboFilter.requestFocus();
		buttonNewAlbumFromSearch.setDisable(true);
	}
	
	private void setListeners() {
		textboxSearch.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.trim().equals("") || !newValue.contains("=") || (newValue.charAt(newValue.length() - 1) == '=')) {
		    	buttonNewAlbumFromSearch.setDisable(true);
		    	setListStateToPhotos(false);
			} else {
		    	buttonNewAlbumFromSearch.setDisable(false);
		    	if (newValue.contains("=")) {
		    		searchAlbumsByTag();
		    	}
		    }
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
	
	private void setListStateToPhotos(boolean showPhotos) {
		if (this.showPhotos == showPhotos)
			return;
		
		if (showPhotos) {
			Bindings.unbindContentBidirectional(albumList.getChildren(), albumPanes);
			Bindings.bindContentBidirectional(albumList.getChildren(), photoPanes);
		} else {
			Bindings.unbindContentBidirectional(albumList.getChildren(), photoPanes);
			Bindings.bindContentBidirectional(albumList.getChildren(), albumPanes);
		}
		this.showPhotos = showPhotos;
	}
	
	@FXML
	private void updateSearchBar(ActionEvent e) {
		boolean isDate = comboFilter.getValue().equals("Sort by: Date");
		paneDateRange.setVisible(isDate);
		textboxSearch.setVisible(!isDate);
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
		LoginViewController lvc = (LoginViewController) switchScene(Scenes.LOGIN);
		lvc.init(s);
	}
	
	@FXML
	private void doQuit(ActionEvent e) {
		Platform.exit();
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
		setListStateToPhotos(false);
		albumPanes.add(loader.getRoot());
		albumPaneControllers.add(albumPaneController);
		scrollpane.setVvalue(1);
	}
	
	private void searchAlbumsByTag() {
		String key = textboxSearch.getText().substring(0, textboxSearch.getText().indexOf('='));
		String val = textboxSearch.getText().substring(textboxSearch.getText().indexOf('=')).replace("=", "");
		photoPanes.clear();
		setListStateToPhotos(true);
		for (AlbumPaneController apc : albumPaneControllers) {
			for (Photo p : apc.getAlbum().getPhotos()) {
				if (p.tagPairExists(key, val)) {
					FXMLLoader loader = loadAsset(Assets.PHOTO_PANE_ALBUM_VIEW);
					PhotoPaneController ppc = loader.getController();
					ppc.init(p);
					photoPanes.add(loader.getRoot());
				}
			}
		}
	}
	
	public void searchAlbumsByDate() {
		photoPanes.clear();
		setListStateToPhotos(true);
		for (AlbumPaneController apc : albumPaneControllers) {
			for (Photo p : apc.getAlbum().getPhotos()) {
				LocalDate photoDate = p.getLocalDate();
				if (photoDate.compareTo(datePickerStartDate.getValue()) >= 0 && photoDate.compareTo(datePickerEndDate.getValue()) <= 0) {
					FXMLLoader loader = loadAsset(Assets.PHOTO_PANE_ALBUM_VIEW);
					PhotoPaneController ppc = loader.getController();
					ppc.init(p);
					photoPanes.add(loader.getRoot());
				}
			}
		}
	}
	
	private boolean duplicateNameFound(String name) {
		for (AlbumPaneController apc : albumPaneControllers) {
			if (apc.getAlbum().getName().equals(name)) {
				showPopup("Error", null, "This album name already exists", AlertType.WARNING);
				return true;
			}
		}
		return false;
	}
	
	public void requestAlbumOpen(Album a) {
		PhotoViewController pvc = (PhotoViewController) switchScene(Scenes.PHOTO_VIEW);
		pvc.init(user, a);
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
		albumPanes.remove(root);
		albumPaneControllers.remove(apc);
	}
	
	public void setVval() {
		scrollpane.setVvalue(1);
	}
}
