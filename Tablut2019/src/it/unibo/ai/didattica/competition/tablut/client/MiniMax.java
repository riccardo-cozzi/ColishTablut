package it.unibo.ai.didattica.competition.tablut.client;

import java.time.LocalTime;
import java.util.LinkedList;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.exceptions.ActionException;
import it.unibo.ai.didattica.competition.tablut.exceptions.BoardException;
import it.unibo.ai.didattica.competition.tablut.exceptions.CitadelException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ClimbingCitadelException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ClimbingException;
import it.unibo.ai.didattica.competition.tablut.exceptions.DiagonalException;
import it.unibo.ai.didattica.competition.tablut.exceptions.OccupitedException;
import it.unibo.ai.didattica.competition.tablut.exceptions.PawnException;
import it.unibo.ai.didattica.competition.tablut.exceptions.StopException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ThroneException;

public class MiniMax {

	public static final int WIN = 3;
	public static final int LOOSE = -3;
	public static final int DRAW = -1;
	public static final int NEUTRAL = 0;
	
	public static final int INITB = 16;
	public static final int INITW = 8;

	public  int maxUtility = 0;
	private Game game;
	private String AIColor;
	private String OpColor;
	private Integer maxDepth = 2;
	private LinkedList<Action> eligibleActions;
	

	private int AIPawns = 0;
	private int opponentPawns = 0;

	/**
	 * 
	 * @param game
	 * @param player player which the CPU wants to win
	 */
	public MiniMax(Game game, Turn player) {
		super();
		this.game = game;
		this.AIColor = player.toString();
		if (player.equals(Turn.WHITE)) {
			AIPawns = INITW;
			opponentPawns = INITB;
			OpColor = Turn.BLACK.toString();
		} else {
			AIPawns = INITB;
			opponentPawns = INITW;
			OpColor = Turn.WHITE.toString();
		}
		this.eligibleActions = new LinkedList<Action>();
	}


	public Action minimaxDecision(State state) {
		LocalTime start = LocalTime.now();

		updatePawns(state);
		this.eligibleActions.clear();
		Integer depth = 0;
		LinkedList<State> successors = successors(state, true);
		int max = Integer.MIN_VALUE;
		int bestMoveIndex = -1;
		int v;
		int counter = 0;
		for (State s : successors) {
			v = minValue(s, depth -1);

			if (v > max) {
				max = v;
				bestMoveIndex = counter;
			}
			counter ++;
		}
		//System.out.println("max: " + max);
		LocalTime end = LocalTime.now();
		System.out.println("\n\n\n___________\nmossa in circa : " + ((end.toSecondOfDay() - start.toSecondOfDay())) + " secondi" + " con valore: " + max);
		return this.eligibleActions.get(bestMoveIndex);

	}

	public int maxValue(State state, Integer depth) {
		depth = depth + 1;
		if (terminalTest(state) || depth == maxDepth) return utility(state) - depth;
		int v = Integer.MIN_VALUE;

		for (State s : successors(state, false)) 			
			v = Math.max(v, minValue(s, depth));

		return v + countCapturedPawns(state);
	}

	public int minValue(State state, Integer depth) {
		depth = depth + 1;
		if (terminalTest(state) || depth == maxDepth) return utility(state) - depth;
		int v = Integer.MAX_VALUE;

		for (State s : successors(state, false)) 
			v = Math.min(v, maxValue(s, depth));

		return v + countCapturedPawns(state);
	}

	public boolean terminalTest(State state) {

		if (state.getTurn().equals(Turn.BLACKWIN) ||
				state.getTurn().equals(Turn.WHITEWIN) || 
				state.getTurn().equals(Turn.DRAW)) {
			//System.out.println("TURNO TERMINALE: " + state.getTurn());
			return true;
		} else return false;

	}


	/**
	 * 
	 * @param state
	 * @return
	 */
	public int utility(State state) {
		String turn = state.getTurn().toString();
		if (AIColor.equals(Turn.BLACK.toString())) {
			if (turn.equals(Turn.BLACKWIN.toString())) {
				return WIN;
			}
			if (turn.equals(Turn.WHITEWIN.toString())) {
				return LOOSE;
			}
		}

		if (AIColor.equals(Turn.WHITE.toString())) {
			if (turn.equals(Turn.WHITEWIN.toString())) {
				return WIN;
			}
			if (turn.equals(Turn.BLACKWIN.toString())) {
				return LOOSE;
			}
		}

		if (turn.equals(Turn.DRAW.toString())) {
			return DRAW;
		}
		
		return NEUTRAL;
	}

	public int countCapturedPawns(State state) {
		int capturedPawns;
		int count = countNPawns(state);
		
		if (state.getTurn().toString().equals(AIColor)){
			capturedPawns = opponentPawns - count;
		} else {
			capturedPawns = (-1)*(AIPawns - count);
		}
		return capturedPawns;
	}
	
	public int countNPawns(State state) {
		Pawn[][] board = state.getBoard();
		String turn = state.getTurn().toString();
		String opponent;
		int count = 0;

		if (turn.equals(Turn.WHITE.toString())) opponent = Turn.BLACK.toString();
		else opponent = Turn.WHITE.toString();

		int dim = board.length;
		for (int row = 0; row < dim; row++)
			for (int col = 0; col < dim; col++) 
				if (board[row][col].toString().equals(opponent))
					count++;
		return count;
	}
	
	public void updatePawns(State state) {
		Pawn[][] board = state.getBoard();
		int countAI = 0;
		int countOp = 0;
		int dim = board.length;
		for (int row = 0; row < dim; row++)
			for (int col = 0; col < dim; col++) {
				if (board[row][col].toString().equals(AIColor))
					countAI++;
				if (board[row][col].toString().equals(OpColor))
					countOp++;
			}
		this.AIPawns = countAI;
		this.opponentPawns = countOp;
	}









	/**
	 * 
	 * @param state
	 * @param turn
	 * @param game
	 * @return List of all the possible next states for a player
	 */
	public LinkedList<State> successors(State state, boolean firstLevel) {

		LinkedList<State> elegibiles = new LinkedList<>();
		if (!terminalTest(state)) {
			Pawn[][] board = state.getBoard();
			int dim = board.length;
			for (int row = 0; row < dim; row++)
				for (int col = 0; col < dim; col++)
					if (board[row][col].toString().equals(state.getTurn().toString()) || 
							( state.getTurn().equals(Turn.WHITE) && board[row][col].toString().equals(Pawn.KING.toString()))) {
						LinkedList<String> toTry = new LinkedList<>();
						String from = state.getBox(row, col);

						// up
						for (int r = row-1; r >= 0; r--)	
							if (board[r][col].equals(Pawn.EMPTY) && !isCitadel(r, col)) toTry.add(state.getBox(r, col));	else break;// free cells on vertical axis 
						// right
						for (int c = col+1; c < dim; c++)	
							if (board[row][c].equals(Pawn.EMPTY) && !isCitadel(row, c)) toTry.add(state.getBox(row, c));	else break;// free cells on horizontal axis 
						// down
						for (int r = row+1; r < dim; r++)	
							if (board[r][col].equals(Pawn.EMPTY) && !isCitadel(r, col)) toTry.add(state.getBox(r, col));	else break; // free cells on vertical axis 
						// elft
						for (int c = col-1; c >= 0; c--)	
							if (board[row][c].equals(Pawn.EMPTY) && !isCitadel(row, c)) toTry.add(state.getBox(row, c));	else break;// free cells on horizontal axis 

						// check citadel


						for (String to:toTry) {

							try {
								Action a = new Action(from, to, state.getTurn());
								elegibiles.add(game.checkMove(state.clone(), a));
								if (firstLevel)
									eligibleActions.add(a);
							} catch (Exception e) { }
						}
					} 
		}
		return elegibiles;
	}


	public boolean isCitadel(int row, int col) {
		return (	((row == 0 || row == 8) && (col >= 3 && col <= 5)) ||
				((col == 0 || col == 8) && (row >= 3 && row <= 5)) ||
				((row == 1 || row == 7) && (col == 4)) ||
				((col == 1 || col == 7) && (row == 4)) );

		/*
		 * TEST PER IL MAIN
		 * 	for (int i=0; i<9; i++) {
				for (int j=0; j<9; j++) {
					if (m.isCitadel(i,  j))
						System.out.print("X");
					else
						System.out.print(".");
				}
				System.out.print("\n");
			}
		}
		 */
	}



	public static void main(String args[]) {
		Game game = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
		MiniMax m = new MiniMax(game, Turn.WHITE);
		MiniMax m2 = new MiniMax(game, Turn.BLACK);

		State s0 = new StateTablut(), s1 = null, s2 = null, s3 = null;
		Action a1, a2, a3;
		s0.setTurn(Turn.WHITE);

		System.out.println("*" +s0.getTurn());

		for (int i=0; i<9; i++)
			for (int j=0; j<9; j++)
				s0.getBoard()[i][j] = Pawn.EMPTY;

		//		s0.getBoard()[3][4] = Pawn.KING;
		//		s0.getBoard()[2][4] = Pawn.WHITE;
		//		s0.getBoard()[3][5] = Pawn.WHITE;
		//		s0.getBoard()[8][5] = Pawn.WHITE; // LUI MANGIA in 2 mosse
		//		s0.getBoard()[6][4] = Pawn.BLACK;

		s0.getBoard()[3][3] = Pawn.BLACK;
		s0.getBoard()[5][3] = Pawn.BLACK;
		s0.getBoard()[4][4] = Pawn.KING;
		s0.getBoard()[6][3] = Pawn.WHITE;
		s0.getBoard()[2][3] = Pawn.WHITE;

		
		System.out.println("Stato 0" + s0);
		try {
			
			a1 = m.minimaxDecision(s0); 
			s1 = game.checkMove(s0, a1);


//			a2 = m2.minimaxDecision(s1); 
//			s2 = game.checkMove(s1, a2);
//
//			a3 = m.minimaxDecision(s2); 
//			s3 = game.checkMove(s2, a3);


			System.out.println(s0 + "\nAzione 1: " + a1);
			System.out.println("Stato 1\n" + s1);

//			System.out.println(s1 + "Azione 2: " + a2);
//			System.out.println("Stato 2" + s2);
//
//			System.out.println(s2 + "Azione 3: " + a3);
//			System.out.println("Stato 3" + s3);

		} catch (BoardException | ActionException | StopException | PawnException | DiagonalException
				| ClimbingException | ThroneException | OccupitedException | ClimbingCitadelException
				| CitadelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 



	}

}
