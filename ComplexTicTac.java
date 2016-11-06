import java.util.LinkedList;
import java.util.Scanner;

public class ComplexTicTac {

	private final char _EMPTY = ' ';
	private final char _HUMAN = 'O';
	private final char _COMPUTER = 'X';

	private final int _MIN = 0;
	private final int _MAX = 1;
	private final int _LIMIT = 8;
	private final long _MAXTIME = 1000 * 59; //computer should not think for more than a minute

	private GameBoard board;
	private int size;
	private Scanner input;
	public ComplexTicTac(int size) {
		this.size = size;
		this.board = new GameBoard(size);
	}

	public void play() {
		boolean isPlayerMove = true;
		this.input = new Scanner(System.in);
		while(true) { //game loop
			if(this.board.isComplete()) {
				display(true);
				break;
			}
			if(isPlayerMove) {
				this.board.makeMove(getPlayerMove());
			} else {
				this.board.makeMove(getComputerMove());
			}
			isPlayerMove = !isPlayerMove; //toggle turn
		}
		this.input.close();
	}

	private void display(boolean end) {
		StringBuilder sb = new StringBuilder();
		int humanScore = this.board.score(_HUMAN);
		int computerScore = this.board.score(_COMPUTER);
		String winner = "";
		
		sb.append(this.board.toString());
		sb.append("Player Score: " + humanScore);
		sb.append("\nComputer Score: " + computerScore);
		System.out.println(sb.toString());
		if(end) {
			if(humanScore > computerScore){
				winner = "Human Wins!\n";
			} else if(computerScore > humanScore) {
				winner = "Computer Wins!\n";
			} else {
				winner = "Draw Game!\n";
			}
		}
		System.out.println(winner);
	}
	
	private int evaluate(GameBoard candidate, int depth) {
		int heuristicValue = (candidate.score(_COMPUTER)) - (candidate.score(_HUMAN)) - depth;
		if(heuristicValue == 0) {
			//return evaluateDraw(_COMPUTER) - evaluateDraw(_HUMAN);
			heuristicValue = candidate.countThrees(_COMPUTER) - candidate.countThrees(_HUMAN);
		}
		return heuristicValue * 3;
	}

	private int evaluateDraw(char player) {
		int count = 0;
		for(int i = 0; i < this.size; i++) {
			for(int j = 0; j < this.size; j++) {
				if(this.board.board[i][j] != player) {
					continue;
				} else {
					if(i > 0) {
						count += (this.board.board[i-1][j] == _EMPTY) ? 1 : 0;
					}
					if(i+1 < this.size) {
						count += (this.board.board[i+1][j] == _EMPTY) ? 1 : 0;
					}
					if(j > 0) {
						count += (this.board.board[i][j-1] == _EMPTY) ? 1 : 0;
					}
					if(j+1 < this.size) {
						count += (this.board.board[i][j+1] == _EMPTY) ? 1 : 0;
					}
				}
			}
		}
		return count;
	}
	
	private String getPlayerMove() {
		display(false);
		String prompt = "Please make a move: ";
		String move = _HUMAN + ":"; //add human player symbol
		System.out.print(prompt);
		int m = this.input.nextInt(); //get row move
		int n = this.input.nextInt(); //get column move
		move += m + ":" + n; //add moves to move string
		if(this.board.board[m][n] != _EMPTY) {
			move = getPlayerMove();
		}
		return move;
	}

	private String getComputerMove() {
		LinkedList<GameBoard> children = generateChildren(this.board, _COMPUTER);
		int maxIndex = -1;
		int maxValue = Integer.MIN_VALUE; //minMax(children.get(0), _MIN, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
		long start = System.currentTimeMillis();
		for(int i = 0; i < children.size(); i++) {
			if((System.currentTimeMillis() - start) > _MAXTIME){
				System.out.println("TIME LIMIT REACHED");
				break;
			}
			int currentValue = minMax(children.get(i), _MIN, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
			System.out.println(maxIndex);
			if(currentValue > maxValue){
				//System.out.println(maxValue);
				maxIndex = i;
				maxValue = currentValue;
				//System.out.println(children.get(maxIndex).toString());
			} /*else if(currentValue == maxValue) {
				System.out.println(currentValue);
				*/System.out.println(children.get(i));/*
				System.out.println("C: " + children.get(i).score(_COMPUTER));
				System.out.println("H: " + children.get(i).score(_HUMAN));
			}*/
		}
		return (children.get(maxIndex).getLastMove()); //mark last move
	}

	private int minMax(GameBoard candidate, int level, int depth, int alpha, int beta) {
		/* Don't take too long to make a move
		 * Don't go past the depth limit
		 * Don't try to make a move on a complete board
		 */
		if( depth >= _LIMIT || candidate.isComplete()) { 
			return evaluate(candidate, depth);
		} else if(level == _MAX) {
			int maxValue = Integer.MIN_VALUE;
			
			LinkedList<GameBoard> children = generateChildren(candidate, _COMPUTER);
			
			for(int i = 0; i < children.size(); i++){
				int currentValue = minMax(children.get(i), _MIN, depth+1, alpha, beta);
				if(currentValue > maxValue){
					maxValue = currentValue;
				}
				if(maxValue >= beta){
					return maxValue; 
				}
				if(maxValue > alpha) {
					alpha = maxValue;
				}
			}
			return maxValue;
		} else {
			int minValue = Integer.MAX_VALUE;
			
			LinkedList<GameBoard> children = generateChildren(candidate, _HUMAN);
			
			for(int i = 0; i < children.size(); i++){
				int currentValue = minMax(children.get(i), _MAX, depth+1, alpha, beta);
				if(currentValue < minValue){
					minValue = currentValue;
				}
				if(minValue <= alpha){
					return minValue;
				}
				if(minValue < beta){
					beta = minValue;
				}
			}
			return minValue;
		}
	}

	private LinkedList<GameBoard> generateChildren(GameBoard parent, char playerSymbol) {
		LinkedList<GameBoard> children = new LinkedList<GameBoard>();

		for(int i = 0; i < this.size; i++){
			for(int j = 0; j < this.size; j++){
				if(parent.board[i][j] == _EMPTY){
					GameBoard child = new GameBoard(parent);
					child.makeMove("" + playerSymbol + ":" + i + ":" + j);
					children.addLast(child);
				}
			}
		}
		return children;
	}

	private class GameBoard {
		private char[][] board;
		private int size;
		private String lastMove;

		private GameBoard(int size) {
			this.size = size;

			this.board = new char[size][size];

			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					this.board[i][j] = _EMPTY;
				}
			}
			
			this.lastMove = "";
		}

		private GameBoard(GameBoard clone) {
			this.size = clone.size;
			this.board = new char[size][size];
			this.lastMove = clone.lastMove;

			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					this.board[i][j] = clone.board[i][j]; // copy values
				}
			}
		}

		private void makeMove(String moveString) {
			this.lastMove = moveString;
			String[] components = moveString.split(":");
			if(components.length != 3){
				return; //error
			} else {
				int m = Integer.parseInt(components[1]); //place in row
				int n = Integer.parseInt(components[2]); //place in column
				this.board[m][n] = components[0].charAt(0); //player symbol 
			}
		}
		
		private String getLastMove(){
			return this.lastMove;
		}
		
		private int score(char player) {
			return (countTwos(player) + countThrees(player));
		}

		private int countTwos(char player) {
			int count = 0;
			for (int i = 0; i < this.size; i++) {
				for (int j = 0; j < this.size; j++) {
					if (this.size > i + 1) { // don't overflow rows
						// check for two in a row
						if (player == this.board[i][j] && player == this.board[i + 1][j]) {
							count += 2;
						}
					}
					if (this.size > j + 1) { // don't overflow columns
						// check for two in a column
						if (player == this.board[i][j] && player == this.board[i][j + 1]) {
							count += 2;
						}
					}
				}
			}
			return count;
		}

		private int countThrees(char player) {
			int count = 0;
			for (int i = 0; i < this.size; i++) {
				for (int j = 0; j < this.size; j++) {
					if (this.size > i + 2) { // don't overflow rows
						// check for two in a row
						if (player == this.board[i][j] && player == this.board[i + 1][j]
								&& player == this.board[i + 2][j]) {
							count += 3;
						}
					}
					if (this.size > j + 2) { // don't overflow columns
						// check for three in a column
						if (player == this.board[i][j] && player == this.board[i][j + 1]
								&& player == this.board[i][j + 2]) {
							count += 3;
						}
					}
				}
			}
			return count;
		}

		private boolean isComplete() {
			for (int i = 0; i < this.size; i++) {
				for (int j = 0; j < this.size; j++) {
					if (this.board[i][j] == _EMPTY) {
						return false; // and empty spot was found
					}
				}
			}
			return true; // no empty spot was found
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < this.size; i++) {
				for(int j=0; j < this.size; j++) {
					sb.append(this.board[i][j]);
					sb.append(" ");
				}
				sb.append("|\n");
			}
			return sb.toString();
		}
	}
}
