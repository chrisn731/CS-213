package model;

/**
 * @author Christopher Naporlee - cmn134
 * @author Michael Nelli - mrn73
 */

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
	private ArrayList<Photo> photos;
	
	/**
	 * Name of the album
	 */
	private String name;
	
	/**
	 * Number of photos inside of the album
	 */
	private int numPhotos;
	
	/**
	 * Earliest date in this albums date range.
	 */
	private LocalDate minDate;
	
	/**
	 * Latest date in this albums date range.
	 */
	private LocalDate maxDate;
	
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
		photos = new ArrayList<Photo>();
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
	
	/**
	 * Adds a photo to the album
	 * @param p The photo to add 
	 * @return True if successful in adding, false indicates the photo already exists
	 */
	public boolean addPhoto(Photo p) {
		if (photos.contains(p))
			return false;
		photos.add(p);
		numPhotos++;
		filePhotoMap.put(p.getPath(), p);
		calculateDateRange();
		return true;
	}
	
	/**
	 * Calculates the date range.
	 */
	private void calculateDateRange() {
		minDate = photos.isEmpty() ? null : photos.get(0).getLocalDate();
		maxDate = photos.isEmpty() ? null : photos.get(0).getLocalDate();
		
		for (Photo p : photos) {
			if (p.getLocalDate().isBefore(minDate))
				minDate = p.getLocalDate();
			if (p.getLocalDate().isAfter(maxDate))
				maxDate = p.getLocalDate();
		}
			
	}
	
	/**
	 * Gets the earliest date of a photo in the album.
	 * @return  the earliest date or null if the album has no photos
	 */
	public String getMinDateAsString() {
		if (minDate == null)
			return null;
		return minDate.format(DateTimeFormatter.ofPattern("MM/dd/uuuu"));
	}
	
	/**
	 * Gets the latest date of a photo in the album.
	 * @return  the latest date or null if the album has no photos
	 */
	public String getMaxDateAsString() {
		if (maxDate == null)
			return null;
		return maxDate.format(DateTimeFormatter.ofPattern("MM/dd/uuuu"));
	}
	
	/**
	 * Removes a photo from the album
	 * @param p The photo to remove from the album
	 */
	public void removePhoto(Photo p) {
		photos.remove(p);
		numPhotos--;
		filePhotoMap.remove(p.getPath());
		calculateDateRange();
	}
	
	/**
	 * Get the number of photos within the album
	 * @return The number of photos
	 */
	public int getPhotoCount() {
		return numPhotos;
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
		if (o == null || !(o instanceof Album)) {
			return false;
		}
		Album a = (Album) o;
		return a.name.equals(name);
	}
}
