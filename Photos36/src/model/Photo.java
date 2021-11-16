package model;

/**
 * @author Michael Nelli - mrn73
 * @author Christopher Naporlee - cmn134
 */

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

/**
 * Internal data structure that backs a photo
 */
public class Photo implements Serializable {
	/**
	 * Needed for serializing
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * A mapping of tag keys to tag values.
	 * Each Tag key can have multiple tag values, thus we map a string to an array list
	 * of strings
	 */
	private Map<String, ArrayList<String>> tags = new HashMap<>();
	
	/**
	 * The last modification date of the photo
	 */
	private LocalDate date = null;
	
	/**
	 * The absolute path to the photo in the filesystem
	 */
	private String filePath = null;
	
	/**
	 * The caption/name of the photo
	 */
	private String caption = "";
	
	/**
	 * Creates a new photo that is backed by some file in the file system
	 * @param f The file reference to the photo
	 */
	public Photo(File f) {
		this.caption = f.getName().substring(0, f.getName().lastIndexOf('.'));
		filePath = f.toString();
		date = Instant.ofEpochMilli(f.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	/**
	 * Get the caption of a photo
	 * @return The caption
	 */
	public String getCaption() {
		return caption;
	}
	
	/**
	 * Sets the caption of a photo
	 * @param caption The new caption of the photo
	 */
	public void setCaption(String caption) {
		if (caption == null || caption.isBlank())
			return;
		this.caption = caption;
	}
	
	/**
	 * Gets all the tag keys within a photo
	 * @return An iterator of all the tag keys of a photo
	 */
	public Iterator<String> getTagKeys() {
		return tags.keySet().iterator();
	}
	
	/**
	 * Gets all the tag values for a given tag key
	 * @param tagKey The tag key to look at it's values
	 * @return An iterator of all the tag values for a given tag key
	 */
	public Iterator<String> getTagValues(String tagKey) {
		ArrayList<String> tagVals = tags.get(tagKey);
		return tagVals != null ? tagVals.iterator() : Collections.emptyIterator();
	}
	
	/**
	 * Removes a tag pair from a photo. If the tag key only has one value, the entire key
	 * is removed from the photo.
	 * @param tagKey The key of the pair
	 * @param tagVal The value of the pair
	 */
	public void removeTagPair(String tagKey, String tagVal) {
		ArrayList<String> vals = tags.get(tagKey);
		vals.remove(tagVal);
		if (vals.size() == 0)
			removeTag(tagKey);
	}
	
	/**
	 * Removes a tag key from a photo
	 * @param tagKey The key to remove
	 */
	public void removeTag(String tagKey) {
		tags.remove(tagKey);
	}
	
	/**
	 * Checks to see if a tag pair exists
	 * @param tagKey The key to check for
	 * @param tagVal The value to check for
	 * @return True if the tag pair exists, false otherwise
	 */
	public boolean tagPairExists(String tagKey, String tagVal) {
		ArrayList<String> tagVals = tags.get(tagKey);
		return tagVals != null ? tagVals.contains(tagVal) : false;
	}
	
	/**
	 * Adds a tag pair to a photo. Silently discards duplicates.
	 * @param tagKey The key we are going to be adding a new value
	 * @param tagVal The new tag value
	 */
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
	
	/**
	 * Gets the file path to the photo
	 * @return The file path
	 */
	public String getPath() {
		return filePath;
	}
	
	/**
	 * Gets the last modification time of the photo
	 * @return Time of the photo
	 */
	public LocalDate getLocalDate() {
		return date;
	}
	
	/**
	 * Gets the last modification time of the photo as a simple date
	 * of the form: MM/DD/YYYY
	 * @return The date as a string
	 */
	public String getDateAsString() {
		return date.format(DateTimeFormatter.ofPattern("MM/dd/uuuu"));
	}
	
	/**
	 * Checks to see if two photos are equal
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Photo))
			return false;
		Photo p = (Photo) o;
		return p.filePath.equals(filePath);
	}
}
