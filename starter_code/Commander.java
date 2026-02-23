import java.util.List;

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

    private static final int REPRODUCTION_ENERGY_MIN = 130;
    private static final int PATROL_STEPS_BEFORE_TURN = 4;
    private static final int BOT_HUNT_CHANCE = 20; // 1 in 10 chance to hunt MaintenanceBots
    private int direction = Entity.getRandomInt(8);
    private int stepsUntilTurn = PATROL_STEPS_BEFORE_TURN;

    @Override
    public void doTimeStep() {
        // Always walk to conserve energy
        walk(direction);

        stepsUntilTurn--;
        if (stepsUntilTurn <= 0) {
            direction = Entity.getRandomInt(8);
            stepsUntilTurn = PATROL_STEPS_BEFORE_TURN;
        }

        if (this.getEnergy() >= REPRODUCTION_ENERGY_MIN){
            reproduce(this, Entity.getRandomInt(8));
        }
    }

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

    @Override
    public void runStats(String className) throws InvalidEntityException {
        List<Entity> commanders = getInstances(className);
        System.out.println(commanders.size() + " total " + className + "s    Status: Hunting to maintain ecosystem balance");
    }

}
