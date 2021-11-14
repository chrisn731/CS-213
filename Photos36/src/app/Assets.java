package app;

public enum Assets implements Loadable {
	ALBUM_PANE("/view/albumBox.fxml"),
	PHOTO_PANE_PHOTO_VIEW("/view/photoPane.fxml"),
	PHOTO_PANE_ALBUM_VIEW("/view/photoPaneAlbumView.fxml"),
	SLIDESHOW("/view/SlideShowView.fxml"),
	TAGS("/view/TagView.fxml");
	
	private final String filePath;
	
	Assets(final String filePath) {
		this.filePath = filePath;
	}
	
	public String getPath() {
		return filePath;
	}
}
