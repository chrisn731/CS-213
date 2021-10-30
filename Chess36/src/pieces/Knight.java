package pieces;

/**
 * @author Michael Nelli - mrn73
 * @author Christopher Naporlee - cmn134
 */

import chess.Board;

/**
 * Represents the Knight chess piece.
 */
public class Knight extends ChessPiece {

	/**
	 * Initializes a new Knight chess piece with the given team color.
	 * @param color  of the chess piece
	 */
	public Knight(Color color) {
		super(color, "Knight");
	}
	
	/* 
	 * --------------------------------------
	 * |           Possible Moves           |
	 * --------------------------------------
	 * 			(-2, -1)	(-2, +1)
	 * 	(-1, -2)					(-1, +2)
	 * 					 N
	 * 	(+1, -2)					(+1, +2)
	 * 			(+2, -1)	(+2, +1)
	 */
	public void calculateMoves(Board board) {
		validMoves.clear();
		enemyKingFound = false;
		
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (i == 0 || j == 0)
					continue;
				if (Math.abs(i) == Math.abs(j))
					continue;
				searchStep(board, i, j);
			}
		}
	}
	
	/*
	 * Overrides toString() found in ChessPiece due to using the second letter
	 */
	public String toString() {
		char prefix = getColor().toString().toLowerCase().charAt(0);
		char suffix = getName().toUpperCase().charAt(1);
		return Character.toString(prefix) + Character.toString(suffix) + " ";
	}

	

}
