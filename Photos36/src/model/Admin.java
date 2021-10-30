package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Utility Class to manipulate user database
 *
 */
public class Admin {
	
	private static ArrayList<User> users = new ArrayList<User>();
	private static final String dataDir = "dat";
	private static final String dataFileName = "photos.dat";
	
	public static ArrayList<User> getUsers() {
		return users;
	}
	
	public static User getUserByName(String username) {
		for (User u : users) {
			if (u.getUserName().equals(username)) {
				return u;
			}
		}
		return null;
	}
	
	public static boolean addUser(String username) {
		if (getUserByName(username) != null)
			return false;
		return users.add(new User(username));
	}
	
	public static void removeUser(String username) {
		users.remove(getUserByName(username));
	}
	
	/**
	 * Writes all data to disk. Called upon application entry.
	 * fsync()
	 * @throws IOException
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
	
	public static void setup() {
		try {
			loadFromDisk();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (users.size() == 0) {
			// TODO: Setup Stock User
		}
	}
	
	/**
	 * Loads in photos data from disk. Called upon application exit and cleanup.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static void loadFromDisk() throws IOException, ClassNotFoundException {
		System.out.println(
				"Loading " +
				dataDir + File.separator + dataFileName +
				" from disk..."
		);
		FileInputStream fis = new FileInputStream(dataDir + File.separator + dataFileName);
		ObjectInputStream ois = new ObjectInputStream(fis);
		users = (ArrayList<User>) ois.readObject();
		ois.close();
		System.out.println("Done.");
	}
}
