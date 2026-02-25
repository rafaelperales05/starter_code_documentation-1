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
     * Commanders move randomly and reproduce at high energy threshold.
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
     * Commanders are apex predators:
     * - Only hunt MaintenanceBots (must work for food)
     * - Cannot consume PowerCells (pure carnivores)
     * - Don't fight Engineers or other Commanders
     */
    @Override
    public boolean fight(String opp) {
        return "M".equals(opp) || "*".equals(opp) || "E".equals(opp) || "C".equals(opp);
    }

    /**
     * Print statistics for Commander entities.
     */
    public static void runStats(java.util.List<Entity> entities) {
        int count = (entities == null) ? 0 : entities.size();
        System.out.println(count + " total Commanders    Status: Hunting to maintain ecosystem balance");
    }

}