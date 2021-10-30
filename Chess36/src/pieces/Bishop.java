package pieces;

/**
 * @author Michael Nelli - mrn73
 * @author Christopher Naporlee - cmn134
 */

import chess.Board;

/**
 * Represents the Bishop chess piece
 */
public class Bishop extends ChessPiece {

	/**
	 * Initializes a new Bishop with the given color
	 * @param color  of the chess piece
	 */
	public Bishop(Color color) {
		super(color, "Bishop");
	}

	
	/*
	 * ------------------------------------------
	 * |            Possible Moves              |   
	 * ------------------------------------------
	 * .                                       .
	 *     .                               .
	 *         .                       .    
	 * 	        (-1, -1)       (-1, +1)
	 *                     B      
	 *          (+1, -1)       (+1, +1)
	 *         .                       .
	 *     .                               .   
	 * .                                       .
	 */
	public void calculateMoves(Board board) {
		validMoves.clear();
		pathToKing.clear();
		enemyKingFound = false;
		
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (Math.abs(i) != Math.abs(j)) 
					continue;
				searchPath(board, i, j);
			}
		}	
	}
}
