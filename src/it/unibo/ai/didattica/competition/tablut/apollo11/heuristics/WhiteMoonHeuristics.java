package it.unibo.ai.didattica.competition.tablut.apollo11.heuristics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class WhiteMoonHeuristics extends Heuristics{
	private final String BEST_POSITIONS = "bestPositions";
    private final String BLACK_EATEN = "numberOfBlackEaten";
    private final String WHITE_ALIVE = "numberOfWhiteAlive";
    private final String NUM_ESCAPES_KING = "numberOfWinEscapesKing";
    private final String BLACK_SURROUND_KING = "blackSurroundKing";
    private final String PROTECTION_KING = "protectionKing";
    private final String TERZA_FILA = "terzafila";


    //Limire per decidere la configurazione per le migliori posizioni delle pedine bianche
    private final static int THRESHOLD_BEST = 2;

    //Matrice delle migliori posizioni delle pedine bianche all'inizio del gioco
    private final static int[][] bestPositions = {
            {2,3},  {3,5},
            {5,3},  {6,5}
            ,{1,2},{1,6},{2,1},{2,7},{6,1},{6,7},{7,2},{7,6}
    };

    private final static int NUM_BEST_POSITION = bestPositions.length;

    private Map<String,Double> weights;
    private String[] keys;

    //Flag per abilitare la print su console
    private boolean flag = false;

    public WhiteMoonHeuristics(State state) {

        super(state);

       
        weights = new HashMap<String,Double>();
        
        weights.put(BEST_POSITIONS, 1.0);
        weights.put(BLACK_EATEN, 37.3);
        weights.put(WHITE_ALIVE, 13.0);
        weights.put(NUM_ESCAPES_KING, 30.0);
        weights.put(BLACK_SURROUND_KING, 5.0);
        weights.put(PROTECTION_KING, 15.0);
        weights.put(TERZA_FILA, 2.0);
        
        //estraggo chiavi da weights
        keys = new String[weights.size()];
        keys = weights.keySet().toArray(new String[0]);

    }
   
    @Override
    public double evaluateState() {

        double utilityValue = 0;
        //Funzioni per ottenere il 'utility value' attraverso la somma pesata
        double bestPositions = (double) getNumberOnBestPositions() / NUM_BEST_POSITION;
        double numberOfWhiteAlive =  (double)(state.getNumberOf(State.Pawn.WHITE)) / GameAshtonTablut.NUM_WHITE;
        double numberOfBlackEaten = (double)(GameAshtonTablut.NUM_BLACK - state.getNumberOf(State.Pawn.BLACK)) / GameAshtonTablut.NUM_BLACK;
        double blackSurroundKing = (double)(getNumEatenPositions(state) - countNearPawns(state, kingPosition(state),State.Turn.BLACK.toString())) / getNumEatenPositions(state);
        double protectionKing = protectionKing();
        double terzafila= terzaFila();

        int numberWinWays = countWinWays(state);
        double numberOfWinEscapesKing = numberWinWays>1 ? (double)countWinWays(state)/4 : 0.0;

        if(flag){
            System.out.println("Number of white alive: " + numberOfWhiteAlive);
            System.out.println("Number of white pawns in best positions " + bestPositions);
            System.out.println("Number of escapes: " + numberOfWinEscapesKing);
            System.out.println("Number of black surrounding king: " + blackSurroundKing);
        }

        Map<String, Double> values = new HashMap<String, Double>();
        values.put(BEST_POSITIONS, bestPositions);
        values.put(WHITE_ALIVE, numberOfWhiteAlive);
        values.put(BLACK_EATEN, numberOfBlackEaten);
        values.put(NUM_ESCAPES_KING,numberOfWinEscapesKing);
        values.put(BLACK_SURROUND_KING,blackSurroundKing);
        values.put(PROTECTION_KING,protectionKing);
        values.put(TERZA_FILA, terzafila);

        for (int i=0; i < weights.size(); i++){
            utilityValue += weights.get(keys[i]) * values.get(keys[i]);
            if(flag){
                System.out.println(keys[i] + ":  "+ weights.get(keys[i]) + " * " + values.get(keys[i]) +
                        " = " + weights.get(keys[i]) * values.get(keys[i]));
            }
        }

        return utilityValue;
    }
    
    private double terzaFila() {
    	double res=0;
    	boolean occupate=false;
        int[] kingPos = kingPosition(state);
        //controllo per ognuna delle 4 linee ce ne è almeno 1 completamente libera 
        int num=0;
   
        //a3 i3
        for (int j=0; j<9; j++) {
        	if(state.getPawn(2,j).equalsPawn(State.Pawn.WHITE.toString())
        		|| state.getPawn(2,j).equalsPawn(State.Pawn.BLACK.toString()
        			)) {
                num++;
                
        	}
        	

        }
        
        if (num ==0)
        {
        	
        	//controllo che tra il re e tale riga non ci siano pedine
        	int diff=Math.abs(kingPos[0]-3);
        	for(int i=1; i<diff;i++) {
        		if(kingPos[0]>3) {
        			//re sta sotto
        			int []p= {kingPos[0]-i,kingPos[1]};
        			if(checkOccupiedPosition(state,p)) {
        				occupate=true;
        			}
        		}else if (kingPos[0]<3) {
        			//re sta sopra
        			int []p= {kingPos[0]+i,kingPos[1]};
        			if(checkOccupiedPosition(state,p)) {
        				occupate=true;
        			}
        		}
        			
        	}
        	if(!occupate) {
        	    if(kingOnThrone(state)) {
        	    	if(state.getPawn(3,3).equalsPawn(State.Pawn.WHITE.toString())
        	    	&&  state.getPawn(3,5).equalsPawn(State.Pawn.WHITE.toString())
        	    	|| lineaLibera(3)
        	    		) {
        	    		res=1;
        	    	}
        	    }else 
        	    	res=1;
        	}
        	
        }
        num=0;
        //c1 c9
        for (int j=0; j<9; j++) {
        		if(state.getPawn(j,2).equalsPawn(State.Pawn.WHITE.toString())
        		|| state.getPawn(j,2).equalsPawn(State.Pawn.BLACK.toString()
        			)) {
                num++;
                
        		}
        }
        
        if (num ==0)
        {
        	//controllo che tra il re e tale riga non ci siano pedine
        	int diff=Math.abs(kingPos[1]-3);
        	for(int i=1; i<diff;i++) {
        		if(kingPos[1]>3) {
        			//re sta a destra
        			int []p= {kingPos[0],kingPos[1]-i};
        			if(checkOccupiedPosition(state,p)) {
        				occupate=true;
        			}
        		}else if (kingPos[1]<3) {
        			//re sta a sinistra
        			int []p= {kingPos[0],kingPos[1]+i};
        			if(checkOccupiedPosition(state,p)) {
        				occupate=true;
        			}
        		}
        			
        	}
        	
        	if(!occupate) {
        	    if(kingOnThrone(state)) {
        	    	if(state.getPawn(3,3).equalsPawn(State.Pawn.WHITE.toString())
        	    	&&  state.getPawn(5,3).equalsPawn(State.Pawn.WHITE.toString())
        	    	|| colonnaLibera(3)) {
        	    		res=1;
        	    	}
        	    }else 
        	    	res=1;
        	}
        }
        	
        
        num=0;
        //riga g1 g9
        for (int j=0; j<9; j++) {
        	if(state.getPawn(j,6).equalsPawn(State.Pawn.WHITE.toString())
        		|| state.getPawn(j,6).equalsPawn(State.Pawn.BLACK.toString()
        			)) {
                num++;
                
        	}
        }
        
        if (num ==0)
        {
        	//controllo che tra il re e tale riga non ci siano pedine
        	int diff=Math.abs(kingPos[1]-7);
        	for(int i=1; i<diff;i++) {
        		if(kingPos[1]>7) {
        			//re sta a destra
        			int []p= {kingPos[0],kingPos[1]-i};
        			if(checkOccupiedPosition(state,p)) {
        				occupate=true;
        			}
        		}else if (kingPos[1]<7) {
        			//re sta a sinistra
        			int []p= {kingPos[0],kingPos[1]+i};
        			if(checkOccupiedPosition(state,p)) {
        				occupate=true;
        			}
        		}
        			
        	}
        
        	if(!occupate) {
        	    if(kingOnThrone(state)) {
        	    	if(state.getPawn(3,5).equalsPawn(State.Pawn.WHITE.toString())
        	    	&&  state.getPawn(5,5).equalsPawn(State.Pawn.WHITE.toString())
        	    	|| colonnaLibera(5)) {
        	    		res=1;
        	    	}
        	    }else 
        	    	res=1;
        	}
        }
        
        num=0;
        //a7 i7
        for (int j=0; j<9; j++) {
        	if(state.getPawn(6,j).equalsPawn(State.Pawn.WHITE.toString())
        		|| state.getPawn(6,j).equalsPawn(State.Pawn.BLACK.toString()
        			)) {
                num++;
                
        	}
        }
        
        if (num ==0)
        {

        	//controllo che tra il re e tale riga non ci siano pedine
        	int diff=Math.abs(kingPos[0]-7);
        	for(int i=1; i<diff;i++) {
        		if(kingPos[0]>7) {
        			//re sta sotto
        			int []p= {kingPos[0]-i,kingPos[1]};
        			if(checkOccupiedPosition(state,p)) {
        				occupate=true;
        			}
        		}else if (kingPos[0]<7) {
        			//re sta sopra
        			int []p= {kingPos[0]+i,kingPos[1]};
        			if(checkOccupiedPosition(state,p)) {
        				occupate=true;
        			}
        		}
        			
        	}
        
        	if(!occupate) {
        	    if(kingOnThrone(state)) {
        	    	if(state.getPawn(5,3).equalsPawn(State.Pawn.WHITE.toString())
        	    	&&  state.getPawn(5,5).equalsPawn(State.Pawn.WHITE.toString())
        	    	|| lineaLibera(5)) {
        	    		res=1;
        	    	}
        	    }else 
        	    	res=1;
        	}
        }
        //se riesce a trovare una linea libera e il re ci va sopra win
        
       
        return res;
    }
	private boolean colonnaLibera(int i) {
		for(int j=0; j<9; j++) {
			if(!state.getPawn(j,i).equals(State.Pawn.EMPTY)){
				return false;
			}
		}
		return true;
	}
	private boolean lineaLibera(int i) {
		for(int j=0; j<9; j++) {
			if(!state.getPawn(i,j).equals(State.Pawn.EMPTY)){
				return false;
			}
		}
		return true;
	}


  
    private int getNumberOnBestPositions(){

        int num = 0;

        if (state.getNumberOf(State.Pawn.WHITE) >= GameAshtonTablut.NUM_WHITE - THRESHOLD_BEST){
            for(int[] pos: bestPositions){
                if(state.getPawn(pos[0],pos[1]).equalsPawn(State.Pawn.WHITE.toString())){
                    num++;
                }
            }
        }

        return num;
    }

   
    private double protectionKing(){

        //Valori quando c'è solo una pedina bianca vicino al re
        final double VAL_NEAR = 0.6;
        final double VAL_TOT = 1.0;

        double result = 0.0;

        int[] kingPos = kingPosition(state);
        //pedine vicino al re
        ArrayList<int[]> pawnsPositions = (ArrayList<int[]>) positionNearPawns(state,kingPos,State.Pawn.BLACK.toString());

        //C'è una pedina nera che minaccia il re e 2 pedine sono abbastanza per mangiarlo
        if (pawnsPositions.size() == 1 && getNumEatenPositions(state) == 2){
            int[] enemyPos = pawnsPositions.get(0);
            //Usato per mantenere altre posizioni da dove il re potrebbe essere mangiato
            int[] targetPosition = new int[2];
            //Nemico alla destra del re
            if(enemyPos[0] == kingPos[0] && enemyPos[1] == kingPos[1] + 1){
                //alla sinistra del re c'è una pedina bianca e il re è protetto
                targetPosition[0] = kingPos[0];
                targetPosition[1] = kingPos[1] - 1;
                if (state.getPawn(targetPosition[0],targetPosition[1]).equalsPawn(State.Pawn.WHITE.toString())){
                    result += VAL_NEAR;
                }
            //Nemico alla sinistra del re
            }else if(enemyPos[0] == kingPos[0] && enemyPos[1] == kingPos[1] -1){
                //alla destra del re c'è una pedina bianca e il re è protetto
                targetPosition[0] = kingPos[0];
                targetPosition[1] = kingPos[1] + 1;
                if(state.getPawn(targetPosition[0],targetPosition[1]).equalsPawn(State.Pawn.WHITE.toString())){
                    result += VAL_NEAR;
                }
            //Nemico sopra il re
            }else if(enemyPos[1] == kingPos[1] && enemyPos[0] == kingPos[0] - 1){
                //Sotto il re c'è una pedina bianca e il re è protetto
                targetPosition[0] = kingPos[0] + 1;
                targetPosition[1] = kingPos[1];
                if(state.getPawn(targetPosition[0], targetPosition[1]).equalsPawn(State.Pawn.WHITE.toString())){
                    result += VAL_NEAR;
                }
            //Nemico sotto il re
            }else{
                //Sopra il re c'è una pedina bianca e il re è protetto
                targetPosition[0] = kingPos[0] - 1;
                targetPosition[1] = kingPos[1];
                if(state.getPawn(targetPosition[0], targetPosition[1]).equalsPawn(State.Pawn.WHITE.toString())){
                    result += VAL_NEAR;
                }
            }

            //Considerando i bianchi da usare come barriere per il pedone bersaglio
            double otherPoints = VAL_TOT - VAL_NEAR;
            double contributionPerN = 0.0;

            //Se è meglio mantenere libera la posizione
            if (targetPosition[0] == 0 || targetPosition[0] == 8 || targetPosition[1] == 0 || targetPosition[1] == 8){
                if(state.getPawn(targetPosition[0],targetPosition[1]).equalsPawn(State.Pawn.EMPTY.toString())){
                    result = 1.0;
                } else {
                    result = 0.0;
                }
            }else{
                //Considerando un numero di pedine vicine ridotto se il bersaglio è vicino alla citadel o al trono
		 
                if (targetPosition[0] == 4 && targetPosition[1] == 2 || targetPosition[0] == 4 && targetPosition[1] == 6
                        || targetPosition[0] == 2 && targetPosition[1] == 4 || targetPosition[0] == 6 && targetPosition[1] == 4
                        || targetPosition[0] == 3 && targetPosition[1] == 4 || targetPosition[0] == 5 && targetPosition[1] == 4
                        || targetPosition[0] == 4 && targetPosition[1] == 3 || targetPosition[0] == 4 && targetPosition[1] == 5){
                    contributionPerN = otherPoints / 2;
                }else{
                    contributionPerN = otherPoints / 3;
                }

                result += contributionPerN * countNearPawns(state, targetPosition,State.Pawn.WHITE.toString());
            }

        }
        return result;
    }
    
    public int[] kingPosition(State state) {
        int[] king= new int[2];
        State.Pawn[][] board = state.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (state.getPawn(i, j).equalsPawn("K")) {
                    king[0] = i;
                    king[1] = j;
                }
            }
        }
        return king;
    }
	
	
	public boolean kingOnThrone(State state){
        if(state.getPawn(4,4).equalsPawn("K"))
            return true;
        else
            return false;
    }
	
	public int countNearPawns(State state, int[] position, String type){
        int count=0;
        State.Pawn[][] board = state.getBoard();
        if(board[position[0]-1][position[1]].equalsPawn(type))
            count++;
        if(board[position[0]+1][position[1]].equalsPawn(type))
            count++;
        if(board[position[0]][position[1]-1].equalsPawn(type))
            count++;
        if(board[position[0]][position[1]+1].equalsPawn(type))
            count++;
        return count;
    }
	
	protected List<int[]> positionNearPawns(State state,int[] position, String type){
        List<int[]> occupiedPosition = new ArrayList<int[]>();
        int[] pos = new int[2];
        //GET TURN
        State.Pawn[][] board = state.getBoard();
        if(board[position[0]-1][position[1]].equalsPawn(type)) {
            pos[0] = position[0] - 1;
            pos[1] = position[1];
            occupiedPosition.add(pos);
        }
        if(board[position[0]+1][position[1]].equalsPawn(type)) {
            pos[0] = position[0] + 1;
            occupiedPosition.add(pos);
        }
        if(board[position[0]][position[1]-1].equalsPawn(type)) {
            pos[0] = position[0];
            pos[1] = position[1] - 1;
            occupiedPosition.add(pos);
        }
        if(board[position[0]][position[1]+1].equalsPawn(type)) {
            pos[0] = position[0];
            pos[1] = position[1] + 1;
            occupiedPosition.add(pos);
        }

        return occupiedPosition;
    }
	
	protected boolean checkNearKing(State state, int[] position){
        return countNearPawns(state, position, "K") > 0;
    }
	
	protected int getNumberOfBlockedEscape(){
        int count = 0;
        int[][] blockedEscapes = {{1,1},{1,2},{1,6},{1,7},{2,1},{2,7},{6,1},{6,7},{7,1},{7,2},{7,6},{7,7}};
        for (int[] position: blockedEscapes){
            if (state.getPawn(position[0],position[1]).equalsPawn(State.Pawn.BLACK.toString())){
                count++;
            }
        }
        return count;

    }
	
	public boolean hasWhiteWon(){
        int[] posKing = kingPosition(state);
        boolean result;
        result = posKing[0] == 0 || posKing[0] == 8 || posKing[1] == 0 || posKing[1] == 8;
        return result;
    }
	
	public boolean safePositionKing(State state,int[] kingPosition){
        if(kingPosition[0] > 2 && kingPosition[0] < 6) {
            if (kingPosition[1] > 2 && kingPosition[1] < 6) {
                return true;
            }
        }
        return false;
    }
	
	public boolean kingGoesForWin(State state){
        int[] kingPosition=this.kingPosition(state);
        int col = 0;
        int row = 0;
        if(!safePositionKing(state,kingPosition)){
            if((!(kingPosition[1] > 2 && kingPosition[1] < 6)) && (!(kingPosition[0] > 2 && kingPosition[0] < 6))){
                //not safe row not safe col
                col = countFreeColumn(state, kingPosition);
                row = countFreeRow(state,kingPosition);
                //System.out.println(col);
            }
            if((kingPosition[1] > 2 && kingPosition[1] < 6)){
                // safe row not safe col
                row = countFreeRow(state, kingPosition);
            }
            if((kingPosition[0] > 2 && kingPosition[0] < 6)) {
                // safe col not safe row
                col = countFreeColumn(state, kingPosition);
            }
            return (col + row > 0);
        }
        return (col + row > 0);
    }
	
	public int countWinWays(State state){
        int[] kingPosition=this.kingPosition(state);
        int col = 0;
        int row = 0;
        if(!safePositionKing(state,kingPosition)){
            if((!(kingPosition[1] > 2 && kingPosition[1] < 6)) && (!(kingPosition[0] > 2 && kingPosition[0] < 6))){
                //not safe row not safe col
                col = countFreeColumn(state, kingPosition);
                row = countFreeRow(state,kingPosition);
            }
            if((kingPosition[1] > 2 && kingPosition[1] < 6)){
                // safe row not safe col
                row = countFreeRow(state, kingPosition);
            }
            if((kingPosition[0] > 2 && kingPosition[0] < 6)) {
                // safe col not safe row
                col = countFreeColumn(state, kingPosition);
            }
            
            return (col + row);
        }

        return (col + row);

    }
	
	 public int countFreeRow(State state,int[] position){
	        int row=position[0];
	        int column=position[1];
	        int[] currentPosition = new int[2];
	        int freeWays=0;
	        int countRight=0;
	        int countLeft=0;
	        //dx
	        for(int i = column+1; i<=8; i++) {
	            currentPosition[0]=row;
	            currentPosition[1]=i;
	            if (checkOccupiedPosition(state,currentPosition)) {
	                countRight++;
	            }
	        }
	        if(countRight==0)
	            freeWays++;
	        //sx
	        for(int i=column-1;i>=0;i--) {
	            currentPosition[0]=row;
	            currentPosition[1]=i;
	            if (checkOccupiedPosition(state,currentPosition)){
	                countLeft++;
	            }
	        }
	        if(countLeft==0)
	            freeWays++;

	        return freeWays;
	    }

	    
	    public int countFreeColumn(State state,int[] position){
	        //blocco colonna
	        int row=position[0];
	        int column=position[1];
	        int[] currentPosition = new int[2];
	        int freeWays=0;
	        int countUp=0;
	        int countDown=0;
	        //giu
	        for(int i=row+1;i<=8;i++) {
	            currentPosition[0]=i;
	            currentPosition[1]=column;
	            if (checkOccupiedPosition(state,currentPosition)) {
	                countDown++;
	            }
	        }
	        if(countDown==0)
	            freeWays++;
	        //su
	        for(int i=row-1;i>=0;i--) {
	            currentPosition[0]=i;
	            currentPosition[1]=column;
	            if (checkOccupiedPosition(state,currentPosition)){
	                countUp++;
	            }
	        }
	        if(countUp==0)
	            freeWays++;

	        return freeWays;
	    }
	    
	    public boolean checkOccupiedPosition(State state,int[] position){
	        return !state.getPawn(position[0], position[1]).equals(State.Pawn.EMPTY);
	    }
	    
	    public int getNumEatenPositions(State state){

	        int[] kingPosition = kingPosition(state);

	        if (kingPosition[0] == 4 && kingPosition[1] == 4){
	            return 4;
	        } else if ((kingPosition[0] == 3 && kingPosition[1] == 4) || (kingPosition[0] == 4 && kingPosition[1] == 3)
	                   || (kingPosition[0] == 5 && kingPosition[1] == 4) || (kingPosition[0] == 4 && kingPosition[1] == 5)){
	            return 3;
	        } else{
	            return 2;
	        }

	    }
}
