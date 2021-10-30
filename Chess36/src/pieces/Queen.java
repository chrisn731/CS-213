package pieces;

/**
 * @author Michael Nelli - mrn73
 * @author Christopher Naporlee - cmn134
 */

import chess.Board;

/**
 * Represents the Queen chess piece.
 */
public class Queen extends ChessPiece {

	/**
	 * Initializes a new Queen chess piece with the given team color.
	 * @param color  of the chess piece
	 */
	public Queen(Color color) {
		super(color, "Queen");
	}

	/*
	 * ---------------------------------------------
	 * |              Possible Moves               |   
	 * ---------------------------------------------
	 *  .                    .                    .
	 *      .                .                .
	 *          .            .            .
	 * 	         (-1, -1) (-1, 0) (-1, +1)
	 *     . . . ( 0, -1)    Q	  ( 0, +1) . . .
	 *           (+1, -1) (+1, 0) (+1, +1)
	 *          .            .            .
	 *      .                .                .   
	 *  .                    .                    .
	 */
	public void calculateMoves(Board board) {
		validMoves.clear();
		pathToKing.clear();
		enemyKingFound = false;
		
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				searchPath(board, i, j);
			}
		}	
	}
}
