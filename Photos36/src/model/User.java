package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private String username;
	private ArrayList<Album> albums;
	private ArrayList<String> tagNames;
	private ArrayList<Photo> uniquePhotos;
	
	public User(String name) {
		username = name;
		albums = new ArrayList<Album>();
		tagNames = new ArrayList<String>(Arrays.asList("location", "person"));
		uniquePhotos = new ArrayList<Photo>();
	}

	public String getUserName() {
		return username;
	}
	
	public boolean addAlbum(Album album) {
		if (!albums.contains(album)) {
			albums.add(album);
			return true;
		}
		return false;
	}
	
	public void removeAlbum(Album album) {
		albums.remove(album);
	}
	
	public ArrayList<Album> getAlbums() {
		return albums;
	}
	
	public Album getAlbum(String albumName) {
		for (Album a : getAlbums())
			if (a.getName().equals(albumName))
				return a;
		return null;
	}
	
	public void addTagName(String tag) {
		if (!tagNames.contains(tag))
			tagNames.add(tag);
	}
	
	public ArrayList<String> getTagNames() {
		return tagNames;
	}
	
	public void addPhoto(Photo p) {
		uniquePhotos.add(p);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof User)) {
			return false;
		}
		User u = (User) o;
		return u.username.equals(username);
	}
}
