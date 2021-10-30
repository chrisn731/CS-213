package pieces;

/**
 * @author Michael Nelli - mrn73
 * @author Christopher Naporlee - cmn134
 */

import java.util.ArrayList;

import chess.Board;
import chess.Board.Cell;


/**
 * Represents a general Chess Piece
 */
public abstract class ChessPiece {
	/**
	 * Denotes the color of the Piece
	 */
	private final Color COLOR;
	
	/**
	 * Holds the name of the piece's type
	 */
	private final String NAME;
	
	/**
	 * The housing cell that the piece resides within
	 */
	private Cell position;
	
	/**
	 * The number of the last turn the piece moved in.
	 * Example: Piece last moved turn 4 (4 being the value)
	 */
	int lastTurnMoved;
	
	/**
	 * The total number of successful moves this piece has made
	 */
	int numMoves;
	
	/**
	 * A list of all cells that a piece is allowed and can legally move to
	 */
	ArrayList<Cell> validMoves;
	
	/**
	 * A list of all clear cells, forming a path, from the piece to the king
	 */
	ArrayList<Cell> pathToKing;
	
	/**
	 * Denotes if this piece is currently attacking the enemy king
	 */
	boolean enemyKingFound;
	
	/**
	 * Calculates which moves are valid on this chess piece from 
	 * its current position.
	 * 
	 * @param  board that the chess piece is on
	 */
	public abstract void calculateMoves(Board board);
	
	/**
	 * Initializes a new chess piece with the specified color and type.
	 * 
	 * @param  color of the chess piece
	 * @param name of the type of chess piece
	 */
	ChessPiece(Color color, String name) {
		this.COLOR = color;
		this.NAME = name;
		validMoves = new ArrayList<Cell>();
		pathToKing = new ArrayList<Cell>();
	}
	
	/**
	 * Checks if a given destination cell is within this piece's valid moves.
	 * 
	 * @param  dest of this chess piece
	 * @return whether or not this piece can move to dest
	 */
	public boolean canMoveTo(Cell dest) {
		if (validMoves.contains(dest))
			return true;
		return false;
	}
	
	/**
	 * Searches a straight-line path of cells in a given direction until a cell is occupied by another piece or the end of the board is hit.
	 * If an occupied cell can be overtaken by this piece, then that cell will mark the end of the path; otherwise, the previous cell marks the end.  
	 * All cells from the start and end of this path will be added to this piece's valid moves. 
	 * <p>
	 * Additionally, if the enemy King is found within a path, all cells from this piece's position to the enemy King's position will be added in pathToKing. 
	 * The cell after the King, if empty, will also be added in pathToKing to anticipate the King's next moves to determine possible checkmate.
	 * <p>
	 * Example: an i value of 0 and j value of -1 would mean to search the columns to the 
	 * left of this chess piece in the same row.
	 * 
	 * @param board  that this chess piece is on
	 * @param i  offset to the row this chess piece is in. -1 = up; 0 = same; 1 = down
	 * @param j  offset to the column this chess piece is in. -1 = left; 0 = same; 1 = right
	 */
	void searchPath(Board board, int i, int j) {
		int row = position.getRow();
		int col = position.getCol();
		int rowSteps = i;
		int colSteps = j;
		
		Cell cell;
		while ((cell = board.getCell(row + rowSteps, col + colSteps)) != null) {
			ChessPiece other = cell.getPiece();
			if (other != null) {
				if (other.getColor() != this.getColor()) {
					validMoves.add(cell);
					/*
					 * added in this if statement that if we find the king, look one more cell ahead
					 */
					if (other.getName().equals("King")) {
						pathToKing.add(cell);
						cell = board.getCell(row + rowSteps + i, col + colSteps + j);
						if (cell != null)
							pathToKing.add(cell);
						enemyKingFound = true;
					}
				}
				break;
			}
			validMoves.add(cell);
			if (!enemyKingFound) {
				pathToKing.add(cell);
			}
			rowSteps += i;
			colSteps += j;
		}
		if (!enemyKingFound) {
			pathToKing.clear();
		}
	}
	
	/**
	 * Searches a single cell in a given direction. If it is empty or can be overtaken, it is marked
	 * as a valid move. If the piece that can be overtaken is the enemy King, then add that cell in pathToKing.
	 * @param board  that this chess piece is on
	 * @param i  offset to the row this chess piece is in. -i = up i; i = down i
	 * @param j  offset to the column this chess piece is in. -j = left j; j = right j
	 */
	void searchStep(Board board, int i, int j) {
		int row = position.getRow();
		int col = position.getCol();
		
		Cell cell = board.getCell(row + i, col + j);
		if (cell != null) {
			ChessPiece other = cell.getPiece();
			if (other != null && other.getColor() == this.COLOR)
				return;
			if (other != null && other.getColor() != this.COLOR && other.getName().equals("King")) {
				pathToKing.add(cell);
				enemyKingFound = true;
			}
			validMoves.add(cell);
		}
	}
	
	/**
	 * Sets the position of this piece to a given cell.
	 * @param cell  new position
	 */
	public void setPosition(Cell cell) {
		position = cell;
	}
	
	/**
	 * Gets the current cell position of this piece.
	 * @return the cell that contains this piece
	 */
	public Cell getPosition() {
		return position;
	}
	
	/**
	 * Gets the name of the type of this chess piece.
	 * @return the name of this chess piece
	 */
	public String getName() {
		return this.NAME;
	}
	
	/**
	 * Gets the team color of this chess piece.
	 * @return the team of this chess piece
	 */
	public Color getColor() {
		return this.COLOR;
	}
	
	/**
	 * Gets the list of cells that this chess piece can legally move to.
	 * @return arraylist of valid moves
	 */
	public ArrayList<Cell> getMoves() {
		return this.validMoves;
	}
	
	/**
	 * Gets the path to the king.
	 * @return path to king
	 */
	public ArrayList<Cell> getPathToKing() {
		return this.pathToKing;
	}
	
	/**
	 * Sets this chess piece's last turn moved to the current turn of the game.
	 * @param totalMoves  current number of moves made in the game
	 */
	public void setTurnMoved(int totalMoves) {
		lastTurnMoved = totalMoves;
	}
	
	/**
	 * Gets the number of moves already made by this chess piece.
	 * @return number of moves
	 */
	public int getNumMoves() {
		return numMoves;
	}
	
	/**
	 * Increments the number of moves made by this chess piece by 1.
	 */
	public void incrementNumMoves() {
		numMoves++;
	}
	
	/**
	 * Decrements the number of moves mad by this chess piece by 1. 
	 * <p>
	 * This is done in the case of having to undo a move. 
	 */
	public void decrementNumMoves() {
		numMoves--;
	}
	
	/**
	 *  Calculates the ASCII representation of this chess piece.
	 *  @return the string representation of this chess piece
	 */
	public String toString() {
		char prefix = COLOR.toString().toLowerCase().charAt(0);
		char suffix = NAME.charAt(0);
		return Character.toString(prefix) + Character.toString(suffix) + " ";
	}
}
