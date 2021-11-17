package controller;

/**
 * @author Christoper Naporlee - cmn134
 * @author Michael Nelli - mrn73
 */

import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Album;
import model.Photo;

/**
 * Controller to manage the slide show view and its components.
 */
public class SlideShowViewController {
	
	/**
	 * Button to control going to the previous photo.
	 */
	@FXML private Button prevPhoto;
	
	/**
	 * Button to control going to the next photo
	 */
	@FXML private Button nextPhoto;
	
	/**
	 * Display panel for the image
	 */
	@FXML private ImageView imageDisplay;
	
	/**
	 * Text display panel for the album name
	 */
	@FXML private Label albumNameLabel;
	
	/**
	 * Label display panel for the name of the image
	 */
	@FXML private Label imageNameLabel;
	
	/**
	 * Label to display the date of the image
	 */
	@FXML private Label imageDateLabel;
	
	/**
	 * Keeps track of our spot within the album so we can easily shift
	 * forwards and backwards
	 */
	private int currPhotoIndex = 0;
	
	/**
	 * The album the slide show is currently iterating over
	 */
	private Album album;
	
	/**
	 * A mapping to link photos to their pre-loaded images from the file system
	 */
	private Map<Photo, Image> photoImageMap;
	
	/**
	 * Initialize the slide show view
	 * @param a The album the slide show will be displaying
	 */
	void init(Album a) {
		album = a;
		photoImageMap = new HashMap<>(album.getPhotos().size());
		for (Photo p : album.getPhotos()) {
			photoImageMap.put(p, new Image("file:" + p.getPath(), 750, 750, true, true, true));
		}
		albumNameLabel.setText(album.getName());
		updateDisplay();
	}
	
	/**
	 * Switch to the previous photo in the album from the current index
	 */
	@FXML
	private void switchPrevPhoto() {
		if (--currPhotoIndex < 0)
			currPhotoIndex = album.getPhotos().size() - 1;
		updateDisplay();
	}
	
	/**
	 * Switch to the next photo in the album from the current index
	 */
	@FXML
	private void switchNextPhoto() {
		if (++currPhotoIndex >= album.getPhotos().size())
			currPhotoIndex = 0;
		updateDisplay();
	}

	/**
	 * Update the display information of the slide show including the display
	 * image and relevant text displays
	 */
	private void updateDisplay() {
		Photo photoToDisplay = album.getPhotos().get(currPhotoIndex);
		imageDisplay.setImage(photoImageMap.get(photoToDisplay));
		imageNameLabel.setText(photoToDisplay.getCaption());
		imageDateLabel.setText(photoToDisplay.getDateAsString());
	}
}
