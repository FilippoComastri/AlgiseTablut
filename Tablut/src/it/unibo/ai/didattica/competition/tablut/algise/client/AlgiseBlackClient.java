package it.unibo.ai.didattica.competition.tablut.algise.client;

import java.io.IOException;
import java.net.UnknownHostException;


public class AlgiseBlackClient {
	
	public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
		String[] array=null;
		if(args.length==0)
		{
			/*
			 * Invocazione senza argomenti, setto i parametri di default per giocare in locale
			 */
			System.out.println("0 argomenti");
			array = new String[]{"BLACK", "15", "localhost"};
		}
		else if(args.length==1)
		{
			/*
			 * Uso un solo argomento, che in questo caso � il timeout. Gli altri 2 sono lasciati a default
			 */
			System.out.println("1 argomenti");
			array = new String[]{"BLACK", args[0], "localhost"};
		}
		else if(args.length==2)
		{
			/*
			 * Invocazione con 2 argomenti: timeout e ip server
			 */
			System.out.println("2 argomenti");
			array = new String[]{"BLACK", args[0], args[1]};
		}
		else
		{
			System.out.println("ALGISE BLACK CLIENT: INVOCAZIONE ERRATA.");
			System.out.println("Invocazione: AlgiseBlackClient [timeout] [ip]");
			System.exit(1);
		}
		AlgiseClient.main(array);
	}


}
