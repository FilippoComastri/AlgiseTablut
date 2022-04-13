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
	private int whiteToCapture; //pedine BIANCHE catturabili
	private int blackWarning; //pedine NERE a rischio cattura
	private int kingToCapture; //è possibile catturare il RE in una mossa?
	private int pesiQuadranti[] = new int[4];
	
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
		this.kingCapture();
		this.countWhiteToCapture();
		this.setPesiQuadranti();
	}
	
	private void calcolaPesoQuadrante(int startRow, int endRow, int startCol, int endCol, int quadrante)
	{
		/*
		 * Dato che dobbiamo difendere una zona, più è popolata di bianchi più sarà alto il peso di questa.
		 * In particolare sarà il RE che avendo un peso maggiore di tutti sbilancerà la situazione
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
	
	private void kingCapture() {
				
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
		this.initializeFields();
		return 0;
	}

}
