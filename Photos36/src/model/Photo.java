package model;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Photo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Map<String, ArrayList<String>> tags = new HashMap<>();
	private LocalDate date = null;
	private String filePath = null;
	private String caption = "";
	
	public Photo() {
		this.caption = "hey";
	}
	
	public Photo(String caption) {
		this.caption = (caption == null) ? "" : caption;
	}
	
	public Photo(File f) {
		this();
		filePath = f.toString();
	}

	public String getCaption() {
		return caption;
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
	
	public LocalDate getDate() {
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
