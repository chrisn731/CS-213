/*
 * Authors:
 * 	Michael Nelli - mrn73
 * 	Christopher Naporlee - cmn134
 */
package songlib.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import songlib.app.Song;

enum Op {
	ADDING, EDITING;
}

//TODO: disable listview when editing

public class SongLibController {
	@FXML Button add;
	@FXML Button edit;
	@FXML Button delete;
	@FXML Button details;
	@FXML Button confirm;
	@FXML Button cancel;
	@FXML ListView<String> listView;

	@FXML VBox inputs;
	@FXML TextField songNameField;
	@FXML TextField artistNameField;
	@FXML TextField albumNameField;
	@FXML TextField yearField;
	@FXML Text headerText;

	@FXML VBox info;
	@FXML Text songNameText;
	@FXML Text artistNameText;
	@FXML Text albumNameText;
	@FXML Text yearText;

	private ObservableList<String> obsList;
	private HashMap<String, Song> songs = new HashMap<String, Song>();
	private Song selectedEdit;
	private Op action;
	private long numEntries;

	private Stage primaryStage;

	private static final String SONG_FILE_PATH = "./songlib.txt";
	private static final boolean DETAILS_ON_CLICK = false;

	public void init(Stage mainStage) {
		primaryStage = mainStage;
		obsList = FXCollections.observableArrayList(loadSongFile());
		listView.setItems(obsList);
		obsList.sort(null);
		if (DETAILS_ON_CLICK) {
			/*
			 * TODO: Enabling this causes a bug when pressing Add. If you press
			 * add and then click on the listview, all buttons are DISABLED and
			 * you can no longer add in a song.
			 */
			listView
				.getSelectionModel()
				.selectedIndexProperty()
				.addListener(
					(obs, oldv, newv) ->
						songListClickHandler(mainStage));
		}
		if (numEntries > 0) {
			listView.requestFocus();
			listView.getSelectionModel().select(0);
		} else {
			add.requestFocus();
			disableButtons(true);
		}
	}

	private void songListClickHandler(Stage mainStage) {
		// TODO Auto-generated method stub
		getDetails();
	}

	/*
	 * Load the song library history into memory.
	 * If the file does not exist, create it.
	 * If the file does exist, read it's entries and store them.
	 *
	 * Notes:
	 *   All input, if done through the application _should_ be processed
	 *   correctly. However, if someone tries to be funny and edit the file
	 *   directly but puts in invalid input (such as letters or negative number
	 *   in the year field) for a field... that field will be DROPPED.
	 */
	private ArrayList<String> loadSongFile() {
		ArrayList<String> songList = new ArrayList<String>();
		File songFile = new File(SONG_FILE_PATH);

		if (!songFile.exists()) {
			try {
				songFile.createNewFile();
			} catch (IOException e1) {
				System.out.println("Failed to create file.");
				e1.printStackTrace();
			}
		}

		if (!songFile.canRead() || !songFile.canWrite()) {
			System.out.println("Can not open song history file for read or writing.");
		}

		try {
			BufferedReader reader = new BufferedReader(new FileReader(songFile));
			String line;
			boolean previouslyCorrupted = false;

			while ((line = reader.readLine()) != null) {
				String[] songPieces = line.split("\\|", -1);

				if (songPieces.length != 4 || songPieces[0].isBlank() || songPieces[1].isBlank()) {
					if (!previouslyCorrupted) {
						System.out.println(
							"Song history file is bad/corrupted. " +
							"Attempting to salvage non-corrupted songs..."
						);
					}
					continue;
				}
				String song = songPieces[0].trim(), artist = songPieces[1].trim(),
						album = songPieces[2].trim(), year = songPieces[3].trim();
				songList.add(song + " | " + artist);
				songs.put(
					formatEntry(song + artist),
					new Song(
						song,
						artist,
						album,
						!year.isBlank() && isPositiveInteger(year) ? year : ""
					)
				);
				numEntries++;
			}
			reader.close();
		} catch (Exception e) {
			System.out.println("Error reading from song history file.");
			e.printStackTrace();
		}
		return songList;
	}

	@FXML
	private void buttonPress(ActionEvent e) {
		Button b = (Button) e.getSource();
		if (b == add) {
			showAddDialogue();
			action = Op.ADDING;
		} else if (b == edit) {
			showEditDialogue();
			action = Op.EDITING;
		} else if (b == confirm) {
			updateSongs();
			updateSongFile();
		} else if (b == cancel){
			clearInputs();
			inputs.setVisible(false);
			disableAllButtons(false);
			listView.setDisable(false);
		} else if (b == details) {
			getDetails();
		} else {
			deleteSong();
			updateSongFile();
		}
	}

	/*
	 * Write the songs to the file.
	 * Song file is updated on each successful song addition or edit.
	 */
	private void updateSongFile() {
		try {
			/*
			 * FileWriter will truncate the file upon opening.
			 * Therefore, if we have no entries to write, we simply clear the
			 * file and move on.
			 */
			FileWriter f = new FileWriter(SONG_FILE_PATH, false);
			if (numEntries > 0) {
				for (Song entry : songs.values()) {
					f.write(
						entry.getName() +
						"|" +
						entry.getArtist() +
						"|" +
						entry.getAlbum() +
						"|" +
						entry.getYear() +
						"\n"
					);
				}
			}
			f.close();
		} catch (Exception e) {
			System.out.println("WARNING! Failed to update song history file.");
			e.printStackTrace();
		}
	}

	private void updateSongs() {
		String song = songNameField.getText().trim();
		String artist = artistNameField.getText().trim();
		String album = albumNameField.getText().trim();
		String year = yearField.getText().replaceAll("\\s", "").trim();

		if (!isValidInput(song, artist, album, year)) {
			return;
		}

		String key = formatEntry(song + artist);
		switch (action) {
			case EDITING:
				Song s = songs.get(key);
				if (selectedEdit == s) {
					selectedEdit.setAlbum(album);
					selectedEdit.setYear(year);
				} else if (s != null) {
					showPopup("Duplicate Song",
							  "",
							  "This song already exists in the library.");
				} else {
					int index = listView.getSelectionModel().getSelectedIndex();
					songs.remove(formatSearch(obsList.get(index)));
					obsList.remove(index);
					numEntries--;
					addSong(key, new Song(song, artist, album, year));
				}
				getDetails();
				break;
			case ADDING:
				if (songs.containsKey(key)) {
					showPopup("Duplicate Song",
							  "",
							  "This song already exists in the library.");
					songNameField.requestFocus();
				} else {
					addSong(key, new Song(song, artist, album, year));
					getDetails();
				}
				break;
		}
		disableAllButtons(false);
		listView.setDisable(false);
	}

	private void addSong(String key, Song song) {
		String name = song.getName() + " | " + song.getArtist();
		songs.put(key, song);
		obsList.add(name);
		obsList.sort(null);
		clearInputs();
		if (numEntries == 0) {
			disableButtons(false);
		}
		numEntries++;
		listView.getSelectionModel().select(obsList.indexOf(name));
	}

	private void getDetails() {
		info.setVisible(true);
		inputs.setVisible(false);

		int index = listView.getSelectionModel().getSelectedIndex();
		if (index >= 0) {
			Song selectedSong = songs.get(formatSearch(obsList.get(index)));
			songNameText.setText(selectedSong.getName());
			artistNameText.setText(selectedSong.getArtist());
			albumNameText.setText(selectedSong.getAlbum());
			yearText.setText(selectedSong.getYear());
		}
	}

	private void deleteSong() {
		int index = listView.getSelectionModel().getSelectedIndex();
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(primaryStage);
		alert.setTitle("Delete Song");
		alert.setHeaderText("");
		String context = "Are you sure you want to delete \""
					  	  + obsList.get(index)
					  	  + "\" from the library?";
		alert.setContentText(context);
		alert.showAndWait();

		if (alert.getResult().equals(ButtonType.CANCEL)) {
			return;
		}

		if (index >= 0) {
			songs.remove(formatSearch(obsList.get(index)));
			obsList.remove(index);
			if (numEntries > index) {
				listView.getSelectionModel().select(index);
			}
			numEntries--;
			if (numEntries != 0) {
				getDetails();
			} else {
				disableButtons(true);
				info.setVisible(false);
				add.requestFocus();
			}
		}
	}

	private boolean isValidInput(String song, String artist, String album, String year) {
		if (song.equals("") || artist.equals("")) {
			showPopup("Missing Fields",
					  "",
					  "Please enter in a song name and artist. Album and year are optional.");
			return false;
		}
		if (song.contains("|") || artist.contains("|") || album.contains("|")) {
			showPopup("Illegal Character",
					  "",
					  "Vertical bar ( | ) not permitted.");
			return false;
		}
		if (!year.equals("") && !isPositiveInteger(year)) {
			showPopup("Invalid Year",
					"",
					"Please enter in a positive integer for the year.");
			return false;
		}
		return true;
	}

	private void showAddDialogue() {
		clearInputs();
		inputs.setVisible(true);
		info.setVisible(false);
		if (DETAILS_ON_CLICK) {
			listView.setDisable(true);
		}
		disableAllButtons(true);
		songNameField.requestFocus();
		headerText.setText("Enter a new song:");
	}

	private void showEditDialogue() {
		inputs.setVisible(true);
		info.setVisible(false);
		disableAllButtons(true);
		headerText.setText("Edit song:");

		int index = listView.getSelectionModel().getSelectedIndex();
		Song s = songs.get(formatSearch(obsList.get(index)));
		selectedEdit = s;
		songNameField.setText(s.getName());
		artistNameField.setText(s.getArtist());
		albumNameField.setText(s.getAlbum());
		yearField.setText(s.getYear());
		listView.setDisable(true);
	}

	private void showPopup(String title, String header, String context) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.initOwner(primaryStage);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(context);
		alert.showAndWait();
	}

	private void clearInputs() {
		songNameField.clear();
		artistNameField.clear();
		albumNameField.clear();
		yearField.clear();
	}

	private void disableAllButtons(boolean b) {
		add.setDisable(b);
		delete.setDisable(b);
		details.setDisable(b);
		edit.setDisable(b);
	}

	private void disableButtons(boolean b) {
		delete.setDisable(b);
		details.setDisable(b);
		edit.setDisable(b);
	}

	private String formatEntry(String s) {
		return s.toLowerCase().replaceAll("\\s", "");
	}

	private String formatSearch(String s) {
		return s.toLowerCase().replaceAll("[\\s+\\|\\s+]", "");
	}

	private boolean isPositiveInteger(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isDigit(s.charAt(i))) {
				return false;
			}
		}
		return Integer.parseInt(s) >= 0;
	}
}
