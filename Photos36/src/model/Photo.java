package model;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sun.jdi.LongValue;

public class Photo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Map<String, ArrayList<String>> tags = new HashMap<>();
	private LocalDate date = null;
	private String filePath = null;
	private String caption = "";
	private int numAlbumRefs = 0;
	
	public Photo(File f) {
		this.caption = f.getName().substring(0, f.getName().lastIndexOf('.'));
		filePath = f.toString();
		date = Instant.ofEpochMilli(f.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public String getCaption() {
		return caption;
	}
	
	public void setCaption(String caption) {
		if (caption == null || caption.isBlank())
			return;
		this.caption = caption;
	}
	
	public Iterator<String> getTagKeys() {
		return tags.keySet().iterator();
	}
	
	public Iterator<String> getTagValues(String tagKey) {
		ArrayList<String> tagVals = tags.get(tagKey);
		return tagVals != null ? tagVals.iterator() : Collections.emptyIterator();
	}
	
	public boolean tagPairExists(String tagKey, String tagVal) {
		ArrayList<String> tagVals = tags.get(tagKey);
		return tagVals != null ? tagVals.contains(tagVal) : false;
	}
	
	public void addTagPair(String tagKey, String tagVal) {
		if (tagPairExists(tagKey, tagVal))
			return;
		
		ArrayList<String> tagVals = tags.get(tagKey);
		if (tagVals == null) {
			tagVals = new ArrayList<>();
			tags.put(tagKey, tagVals);
		}
		tagVals.add(tagVal);
	}
	
	public String getPath() {
		return filePath;
	}
	
	public LocalDate getLocalDate() {
		return date;
	}
	
	public String getDateAsString() {
		return date.format(DateTimeFormatter.ofPattern("MM/dd/uuuu"));
	}
	
	public void incrementAlbumRefs() {
		numAlbumRefs++;
	}
	
	public void decrementAlbumRefs() {
		numAlbumRefs--;
		if (numAlbumRefs < 0)
			numAlbumRefs = 0;
	}
	
	public int getNumAlbumRefs() {
		return numAlbumRefs;
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
