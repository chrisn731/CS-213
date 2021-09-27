/*
 * Authors:
 * 	Michael Nelli
 * 	Christopher Naporlee
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
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import songlib.app.Song;

enum Op {
	ADDING, EDITING;
}

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
	private long numEntries;
	private Op o;
	
	private static final String SONG_FILE_PATH = "./songlib.txt";
	/*
	 * Load the song library history into memory.
	 * If the file does not exist, create it.
	 * If the file does exist, read it's entries and store them.
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
			System.out.println("Can not open file for read or writing.");
		}
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(songFile));
			String line;
				
			while ((line = reader.readLine()) != null) {
				String[] x = line.split("\\|", -1);
				
				if (x.length != 4) {
					// TODO Corrupted file
					System.out.println("File is bad/corrupted, starting from fresh file.");
					reader.close();
					return new ArrayList<String>();
				}
				String song = x[0], artist = x[1], album = x[2], year = x[3];
				songList.add(song + " | " + artist);
				songs.put(formatEntry(song + artist), 
						new Song(song, artist, album, year));
				numEntries++;
			}
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to read from file");
			e.printStackTrace();
		}
		return songList;
	}
	
	public void init() {
		//Have to set this up to read from a file of songs
		obsList = FXCollections.observableArrayList(loadSongFile());
		listView.setItems(obsList);
		if (numEntries > 0) {
			listView.requestFocus();
			listView.getSelectionModel().select(0);	
		} else {
			add.requestFocus();
		}
	}
	
	public void buttonPress(ActionEvent e) {
		Button b = (Button) e.getSource();
		if (b == add) {
			o = Op.ADDING;
			//addSong();
			inputs.setVisible(true);
			info.setVisible(false);
			songNameField.requestFocus();
			headerText.setText("Enter a new song:");
		} else if (b == edit) {
			o = Op.EDITING;
			//editSong();
			inputs.setVisible(true);
			info.setVisible(false);
			headerText.setText("Edit song:");
		} else if (b == details) {
			getDetails();
		} else if (b == confirm) {
			addSong();
			updateSongFile();
		} else if (b == cancel){
			inputs.setVisible(false);
		} else {
			deleteSong();
			updateSongFile();
		}
	}
	
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
			
		}
	}
	
	public void addSong() {
		//inputs.setVisible(true);
		//info.setVisible(false);
		
		String song = songNameField.getText();
		String artist = artistNameField.getText();
		String year = yearField.getText().replaceAll(" ", "");
		
		if (song.equals("") || artist.equals("")) {
			//TODO: POPUP
			System.out.println("Must enter a name and artist bruh");
			return;
		}
		
		if (!year.equals("")) {
			if (!isPositiveInteger(year)) {
				//TODO: POPUP
				System.out.println("Enter a valid positive integer");
				return;
			}
		}
		
		//String key = (song + artist).toLowerCase().replaceAll(" ", "");
		String key = formatEntry(song + artist);
		if (songs.containsKey(key)) {
			//TODO: POPUP
			System.out.println("we already added this brah");
			songNameField.requestFocus();
		} else {
			Song s = new Song(song, artist, albumNameField.getText(), year);	
			songs.put(key, s);
			obsList.add(song + " | " + artist);
			obsList.sort(null);
			songNameField.clear();
			artistNameField.clear();
			albumNameField.clear();
			yearField.clear();
			numEntries++;
		}
		
		//TODO: Focus on newly added item:
	}
	
	public void editSong() {
		inputs.setVisible(true);
		info.setVisible(false);
	}
	
	public void getDetails() {
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
	
	public void deleteSong() {
		//TODO: Have to add a warning before going through with it
		int index = listView.getSelectionModel().getSelectedIndex();
		
		if (index >= 0) {
			songs.remove(formatSearch(obsList.get(index)));
			obsList.remove(index);
			numEntries--;
		}
	}
	
	private String formatEntry(String s) {
		return s.toLowerCase().replaceAll("\\s", "");
	}
	
	/*
	 * TODO: Probably will have to change this separator, because a
	 * song could have the word "by" in it.
	 */
	private String formatSearch(String s) {
		return s.toLowerCase().replaceAll("[\\s+\\|\\s+]", "");
	}
	
	private boolean isPositiveInteger(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isDigit(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
