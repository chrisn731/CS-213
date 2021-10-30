package model;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private String username;
	private ArrayList<Album> albums;
	
	public User(String name) {
		username = name;
	}

	public String getUserName() {
		return username;
	}
	
	public ArrayList<Album> getAlbums() {
		return albums;
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
