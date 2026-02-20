/**
 * PowerCell — the basic energy source for the space station.
 * 
 * PowerCells represent solar panels and batteries. They do not move on their
 * own, but they gain energy each step through solar charging. They never
 * fight — any entity that encounters a PowerCell absorbs its energy.
 * 
 * New PowerCells spawn automatically each time step (see Params).
 * 
 * Display character: *
 */
public class PowerCell extends Entity {

    // TODO: Implement this class
    @Override 
    public void doTimeStep(){};  
    @Override
    public String toString() {
        return "";
    }

    @Override
    public boolean fight(String opp) {
        return false;
    }

}
