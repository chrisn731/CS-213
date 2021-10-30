package controller;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class PhotoViewController {
	
	
	/*
	 * This is just a placeholder so i dont forget how this works 
	 * This is not staying, I just dont feel like googling it again
	 */
	private void invokeFileChooser() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose picture to add");
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter(
						"Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"
				)
		);
		//File selectedFile = fileChooser.showOpenDialog(the stage);
		// SELECTED FILE CAN RETURN NULL!
	}
}
