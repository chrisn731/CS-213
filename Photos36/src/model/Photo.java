package model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Photo implements Serializable {
	private static final long serialVersionUID = 1L;

	/*
	 * Photo is the only class that should worry about the internals of Tag
	 */
	public class Tag implements Serializable {
		private static final long serialVersionUID = 1L;
		private String name;
		private String value;
		
		public Tag(String name) {
			this.name = name;
			this.value = "";
		}
		
		public Tag(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}
	
	private ArrayList<Tag> tags = new ArrayList<>();
	private Date date = null;
	private File filePath = null;
	private String caption = "";
	
	public Photo() {
		this.caption = "";
	}
	public ArrayList<Tag> getTags() {
		return tags;
	}
	
	public File getFile() {
		return filePath;
	}
	
	public boolean addTags() {
		return false;
	}
	
	public boolean removeTag() {
		return false;
	}
	
	public Date getDate() {
		return date;
	}
	
	/*
	 * TODO: Make sure this equals method is good for comparing photos
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Photo))
			return false;
		Photo p = (Photo) o;
		return p.filePath.equals(filePath);
	}
}
