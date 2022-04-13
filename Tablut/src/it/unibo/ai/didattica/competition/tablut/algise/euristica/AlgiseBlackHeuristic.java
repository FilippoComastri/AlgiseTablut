package it.unibo.ai.didattica.competition.tablut.algise.euristica;

import java.util.Arrays;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.algise.domain.Coordinate;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class AlgiseBlackHeuristic {
	
	private State state;
	private int initB; // pedine NERE iniziali
	private int initW; // pedine BIANCHE iniziali
	private List<String> camps;
	private List<String> escape;
	private List<String> nearsThrone;
	private String throne;
	private Coordinate kingCoordinate;
	private int blackNearKing; // pedine NERE vicine al RE
	private int whiteNearKing; // pedine BIANCHE vicine al RE
	
	// Parametri da calcolare
	private int pawnsB; // pedine NERE attuali
	private int pawnsW; // pedine BIANCHE attuali
	private int kingToCapture; //� possibile catturare il RE in una mossa? oppure essere accerchiato
	private int pesiQuadranti[] = new int[4];
	/*
	 * private int whiteToCapture; //pedine BIANCHE catturabili
	 * private int blackWarning; //pedine NERE a rischio cattura
	 */
	
	private double WHITE_WEIGHT = 1.0;
	private double BLACK_WEIGHT = 1.0;
	private double KING_WEIGHT = 10.0;
	/*
	 * Estremi dei quadranti
	 */
	//Quadrante 1, quello alto a sinistra
	private int QUAD_1=0; //Indice nel quadrante 1 nell'array
	private int START_ROW_QUAD_1=0;
	private int END_ROW_QUAD_1=3;
	private int START_COL_QUAD_1=0;
	private int END_COL_QUAD_1=0;
	
	//Quadrante 2, quello alto a destra
	private int QUAD_2=1; //Indice nel quadrante 2 nell'array
	private int START_ROW_QUAD_2=0;
	private int END_ROW_QUAD_2=3;
	private int START_COL_QUAD_2=5;
	private int END_COL_QUAD_2=8;
	
	//Quadrante 3, quello basso a sinistra
	private int QUAD_3=2; //Indice nel quadrante 3 nell'array
	private int START_ROW_QUAD_3=5;
	private int END_ROW_QUAD_3=8;
	private int START_COL_QUAD_3=0;
	private int END_COL_QUAD_3=0;
	
	//Quadrante 4, quello basso a destra
	private int QUAD_4=3; //Indice nel quadrante 4 nell'array
	private int START_ROW_QUAD_4=5;
	private int END_ROW_QUAD_4=8;
	private int START_COL_QUAD_4=5;
	private int END_COL_QUAD_4=8;

	//Pesi
	private double REMAINING_BLACK_WEIGHT = 5.0;
	private double REMAINING_WHITE_WEIGHT = 7.0;
	private double WHITE_TO_CAPTURE_WEIGHT = 10.0;
	private double BLACK_WARNING_WEIGHT = 8.0;
	private double KING_TO_CAPTURE = 100.0;

	
	private void initializeFields() {
		this.countPawns();
		this.countPawnsNearKing();
		this.setKingToCapture();
		this.countWhiteToCapture();
		this.setPesiQuadranti();
	}
	
	private void calcolaPesoQuadrante(int startRow, int endRow, int startCol, int endCol, int quadrante)
	{
		/*
		 * Dato che dobbiamo difendere una zona, pi� � popolata di bianchi pi� sar� alto il peso di questa.
		 * In particolare sar� il RE che avendo un peso maggiore di tutti sbilancer� la situazione
		 * Per i bianchi SOMMO, per i neri SOTTRAGGO
		 */
		//Quadrante 1
		for(int i=startRow; i<endRow; i++)
		{
			for(int j=startCol; j<endCol; j++)
			{
				if(this.state.getPawn(i, j).equals(State.Pawn.WHITE))
				{
					this.pesiQuadranti[quadrante]+=WHITE_WEIGHT;
				}
				else if(this.state.getPawn(i, j).equals(State.Pawn.BLACK))
				{
					this.pesiQuadranti[quadrante]-=BLACK_WEIGHT;
				}
				else if(this.state.getPawn(i, j).equals(State.Pawn.KING))
				{
					this.pesiQuadranti[quadrante]+=KING_WEIGHT;
				}
				
			}
		}
	}

	private void setPesiQuadranti()
	{
		//Quadrante 1
		this.calcolaPesoQuadrante(START_ROW_QUAD_1, END_ROW_QUAD_1, START_COL_QUAD_1, END_COL_QUAD_1, QUAD_1);
		
		//Quadrante 2
		this.calcolaPesoQuadrante(START_ROW_QUAD_2, END_ROW_QUAD_2, START_COL_QUAD_2, END_COL_QUAD_2, QUAD_2);
		
		//Quadrante 3
		this.calcolaPesoQuadrante(START_ROW_QUAD_3, END_ROW_QUAD_3, START_COL_QUAD_3, END_COL_QUAD_3, QUAD_3);
		
		//Quadrante 4
		this.calcolaPesoQuadrante(START_ROW_QUAD_4, END_ROW_QUAD_4, START_COL_QUAD_4, END_COL_QUAD_4, QUAD_4);
	}



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
	
	
	private void countWhiteToCapture () {
		
	}
	
	/**
	 * Controlla se il re inizia ad essere accerchiato
	 */
	private void setKingToCapture() {
		if(this.kingCoordinate!=null)
		{
			String kingBox = this.state.getBox(this.kingCoordinate.getX(), this.kingCoordinate.getY());
			//Re sul trono, � accerchiato su 3 lati
			if(kingBox.equals(State.Pawn.THRONE))
			{
				
			}
			//Re vicino ad un accampamento
			else if(this.campsNearAttack(this.kingCoordinate.getX(), this.kingCoordinate.getY(), this.state))
			{
				
			}
			//Re di fianco al castello accerchiato su 2 lati
			/*
			else if()
			{
				
			}
			*/
		}
	}
	
	/**
	 * Questo metodo controlla se il re � vicino al trono ed � circondato da due lati da una pedina nemica e il 
	 * terzo � libero.
	 * Esempio: 	T
	 * 			   BKB
	 * 				O
	 * 
	 * oppure		T
	 * 			   OKB
	 * 				B
	 * 
	 * oppure		T
	 * 			   BKO
	 * 				B
	 * Nota: questi 3 casi di accerchiamento sono ad esempio solo per il caso del Trono sopra al RE, ma vanno ripetuti
	 * anche per le casitiche di RE sopra al trono(riga -1), a destra del trono e a sinistra del trono.
	 * @param row
	 * @param column
	 * @param state
	 * @return
	 */
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
	
	/**
	 * Questo metodo controlla se il re � vicino ad un accampamento e il lato opposto � libero. La situazione
	 * pu� essere vantaggiosa per il nero
	 * @param row
	 * @param column
	 * @param state
	 * @return
	 */
	private boolean campsNearAttack(int row, int column, State state) {
		boolean blackInCamp = camps.contains(state.getBox(row, column)); //TODO chiedere a filo
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
	
	public AlgiseBlackHeuristic (State state) {
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

	public double evaluateState() {
		this.reset();
		this.initializeFields();
		double result = 0;
		if(kingCoordinate==null)
		{
			return Double.MAX_VALUE;
		}
		result+=this.pawnsB*this.REMAINING_BLACK_WEIGHT;
		result-=this.pawnsW*this.REMAINING_WHITE_WEIGHT;
		return result;
	}

	private void reset() {
		this.kingCoordinate=null;
		this.pawnsB=0;
		this.pawnsW=0;
		this.blackNearKing=0;
		//this.blackWarning=0;
		this.kingToCapture=0;
	}

}
