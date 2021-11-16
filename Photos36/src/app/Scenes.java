package app;

/**
 * @author Michael Nelli - mrn73
 * @author Christopher Naporlee - cmn134
 */

/**
 * Enum that holds file system location of all needed scenes (views)
 */
public enum Scenes implements Loadable {
	/**
	 * The login view
	 */
	LOGIN("/view/LoginView.fxml"),
	
	/**
	 * The admin view
	 */
	ADMIN_VIEW("/view/AdminView.fxml"),
	
	/**
	 * The album view
	 */
	ALBUM_VIEW("/view/AlbumView.fxml"),
	
	/**
	 * The photo view
	 */
	PHOTO_VIEW("/view/PhotoView.fxml");
	
	/**
	 * The path to the view
	 */
	private final String filePath;
	
	/**
	 * Constructs new scene enum
	 * @param filePath Path to the scene file
	 */
	Scenes(final String filePath) {
		this.filePath = filePath;
	}
	
	public String getPath() {
		return filePath;
	}
}
