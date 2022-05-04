package it.unibo.ai.didattica.competition.tablut.apollo11;

import java.io.IOException;
import java.net.UnknownHostException;

public class TablutWhiteMoon {

	   public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
	       String[] array = new String[]{"WHITE", "20", "localhost", "debug"};
	       if (args.length>0){
	            array = new String[]{"WHITE", args[0]};
	       }
	       TablutApollo11Client.main(array);
	   }
}
