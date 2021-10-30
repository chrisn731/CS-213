package pieces;

/**
 * @author Michael Nelli - mrn73
 * @author Christopher Naporlee - cmn134
 */

import chess.Board;
import chess.Board.Cell;

/**
 * Represents the pawn chess piece.
 */
public class Pawn extends ChessPiece {

	/**
	 * Initializes a new pawn chess piece with the given team color.
	 * @param color  of the chess piece
	 */
	public Pawn(Color color) {
		super(color, "pawn");
	}
	
	public void calculateMoves(Board board) {
		validMoves.clear();
		pathToKing.clear();
		int row = getPosition().getRow();
		int col = getPosition().getCol();
		int dir = (getColor() == Color.WHITE) ? -1 : 1;
		
		/*
		 * Checks if pawn can move forward one or two spaces.
		 */
		if (board.getCell(row + dir, col).getPiece() == null) {
			validMoves.add(board.getCell(row + dir, col));
			if (lastTurnMoved == 0 && board.getCell(row + (2 * dir), col).getPiece() == null)
				validMoves.add(board.getCell(row + (2 * dir), col));
		}
		
		/*
		 * Left-Diagonal overtake or left-side enpassant.
		 * Ensures pawns in column A don't look to overtake out of bounds.
		 */
		Cell diag = board.getCell(row + dir, col - 1);
		if (diag != null) {
			ChessPiece other = diag.getPiece();
			if (other != null && other.getColor() != this.getColor()) {
				validMoves.add(diag);
				if (other.getName().equals("King"))
					pathToKing.add(diag);
			}
			if (other == null) {
				Cell leftCell = board.getCell(row, col - 1);
				if (leftCell != null && canEnpassant(board, leftCell)) {
					validMoves.add(diag);
				}
			}
		}
		
		/*
		 * Right-Diagonal overtake or right-side enpassant.
		 * Ensures pawns in column H don't look to overtake out of bounds.
		 */
		diag = board.getCell(row + dir, col + 1);
		if (diag != null) {
			ChessPiece other = diag.getPiece();
			if (other != null && other.getColor() != this.getColor()) {
				validMoves.add(diag);
				if (other.getName().equals("King"))
					pathToKing.add(diag);
			}
			if (other == null) {
				Cell leftCell = board.getCell(row, col + 1);
				if (leftCell != null && canEnpassant(board, leftCell)) {
					validMoves.add(diag);
				}
			}
		}
	}

	/**
	 * Determines if the pawn next to this pawn can be overtaken through enpassant.
	 * @param board  that this piece is on.
	 * @param side  chess piece next to this piece
	 * @return whether or not this pawn can enpassant
	 */
	private boolean canEnpassant(Board board, Cell side) {
		ChessPiece other = side.getPiece();
		if (other == null || other.getColor() == this.getColor()) {
			return false;
		}
		if (other instanceof Pawn && other.numMoves == 1 && other.lastTurnMoved == board.getTotalMoves() - 1) {
			if (other.getColor() == Color.WHITE && other.getPosition().getRow() == 4) {
				return true;
			}
			if (other.getColor() == Color.BLACK && other.getPosition().getRow() == 3) {
				return true;
			}
		}
		return false;
	}
}
