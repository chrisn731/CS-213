package pieces;

/**
 * @author Michael Nelli - mrn73
 * @author Christopher Naporlee - cmn134
 */

import chess.Board;

/**
 * Represents the Rook chess piece.
 */
public class Rook extends ChessPiece {

	/**
	 * Initializes a new Rook chess piece with the given team color.
	 * @param color  of the chess piece
	 */
	public Rook(Color color) {
		super(color, "Rook");
	}
	
	/*
	 * -----------------------------------
	 * |          Possible Moves         |   
	 * -----------------------------------
	 *                  .
	 *                  .
	 *                  .
	 * 			     (-1, 0)
	 *	. . . (0, -1)   R	(0, +1) . . .
	 *  		     (+1, 0)
	 *                  .
	 *                  .
	 *                  .
	 */
	public void calculateMoves(Board board) {
		validMoves.clear();
		pathToKing.clear();
		enemyKingFound = false;
		
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (Math.abs(i) == Math.abs(j)) 
					continue;
				searchPath(board, i, j);
			}
		}
	}
}
