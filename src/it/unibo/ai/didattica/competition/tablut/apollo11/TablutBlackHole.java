package it.unibo.ai.didattica.competition.tablut.apollo11;

import java.io.IOException;
import java.net.UnknownHostException;


public class TablutBlackHole {

	   public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
	       String[] array = new String[]{"BLACK", "60", "localhost", "debug"};
	       if (args.length>0){
	           array = new String[]{"BLACK", args[0]};
	       }
	       TablutApollo11Client.main(array);
	   }	
}
