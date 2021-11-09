package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<Photo> photos;
	private String name;
	private int numPhotos;
	
	public Album(String name) {
		this.name = name;
		photos = new ArrayList<Photo>();
	}
	
	public ArrayList<Photo> getPhotos() {
		return photos;
	}
	
	public boolean addPhoto(Photo p) {
		if (photos.contains(p))
			return false;
		photos.add(p);
		numPhotos++;
		return true;
	}
	
	public void removePhoto(Photo p) {
		photos.remove(p);
		numPhotos--;
	}
	
	public int getPhotoCount() {
		return numPhotos;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Album)) {
			return false;
		}
		Album a = (Album) o;
		return a.name.equals(name);
	}
}
