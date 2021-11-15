package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Album implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<Photo> photos;
	private String name;
	private int numPhotos;
	Map<String, Photo> filePhotoMap = new HashMap<>();
	
	public Album(String name) {
		this.name = name;
		photos = new ArrayList<Photo>();
	}
	
	public ArrayList<Photo> getPhotos() {
		return photos;
	}
	
	public Photo getPhotoByFile(String filePath) {
		return filePhotoMap.get(filePath);
	}
	
	public boolean addPhoto(Photo p, User u) {
		if (photos.contains(p))
			return false;
		photos.add(p);
		numPhotos++;
		p.incrementAlbumRefs();
		filePhotoMap.put(p.getPath(), p);
		return true;
	}
	
	public void removePhoto(Photo p) {
		photos.remove(p);
		numPhotos--;
		p.decrementAlbumRefs();
		filePhotoMap.remove(p.getPath());
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
