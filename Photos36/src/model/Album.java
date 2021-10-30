package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<Photo> photos;
	private String name;
	
	public ArrayList<Photo> getPhotos() {
		return photos;
	}
	
	public boolean addPhoto(Photo p) {
		return false;
	}
	
	public void removePhoto(Photo p) {
		
	}
	
	public String getName() {
		return name;
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
