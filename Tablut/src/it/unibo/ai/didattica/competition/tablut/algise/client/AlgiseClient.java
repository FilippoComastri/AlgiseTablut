package it.unibo.ai.didattica.competition.tablut.algise.client;

import java.io.IOException;
import java.net.UnknownHostException;

import it.unibo.ai.didattica.competition.tablut.algise.euristica.AlgiseAlphaBeta;
import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
public class AlgiseClient extends TablutClient {

	

	public AlgiseClient(String player, String name, int timeout, String ipAddress) 
			throws UnknownHostException, IOException {
		super(player, name, timeout, ipAddress);
	}

	public static void main(String[] args) throws IOException {
		String name = "Algise" ; 
		if (args.length != 3) {
			System.out.println("ALGISE dice : NO! Moc sa fet!! Brisa fer l'esen!! Gnurant!!");
			System.out.println("USAGE: Il pistolotto dev'essere invocato ./runmyplayer.sh <WHITE|BLACK> <timeout> <ip_server>");
			System.exit(-1);
		} else {
			String player = args[0] ;
			int timeout = Integer.parseInt(args[1]);
			String ipServer = args[2];
			System.out.println(timeout);
			AlgiseClient client = new AlgiseClient(player,name,timeout,ipServer);
			client.run();
		}
		
	}


	@Override
	public void run() {

		// Inviamo al server il nome del gruppo
		try {
			this.declareName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Algise saluta
		this.salutaAlgise();
		
		// Il bianco deve fare la prima mossa
		State state = new StateTablut();
		state.setTurn(State.Turn.WHITE);
		GameAshtonTablut gameRules = null;
		// Impostiamo le regole del gioco
		if(this.getPlayer().equals(State.Turn.WHITE))
		{
			gameRules = new GameAshtonTablut(99, 0, "logs", this.getName(), "blackOpponent");
		}
		else
		{
			gameRules = new GameAshtonTablut(99, 0, "logs", "whiteOpponent", this.getName());
		}

		while (true) {

			// recuperiamo lo stato dal server
			try {
				this.read();
			} catch (ClassNotFoundException | IOException e1) {
				e1.printStackTrace();
				System.exit(1);
			}

			// stampiamo stato corrente
			System.out.println("Stato corrente:");
			state = this.getCurrentState();
			System.out.println(state.toString());

			// se sono WHITE
			if (this.getPlayer().equals(State.Turn.WHITE)) {

				// se è il mio turno (WHITE)
				if (state.getTurn().equals(StateTablut.Turn.WHITE)) {

					System.out.println("\n Cercando la prossima mossa... ");

					
                    // cerchiamo la mossa migliore
                    Action a = findBestMove(gameRules, state);


                    System.out.println("\nAzione selezionata: " + a.toString());
                    try {
                    	this.write(a);
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
					 
				}

				// se è il turno dell'avversario (BLACK)
				else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
					System.out.println("Aspettando la mossa dell'avversario...\n");
				}
				// se vinco
				else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				}
				// se perdo
				else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				}
				// se pareggio
				else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}

			}
			// se sono BLACK 
			else {

				// mio turno (BLACK)
				if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK)) {

					System.out.println("\n Cercando la prossima mossa... ");

					
					//cerchiamo la mossa migliore
					Action a = findBestMove(gameRules, state);


					System.out.println("\nAzione selezionata: " + a.toString());
					try { 
						this.write(a);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
					
				}

				// turno dell'avversario (WHITE)
				else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
					System.out.println("Aspettando la mossa dell'avversario...\n");
				}

				// se perdo
				else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				}

				// se vinco
				else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				}

				// se pareggio
				else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}
			}
		}

	}
	
	/**
	 * Print some greeting
	 */
	public void salutaAlgise() {
		System.out.println("\r\n"
				+ " ________  ___       ________  ___  ________  _______      \r\n"
				+ "|\\   __  \\|\\  \\     |\\   ____\\|\\  \\|\\   ____\\|\\  ___ \\     \r\n"
				+ "\\ \\  \\|\\  \\ \\  \\    \\ \\  \\___|\\ \\  \\ \\  \\___|\\ \\   __/|    \r\n"
				+ " \\ \\   __  \\ \\  \\    \\ \\  \\  __\\ \\  \\ \\_____  \\ \\  \\_|/__  \r\n"
				+ "  \\ \\  \\ \\  \\ \\  \\____\\ \\  \\|\\  \\ \\  \\|____|\\  \\ \\  \\_|\\ \\ \r\n"
				+ "   \\ \\__\\ \\__\\ \\_______\\ \\_______\\ \\__\\____\\_\\  \\ \\_______\\\r\n"
				+ "    \\|__|\\|__|\\|_______|\\|_______|\\|__|\\_________\\|_______|\r\n"
				+ "                                      \\|_________|         \r\n"
				+ "                                                           \r\n"
				+ "                                                           \r\n"
				+ "");
	}

	private Action findBestMove(GameAshtonTablut gameRules, State state) {
		AlgiseAlphaBeta search = new AlgiseAlphaBeta(gameRules, Double.MIN_VALUE, Double.MAX_VALUE, this.timeout-2);
		return search.makeDecision(state);
	}

}
