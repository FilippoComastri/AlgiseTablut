package it.unibo.ai.didattica.competition.tablut.algise.euristica;

import java.util.Arrays;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.algise.domain.Coordinate;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class AlgiseWhiteHeuristic {
	
	// Parametri iniziali
	private State state;
	private int initB; // pedine NERE iniziali
	private int initW; // pedine BIANCHE iniziali
	private List<String> camps;
	private List<String> escape;
	private List<String> nearsThrone;
	private String throne;
	
	// Parametri da calcolare
	private int pawnsB; // pedine NERE attuali
	private int pawnsW; // pedine BIANCHE attuali
	private int blackRisk; // pedine NERE a rischio cattura (una bianca, un accampamento o trono vicina) 
	private int freeWayForKing; // vie libere per il RE
	private Coordinate kingCoordinate;
	private int blackNearKing; // pedine NERE vicine al RE
	private int whiteNearKing; // pedine BIANCHE vicine al RE
	
	// PESI
	private double REMAINING_BLACK_WEIGHT = 5.0;
	private double REMAINING_WHITE_WEIGHT = 7.0;
	private double BLACK_RISCK_WEIGHT = 8.0;
	private double FREE_WAY_KING_WEIGHT = 20.0;
	private double BLACK_NEAR_KING_WEIGHT = 6.0;
	private double WHITE_NEAR_KING_WEIGHT = 7.0;
	
	
	public AlgiseWhiteHeuristic (State state) {
		this.state = state;
		this.initB = 16;
		this.initW = 9; // 8 + KING
		
		this.camps = Arrays.asList("a4", "a5", "a6", "b5", "d1", "e1", "f1", "e2", "i4", "i5", "i6", "h5", "d9",
				"e9", "f9", "e8");

		this.escape = Arrays.asList("a2", "a3", "a7", "a8", "b1", "b9", "c1", "c9", "g1", "g9", "h1", "h9", "i2", "i3",
				"i7", "i8");

		this.nearsThrone = Arrays.asList("e4", "e6", "d5", "f5");
		this.throne = "e5";

	}
	
	private void initializeFields() {
		this.countPawns();
		this.countPawnsNearKing();
		this.freeWay();
		this.countBlackRisk();
	}
	
	public double evaluateState() {
		double result;
		
		initializeFields();
		/*private double REMAINING_BLACK_WEIGHT = 5.0;
	private double REMAINING_WHITE_WEIGHT = 8.0;
	private double BLACK_RISCK_WEIGHT = 7.0;
	private double FREE_WAY_KING_WEIGHT = 20.0;
	private double BLACK_NEAR_KING_WEIGHT = 6.0;
	private double WHITE_NEAR_KING_WEIGHT = 9.0;*/
		
		/*
		 * private int pawnsB; // pedine NERE attuali
	private int pawnsW; // pedine BIANCHE attuali
	private int blackRisk; // pedine NERE a rischio cattura (una bianca, un accampamento o trono vicina) 
	private int freeWayForKing; // vie libere per il RE
	private Coordinate kingCoordinate;
	private int blackNearKing; // pedine NERE vicine al RE
	private int whiteNearKing; // pedine BIANCHE vicine al RE
		 */
		result = pawnsB*REMAINING_BLACK_WEIGHT+pawnsW*REMAINING_WHITE_WEIGHT+blackRisk*BLACK_RISCK_WEIGHT+
				freeWayForKing*FREE_WAY_KING_WEIGHT+blackNearKing*BLACK_NEAR_KING_WEIGHT+
				whiteNearKing*WHITE_NEAR_KING_WEIGHT;
		return result;
	}
	// TODO: UNIFICARE il tutto in un unico ciclo
	
	// Calcolo pedine presenti nella board
	private void countPawns() {
		for (int i = 0; i < state.getBoard().length; i++) {
			for (int j = 0; j <state.getBoard()[i].length; j++) {
				//System.out.println("SOUT CountPawns "+state.getPawn(i, j));
				if(state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())) {
					pawnsW++;
				}
				else if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())){
					pawnsW++;
					kingCoordinate = new Coordinate(i,j);
				}
				else if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())) {
					pawnsB++;
				}
			}
		}
	}
	
	// Calcolo pedine NERE a rischio cattura
	private void countBlackRisk() {
		for (int i = 0; i < state.getBoard().length; i++) {
			for (int j = 0; j < state.getBoard()[i].length; j++) {
				if(state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString()) &&
						(whiteNearAttack(i,j,state) || throneNearAttack(i,j, state) || campsNearAttack(i,j, state) ))
							blackRisk++;
					
				
			}
		}
	}
	
	private boolean whiteNearAttack(int row, int column, State state) {
		if(row > 0 && state.getPawn(row-1,column).equalsPawn(State.Pawn.WHITE.toString()) && 
				row < state.getBoard().length-1 && state.getPawn(row+1,column).equalsPawn(State.Pawn.EMPTY.toString()) )
			return true;
		if( row < state.getBoard().length-1 && state.getPawn(row+1,column).equalsPawn(State.Pawn.WHITE.toString()) && 
				row > 0  && state.getPawn(row-1,column).equalsPawn(State.Pawn.EMPTY.toString()) )
			return true;
		if(column > 0 && state.getPawn(row,column-1).equalsPawn(State.Pawn.WHITE.toString()) && 
				column < state.getBoard().length-1 && state.getPawn(row,column+1).equalsPawn(State.Pawn.EMPTY.toString()) )
			return true;
		if( column < state.getBoard().length-1 && state.getPawn(row,column+1).equalsPawn(State.Pawn.WHITE.toString()) && 
				column > 0  && state.getPawn(row,column-1).equalsPawn(State.Pawn.EMPTY.toString()) )
			return true;
		
		return false;
						
	}
	
	private boolean throneNearAttack(int row, int column, State state) {
		if(row > 0 && state.getPawn(row-1,column).equalsPawn(State.Pawn.THRONE.toString()) && 
				row < state.getBoard().length-1 && state.getPawn(row+1,column).equalsPawn(State.Pawn.EMPTY.toString()) )
			return true;
		if( row < state.getBoard().length-1 && state.getPawn(row+1,column).equalsPawn(State.Pawn.THRONE.toString()) && 
				row > 0  && state.getPawn(row-1,column).equalsPawn(State.Pawn.EMPTY.toString()) )
			return true;
		if(column > 0 && state.getPawn(row,column-1).equalsPawn(State.Pawn.THRONE.toString()) && 
				column < state.getBoard().length-1 && state.getPawn(row,column+1).equalsPawn(State.Pawn.EMPTY.toString()) )
			return true;
		if( column < state.getBoard().length-1 && state.getPawn(row,column+1).equalsPawn(State.Pawn.THRONE.toString()) && 
				column > 0  && state.getPawn(row,column-1).equalsPawn(State.Pawn.EMPTY.toString()) )
			return true;
		
		return false;
	}
	
	private boolean campsNearAttack(int row, int column, State state) {
		boolean blackInCamp = camps.contains(state.getBox(row, column));
		if (blackInCamp) return false;
		else {
			if(row > 0 && camps.contains(state.getBox(row-1, column)) && 
					row < state.getBoard().length-1 && state.getPawn(row+1,column).equalsPawn(State.Pawn.EMPTY.toString()) )
				return true;
			if( row < state.getBoard().length-1 && camps.contains(state.getBox(row+1, column)) && 
					row > 0  && state.getPawn(row-1,column).equalsPawn(State.Pawn.EMPTY.toString()) )
				return true;
			if(column > 0 && camps.contains(state.getBox(row, column-1)) && 
					column < state.getBoard().length-1 && state.getPawn(row,column+1).equalsPawn(State.Pawn.EMPTY.toString()) )
				return true;
			if( column < state.getBoard().length-1 && camps.contains(state.getBox(row, column+1)) && 
					column > 0  && state.getPawn(row,column-1).equalsPawn(State.Pawn.EMPTY.toString()) )
				return true;
						
		}
		return true;
	}
	
	// Calcolo vie libere RE 
	private void freeWay() {
		int x = kingCoordinate.getX();
		int y = kingCoordinate.getY();
		
		if(x < 3 || x > 5) {
			if(checkLeft(x,y)) freeWayForKing++;
			if(checkRight(x,y)) freeWayForKing++;
		}
		
		if(y < 3 || y > 5) {
			if(checkUp(x,y)) freeWayForKing++;
			if(checkDown(x,y)) freeWayForKing++;
		}
		
	}
	
	private boolean checkLeft(int row,int column) {
		for( int i=row; i>= 0; i--) {
			if(this.state.getPawn(i, column).equalsPawn(State.Pawn.BLACK.toString()) ||
				this.state.getPawn(i, column).equalsPawn(State.Pawn.WHITE.toString()) ||
					camps.contains(state.getBox(i, column)))
				return false;
		}
		return true;
	}
	
	private boolean checkRight(int row,int column) {
		for( int i=row; i<=8; i++) {
			if(this.state.getPawn(i, column).equalsPawn(State.Pawn.BLACK.toString()) ||
				this.state.getPawn(i, column).equalsPawn(State.Pawn.WHITE.toString()) ||
					camps.contains(state.getBox(i, column)))
				return false;
		}
		return true;
	}
	
	private boolean checkUp(int row,int column) {
		for( int i=column; i>= 0; i--) {
			if(this.state.getPawn(row, i).equalsPawn(State.Pawn.BLACK.toString()) ||
				this.state.getPawn(row, i).equalsPawn(State.Pawn.WHITE.toString()) ||
					camps.contains(state.getBox(row, i)))
				return false;
		}
		return true;
	}
	
	private boolean checkDown(int row,int column) {
		for( int i=column; i <= 9; i++) {
			if(this.state.getPawn(row, i).equalsPawn(State.Pawn.BLACK.toString()) ||
				this.state.getPawn(row, i).equalsPawn(State.Pawn.WHITE.toString()) ||
					camps.contains(state.getBox(row, i)))
				return false;
		}
		return true;
	}
	
	// Calcolo pedine vicino al RE
	private void countPawnsNearKing () {
		int x = kingCoordinate.getX();
		int y = kingCoordinate.getY();
		if(x > 0) {
			if (this.state.getPawn(x-1, y).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
			else if (this.state.getPawn(x-1, y).equalsPawn(State.Pawn.WHITE.toString())) whiteNearKing++;
		}
		if(x < state.getBoard().length-1) {
			if (this.state.getPawn(x+1, y).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
			else if (this.state.getPawn(x+1, y).equalsPawn(State.Pawn.WHITE.toString())) whiteNearKing++;
		}
		if(y > 0) {
			if (this.state.getPawn(x, y-1).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
			else if (this.state.getPawn(x, y-1).equalsPawn(State.Pawn.WHITE.toString())) whiteNearKing++;
		}
		if(y < state.getBoard().length-1) {
			if (this.state.getPawn(x, y+1).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
			else if (this.state.getPawn(x, y+1).equalsPawn(State.Pawn.WHITE.toString())) whiteNearKing++;
		}
	}
	
	
	
	

}
