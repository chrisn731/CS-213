package controller;

import java.util.ArrayList;
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
import javafx.stage.Stage;
import model.Admin;
import model.User;

public class AdminViewController extends SceneController {

	@FXML private Button addUserButton;
	@FXML private Button deleteUserButton;
	@FXML private ListView<String> userView;
	@FXML private MenuItem logoutOption;
	@FXML private MenuItem quitOption;
	private ArrayList<User> users;
	private ObservableList<String> obsList;
	//private Stage s;
	
	public void init(Stage s) {
		this.s = s;
		users = Admin.getUsers();
		ArrayList<String> list = new ArrayList<>();
		for (User u : users) {
			list.add(u.getUserName());
		}
		obsList = FXCollections.observableArrayList(list);
		userView.setItems(obsList);
		userView.requestFocus();
		userView.getSelectionModel().select(0);
	}
	
	@FXML
	private void addUser(MouseEvent m) {
		String username = getUserInput("New User", null, "Enter new user's name: ", null, true);
		if (username == null)
			return;
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
		boolean b = showPopup(
				"Delete '" + userToDelete + "'?",
				"",
				"Are you sure you want to delete '" + userToDelete + "'?",
				AlertType.CONFIRMATION
		);
		if (!b)
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
		lvc.init(s);
	}
}
