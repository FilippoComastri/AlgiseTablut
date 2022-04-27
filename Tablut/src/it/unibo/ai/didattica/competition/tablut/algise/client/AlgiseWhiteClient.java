package it.unibo.ai.didattica.competition.tablut.algise.client;

import java.io.IOException;
import java.net.UnknownHostException;


public class AlgiseWhiteClient {
	
	public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
		String[] array=null;
		for(int i=0; i<args.length; i++)
		{
			System.out.println(args[i]);
		}
		
		if(args.length==0)
		{
			/*
			 * Invocazione senza argomenti, setto i parametri di default per giocare in locale
			 */
			System.out.println("Invocazione senza argomenti, settaggio a default");
			array = new String[]{"WHITE", "30", "localhost"};
		}
		else if(args.length==1)
		{
			/*
			 * Uso un solo argomento, che in questo caso è il timeout. Gli altri 2 sono lasciati a default
			 */
			System.out.println("Invocazione ad un argomento, ricevuto timeout");
			array = new String[]{"WHITE", args[0], "localhost"};
		}
		else if(args.length==2)
		{
			/*
			 * Invocazione con 2 argomenti: timeout e ip server
			 */
			System.out.println("Invocazione ad un argomento, ricevuto timeout ed ip server");
			array = new String[]{"WHITE", args[0], args[1]};
		}
		else
		{
			System.out.println("ALGISE WHITE CLIENT: INVOCAZIONE ERRATA.");
			System.out.println("Invocazione: AlgiseBlackClient [timeout] [ip]");
		}
		AlgiseClient.main(array);
	}


}
