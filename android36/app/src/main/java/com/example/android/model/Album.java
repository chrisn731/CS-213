package com.example.android.model;

/**
 * @author Christopher Naporlee - cmn134
 * @author Michael Nelli - mrn73
 */

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Internal data structure representing an album
 */
public class Album implements Serializable {

    /**
     * Needed to serialize
     */
    private static final long serialVersionUID = 1L;

    /**
     * List of photos contained within the album
     */
    private ArrayList<Photo> photos = new ArrayList<>();

    /**
     * Name of the album
     */
    private String name;

    /**
     * Mapping of file path to a photo. Used for fast lookups within an album
     * if a photo already exists.
     */
    Map<String, Photo> filePhotoMap = new HashMap<>();

    /**
     * Created a new album with a given name
     * @param name The name of the new album
     */
    public Album(String name) {
        this.name = name;
    }

    /**
     * Gets all the photos within the album
     * @return List of photos
     */
    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    /**
     * Gets the photo within the album by its file path
     * @param filePath The file path of the photo to search
     * @return The photo if it exists, null otherwise
     */
    public Photo getPhotoByFile(String filePath) {
       return filePhotoMap.get(filePath);

    }

    public Photo getPhotoByFile(Uri uri) {
        return getPhotoByFile(uri.toString());
    }

    /**
     * Adds a photo to the album
     * @param p The photo to add
     * @return True if successful in adding, false indicates the photo already exists
     */
    public boolean addPhoto(Photo p) {
        photos.add(p);
        System.out.println("adding " + p);
        filePhotoMap.put(p.getUri().toString(), p);
        return true;
    }

    public boolean contains(Photo p) {
        return photos.contains(p);
    }

    public void updatePhoto(Photo p) {
        int x = photos.indexOf(p);
        assert x >= 0;
        photos.get(x).update(p);
    }

    /**
     * Removes a photo from the album
     * @param p The photo to remove from the album
     */
    public void removePhoto(Photo p) {
        photos.remove(p);
        filePhotoMap.remove(p.getUri().toString());
    }

    /**
     * Get the number of photos within the album
     * @return The number of photos
     */
    public int getPhotoCount() {
        return photos.size();
    }

    /**
     * Get the name of the album
     * @return Name of the album
     */
    public String getName() {
        return name;
    }

    /**
     * Change the name of the album
     * @param name Name to set the album to
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Checks to see if two albums are equivalent.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Album)) {
            return false;
        }
        Album a = (Album) o;
        return a.name.equals(name);
    }
}
