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

    
     
    /** 
     * PowerCell's doTimestep increases the energy that can be consumed by 1
     */
    @Override 
    public void doTimeStep(){ 
        this.setEnergy(this.getEnergy() + Params.solar_energy_amount); 
    };   

    /** 
     * PowerCell's toString returns its display Character '*'
     */
    @Override
    public String toString() {
        return "*";
    }

    /** 
     * PowerCell's fight method returns false, as it is always consumed upon conflict
     */
    @Override
    public boolean fight(String opp) {
        return false;
    }

}
