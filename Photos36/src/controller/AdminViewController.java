package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Admin;
import model.User;

public class AdminViewController {

	@FXML Button addUserButton;
	@FXML Button deleteUserButton;
	@FXML ListView<String> userView;
	@FXML MenuItem logoutOption;
	@FXML MenuItem quitOption;
	private ArrayList<User> users;
	private ObservableList<String> obsList;
	private Stage s;
	
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
		String username = getUserToAdd("New User", null, "Enter new user's name: ", null, true);
		if (username == null)
			return;
		if (Admin.getUserByName(username) != null) {
			showPopup(
					"Error adding " + username,
					"", 
					"You can not add '" + username + "' as they already exist!"
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
				"You can not delete '" + userToDelete + "'!"
			);
			return;
		}
		ButtonType b = getConfirmation(
				"Delete '" + userToDelete + "'?",
				"",
				"Are you sure you want to delete '" + userToDelete + "'?"
		);
		if (b.equals(ButtonType.CANCEL))
			return;
		Admin.removeUser(userToDelete);
		obsList.remove(userToDelete);
	}
	
	private String getUserToAdd(String title, String header, String content, String prompText, boolean disableOK) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.initOwner(s);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(content);
		Node buttonOK = dialog.getDialogPane().lookupButton(ButtonType.OK);
		
		TextField editor = dialog.getEditor();
		editor.setText(prompText);
		buttonOK.setDisable(disableOK);
		
		/* Prevents the user from clicking OK without having a proper album name */
		editor.textProperty().addListener((observable, oldValue, newValue) -> {
		    if (newValue.length() > 20)
		    	editor.setText(newValue = oldValue);
		    buttonOK.setDisable(newValue.isBlank());
		});
		
		/* Making it here means our album has at least 1 letter in it. Clicking cancel will return null. */
		Optional<String> ret = dialog.showAndWait();
		return ret.isPresent() ? ret.get() : null;
	}
	

	private void showPopup(String title, String header, String context) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.initOwner(s);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(context);
		alert.showAndWait();
	}
	
	private ButtonType getConfirmation(String title, String header, String context) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(s);
		alert.setTitle(title);
		alert.setHeaderText("");
		alert.setContentText(context);
		alert.showAndWait();
		return alert.getResult();
	}
	
	@FXML
	private void doQuit() {
		Platform.exit();
	}
	
	@FXML
	private void doLogout() {
		FXMLLoader loader = new FXMLLoader();
		AnchorPane root = null;
		loader.setLocation(getClass().getResource("/view/LoginView.fxml"));
		try {
			root = (AnchorPane)loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LoginViewController lvc = loader.getController();
		lvc.init(s);
		Scene scene = new Scene(root);
		s.setScene(scene);
	}
}
