/**
 * Commander — a custom entity type (your design).
 * 
 * Commanders are apex predators who hunt other entities to maintain
 * the station's population balance. They don't compete for PowerCells,
 * instead gaining energy by hunting MaintenanceBots and Engineers.
 * This creates a natural food chain:
 * PowerCells → Bots/Engineers → Commanders
 * 
 * Display character: C
 */
public class Commander extends Entity {

    private static final int REPRODUCTION_ENERGY_MIN = 150;
    private static final int BOT_HUNT_CHANCE = 30;
    private int direction = Entity.getRandomInt(8);
    private int stepsUntilTurn = 4;

    /**
     * Commanders walk in straight lines, changing direction every 4 steps.
     * They reproduce when energy reaches 150.
     */
    @Override
    public void doTimeStep() {
        walk(direction);
        stepsUntilTurn--;
        if (stepsUntilTurn <= 0) {
            direction = Entity.getRandomInt(8);
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
     * Commanders are hunters with simple behavior:
     * - Harvest PowerCells (base food source)
     * - Hunt Engineers (primary prey)
     * - Rarely hunt MaintenanceBots (1/10 times)
     */
    @Override
    public boolean fight(String opp) {
        // Harvest PowerCells for base energy
        if ("*".equals(opp)) {
            return true;
        }
        
        // Hunt Engineers as primary prey
        if ("E".equals(opp)) {
            return true;
        }
        
        // Rarely hunt MaintenanceBots to keep their population viable
        if ("M".equals(opp)) {
            return Entity.getRandomInt(BOT_HUNT_CHANCE) == 0;
        }
        
        // Peaceful toward other entities
        return false;
    }

    /**
     * Print statistics for Commander entities.
     */
    public static void runStats(java.util.List<Entity> entities) {
        int count = (entities == null) ? 0 : entities.size();
        System.out.println(count + " total Commanders    Status: Hunting to maintain ecosystem balance");
    }

}
