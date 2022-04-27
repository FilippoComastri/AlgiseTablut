package it.unibo.ai.didattica.competition.tablut.algise.euristica;

import java.util.Arrays;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.algise.domain.Coordinate;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

public class AlgiseWhiteHeuristic {

	// Parametri iniziali
	private State state;
	private List<String> camps;
	private List<String> escape;
	private List<String> nearsThrone;
	private String throne;
	private double[][] pesi_posizioni_bianco=new double[][]
		{{0,  1,-3,-6, -6, -6,-3, 1, 0},
		{1,  1, 1,-3, -6, -3, 1, 1, 1},
		{-3, 1, 2, 1,  0,  1, 2, 1, -3},
		{-6,-3, 1, 2,  0,  2, 1,-3, -6},
		{-6,-6, 0, 0, -6,  0, 0,-6, -6},
		{-6,-3, 1, 2,  0,  2, 1,-3, -6},
		{-3, 1, 2, 1,  0,  1, 2, 1, -3},
		{1,  1, 1,-3, -6, -3, 1, 1, 1},
		{0,  1,-3,-6, -6,-6,-3, 1, 0}};

	private double[][] pesi_posizione_re=new double[][]
			{{0, 20, 20,-6, -6, -6,20, 20, 0},
			{20, 1, 1, -5, -6, -5, 1,  1, 20},
			{20, 1, 4,  1, -2,  1, 4,  1, 20},
			{-6,-5, 1,  1,  0,  1, 1, -5, -6},
			{-6,-6,-2,  0,  2,  0,-2, -6, -6},
			{-6,-5, 1,  1,  0,  1, 1, -5, -6},
			{20, 1, 4,  1, -2,  1, 4,  1, 20},
			{20, 1, 1, -5, -6, -5, 1,  1, 20},
			{0, 20, 20,-6, -6, -6,20, 20, 0}};


			// Parametri da calcolare
			private int pawnsB; // pedine NERE attuali
			private int pawnsW; // pedine BIANCHE attuali
			private int freeWayForKing; // vie libere per il RE
			private Coordinate kingCoordinate;
			private int blackNearKing; // pedine NERE vicine al RE
			private double positions_sum; // pesi delle posizioni dei bianchi
			private double CRStartegicheFree;

			
			 /*
			private int blackRisk; // pedine NERE a rischio cattura (una bianca, un accampamento o trono vicina) 
			private int whiteNearKing; // pedine BIANCHE vicine al RE
			 */

			//TODO King at risk 
			//TODO Valore alle posizioni
			//TODO Provare HASH MAP per non ricalcolare euristica di stati
			//TODO Usare le posizioni invece che per tutte le pedine solo per il RE e valutare la mossa usando anche i quadranti

			// PESI
			private double REMAINING_BLACK_WEIGHT = 10.0;
			private double REMAINING_WHITE_WEIGHT = 22.0;
			private double FREE_WAY_KING_WEIGHT = 50.0;
			private double BLACK_NEAR_KING_WEIGHT = 6.0;
			private double POSITION_WEIGHT = 0.5;
			private double KING_POSITION_WEIGHT = 2;
			
			/*
			private double BLACK_RISCK_WEIGHT = 8.0;
			private double WHITE_NEAR_KING_WEIGHT = 7.0; */
			


			private static int LOOSE = -1;
			private static int WIN = 1;



			public AlgiseWhiteHeuristic (State state) {
				this.state = state;

				this.camps = Arrays.asList("a4", "a5", "a6", "b5", "d1", "e1", "f1", "e2", "i4", "i5", "i6", "h5", "d9",
						"e9", "f9", "e8");

				this.escape = Arrays.asList("a2", "a3", "a7", "a8", "b1", "b9", "c1", "c9", "g1", "g9", "h1", "h9", "i2", "i3",
						"i7", "i8");

				this.nearsThrone = Arrays.asList("e4", "e6", "d5", "f5");
				this.throne = "e5";

			}



			public double evaluateState() {
				double result=0;
				resetFields();	
				int state = extractFields();

				// se non c'è il re ho perso
				if(state == LOOSE)
				{
					
					return Double.MIN_VALUE;
				}// se il re è in escape ho vinto
				else if (state == WIN)
				{
					
					return Double.MAX_VALUE;
				}
				result-= pawnsB*REMAINING_BLACK_WEIGHT;
				result+= pawnsW*REMAINING_WHITE_WEIGHT;
				result+= freeWayForKing*FREE_WAY_KING_WEIGHT;
				result-= blackNearKing*BLACK_NEAR_KING_WEIGHT;
				result+= this.positions_sum*POSITION_WEIGHT;
				result+=CRStartegicheFree;

				return result;
			}

			private void resetFields() {
				this.pawnsB=0; 
				this.pawnsW=0; 
				this.freeWayForKing=0; 
				this.kingCoordinate=null;
				this.blackNearKing=0; 
				this.positions_sum=0;
				this.CRStartegicheFree = 0;
			}


			// Calcolo pedine presenti nella board
			private int extractFields() {

				for (int i = 0; i < state.getBoard().length; i++) {
					for (int j = 0; j <state.getBoard()[i].length; j++) {

						// CALCOLO PEDINE SULLA BOARD e LORO PESO
						if(state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())) {
							pawnsW++;
							this.positions_sum+=this.pesi_posizioni_bianco[i][j];
						}
						else if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())){
							pawnsW++;
							kingCoordinate = new Coordinate(i,j);
							this.positions_sum+=this.pesi_posizione_re[i][j]*KING_POSITION_WEIGHT;
						}
						else if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())) {
							pawnsB++;
						}


					} // for j
				} // for i

				// STATISTICHE RE
				// Se non c'è il re => HO PERSO (termino)
				if(this.kingCoordinate==null) {
					return LOOSE;
				}// Se il re è salvo => HO VINTO (termino)
				else if (escape.contains(state.getBox(this.kingCoordinate.getX(), this.kingCoordinate.getY()))) {
					return WIN;
				}

				// PEDINE NERE VICINE AL RE
				int x = kingCoordinate.getX();
				int y = kingCoordinate.getY();
				if(x > 0) {
					if (this.state.getPawn(x-1, y).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
					//else if (this.state.getPawn(x-1, y).equalsPawn(State.Pawn.WHITE.toString())) whiteNearKing++;
				}
				if(x < state.getBoard().length-1) {
					if (this.state.getPawn(x+1, y).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
					//else if (this.state.getPawn(x+1, y).equalsPawn(State.Pawn.WHITE.toString())) whiteNearKing++;
				}
				if(y > 0) {
					if (this.state.getPawn(x, y-1).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
					//else if (this.state.getPawn(x, y-1).equalsPawn(State.Pawn.WHITE.toString())) whiteNearKing++;
				}
				if(y < state.getBoard().length-1) {
					if (this.state.getPawn(x, y+1).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
					//else if (this.state.getPawn(x, y+1).equalsPawn(State.Pawn.WHITE.toString())) whiteNearKing++;
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
				
				// Colonne e righe strategiche libere
				CRStartegicheFree = righeColonneStrategicheLibere(state);

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
			
			
			private double righeColonneStrategicheLibere(State s) {
				double soloNere = 0;
				//double soloBianche = 0;
				double vuote = 0;
				double soloRe = 0;
				int RBlack, RWhite, CBlack, CWhite;
				boolean RKing, CKing;
				int[] CRStrategiche = {2,6};
				
				for (Integer i : CRStrategiche) {
					RBlack=0; // pedine NERE nella riga
					RWhite=0; // pedine BIANCHE nella riga
					CBlack=0; // pedine NERE nella colonna
					CWhite=0; // pedine BIANCHE nella colonna
					RKing=false; // RE nella riga
					CKing=false; // RE nella colonna
					
					for(int j=0; j<state.getBoard().length;j++) {
						if(s.getPawn(i, j).equals(Pawn.BLACK)) RBlack++;
						if(s.getPawn(i, j).equals(Pawn.WHITE)) RWhite++;
						if(s.getPawn(i, j).equals(Pawn.KING)) RKing=true;
						if(s.getPawn(j, i).equals(Pawn.BLACK)) CBlack++;
						if(s.getPawn(j, i).equals(Pawn.WHITE)) CWhite++;
						if(s.getPawn(j, i).equals(Pawn.KING)) CKing=true;
					}
					
					if(RBlack==0 && RWhite==0 && !RKing) vuote++;
					if(CBlack==0 && CWhite==0 && !CKing) vuote++;
					if(RBlack>=1 && RWhite==0 && !RKing) soloNere++;
					//if(RBlack==0 && RWhite>=1 && !RKing) soloBianche++;
					if(RBlack==0 && RWhite==0 && RKing) soloRe=10;
					if(CBlack>=1 && CWhite==0 && !CKing) soloNere++;
					//if(CBlack==0 && CWhite>=1 && !CKing) soloBianche++;
					if(CBlack==0 && CWhite==0 && CKing) soloRe=10;
				}
				
				return soloRe + vuote*3 - soloNere*0.5;
			}



			// Calcolo pedine NERE a rischio cattura
			/*
	private void countBlackRisk() {
		for (int i = 0; i < state.getBoard().length; i++) {
			for (int j = 0; j < state.getBoard()[i].length; j++) {
				if(state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString()) &&
						(whiteNearAttack(i,j,state) || throneNearAttack(i,j, state) || campsNearAttack(i,j, state) ))
							blackRisk++;


			}
		}
	}
			 */

			/*private boolean whiteNearAttack(int row, int column, State state) {
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
			}*/
			
}
