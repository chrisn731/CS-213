package songlib.view;

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
	private Op o;
	
	public void init() {
		//Have to set this up to read from a file of songs
		obsList = FXCollections.observableArrayList();
		listView.setItems(obsList);
		add.requestFocus();
	}
	
	public void buttonPress(ActionEvent e) {
		Button b = (Button)e.getSource();
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
		} else if (b == cancel){
			inputs.setVisible(false);
		} else {
			deleteSong();
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
		String key = formatEntry((song + artist));
		if (songs.containsKey(key)) {
			//TODO: POPUP
			System.out.println("we already added this brah");
			songNameField.requestFocus();
		} else {
			Song s = new Song(song, artist, albumNameField.getText(), year);	
			songs.put(key, s);
			obsList.add(song + " by " + artist);
			obsList.sort(null);
			songNameField.clear();
			artistNameField.clear();
			albumNameField.clear();
			yearField.clear();
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
		Song selectedSong = songs.get(formatSearch(obsList.get(index)));
		songNameText.setText(selectedSong.getName());
		artistNameText.setText(selectedSong.getArtist());
		albumNameText.setText(selectedSong.getAlbum());
		yearText.setText(selectedSong.getYear());
	}
	
	public void deleteSong() {
		//TODO: Have to add a warning before going through with it
		int index = listView.getSelectionModel().getSelectedIndex();
		songs.remove(formatSearch(obsList.get(index)));
		obsList.remove(index);
	}
	
	private String formatEntry(String s) {
		return s.toLowerCase().replaceAll(" ", "");
	}
	
	/*
	 * TODO: Probably will have to change this separator, because a
	 * song could have the word "by" in it.
	 */
	private String formatSearch(String s) {
		return s.toLowerCase().replaceAll(" by ", "");
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
