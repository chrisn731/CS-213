/*
 * Authors:
 * 	Michael Nelli - mrn73
 * 	Christopher Naporlee - cmn134
 */
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
		this.name = name.trim();
	}
	
	public void setArtist(String artist) {
		this.artist = artist.trim();
	}
	
	public void setAlbum(String album) {
		this.album = album.trim();
	}
	
	public void setYear(String year) {
		if (!year.equals("") && Integer.parseInt(year) < 0) {
			throw new IllegalArgumentException("Input year must be positive value.");
		} else {
			this.year = year.trim();
		}
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
