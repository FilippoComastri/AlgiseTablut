package it.unibo.ai.didattica.competition.tablut.algise.client;

import java.io.IOException;
import java.net.UnknownHostException;


public class AlgiseBlackClient {
	
	public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
		String[] array = new String[]{"BLACK", "15", "localhost"};
		/*
		if (args.length>0){
			array = new String[]{"BLACK", args[0]};
		}
		*/
		AlgiseClient.main(array);
	}


}
