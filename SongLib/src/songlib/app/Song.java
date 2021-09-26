package songlib.app;

public class Song {
	private String name;
	private String artist;
	private String album;
	private String year;
	
	public Song(String name, String artist, String album, String year) {
		if (name.equals("") || artist.equals("")) {
			throw new IllegalArgumentException();
		}
		this.name = name;
		this.artist = artist;
		this.album = album;
		setYear(year);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	public void setAlbum(String album) {
		this.album = album;
	}
	
	public void setYear(String year) {
		if (year.equals("")) {
			year = "";
			return;
		}
		
		if (Integer.parseInt(year) < 0) {
			throw new IllegalArgumentException("Input year must be positive value.");
		}
		this.year = year;
	}
	
	public String getName() {
		return name;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public String getAlbum() {
		return album;
	}
	
	public String getYear() {
		return year;
	}
}
