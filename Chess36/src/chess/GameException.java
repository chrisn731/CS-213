package chess;

/**
 * @author Christopher Naporlee - cmn134
 * @author Michael Nelli - mrn73
 */

/**
 * Notification system for the {@link chess.Board} to notify the
 * {@link chess.GameManager} of the state of the game changing.
 */
enum GameException {
	/**
	 * No game exception. Everything normal.
	 */
	NONE,
	
	/**
	 * The requested move is invalid and or not possible.
	 */
	INVALID_MOVE,
	
	/**
	 * The requested move has left the opposition in check.
	 */
	CHECK,
	
	/**
	 * The requested move has left the opposition in checkmate.
	 */
	CHECKMATE,
}