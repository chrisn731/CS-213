package chess;

/**
 * @author Christopher Naporlee - cmn134
 * @author Michael Nelli - mrn73
 */
import java.util.Scanner;

import pieces.Color;

/**
 * Manages current game state and handles user input
 *
 * <p>
 * Main functionality
 * 	1. Determines who's turn it is
 *	2. Renders board every turn
 *	3. Determines when game ends
 * </p>
 */
public class GameManager {
	/**
	 * Holds the current state of the board.
	 * For more information check {@link chess.Board}
	 */
	private Board board;
	
	/**
	 * Keeps track if the game is currently active and running.
	 */
	private boolean isRunning;
	
	/**
	 * Keeps track if a draw for the current game has been requested.
	 * A draw must be requested by one player, and then confirmed by another
	 * for a draw to be completed.
	 */
	private boolean drawRequested = false;
	
	/**
	 * Holds the color of the current player.
	 */
	private Color colorTurn;
	
	/**
	 * Creates a new Game Manager to manage a chess game.
	 * 
	 * <p>
	 * Also, builds a new board for the game to be played on
	 * </p>
	 */
	public GameManager() {
		this.board = new Board();
		colorTurn = Color.WHITE;
	}
	
	/**
	 * Sets up and starts game loop.
	 * 
	 * <p>
	 * Function sets up basic game state and then begins game loop.
	 * In charge of handing off valid user input to the board and handling
	 * any state changes the board detects.
	 * </p>
	 */
	public void run() {
		isRunning = true;
		Scanner sc = new Scanner(System.in);
		GameException e = null;
		
		while (isRunning) {
			render();
			if (e == GameException.CHECK)
				System.out.println("Check");
			promptUserInput();
			for (;;) {
				String[] inputs = getInput(sc);
				if (inputs == null) {
					System.out.println("Invalid input");
					continue;
				}
				e = board.tryMove(inputs, colorTurn);
				if (e != GameException.INVALID_MOVE)
					break;
				System.out.println("Illegal move, try again");	
				promptUserInput();
			}
			inspectState(e);
			switchTurns();
			System.out.println();
		}
		sc.close();
	}
	
	/**
	 * Inspects a game exception that is formed by doing a board move.
	 * 
	 * @param e	A GameException to inspect
	 */
	private void inspectState(GameException e) {
		switch (e) {
		case NONE:
			break;
		case CHECK: 
			// Handled in main game loop
			break;
		case CHECKMATE:
			System.out.println();
			render();
			System.out.println("Checkmate");
			System.out.println(
				(colorTurn == Color.WHITE ? "White" : "Black") +
				" wins\n"
			);
			isRunning = false;
			break;
		default:
			System.out.println("Unknown game exception!");
			System.exit(1);
			break;
		}
	}
	
	/**
	 * Render the board to the terminal/console.
	 */
	private void render() {
		System.out.println(board);
	}
	
	/**
	 * Print out a statement that prompts the user to enter input to
	 * move a piece.
	 */
	private void promptUserInput() {
		System.out.print(
			(colorTurn == Color.WHITE ? "White's" : "Black's") + 
			" move: "
		);
	}
	
	/**
	 * Switches the turn from the current player's color, to the opposite color.
	 */
	private void switchTurns() {
		colorTurn = colorTurn == Color.WHITE ? Color.BLACK : Color.WHITE;
	}
	
	/**
	 * Fetches input from player through the terminal
	 * 
	 * @param sc Scanner object for terminal input
	 * @return Array of strings that denote each space seperated token
	 */
	private String[] getInput(Scanner sc) {
		String input = sc.nextLine();
		String[] inputs = input.split("\\s");
		if (!isValidInput(inputs))
			return null;
		return inputs;
	}
	
	/**
	 * Checks whether the input from a player is of a valid format
	 * 
	 * @param inputs String array of space seperated tokens
	 * @return Whether or not the input given was in correct format
	 */
	private boolean isValidInput(String[] inputs) {
		/*
		 * An input length of 1 indicates
		 * 	A response to a "draw?" with "draw"
		 *  -- OR --
		 *  A player resigning
		 */
		if (inputs.length == 1) {
			String str = inputs[0];
			if (str.equals("draw")) {
				if (!drawRequested)
					return false;
				System.exit(0);
			}
			
			if (str.equals("resign")) {
				System.out.println(
					(colorTurn == Color.WHITE ? "Black" : "White") +
					" wins"
				);
				System.exit(0);
			}
		}
		
		if (inputs.length < 2 || inputs[0].length() != 2 || inputs[1].length() != 2)
			return false;
		
		/*
		 * Having more than 2 inputs indicates
		 * 	A request for a draw
		 * 	-- OR --
		 * 	The letter that denotes what piece a pawn will be promoted to
		 */
		if (inputs.length > 2) {
			if (inputs[2].equals("draw?"))
				drawRequested = true;
			else if (!inputs[2].matches("[RNBQ]"))
				return false;
		}
		
		/*
		 * Validate that user input for movement has valid words and letters that
		 * are within the bounds of the board.
		 */
		for (int i = 0; i < 2; i++) {
			String str = inputs[i];
			if (str.charAt(0) < 'a' || str.charAt(0) > 'h') {
				return false;
			}
			if (str.charAt(1) < '1' || str.charAt(1) > '8') {
				return false;
			}
		}
		return true;
	}
}