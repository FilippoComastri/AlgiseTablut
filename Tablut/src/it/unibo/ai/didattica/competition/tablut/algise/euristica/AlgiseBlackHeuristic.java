package it.unibo.ai.didattica.competition.tablut.algise.euristica;

import java.util.Arrays;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.algise.domain.Coordinate;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class AlgiseBlackHeuristic {

	private State state;
	private List<String> camps; // cittadelle
	private List<String> escape; // stars
	private Coordinate kingCoordinate;
	

	// Parametri da calcolare
	private int pawnsB; // pedine NERE attuali
	private int pawnsW; // pedine BIANCHE attuali
	private int blackNearKing; // pedine NERE vicine al RE
	private int freeWayForKing; // vie libere per il re
	private int totalDistanceFromBlackThroneCamps; // per stabilire se stiamo accerchiando i bianchi
	
	//Pesi
	private double BLACK_WEIGHT = 6.0;
	private double WHITE_WEIGHT = 10.0;
	private double FREE_WAY_FOR_KING = 15.0;
	private double KING_BONUS = 9.0 ;
	private double ACCERCHIAMENTO_WEIGHT = 900;
	

	private static int LOOSE = -1;
	private static int WIN = 1;
	
	public AlgiseBlackHeuristic (State state) {
		this.state = state;

		this.camps = Arrays.asList("a4", "a5", "a6", "b5", "d1", "e1", "f1", "e2", "i4", "i5", "i6", "h5", "d9",
				"e9", "f9", "e8");

		this.escape = Arrays.asList("a2", "a3", "a7", "a8", "b1", "b9", "c1", "c9", "g1", "g9", "h1", "h9", "i2", "i3",
				"i7", "i8");
	}

	public double evaluateState() {

		//Resetto
		resetFields();

		//Calcolo dei vari parametri
		int state = extractFields();
		
		if (state == LOOSE) {
			return Double.MIN_VALUE;
		}
		else if (state == WIN){
			return Double.MAX_VALUE;
		}

		double result = 0;
		
		result+=this.pawnsB*this.BLACK_WEIGHT;
		result-=this.pawnsW*this.WHITE_WEIGHT;
		result+=this.blackNearKing*this.BLACK_WEIGHT;
		result-=this.freeWayForKing*this.FREE_WAY_FOR_KING;
		result+=this.ACCERCHIAMENTO_WEIGHT/this.totalDistanceFromBlackThroneCamps;

		return result;
	}

	private void resetFields() {
		this.kingCoordinate=null;
		this.pawnsB=0;
		this.pawnsW=0;
		this.blackNearKing=0;
		this.freeWayForKing = 0;
	}
	
	private int extractFields() {
		
		for (int i = 0; i < state.getBoard().length; i++) {
			for (int j = 0; j <state.getBoard()[i].length; j++) {
				
				// CALCOLO PEDINE NELLA BOARD
				if(state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())) {
					pawnsW++;
					this.totalDistanceFromBlackThroneCamps+=getDistanceFromBlackThroneCamps(i, j);
				}
				else if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())){
					pawnsW++;
					kingCoordinate = new Coordinate(i,j);
					this.totalDistanceFromBlackThroneCamps+=getDistanceFromBlackThroneCamps(i, j)*this.KING_BONUS;
				}
				else if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())) {
					pawnsB++;
				}
			} // for j
		} // for i
		
		// STATISTICHE RE
		if(kingCoordinate==null)
		{
			return WIN;
		}
		else if (escape.contains(state.getBox(this.kingCoordinate.getX(), this.kingCoordinate.getY()))) {
			return LOOSE;
		}

		
		int x = kingCoordinate.getX();
		int y = kingCoordinate.getY();
		
		
		
		// PEDINE NERE E BIANCHE VICINE AL RE
		if(x > 0) {
			if (this.state.getPawn(x-1, y).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
		}
		if(x < state.getBoard().length-1) {
			if (this.state.getPawn(x+1, y).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
		}
		if(y > 0) {
			if (this.state.getPawn(x, y-1).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
		}
		if(y < state.getBoard().length-1) {
			if (this.state.getPawn(x, y+1).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
		}
		
		// VIE LIBERE PER IL RE
		if(x < 3 || x > 5) {
			if(checkLeft(x,y)) freeWayForKing++;
			if(checkRight(x,y)) freeWayForKing++;
		}

		if(y < 3 || y > 5) {
			if(checkUp(x,y)) freeWayForKing++;
			if(checkDown(x,y)) freeWayForKing++;
		}

		return 0;
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
		for( int i=row; i< 9; i++) {
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
		for( int i=column; i < 9; i++) {
			if(this.state.getPawn(row, i).equalsPawn(State.Pawn.BLACK.toString()) ||
					this.state.getPawn(row, i).equalsPawn(State.Pawn.WHITE.toString()) ||
					camps.contains(state.getBox(row, i)))
				return false;
		}
		return true;
	}


	/**
	 * Metodo che calcola la distanza di una pedina da altre pedine nere, trono, accampamenti
	 * @param row
	 * @param column
	 * @return total distance
	 */

	
	public int getDistanceFromBlackThroneCamps(int row, int column) {
		int distanceResult = 0 ; 
		
		// calcolo distanza SOPRA
		
		distanceResult += getDistanceUp(row,column);
		
		//SOTTO
		
		distanceResult += getDistanceDown(row,column);
		
		//DESTRA
		
		distanceResult += getDistanceRight(row,column);
		
		//SINISTRA
		
		distanceResult += getDistanceLeft(row,column);
		
		return distanceResult;
	}
	
	public int getDistanceUp(int row, int column) {
		int dstResult = 0 ;
		boolean fine = false ;
		for (int i = row-1 ; i>=0 && !fine; i--) {
			if (this.state.getPawn(i, column).equalsPawn(State.Pawn.BLACK.toString())
					|| this.state.getPawn(i, column).equalsPawn(State.Pawn.THRONE.toString())
					|| camps.contains(state.getBox(i, column)))
				fine = true;
			else 
				dstResult++;
		}
		
		return dstResult;
	}
	
	public int getDistanceDown(int row, int column) {
		int dstResult = 0 ;
		boolean fine = false ;
		for (int i = row+1 ; i<this.state.getBoard().length && !fine; i++) {
			if (this.state.getPawn(i, column).equalsPawn(State.Pawn.BLACK.toString())
					|| this.state.getPawn(i, column).equalsPawn(State.Pawn.THRONE.toString())
					|| camps.contains(state.getBox(i, column)))
				fine = true;
			else 
				dstResult++;
		}
		
		return dstResult;
	}
	
	public int getDistanceRight(int row, int column) {
		int dstResult = 0 ;
		boolean fine = false ;
		
		for (int i = column+1 ; i<state.getBoard().length && !fine; i++) {
			if (this.state.getPawn(row, i).equalsPawn(State.Pawn.BLACK.toString())
					|| this.state.getPawn(row, i).equalsPawn(State.Pawn.THRONE.toString())
					|| camps.contains(state.getBox(row, i)))
				fine = true;
			else 
				dstResult++;
		}
		
		return dstResult;
	}
	
	public int getDistanceLeft(int row, int column) {
		int dstResult = 0;
		boolean fine = false;
		
		for (int i = column-1 ; i>=0 && !fine; i--) {
			if (this.state.getPawn(row, i).equalsPawn(State.Pawn.BLACK.toString())
					|| this.state.getPawn(row, i).equalsPawn(State.Pawn.THRONE.toString())
					|| camps.contains(state.getBox(row, i)))
				fine = true;
			else 
				dstResult++;
		}
		
		return dstResult;
	}
	
		
}

