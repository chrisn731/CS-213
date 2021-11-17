package controller;

/**
 * @author Christopher Naporlee - cmn134
 * @author Michael Nelli - mrn73
 */
import app.Scenes;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.Admin;
import model.User;

/**
 * Main controller of the login stage of the program
 */
public class LoginViewController extends SceneController {
	
	/**
	 * Text field of the username box
	 */
	@FXML private TextField textboxUsername;
	
	/**
	 * Text field of the password box. Note: currently UNUSED, just for show.
	 */
	@FXML private TextField textboxPassword;
	
	/**
	 * Controller's main init. Sets up special listeners to use keyboard for login.
	 */
	public void init() {
		textboxUsername.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent key) {
				if (key.getCode().equals(KeyCode.ENTER))
					doLogin();
			}
		});
		s.setTitle("Photos Library - Login");
	}
	
	/**
	 * This init function is special as it acts as our entry point into the application.
	 * This function is to ONLY be used by {@link app.Photos}
	 * @param s The stage passed in from the main start() function
	 */
	public void init(Stage s) {
		this.s = s;
		init();
	}
	
	/**
	 * Login Routine. Activated upon pressing the sign in button or pressing enter
	 * within the username text field.
	 */
	@FXML
	private void doLogin() {
		String input = textboxUsername.getText();

		if (input.isBlank()) {
			System.out.println("Please enter username.");
			return;
		}
		
		input = input.strip();
		User u = Admin.getUserByName(input);
		if (u == null) {
			showPopup(
				"Login Error ",
				"", 
				"Can not log in as '" + input + "', please try again.",
				AlertType.WARNING
			);
			return;
		}
		
		if (u.getUserName().equals("admin")) {
			loginAsAdmin();
		} else {
			loginNormal(u);
		}

	}
	
	/**
	 * Logs into the application as admin, thus launching the Admin View
	 */
	private void loginAsAdmin() {
		AdminViewController avc = (AdminViewController) switchScene(Scenes.ADMIN_VIEW);
		avc.init();
	}

	/**
	 * Logs into the application as a regular user, thus launching the AlbumView
	 * @param u The user to log in as
	 */
	private void loginNormal(User u) {
		AlbumViewController avc = (AlbumViewController) switchScene(Scenes.ALBUM_VIEW);
		avc.init(u);
	}

	/**
	 * Gracefully quits the application.
	 */
	@FXML
	private void doExit() {
		Platform.exit();
	}
}
