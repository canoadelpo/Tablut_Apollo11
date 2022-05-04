package it.unibo.ai.didattica.competition.tablut.apollo11.heuristics;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public abstract class Heuristics {
	
	protected State state;
	
	public Heuristics(State state) {
        this.state = state;
    }

	/**
    *
    * @return true if king is on throne, false otherwise
    */
   public boolean checkKingPosition(State state){
       if(state.getPawn(4,4).equalsPawn("K"))
           return true;
       else
           return false;
   }

	public double evaluateState() {
		return 1;
	}
}