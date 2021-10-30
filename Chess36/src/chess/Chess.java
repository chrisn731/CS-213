package chess;

/**
 * @author Michael Nelli - mrn73
 * @author Christopher Naporlee - cmn134
 */

/**
 * Main class of the chess application.
 * Acts as a launchpad for setup to begin.
 */
public class Chess {
	
	/**
	 * Application entry point
	 * @param args Command line arguments (UNUSED)
	 */
	public static void main(String[] args) {
		new GameManager().run();
	}
}
