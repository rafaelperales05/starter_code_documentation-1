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

    private static final int REPRODUCTION_ENERGY_MIN = 160;
    private static final int REPRODUCTION_CHANCE = 120;
    private static final int PATROL_STEPS_BEFORE_TURN = 4;
    private static final int FIGHT_ENERGY_MIN = 115;
    private static final int RUN_ENERGY_THRESHOLD = 180; 
    private static final int BOT_HUNT_CHANCE = 10; // 1 in 5 chance (20%) to hunt MaintenanceBots
    private int direction = Entity.getRandomInt(8);
    private int stepsUntilTurn = PATROL_STEPS_BEFORE_TURN;

    @Override
    public void doTimeStep() {
        if (this.getEnergy() >= RUN_ENERGY_THRESHOLD) {
            run(direction);
        } else {
            walk(direction);
        }

        stepsUntilTurn--;
        if (stepsUntilTurn <= 0) {
            direction = Entity.getRandomInt(8);
            stepsUntilTurn = PATROL_STEPS_BEFORE_TURN;
        }

        if (this.getEnergy() >= REPRODUCTION_ENERGY_MIN && Entity.getRandomInt(REPRODUCTION_CHANCE) == 0) {
            reproduce(this, Entity.getRandomInt(8));
        }
    }

    @Override
    public String toString() {
        return "C";
    }

    /**
     * Commanders are apex predators:
     * - Don't fight PowerCells (let bots/engineers have them)
     * - Hunt MaintenanceBots 1/5 times for population control
     * - Always hunt Engineers (primary prey)
     * - Fight other entities conditionally based on energy
     */
    @Override
    public boolean fight(String opp) {
        // Don't compete for PowerCells - let bots harvest them
        if ("*".equals(opp)) {
            return true;
        }
        
        // Hunt MaintenanceBots 1/5 times to keep population balanced
        if ("M".equals(opp)) {
            return Entity.getRandomInt(BOT_HUNT_CHANCE) == 0;
        }
        
        // Always hunt Engineers (primary prey)
        if ("E".equals(opp)) {
            return true;
        }
        
        // Fight other entities only when strong enough
        return this.getEnergy() >= FIGHT_ENERGY_MIN;
    }

    @Override
    public void runStats(String className) throws InvalidEntityException {
        List<Entity> commanders = getInstances(className);
        System.out.println(commanders.size() + " total " + className + "s    Status: Hunting to maintain ecosystem balance");
    }

}
