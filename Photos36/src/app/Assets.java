package app;

/**
 * @author Michael Nelli - mrn73
 * @author Christopher Naporlee - cmn134
 */

/**
 * Enum that holds file system location of all needed assets that are utilized
 * by certain scenes.
 */
public enum Assets implements Loadable {
	/**
	 * Album pane that is utilized within the album view scene
	 */
	ALBUM_PANE("/view/albumBox.fxml"),
	
	/**
	 * Photo Pane asset is utilized within the photo view scene
	 */
	PHOTO_PANE_PHOTO_VIEW("/view/photoPane.fxml"),
	
	/**
	 * Photo Pane asset that is utilized within the album view scene
	 */
	PHOTO_PANE_ALBUM_VIEW("/view/photoPaneAlbumView.fxml"),
	
	/**
	 * Slide show asset that is utilized within the photo view scene
	 */
	SLIDESHOW("/view/SlideShowView.fxml"),
	
	/**
	 * Tag asset that is utilized within the photo view scene
	 */
	TAGS("/view/TagView.fxml");
	
	/**
	 * The file path to the asset
	 */
	private final String filePath;
	
	/**
	 * Makes a new asset based on file path to ass
	 * @param filePath The file path to the asset
	 */
	Assets(final String filePath) {
		this.filePath = filePath;
	}
	
	public String getPath() {
		return filePath;
	}
}
