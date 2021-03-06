package model;

/**
 * @author Christopher Naporlee - cmn134
 * @author Michael Nelli - mrn73
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Utility Class to manipulate user database.
 */
public class Admin {
	
	/**
	 * List of all users
	 */
	private static ArrayList<User> users = new ArrayList<User>();
	
	/**
	 * Housing directory of photos application save data
	 */
	private static final String dataDir = "dat";
	
	/**
	 * Name to give the photos application save data
	 */
	private static final String dataFileName = "photos.dat";
	
	/**
	 * Number of stock photos
	 */
	private static final int NUM_STOCK_PHOTOS = 6;
	
	/**
	 * Get all the users that are registered to the application
	 * @return Iterator over the list of all users
	 */
	public static Iterator<User> getUsers() {
		return users.iterator();
	}
	
	/**
	 * Retrieve a user by their username
	 * @param username User's username
	 * @return The User
	 */
	public static User getUserByName(String username) {
		for (User u : users) {
			if (u.getUserName().equals(username)) {
				return u;
			}
		}
		return null;
	}
	
	/**
	 * Add a user to the list of all users
	 * @param username Username to add into the user list
	 * @return True if successful, false indicates user already exists
	 */
	public static boolean addUser(String username) {
		if (getUserByName(username) != null)
			return false;
		return users.add(new User(username));
	}
	
	/**
	 * Remove a user from the list of all users
	 * @param username The user's username
	 */
	public static void removeUser(String username) {
		users.remove(getUserByName(username));
	}
	
	/**
	 * Writes all data to disk. Called upon application entry.
	 * @throws IOException IO error from writing to disk
	 */
	public static void syncToDisk() throws IOException {
		System.out.println(
				"Syncing to disk at: " +
				dataDir + File.separator + dataFileName
		);
		FileOutputStream fos = new FileOutputStream(dataDir + File.separator + dataFileName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(users);
		oos.close();
		System.out.println("Done.");
	}
	
	/**
	 * Necessary setup to initialize the stock user of the program. Only called
	 * if we are installing a fresh photos save.
	 */
	private static void setupStockUser() {
		User stock = getUserByName("stock");
		stock.addAlbum(new Album("stock"));
		for (int i = 1; i <= NUM_STOCK_PHOTOS; i++) {
			stock.getAlbum("stock").addPhoto(new Photo(new File("./data/stock" + i + ".jpg")));
		}
	}
	
	/**
	 * Setup the photos application with the admin and stock user. Only called
	 * if we are installing a fresh photos save.
	 */
	private static void setupDefaults() {
		users.add(new User("stock"));
		setupStockUser();
		users.add(new User("admin"));
		try {
			syncToDisk();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads in photos data from disk. Called upon application exit and cleanup.
	 * @throws ClassNotFoundException Thrown if failed to store data into users list
	 */
	@SuppressWarnings("unchecked")
	public static void loadFromDisk() throws ClassNotFoundException {
		System.out.println(
				"Loading " +
				dataDir + File.separator + dataFileName +
				" from disk..."
		);
		
		try {
			FileInputStream fis = new FileInputStream(dataDir + File.separator + dataFileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			users = (ArrayList<User>) ois.readObject();
			ois.close();
			System.out.println("Done.");
		} catch (FileNotFoundException e) {
			setupDefaults();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
