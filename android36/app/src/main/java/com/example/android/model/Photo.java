package com.example.android.model;

/**
 * @author Michael Nelli - mrn73
 * @author Christopher Naporlee - cmn134
 */

import android.net.Uri;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
    public Photo(Uri f) {
        filePath = f.toString();
    }

    public Photo(Uri u, String f) {
        this(u);
        caption = f;
    }

    /**
     * Get the caption of a photo
     * @return The caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * TODO: isBlank() doesn't exist so we may have to look at this
     * Sets the caption of a photo
     * @param caption The new caption of the photo
     */
    public void setCaption(String caption) {
        if (caption == null || caption.isEmpty())
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

    public String getTagKey(int pos) {
        int i = 0;

        for (String s1 : tags.keySet()) {
            for (String s2 : tags.get(s1)) {
                if (i++ == pos)
                    return s1;
            }
        }
        return "";
    }

    public int getNumTags() {
        Set<String> keys = tags.keySet();
        int numTags = 0;
        for (String key : keys)
            numTags += tags.get(key).size();
        return numTags;
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

    public String getTagValuePos(String tagKey, int pos) {
        int i = 0;

        for (String s1 : tags.keySet()) {
            for (String s2 : tags.get(s1)) {
                if (i++ == pos)
                    return s2;
            }
        }
        return "";
    }

    /**
     * Removes a tag pair from a photo. If the tag key only has one value, the entire key
     * is removed from the photo.
     * @param _tagKey The key of the pair
     * @param _tagVal The value of the pair
     */
    public void removeTagPair(String _tagKey, String _tagVal) {
        String tagKey = _tagKey.trim();
        String tagVal = _tagVal.trim();
        ArrayList<String> vals = tags.get(tagKey);
        System.out.println("WE WANT TO REMOVE: " + tagVal + " and VALS HAS: " + vals);
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
        if (tagKey.toLowerCase().equals("location"))
                tagVals.clear();
        tagVals.add(tagVal);
    }

    /**
     * Gets the file path to the photo
     * @return The file path
     */
    public Uri getUri() {
        return Uri.parse(filePath);
    }

    public void update(Photo p) {
        this.tags = p.tags;
        this.caption = p.caption;
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

