package model;

/**
 * @author Michael Nelli - mrn73
 * @author Christopher Naporlee - cmn134
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Internal data structure to back the user type
 */
public class User implements Serializable {
	/**
	 * Needed for serializable
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The name of the user
	 */
	private String username;
	
	/**
	 * The list of the user's albums
	 */
	private ArrayList<Album> albums;
	
	/**
	 * A list of tag names/keys that the user has used in the past so the
	 * user can make quick references to use it again.
	 */
	private ArrayList<String> tagNames;
	
	/**
	 * Creates a new user under a username
	 * @param name The username of the user
	 */
	public User(String name) {
		username = name;
		albums = new ArrayList<Album>();
		tagNames = new ArrayList<String>(Arrays.asList("location", "person"));
	}

	/**
	 * Gets the name of the user
	 * @return The username
	 */
	public String getUserName() {
		return username;
	}
	
	/**
	 * Adds an album to the user's list of albums
	 * @param album The album to add
	 * @return True if the operation succeeded, false if the user already has an album under that name
	 */
	public boolean addAlbum(Album album) {
		return !albums.contains(album) ? albums.add(album) : false;
	}
	
	/**
	 * Removes an album from the user's album list
	 * @param album The album to remove
	 */
	public void removeAlbum(Album album) {
		albums.remove(album);
	}
	
	/**
	 * Gets the list of all the user's albums
	 * @return User's album list
	 */
	public ArrayList<Album> getAlbums() {
		return albums;
	}
	
	/**
	 * Gets an album from the user's list by the album's name
	 * @param albumName The album's name to search for
	 * @return The album, null if not found
	 */
	public Album getAlbum(String albumName) {
		for (Album a : getAlbums())
			if (a.getName().equals(albumName))
				return a;
		return null;
	}
	
	/**
	 * Adds a tag key to the user's list of tags
	 * @param tag The tag to add
	 */
	public void addTagName(String tag) {
		if (!tagNames.contains(tag))
			tagNames.add(tag);
	}
	
	/**
	 * Gets the list of all the user's used tag names
	 * @return The list of tag names
	 */
	public ArrayList<String> getTagNames() {
		return tagNames;
	}
	
	/**
	 * Checks to see if two users are equivalent
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof User)) {
			return false;
		}
		User u = (User) o;
		return u.username.equals(username);
	}
}
