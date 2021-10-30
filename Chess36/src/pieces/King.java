package pieces;

/**
 * @author Michael Nelli - mrn73
 * @author Christopher Naporlee - cmn134
 */

import chess.Board;
import chess.Board.Cell;

/**
 * Represents the King chess piece.
 */
public class King extends ChessPiece {
	
	/**
	 * Initializes a new King chess piece with the given team color.
	 * @param color  of the chess piece
	 */
	public King(Color color) {
		super(color, "King");
	}
	
	/*
	 * ---------------------------
	 * |      Possible Moves     |   
	 * ---------------------------
	 *  (-1, -1) (-1, 0) (-1, +1)
	 *  ( 0, -1)    K	 ( 0, +1)
	 *  (+1, -1) (+1, 0) (+1, +1)
	 */
	public void calculateMoves(Board board) {
		validMoves.clear();
		enemyKingFound = false;
		
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				searchStep(board, i, j);
			}
		}	
		
		Cell rookCell = findRookInDir(board, -1);
		Cell thisCell = this.getPosition();
		if (canCastle(rookCell)) {
			validMoves.add(board.getCell(thisCell.getRow(), thisCell.getCol() - 2));
		}
		rookCell = findRookInDir(board, 1);
		if (canCastle(rookCell)) {
			validMoves.add(board.getCell(thisCell.getRow(), thisCell.getCol() + 2));
		}
	}
	
	/**
	 * Checks for a clear path between the King and Rook to possibly castle.
	 * @param board  that this piece is on
	 * @param xDir  the horizontal direction to search; -1 = left; 1 = right
	 * @return the cell of the Rook, if found
	 */
	private Cell findRookInDir(Board board, int xDir) {
		int row = getPosition().getRow();
		int col = getPosition().getCol();
		int colSteps;
		
		Cell cell;
		colSteps = xDir;
		while ((cell = board.getCell(row, col + colSteps)) != null) {
			ChessPiece other = cell.getPiece();
			if (other == null) {
				colSteps += xDir;
				continue;
			}
			if (other.getName().equals("Rook") && other.getColor() == this.getColor())
				return cell;
			break;
		}
		return null;
	}
	
	/**
	 * Takes a cell that contains a rook and makes sure that neither the rook nor the King have moved.
	 * @param rookCell location of rook
	 * @return whether or not this piece can castle with the given rook
	 */
	private boolean canCastle(Cell rookCell) {
		if (rookCell == null || !(rookCell.getPiece().getNumMoves() == 0 && getNumMoves() == 0))
			return false;
		
		return true;
	}
}
