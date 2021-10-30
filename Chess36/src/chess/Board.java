package chess;

/**
 * @author Christopher Naporlee - cmn134
 * @author Michael Nelli - mrn73
 */
import java.util.ArrayList;

import pieces.*;

/**
 * Board houses the internal data structures of the game.
 *
 * <p>
 * The board has the very important job of finalizing piece movement and 
 * detecting GameExceptions.
 * </p>
 */
public class Board {
	
	/**
	 * Type that makes up a single point on the board
	 */
	public class Cell {
		/**
		 * Piece that is currently on the cell
		 */
		private ChessPiece p;
		
		/**
		 * Denotes the cell being white or not
		 */
		private final boolean IS_WHITE;
		
		/**
		 * Row (or Rank) the cell is contained within
		 */
		private final int row;
		
		/**
		 * Column (or File) that the cell is contained within
		 */
		private final int col;
		
		/**
		 * Creates a new cell type
		 * 
		 * <p>
		 * This is ONLY used by the {@link chess.Board} because only the board is in charge
		 * of creating cells to build itself
		 * </p>
		 * @param isWhite Whether the cell is white or not
		 * @param row Index of the row/rank
		 * @param col Index of the column/file
		 */
		private Cell(boolean isWhite, int row, int col) {
			this.IS_WHITE = isWhite;
			this.row = row;
			this.col = col;
		}
		
		/**
		 * Returns piece in the cell
		 * @return The Chess Piece
		 */
		public ChessPiece getPiece() {
			return p;
		}
		
		/**
		 * Sets the cell to contain a chess piece
		 * @param p The chess piece to put into the cell
		 */
		public void setPiece(ChessPiece p) {
			this.p = p;
			if (p != null) {
				p.setPosition(this);
			}
		}
		
		/**
		 * Gets the row the cell is located in
		 * @return Index of the row
		 */
		public int getRow() {
			return row;
		}
		
		/**
		 * Gets the column the cell is located in
		 * @return Index of the column
		 */
		public int getCol() {
			return col;
		}
		
		/**
		 * Determines if the cell is white
		 * @return Whether or not the cell is a white cell
		 */
		public boolean isWhite() {
			return IS_WHITE;
		}
		
		/**
		 * Prints out the piece that is contained in the cell
		 */
		public String toString() {
			return p.toString();
		}
	}
	
	/* 
	 * 2D ARRAY STORAGE:					DISPLAY:
	 * [0][0] ... [0][7]					[7][0] ... [7][7] 8
	 *  ...		   ...		display as		 ...        ...
	 *  ...		   ...	   ----------->		 ...	    ...
	 *  ...		   ...						 ...        ...
	 * [7][0] ... [7][7]					[0][0] ... [7][0] 1
	 * 										   a          h
	 */
	
	/**
	 * 2-D array of cells to construct the board
	 */
	Cell[][] board;
	
	/**
	 * List of current pieces that are on the board
	 */
	ArrayList<ChessPiece> activePieces;
	
	/**
	 * Counter of how many successful moves have been made
	 */
	int totalMoves = 0;
	
	/**
	 * Creates a new board. Puts all chess pieces onto the board into the
	 * relevant cells.
	 */
	public Board() {
		board = new Cell[8][8];
		activePieces = new ArrayList<ChessPiece>();
		init();
	}
	
	/**
	 * Initializes the chess board with all the needed piece to play
	 */
	private void init() {
		boolean isWhite = true;
		for (int i = 7; i >= 0; i--) {
			for (int j = 0; j < 8; j++) {
				board[i][j] = new Cell(isWhite, i, j);
				isWhite = !isWhite;
			}
			isWhite = !isWhite;
		}
		
		Color c = Color.WHITE;
		initNewPiece(new Rook(c), 7, 0);
		initNewPiece(new Knight(c), 7, 1);
		initNewPiece(new Bishop(c), 7, 2);
		initNewPiece(new Queen(c), 7, 3);
		initNewPiece(new King(c), 7, 4);
		initNewPiece(new Bishop(c), 7, 5);
		initNewPiece(new Knight(c), 7, 6);
		initNewPiece(new Rook(c), 7, 7);
		for (int j = 0; j < 8; j++) {
			initNewPiece(new Pawn(c), 6, j);
		}
		
		c = Color.BLACK;
		initNewPiece(new Rook(c), 0, 0);
		initNewPiece(new Knight(c), 0, 1);
		initNewPiece(new Bishop(c), 0, 2);
		initNewPiece(new Queen(c), 0, 3);
		initNewPiece(new King(c), 0, 4);
		initNewPiece(new Bishop(c), 0, 5);
		initNewPiece(new Knight(c), 0, 6);
		initNewPiece(new Rook(c), 0, 7);
		for (int j = 0; j < 8; j++) {
			initNewPiece(new Pawn(c), 1, j);
		}
		
		calculateAllMoves();
	}
	
	/**
	 * Initializes a new piece into the game. Puts into onto the board and 
	 * adds it to the list of currently active pieces.
	 * 
	 * @param piece The Chess piece to add to the game
	 * @param row The row in which we are to place the piece
	 * @param col The column in which we are to place the piece
	 */
	private void initNewPiece(ChessPiece piece, int row, int col) {
		// Add the pieces to the board and the set of all active pieces
		board[row][col].setPiece(piece);
		activePieces.add(piece);
	}
	
	/**
	 * Attempts to do the requested move from the player
	 * 
	 * @param inputs The current player's input
	 * @param playerColor The color of whoever's turn it is
	 * @return A notification of if the move was successful
	 */
	public GameException tryMove(String[] inputs, Color playerColor) {
		Cell[] endPoints = mapToBoard(inputs);
		ChessPiece requestedPiece = endPoints[0].getPiece();
		
		if (requestedPiece == null || requestedPiece.getColor() != playerColor)
			return GameException.INVALID_MOVE;
		
		Cell start = endPoints[0], dest = endPoints[1];
		if (!requestedPiece.canMoveTo(dest))
			return GameException.INVALID_MOVE;
		
		ChessPiece overTaken = null;
		boolean specialMove = isSpecialMove(requestedPiece, start, dest);
		if (specialMove) {
			// Do necessary preparations for special moves
			overTaken = handleSpecialMoveCase(requestedPiece, endPoints, inputs);
			
			// If we castled we should not remove the rook
			if (!(overTaken instanceof Rook) || !(requestedPiece instanceof King))
				killPiece(overTaken);
		} else {
			// Nothing fancy, just do the move.
			overTaken = doMove(start, dest);
			killPiece(overTaken);
		}
		
		// A piece moving allows opportunities for other pieces to move.
		calculateAllMoves();
		
		if (movePutsSelfInCheck(playerColor)) {
			// The move put yourself in check, undo it.
			undoMove(start, dest, overTaken, specialMove);
			calculateAllMoves();
			return GameException.INVALID_MOVE;
		}
		
		if (movePutsEnemyInCheck(playerColor)) {
			Color enemyColor = playerColor == Color.WHITE ? Color.BLACK : Color.WHITE;
			return enemyInCheckMate(enemyColor) ? GameException.CHECKMATE : GameException.CHECK;
		}

		/*
		 * At this point
		 * 
		 * 1. No one is in check
		 * 2. We have finalized the move that it is legal
		 */
		return GameException.NONE;
	}
	
	/**
	 * Checks if the enemy is currently in checkmate.
	 * 
	 * @param enemyColor The color of the enemy
	 * @return Whether the enemy in in checkmate
	 */
	private boolean enemyInCheckMate(Color enemyColor) {
		/*
		 * This function is a bit complicated, but it is broken up into two
		 * main steps. If either step finds the king can get out of check then
		 * the game is not over thus not checkmate.
		 */
		
		/*
		 * Step 1: Check if the king has any valid moves out of check
		 * This can be done in two ways:
		 * 	1) The king moves onto a space not in check.
		 * 	2) The king can overtake whoever put him into check.
		 */
		Cell kingLocation = kingColorLocation(enemyColor);
		/*
		 * We want to be able to iterate through king moves, without
		 * it being modified by calculateAllMoves()
		 */
		ArrayList<Cell> kingMoves = new ArrayList<Cell>(kingLocation.getPiece().getMoves());
		for (Cell c : kingMoves) {
			/* First try to move the king to a valid spot */
			ChessPiece taken = doMove(kingLocation, c);
			killPiece(taken);
			calculateAllMoves();
			
			/* Determine if that move can save him */
			boolean safeMoveAvailable = !movePutsSelfInCheck(enemyColor);
			undoMove(kingLocation, c, taken, false);
			calculateAllMoves();
			if (safeMoveAvailable)
				return false;
		}
		
		/*
		 * Step 2: King can not move, need a friendly piece to come save him.
		 * Therefore, we need to check either: 
		 * 	-> A piece can block the attacker from seeing the king
		 * 	-> A piece can kill off the attacker
		 */
		int numAttackers = 0;
		boolean isSaved = false;
		for (ChessPiece potentialAttacker : activePieces) {
			if (potentialAttacker.getColor() == enemyColor || potentialAttacker.getPathToKing().isEmpty())
				continue;

			numAttackers++;
			if (numAttackers > 1) {
				/*
				 * If the king cannot move and two pieces see him, it's impossible
				 * to get him out of check within one move. Game over.
				 */
				return true;
			}
			for (ChessPiece potentialSavior : activePieces) {
				if (potentialSavior.getColor() != enemyColor || potentialSavior instanceof King) 
					continue;
				
				isSaved = false;
				ArrayList<Cell> saviorMoves = potentialSavior.getMoves();
				for (Cell attackedCell : potentialAttacker.getPathToKing()) {
					if (saviorMoves.contains(attackedCell) ||
						saviorMoves.contains(potentialAttacker.getPosition())) {
						
						/*
						 * A potential savior can either block the check, or take
						 * the piece that put the king in check. This attacker can
						 * now be disregarded.
						 */
						numAttackers--;
						isSaved = true;
						break;
					}
				}
				if (isSaved)
					break;
			}
		}
		return numAttackers > 0;
	}

	/**
	 * Handles special movement cases for chess pieces.
	 * <p>
	 * 	Special movements include:
	 * 	EnPassant, Pawn Promotion, and Castling 
	 * @param requestedPiece The piece that is requesting to move
	 * @param endPoints The set of cells that denote the start and end of the move
	 * @param input User input from the requested move
	 * @return Any OTHER chess piece that was affected by the requested piece to move
	 */
	private ChessPiece handleSpecialMoveCase(ChessPiece requestedPiece, Cell[] endPoints, String[] input) {
		Cell start = endPoints[0], dest = endPoints[1];
		if (requestedPiece instanceof Pawn) {
			int destCol = dest.getCol(), startRow = start.getRow();
			
			if (dest.row == 0 || dest.row == 7) {
				// Pawn Promotion
				ChessPiece promoted;
				if (input.length == 3) {
					switch (input[2]) {
					case "R":
						promoted = new Rook(requestedPiece.getColor());
						break;
					case "N":
						promoted = new Knight(requestedPiece.getColor());
						break;
					case "B":
						promoted = new Bishop(requestedPiece.getColor());
						break;
					default:
					case "Q":
						promoted = new Queen(requestedPiece.getColor());
						break;
					}
				} else {
					promoted = new Queen(requestedPiece.getColor());
				}
				// Remove pawn from active pieces
				killPiece(start.getPiece());
				// Put our new promoted piece on the board
				start.setPiece(promoted);
				// Add the promoted piece to active Pieces
				activePieces.add(promoted);
				// Return any piece we took over
				return doMove(start, dest);
			} else {
				// EnPassant
				Cell neighbor = board[startRow][destCol];
				ChessPiece taken = neighbor.getPiece();
				
				/*
				 * By the time we get here we can just run straight through
				 * without checks. We know that the move is at least legal
				 * and there is some pawn that we will take right next to us.
				 */
				neighbor.setPiece(null);
				doMove(start, dest);
				return taken;
			}
		} else if (requestedPiece instanceof King) {
			int destRow = dest.getRow(), destCol = dest.getCol();
			int newRookCol;
			int currRookCol;
			if (destCol == 2) {
				newRookCol = 3;
				currRookCol = 0;
			} else {
				newRookCol = 5;
				currRookCol = 7;
			}
			Cell toCastle = board[destRow][currRookCol];
			ChessPiece rook = toCastle.getPiece();
			board[destRow][currRookCol].setPiece(null);
			board[destRow][newRookCol].setPiece(rook);
			rook.incrementNumMoves();
			rook.setTurnMoved(totalMoves);
			doMove(start, dest);
			return rook;
		}
		return null;
	}

	/**
	 * Determines if the move requested will result in a special move and must
	 * be handled differently.
	 * 
	 * @param requestedPiece The piece that is requesting to move
	 * @param start The starting cell of the move
	 * @param dest The destination cell of the move
	 * @return Whether the requested move must be handled as a special case
	 */
	private boolean isSpecialMove(ChessPiece requestedPiece, Cell start, Cell dest) {
		if (requestedPiece instanceof Pawn) {
			// EnPassant & Pawn Promotion
			int destCol = dest.getCol(), startCol = start.getCol(), startRow = start.getRow();
			
			if (dest.row == 7 || dest.row == 0) {
				// We are promoting our pawn
				return true;
			}
			// We are not promoting our pawn, so check for EnPassant...
			
			// Check if the pawn only moved forward
			if (destCol == startCol) {
				return false;
			}
			
			ChessPiece neighborPiece = board[startRow][destCol].getPiece();
			/*
			 * If the piece next to us is a pawn AND the destination is empty
			 * then we know for sure that we are doing EnPassant. If the
			 * destination was not empty, then we are just doing a regular
			 * piece capture.
			 */
			if (neighborPiece instanceof Pawn && dest.getPiece() == null) {
				return true;
			}
		} else if (requestedPiece instanceof King) {
			int distX = dest.getCol() - start.getCol();
			return Math.abs(distX) == 2;
		}
		return false;
	}

	/**
	 * Actually executes the movement of the piece from one cell to another
	 * 
	 * @param start The starting cell of the move
	 * @param dest The destination cell of the move
	 * @return A chess piece that was "taken" by the move. If no piece taken, null.
	 */
	private ChessPiece doMove(Cell start, Cell dest) {
		ChessPiece overTaken = dest.getPiece();
		
		/*
		 * Heart of the doMove function. All checks are already done.
		 * If something bad happens from this we can simply __undoMove()
		 */
		dest.setPiece(start.getPiece());
		start.setPiece(null);
		dest.getPiece().setTurnMoved(totalMoves);
		dest.getPiece().incrementNumMoves();
		totalMoves++;
		return overTaken;
	}
	
	/**
	 * Undoes a move that was executed by doMove()
	 * 
	 * @param start The starting cell of the movement
	 * @param dest The ending cell of the movement
	 * @param old The old ChessPiece that was manipulated by the called upon piece
	 * @param special Denotes if this movement was a special move
	 */
	private void undoMove(Cell start, Cell dest, ChessPiece old, boolean special) {
		ChessPiece mover = dest.getPiece();
		
		if (special) {
			if (mover instanceof Pawn) {
				// The piece that moved is a pawn and did EnPassant
				int destCol = dest.getCol(), startRow = start.getRow();
				Cell takenCell = board[startRow][destCol];
					
				/*
				 * Reset the EnPassant cell, and return the old taken piece back
				 * to the neighboring cell. Since we returned the piece back
				 * set piece to old so the rest of the cleanup goes smoothly.
				 */
				takenCell.setPiece(old);
				activePieces.add(old);
				old = null;
			} else if (mover instanceof King) {
				int oldRookCol = dest.getCol() == 2 ? 0 : 7;
				board[dest.getRow()][old.getPosition().getCol()].setPiece(null);
				board[dest.getRow()][oldRookCol].setPiece(old);
				
				old.decrementNumMoves();
				old.setTurnMoved(totalMoves - 1);
				old = null;
			} else {
				// Undo Pawn Promotion
				killPiece(mover);
				mover = new Pawn(mover.getColor());
				activePieces.add(mover);
			}
		}
		
		if (old != null) {
			/*
			 * This check is necessary because of the special moves.
			 * 	-> A failed EnPassant will add back its own piece.
			 *  -> A failed castle does "overtake" any piece. However, we use
			 *  	@old as a placeholder of the rook.
			 */
			activePieces.add(old);
		}
		
		/*
		 * Reset the pieces to their original state. Return the destination
		 * piece back to start, and return the old taken piece to the
		 * destination.
		 */
		start.setPiece(mover);
		dest.setPiece(old);
		totalMoves--;
		start.getPiece().setTurnMoved(totalMoves);
		start.getPiece().decrementNumMoves();
	}
	
	/**
	 * Removes a piece from the set of currently active pieces on the board
	 * 
	 * @param taken The piece that was taken off the board
	 */
	private void killPiece(ChessPiece taken) {
		if (taken != null) {
			activePieces.remove(taken);
		}
	}
	
	/**
	 * Determines if a move put the enemy into check
	 * @param playerColor The color of the player that is doing the move
	 * @return Whether the enemy in now in check
	 */
	private boolean movePutsEnemyInCheck(Color playerColor) {
		Color enemyColor = playerColor == Color.WHITE ? Color.BLACK : Color.WHITE;
		return isKingAttackedByColor(kingColorLocation(enemyColor), playerColor);
	}
	
	/**
	 * Determines if a player's move puts themselves into check
	 * @param playerColor The color of the player that is doing the move
	 * @return Whether the player is now in check
	 */
	private boolean movePutsSelfInCheck(Color playerColor) {
		Color enemyColor = playerColor == Color.WHITE ? Color.BLACK : Color.WHITE;
		return isKingAttackedByColor(kingColorLocation(playerColor), enemyColor);
	}
	
	/**
	 * Locates the cell that a king is located within
	 * 
	 * @param colorToFind The color of the King
	 * @return The cell in which the king is located
	 */
	private Cell kingColorLocation(Color colorToFind) {
		for (ChessPiece piece : activePieces) {
			if (piece.getColor() == colorToFind && piece instanceof King) {
				return piece.getPosition();
			}
		}
		/*
		 * This should _never_ happen, but here to stop the compiler from complaining.
		 */
		return null;
	}
	
	/**
	 * Determines if a king is currently being attacked by an enemy teams pieces
	 * @param kingLocation The cell that the king is currently in
	 * @param attacker The color of the attacking team
	 * @return Whether the king is being attacked
	 */
	private boolean isKingAttackedByColor(Cell kingLocation, Color attacker) {
		for (ChessPiece piece : activePieces) {
			if (piece.getColor() == attacker && piece.getMoves().contains(kingLocation)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Recalculates all possible moves for every active piece on the board
	 */
	private void calculateAllMoves() {
		for (ChessPiece piece : activePieces) {
			piece.calculateMoves(this);
		}
	}
	
	/**
	 * Sets a cell to contain a chess piece
	 *  
	 * @param row The row/rank the cell is in
	 * @param col The column/file the cell is in
	 * @param p The piece to place into the cell
	 */
	public void setCell(int row, int col, ChessPiece p) {
		board[row][col].setPiece(p);
	}
	
	/**
	 * Fetches a cell from the board
	 * 
	 * @param row The row/rank the cell is in
	 * @param col The column/file the cell is in
	 * @return The requested cell on the board
	 */
	public Cell getCell(int row, int col) {
		if (!(row >= 0 && row < 8 && col >= 0 && col < 8))
			return null;
		return board[row][col];
	}
	
	/**
	 * Gets the current total amount of successful moves within the game
	 * @return The number of successful moves so far
	 */
	public int getTotalMoves() {
		return totalMoves;
	}
	
	/**
	 * Maps a set of inputs to two cells on the board
	 * @param inputs The input from the user
	 * @return Two cells mapped from inputs
	 */
	private Cell[] mapToBoard(String[] inputs) {
		int startRow = 7 - (inputs[0].charAt(1) - '1');
		int startCol = inputs[0].charAt(0) - 'a';
		int destRow = 7 - (inputs[1].charAt(1) - '1');
		int destCol = inputs[1].charAt(0) - 'a';
		Cell[] r = {board[startRow][startCol], board[destRow][destCol]};
		return r;
	}
	
	/**
	 * Prints out the board and all pieces to the console.
	 */
	public String toString() {
		String r = "";
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j].getPiece() == null) {
					r += board[i][j].isWhite() ? "## " : "   ";
				} else {
					r += board[i][j];
				}
			}
			r += (8 - i) + "\n";
		}
		r += " a  b  c  d  e  f  g  h\n";
		return r;
	}
}