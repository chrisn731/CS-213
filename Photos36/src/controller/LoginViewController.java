package controller;

import java.io.IOException;

import app.Scenes;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Admin;
import model.User;

public class LoginViewController extends SceneController {
	
	@FXML TextField textboxUsername;
	@FXML TextField textboxPassword; // UNUSED
	//Stage s;
	
	public void init(Stage s) {
		this.s = s;
		
		textboxUsername.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent key) {
				if (key.getCode().equals(KeyCode.ENTER))
					doLogin(null);
			}
		});
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
		AdminViewController avc = (AdminViewController) switchScene(Scenes.ADMIN_VIEW);
		avc.init(s);
	}

	private void loginNormal(String input) {
		AlbumViewController avc = (AlbumViewController) switchScene(Scenes.ALBUM_VIEW);
		avc.init(s, Admin.getUserByName(input));
	}

	@FXML
	private void doExit(MouseEvent m) {
		Platform.exit();
	}
}
