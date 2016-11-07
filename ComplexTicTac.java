import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

public class SimpleChess {
	private final int _TOP = 0;
	private final int _BOTTOM = 5;
	private final char _EMPTY = ' ';
	private final long _TIMELIMIT = (100 * 59); // don't think for more than a
												// minute
	private final int _DEPTHLIMIT = 5; // limit the depth of a search

	private Scanner in;

	private GameBoard board;

	public SimpleChess() {
		this.board = new GameBoard(6);
	}

	/* deprecated *//*
					 * private boolean parseInput(String input) { int currentN,
					 * currentM, newM, newN; // holds moves String[] moves =
					 * input.split(" ");
					 * 
					 * if (moves.length != 4) { return false; // not correct
					 * input } else { try { currentM =
					 * Integer.parseInt(moves[0]); // first value currentN =
					 * Integer.parseInt(moves[1]); // second value newM =
					 * Integer.parseInt(moves[2]); // third value newN =
					 * Integer.parseInt(moves[3]); // fourth value } catch
					 * (Exception e) { // error happened while parsing return
					 * false; } this.board.makeMove(true, currentM, currentN,
					 * newM, newN); // only // parsing // movement // from //
					 * human // player } return false; // something terrible
					 * happened if we got to heres }
					 */

	public void play() {
		this.in = new Scanner(System.in); // open scanner for play
		boolean humanTurn = true;
		while (true) { // game loops
			if (this.board.isComplete()) {
				break; // game over, exit loop
			}
			if (humanTurn) {
				getHumanMove();
			} else {
				this.board = getComputerMove(this.board); // get best computer
															// move
			}
			display();
			humanTurn = !humanTurn;
		}
		this.in.close(); // close scanner
		// TODO: handle win state
	}

	private int evaluate(GameBoard candidate) {
		/*
		 * number of pieces pieces close to you when it is your turn where your
		 * king is
		 */
		int heuristicValue = 0;
		// TODO
		return heuristicValue;
	}

	private int minMax(GameBoard candidate, boolean max, int depth, int alpha, int beta, long start) {
		if ((depth >= _DEPTHLIMIT) || (System.currentTimeMillis() - start >= _TIMELIMIT) || (candidate.isComplete())) {
			return evaluate(candidate); // return the value of this board
		} else if (max) { // if move is maximizing move
			int maxValue = Integer.MIN_VALUE; // as low as we can be, should be
												// replaced immediately
			LinkedList<GameBoard> children = generateChildren(!max, candidate); // get
																				// children
			for (int i = 0; i < children.size(); i++) { // for all children
				int currentValue = minMax(children.get(i), !max, depth + 1, alpha, beta, start); // get
																									// the
																									// value
																									// of
																									// child
				if (currentValue > maxValue) {
					maxValue = currentValue; // update the maxValue
				}
				if (maxValue >= beta) {
					return maxValue; // stop and return value
				}
				if (maxValue > alpha) {
					alpha = maxValue; // update alpha
				}
			}
			return maxValue; // return whatever the highest value was
		} else { // if move is minimizing move
			int minValue = Integer.MAX_VALUE;
			LinkedList<GameBoard> children = generateChildren(!max, candidate); // get
																				// children
			for (int i = 0; i < children.size(); i++) {
				int currentValue = minMax(children.get(i), !max, depth + 1, alpha, beta, start);
				if (currentValue < minValue) {
					minValue = currentValue; // update minValue
				}
				if (minValue <= alpha) {
					return minValue; // stop and return value
				}
				if (minValue < beta) {
					beta = minValue; // update beta
				}
			}
			return minValue; // return lowest value
		}
	}

	private LinkedList<GameBoard> generateChildren(boolean human, GameBoard parent) {
		LinkedList<GameBoard> children = new LinkedList<GameBoard>();

		for (int i = 0; i < 5; i++) { // TODO: why 5?
			children.addAll(parent.getPossibleMoves(human, i));
		}

		return children;
	}

	/* This method is destructive */
	private void getHumanMove() {
		System.out.print("Please make a mew (cM cN nM nM): ");
		int oldM = this.in.nextInt(); // first value
		int oldN = this.in.nextInt(); // second value
		int newM = this.in.nextInt(); // third value
		int newN = this.in.nextInt(); // first value
		this.board.makeMove(true, oldM, oldN, newM, newN); // make the move

		// display(); // show the move
	}

	private GameBoard getComputerMove(GameBoard candidate) {
		LinkedList<GameBoard> children = generateChildren(false, candidate);
		long start = System.currentTimeMillis(); // time started the search
		int maxIndex = 0;
		int maxValue = minMax(children.get(0), false, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, start);

		for (int i = 1; i < children.size(); i++) {
			int currentValue = minMax(children.get(i), false, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, start); // get
			// value
			// of
			// this
			// child

			if (currentValue > maxValue) {
				maxIndex = i; // set new index
				maxValue = currentValue; // set new max value
			}
		}
		// this.board = children.get(maxIndex); //get best computer move
		return (children.get(maxIndex));
	}

	private void display() {
		System.out.println(this.board.toString());// show the board
	}

	// internal class
	private class GameBoard {
		private char[][] board;
		private int size;
		private int[][] computerPieces;
		private int[][] humanPieces;

		private GameBoard(int size) {
			this.board = new char[size][size];
			this.computerPieces = new int[5][2];
			this.humanPieces = new int[5][2];
			this.size = size;
			initBoard(this.board, size);
			System.out.println(this.toString());
		}

		private GameBoard(GameBoard clone) {
			this.size = clone.size;
			this.board = new char[this.size][this.size];
			this.computerPieces = new int[5][2];
			this.humanPieces = new int[5][2];

			for (int i = 0; i < this.size; i++) {
				for (int j = 0; j < this.size; j++) {
					this.board[i][j] = clone.board[i][j]; // clone board pieces
				}
			}

			for (int i = 0; i < 5; i++) { // TODO: why 5?
				this.computerPieces[i][0] = clone.computerPieces[i][0]; // clone
																		// piece
																		// locations
				this.computerPieces[i][1] = clone.computerPieces[i][1]; // clone
																		// piece
																		// locations
				this.humanPieces[i][0] = clone.humanPieces[i][0]; // clone piece
																	// locations
				this.humanPieces[i][1] = clone.humanPieces[i][1]; // clone piece
																	// locations
			}
		}

		private void initBoard(char[][] board, int size) {
			int K = 0; // number of kings placed
			int R = 0; // number of rooks placed
			int B = 0; // number of bishops placed

			int maxK = 1; // 1 king per player
			int maxR = 2; // 2 rooks per player
			int maxB = 2; // 2 bishops per player

			Random random = new Random(); // random number generator

			for (int i = 0; i < this.size; i++) {
				for (int j = 0; j < this.size; j++) {
					this.board[i][j] = _EMPTY;
				}
			}

			// place capital player
			boolean condition = true; // while still need to place pieces
			while (condition) {
				int index = random.nextInt(this.size); // get piece placement
				if (this.board[_TOP][index] == _EMPTY) { // no piece here
					if (K < maxK) {
						this.board[_TOP][index] = 'K'; // place a king
						++K; // increase king count

						this.computerPieces[0][0] = _TOP; // track piece
															// position
						this.computerPieces[0][1] = index; // track piece
															// position

					} else if (R < maxR) {
						this.board[_TOP][index] = 'R'; // place a rook
						++R; // increase rook count
						this.computerPieces[R][0] = _TOP; // track piece
															// position
						this.computerPieces[R][1] = index; // track piece
															// position
					} else if (B < maxB) {
						this.board[_TOP][index] = 'B'; // place a bishop
						++B; // increase bishop count
						this.computerPieces[R + B][0] = _TOP; // track piece
																// position
						this.computerPieces[R + B][1] = index; // track piece
																// position
					} else {
						condition = false; // exit loop
					}
				}
			}

			K = R = B = 0; // reset piece count
			// place lower case player
			condition = true;
			while (condition) {
				int index = random.nextInt(this.size);
				if (this.board[_BOTTOM][index] == _EMPTY) {
					if (K < maxK) {
						this.board[_BOTTOM][index] = 'k';
						this.humanPieces[0][0] = _BOTTOM; // track piece
															// position
						this.humanPieces[0][1] = index; // track piece position
						++K;
					} else if (R < maxR) {
						this.board[_BOTTOM][index] = 'r';
						++R;
						this.humanPieces[R][0] = _BOTTOM; // track piece
															// position
						this.humanPieces[R][1] = index; // track piece position
					} else if (B < maxB) {
						this.board[_BOTTOM][index] = 'b';
						++B;
						this.humanPieces[R + B][0] = _BOTTOM; // track piece
																// position
						this.humanPieces[R + B][1] = index; // track piece
															// position
					} else {
						condition = false; // exit loop
					}
				}
			}
		}

		/* handles making a movement */
		private boolean makeMove(boolean human, int lookUpM, int lookUpN, int newM, int newN) {
			boolean isSuccess = false; // found piece or not

			int pieceIndex = lookUpPieceByLocation(human, lookUpM, lookUpN);

			if (pieceIndex > -1) {
				if (human) {
					if (!isHuman(this.board[newM][newN])) { // don't attack your
															// own pieces
						this.humanPieces[pieceIndex][0] = newM; // track new
																// piece
																// location
						this.humanPieces[pieceIndex][1] = newN; // track new
																// piece
																// location
						isSuccess = true;
					}
				} else if (!isComputer(this.board[newM][newN])) { // don't
																	// attack
																	// your own
																	// pieces
					this.computerPieces[pieceIndex][0] = newM; // track new
																// piece
																// location
					this.computerPieces[pieceIndex][1] = newN; // track new
																// piece
																// location
					isSuccess = true;
				}
			}

			if (isSuccess) { // if there was a successful piece lookup
				if (this.board[newM][newN] != _EMPTY) {
					destroyPiece(!human, pieceIndex); // this move was an attack
														// on the other player
				}
				this.board[newM][newN] = this.board[lookUpM][lookUpN]; // move
																		// piece
				this.board[lookUpM][lookUpN] = _EMPTY; // replace empty position
			}
			return isSuccess;
		}

		private boolean isHuman(char piece) {
			return (piece == 'r' || piece == 'b' || piece == 'k'); // human is
																	// lower
																	// case
																	// player
		}

		private boolean isComputer(char piece) {
			return (piece == 'R' || piece == 'B' || piece == 'K'); // computer
																	// is
																	// capital
																	// player
		}

		// handles attacks when moving pieces
		private void destroyPiece(boolean human, int m) {
			if (human) {
				this.humanPieces[m][0] = -1; // mark as not valid
				this.humanPieces[m][1] = -1; // mark as not valid
			} else {
				this.computerPieces[m][0] = -1; // mark as not valid
				this.computerPieces[m][1] = -1; // mark as not valid
			}
		}

		/* find piece in position array from board position */
		private int lookUpPieceByLocation(boolean human, int m, int n) {
			for (int i = 0; i < 5; i++) { // TODO: why 5?
				if (human) {
					if (this.humanPieces[i][0] == m && this.humanPieces[i][1] == n) {
						return i;
					}
				} else {
					if (this.computerPieces[i][0] == m && this.computerPieces[i][1] == n) {
						return i;
					}
				}
			}
			return -1; // no piece found at this location
		}

		private LinkedList<GameBoard> getPossibleMoves(boolean human, int pieceIndex) {
			LinkedList<GameBoard> children = new LinkedList<GameBoard>(); // empty
																			// child
																			// list
			if (human) {
				if (this.humanPieces[pieceIndex][0] < 0) {
					return children; // child does not exist
				}
			} else {
				if (this.computerPieces[pieceIndex][0] < 0) {
					return children; // child does not exist
				}
			}
			if (pieceIndex == 0) {
				// move King
				children.addAll(this.getKingMoves(human, pieceIndex));
			} else if (pieceIndex == 1 || pieceIndex == 2) {
				// Move Rook
				children.addAll(this.getRookMoves(human, pieceIndex));
			} else if (pieceIndex == 3 || pieceIndex == 4) {
				// Move Bishop
				children.addAll(this.getBishopMoves(human, pieceIndex));
			} else {
				return null; // outside of piece range
			}
			return children;
		}

		private LinkedList<GameBoard> getKingMoves(boolean human, int pieceIndex) {
			LinkedList<GameBoard> children = new LinkedList<GameBoard>();
			int[][] locationsReference = (human) ? this.humanPieces : this.computerPieces; // holds
																							// a
																							// reference
																							// to
																							// the
																							// locations
			int m = locationsReference[pieceIndex][0];
			int n = locationsReference[pieceIndex][1];

			if (m + 1 < this.size) {
				if (n + 1 < this.size) {
					GameBoard child = new GameBoard(this); // clone this
					if (child.makeMove(human, m, n, m + 1, n + 1)) { // if a
																		// legal
																		// move
						children.addLast(child);
					}
				}
				if (n - 1 >= 0) {
					GameBoard child = new GameBoard(this); // clone this
					if (child.makeMove(human, m, n, m + 1, n - 1)) { // if a
																		// legal
																		// move
						children.addLast(child);
					}
				}
				GameBoard child = new GameBoard(this);
				if (child.makeMove(human, m, n, m + 1, n)) { // if a legal move
					children.add(child);
				}
			}
			if (m - 1 >= 0) {
				if (n + 1 < this.size) {
					GameBoard child = new GameBoard(this); // clone this
					if (child.makeMove(human, m, n, m - 1, n + 1)) { // if a
																		// legal
																		// move
						children.addLast(child);
					}
				}
				if (n - 1 >= 0) {
					GameBoard child = new GameBoard(this); // clone this
					if (child.makeMove(human, m, n, m - 1, n - 1)) { // if a
																		// legal
																		// move
						children.addLast(child);
					}
				}
				GameBoard child = new GameBoard(this);
				if (child.makeMove(human, m, n, m - 1, n)) { // if a legal move
					children.add(child);
				}
			}
			if (n + 1 < this.size) { // if a legal move
				GameBoard child = new GameBoard(this);
				if (child.makeMove(human, m, n, m, n + 1)) { // if a legal move
					children.add(child);
				}
			}
			if (n - 1 >= 0) {
				GameBoard child = new GameBoard(this);
				if (child.makeMove(human, m, n, m, n - 1)) { // if a legal move
					children.add(child);
				}
			}
			return children;
		}

		private LinkedList<GameBoard> getRookMoves(boolean human, int pieceIndex) {
			LinkedList<GameBoard> children = new LinkedList<GameBoard>();
			int[][] locationsReference = (human) ? this.humanPieces : this.computerPieces; // holds
																							// a
																							// reference
																							// to
																							// the
																							// locations

			int m = locationsReference[pieceIndex][0];
			int n = locationsReference[pieceIndex][1];
			if (m + 1 < this.size) {
				GameBoard child = new GameBoard(this);
				if (child.makeMove(human, m, n, m + 1, n)) {
					children.addLast(child);
				}
			}
			if (m - 1 >= 0) {
				GameBoard child = new GameBoard(this);
				if (child.makeMove(human, m, n, m - 1, n)) {
					children.addLast(child);
				}
			}
			if (n + 1 < this.size) {
				GameBoard child = new GameBoard(this);
				if (child.makeMove(human, m, n, m, n + 1)) {
					children.addLast(child);
				}
			}
			if (n - 1 >= 0) {
				GameBoard child = new GameBoard(this);
				if (child.makeMove(human, m, n, m, n - 1)) {
					children.addLast(child);
				}
			}
			return children;
		}

		private LinkedList<GameBoard> getBishopMoves(boolean human, int pieceIndex) {
			LinkedList<GameBoard> children = new LinkedList<GameBoard>();
			int[][] locationsReference = (human) ? this.humanPieces : this.computerPieces; // holds
																							// a
																							// reference
																							// to
																							// locations
																							// array

			int m = locationsReference[pieceIndex][0];
			int n = locationsReference[pieceIndex][1];
			if (m + 1 < this.size) {
				if (n + 1 < this.size) {
					GameBoard child = new GameBoard(this); // clone this
					if (child.makeMove(human, m, n, m + 1, n + 1)) { // if a
																		// legal
																		// move
						children.addLast(child);
					}
				}
				if (n - 1 >= 0) {
					GameBoard child = new GameBoard(this); // clone this
					if (child.makeMove(human, m, n, m + 1, n - 1)) { // if a
																		// legal
																		// move
						children.addLast(child);
					}
				}
			}
			if (m - 1 >= 0) {
				if (n + 1 < this.size) {
					GameBoard child = new GameBoard(this); // clone this
					if (child.makeMove(human, m, n, m - 1, n + 1)) { // if a
																		// legal
																		// move
						children.addLast(child);
					}
				}
				if (n - 1 >= 0) {
					GameBoard child = new GameBoard(this); // clone this
					if (child.makeMove(human, m, n, m - 1, n - 1)) { // if a
																		// legal
																		// move
						children.addLast(child);
					}
				}
			}
			return children;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			String delimiter = "--------------------------\n";
			for (int i = 0; i < this.size; i++) {
				sb.append(delimiter);// print row delimiter
				for (int j = 0; j < this.size; j++) {
					sb.append(" | " + this.board[i][j]);
				}
				sb.append(" |\n");
			}
			sb.append(delimiter);
			return sb.toString();
		}

		private boolean isComplete() {
			// if the king is in position -1, it has been killed
			return (this.computerPieces[0][0] < 0) || (this.humanPieces[0][0] < 0);
		}
	}
}
