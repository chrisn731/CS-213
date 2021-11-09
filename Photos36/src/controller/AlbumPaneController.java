package controller;

import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import model.Album;

public class AlbumPaneController {
	@FXML
	private Text labelAlbumName;
	
	@FXML 
	private Text labelPhotoCount;
	
	@FXML
	private Text labelDates;
	
	@FXML
	private Button buttonEdit;
	
	@FXML
	private Button buttonDelete;
	
	@FXML
	private Pane paneAlbum;
	
	@FXML
	private AnchorPane root;
	
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
		
		labelPhotoCount.setText("0 Photos");
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
	
	
	/* Easter egg purposes */
	private void playSpinAnimation() {
		RotateTransition rt = new RotateTransition();
		rt.setDuration(Duration.millis(500));
		rt.setNode(paneAlbum);
		rt.setByAngle(360);
		rt.setCycleCount(1);
		rt.setAutoReverse(true);
		rt.play();
	}
	
	/* This kinda nice ngl */
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
