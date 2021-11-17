package controller;

/**
 * @author Christopher Naporlee - cmn134
 * @author Michael Nelli - mrn73
 */
import java.util.Iterator;

import app.Scenes;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import model.Admin;
import model.User;

/**
 * Main Controller of the Admin View Scene
 */
public class AdminViewController extends SceneController {

	/**
	 * Button handling adding users
	 */
	@FXML private Button addUserButton;
	
	/**
	 * Button handling deleting users
	 */
	@FXML private Button deleteUserButton;
	
	/**
	 * List view that displays all active users in the database
	 */
	@FXML private ListView<String> userView;
	
	/**
	 * Top bar menu option to logout
	 */
	@FXML private MenuItem logoutOption;
	
	/**
	 * Top bar menu option to quit the program
	 */
	@FXML private MenuItem quitOption;
	
	/**
	 * List that backs the List view
	 */
	private ObservableList<String> obsList;
	
	/**
	 * Sets up the list view to add and delete users
	 */
	public void init() {
		obsList = FXCollections.observableArrayList();
		for (Iterator<User> i = Admin.getUsers(); i.hasNext();)
			obsList.add(i.next().getUserName());
		userView.setItems(obsList);
		userView.requestFocus();
		userView.getSelectionModel().select(0);
		s.setTitle("Photo Library - Admin Panel");
	}
	
	/**
	 * Handles adding a new user.
	 * Displays an error if the user already exists.
	 */
	@FXML
	private void addUser() {
		String username = getUserInput("New User", null, "Enter new user's name: ", null, true);
		if (username == null)
			return;
		username = username.strip();
		if (Admin.getUserByName(username) != null) {
			showPopup(
				"Error adding " + username,
				"", 
				"You can not add '" + username + "' as they already exist!",
				AlertType.WARNING
			);
			return;
		}
		Admin.addUser(username);
		obsList.add(username);
	}
	
	/**
	 * Handles deleting a user.
	 * Displays an error if the user can not be deleted. (i.e. admin)
	 */
	@FXML
	private void deleteUser() {
		int index = userView.getSelectionModel().getSelectedIndex();
		if (index < 0)
			return;
		
		String userToDelete = obsList.get(index);
		if (userToDelete.equals("admin")) {
			showPopup(
				"Error Deleting " + userToDelete,
				"", 
				"You can not delete '" + userToDelete + "'!",
				AlertType.WARNING
			);
			return;
		}
		boolean approved = showPopup(
				"Delete '" + userToDelete + "'?",
				"",
				"Are you sure you want to delete '" + userToDelete + "'?",
				AlertType.CONFIRMATION
		);
		if (!approved)
			return;
		Admin.removeUser(userToDelete);
		obsList.remove(userToDelete);
	}
	
	/**
	 * Handles exiting the entire application.
	 */
	@FXML
	private void doQuit() {
		Platform.exit();
	}
	
	/**
	 * Handles logging out. Brings user back to the login screen.
	 */
	@FXML
	private void doLogout() {
		LoginViewController lvc = (LoginViewController) switchScene(Scenes.LOGIN);
		lvc.init();
	}
}
