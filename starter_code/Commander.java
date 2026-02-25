/** 
 * Commanders are apex predators who hunt other entities to maintain
 * the station's population balance. 
 * 
 * Display character: C
 */
public class Commander extends Entity {

    private static final int REPRODUCTION_ENERGY_MIN = 150;
    private int direction = Entity.getRandomInt(8);
    private int stepsUntilTurn = 6; 

    /**
     * Commanders walk and run randomly every 6 timesteps. They 
     * reproduce whenever their energy is above 150.
     */
    @Override 
    public void doTimeStep() { 
        walk(direction);
        stepsUntilTurn--;
        if (stepsUntilTurn <= 0) {
            direction = Entity.getRandomInt(8);
            run(direction); 
            stepsUntilTurn = 4;
        }
        if (this.getEnergy() >= REPRODUCTION_ENERGY_MIN) {
            reproduce(this, Entity.getRandomInt(8));
        }
    }
 
    /**
     * Display character is 'C' for Commander.
     */
    @Override
    public String toString() {
        return "C";
    }

    /**
     * Commanders always fight. 
     */
    @Override
    public boolean fight(String opp) {
        return true;
    }

    /**
     * Print statistics for Commander entities.
     */
    public static void runStats(java.util.List<Entity> entities) {
        int count = (entities == null) ? 0 : entities.size();
        System.out.println(count + " total Commanders    Status: Hunting to maintain ecosystem balance");
    }

}