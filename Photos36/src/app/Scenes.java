package app;

public enum Scenes implements Loadable {
	LOGIN("/view/LoginView.fxml"),
	ADMIN_VIEW("/view/AdminView.fxml"),
	ALBUM_VIEW("/view/AlbumView.fxml"),
	PHOTO_VIEW("/view/PhotoView.fxml");
	
	private final String filePath;
	
	Scenes(final String filePath) {
		this.filePath = filePath;
	}
	
	public String getPath() {
		return filePath;
	}
}
