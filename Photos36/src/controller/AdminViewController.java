package controller;

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
import javafx.scene.input.MouseEvent;
import model.Admin;
import model.User;

public class AdminViewController extends SceneController {

	@FXML private Button addUserButton;
	@FXML private Button deleteUserButton;
	@FXML private ListView<String> userView;
	@FXML private MenuItem logoutOption;
	@FXML private MenuItem quitOption;
	private ObservableList<String> obsList;
	
	public void init() {
		obsList = FXCollections.observableArrayList();
		for (Iterator<User> i = Admin.getUsers(); i.hasNext();)
			obsList.add(i.next().getUserName());
		userView.setItems(obsList);
		userView.requestFocus();
		userView.getSelectionModel().select(0);
		s.setTitle("Photo Library - Admin Panel");
	}
	
	@FXML
	private void addUser(MouseEvent m) {
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
	
	@FXML
	private void deleteUser(MouseEvent m) {
		int index = userView.getSelectionModel().getSelectedIndex();
		if (index < 0)
			return;
		
		String userToDelete = obsList.get(index);
		if (userToDelete.equals("admin") || userToDelete.equals("stock")) {
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
	
	@FXML
	private void doQuit() {
		Platform.exit();
	}
	
	@FXML
	private void doLogout() {
		LoginViewController lvc = (LoginViewController) switchScene(Scenes.LOGIN);
		lvc.init();
	}
}
