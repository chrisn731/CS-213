package app;

/**
 * @author Christopher Naporlee - cmn134
 * @author Michael Nelli - mrn73
 */

/**
 * Interface of any scene or asset that is capable of being loaded into an FXML loader.
 */
public interface Loadable {

	/**
	 * Gets a path, as a string, to a FXML file within workspace
	 * @return path
	 */
	String getPath();
}
