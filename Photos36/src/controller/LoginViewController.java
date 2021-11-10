package controller;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Admin;
import model.User;

public class LoginViewController {
	
	@FXML TextField textboxUsername;
	@FXML TextField textboxPassword; // UNUSED
	Stage s;
	
	public void init(Stage s) {
		this.s = s;
	}
	
	@FXML
	private void doLogin(MouseEvent m) {
		String input = textboxUsername.getText();
		
		if (input.isBlank()) {
			System.out.println("Please enter username.");
			return;
		}
		
		User u = Admin.getUserByName(input);
		if (u == null) {
			System.out.println("User not found");
			return;
		}
		
		System.out.println("Login successful!");
		if (input.equals("admin")) {
			loginAsAdmin();
		} else {
			loginNormal(input);
		}

	}
	
	private void loginAsAdmin() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/AdminView.fxml"));
		AnchorPane root = null;
		try {
			 root = (AnchorPane)loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AdminViewController avc = loader.getController();
		avc.init(s);
		Scene scene = new Scene(root);
		s.setScene(scene);
	}

	private void loginNormal(String input) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/AlbumView.fxml"));
		AnchorPane root = null;
		try {
			 root = (AnchorPane)loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AlbumViewController avc = loader.getController();
		avc.init(s, new User(null));
		Scene scene = new Scene(root);
		s.setScene(scene);
	}

	@FXML
	private void doExit(MouseEvent m) {
		Platform.exit();
	}
}
