import java.util.List;

/**
 * Engineer — a custom entity type (your design).
 * 
 * Engineers are efficient farmers who tend to the station's resources.
 * They move systematically in rows like tending organized crop fields.
 * They are completely peaceful toward all entities EXCEPT other Engineers,
 * with whom they compete for resources (fight 1/10 times for population control).
 * This keeps their population in check while making them non-threatening to others.
 * 
 * Movement pattern: Travels East across a row, moves South, then travels
 * West across the next row, creating efficient coverage like plowing fields.
 * 
 * Display character: E
 */
public class Engineer extends Entity {

    private static final int LOW_ENERGY_THRESHOLD = 15;
    private static final int REPRODUCTION_ENERGY_MIN = 135; // Increased to slow population growth
    private static final int RUN_ENERGY_THRESHOLD = 180; // Conserve energy by walking more
    private static final int ENGINEER_FIGHT_CHANCE = 2; // 1 in 5 chance (20%) per Engineer for population control
    private static final int COMMANDER_FIGHT_CHANCE = 10; // 1 in 10 chance (10%) to fight Commanders
    private static final int OTHER_FIGHT_CHANCE =  10; // 1 in 10 chance (10%) to fight other entities
    private static final int FLEE_STEPS_MIN = 1;
    private static final int FLEE_STEPS_MAX = 3;
    private static final int ROW_LENGTH = Math.max(8, Params.world_width / 3); // Length of each farming row

    // Smart farming movement: systematic row-based pattern like tending organized crops
    // Pattern: Move East across a row, then South one step, then West across, repeat
    private int horizontalDirection = 0; // 0 = East, 4 = West
    private int stepsInCurrentRow = 0;
    
    // Fleeing state: runs away from encounters at random
    private int fleeDirection = Entity.getRandomInt(8);
    private int fleeStepsRemaining = 0;

    @Override
    public void doTimeStep() {
        performFarmingMovement();

        // Reproduce more readily than fighting entities
        if (this.getEnergy() >= REPRODUCTION_ENERGY_MIN) {
            reproduce(this, Entity.getRandomInt(8));
        }
    }

    /**
     * Smart farming movement pattern:
     * - If fleeing from an encounter, continue fleeing
     * - Otherwise, move systematically in rows like tending organized crop fields
     * - Goes East for a row, then South one step, then West for a row, repeat
     */
    private void performFarmingMovement() {
        // If currently fleeing from an encounter, continue fleeing
        if (fleeStepsRemaining > 0) {
            if (this.getEnergy() >= RUN_ENERGY_THRESHOLD) {
                run(fleeDirection);
            } else {
                walk(fleeDirection);
            }
            fleeStepsRemaining--;
            return;
        }

        // Smart systematic farming pattern
        stepsInCurrentRow++;
        
        // Move along the current row
        if (stepsInCurrentRow < ROW_LENGTH) {
            if (this.getEnergy() >= RUN_ENERGY_THRESHOLD) {
                run(horizontalDirection);
            } else {
                walk(horizontalDirection);
            }
        } else {
            // End of row: move South to next row and switch direction
            walk(6); // 6 = South
            stepsInCurrentRow = 0;
            horizontalDirection = (horizontalDirection == 0) ? 4 : 0; // Toggle between East and West
        }
    }

    @Override
    public String toString() {
        return "E";
    }

    /**
     * Engineers are efficient farmers who harvest PowerCells for energy.
     * They compete with other Engineers (1/5 chance per Engineer),
     * are peaceful toward MaintenanceBots (station maintenance),
     * fight Commanders 1/10 times (allow predation but not always),
     * and rarely fight others (1/50 chance).
     */
    @Override
    public boolean fight(String opp) {
        // Always harvest PowerCells for energy (their food source)
        if ("*".equals(opp)) {
            return true;
        }
        
        // Fight other Engineers 1 in 5 times to keep population under control
        if ("E".equals(opp)) {
            return Entity.getRandomInt(ENGINEER_FIGHT_CHANCE) == 0;
        }
        
        // Never fight MaintenanceBots - they maintain the station!
        if ("M".equals(opp)) {
            fleeDirection = Entity.getRandomInt(8);
            fleeStepsRemaining = FLEE_STEPS_MIN + Entity.getRandomInt(FLEE_STEPS_MAX - FLEE_STEPS_MIN + 1);
            return false;
        }
        
        // Fight Commanders 1 in 10 times - allows predation to work
        if ("C".equals(opp)) {
            if (Entity.getRandomInt(COMMANDER_FIGHT_CHANCE) == 0) {
                return true;
            }
            // Most of the time, flee from Commanders
            fleeDirection = Entity.getRandomInt(8);
            fleeStepsRemaining = FLEE_STEPS_MIN + Entity.getRandomInt(FLEE_STEPS_MAX - FLEE_STEPS_MIN + 1);
            return false;
        }
        
        // Fight other entities 1 in 50 times for balance
        if (Entity.getRandomInt(OTHER_FIGHT_CHANCE) == 0) {
            return true;
        }
        
        // Most of the time, flee from other entities
        fleeDirection = Entity.getRandomInt(8);
        fleeStepsRemaining = FLEE_STEPS_MIN + Entity.getRandomInt(FLEE_STEPS_MAX - FLEE_STEPS_MIN + 1);
        return false;
    }

    @Override
    public void runStats(String className) throws InvalidEntityException {
        List<Entity> engineers = getInstances(className);

        int totalEnergy = 0;
        int lowEnergyCount = 0;
        for (Entity entity : engineers) {
            int energy = entity.getEnergy();
            totalEnergy += energy;
            if (energy <= LOW_ENERGY_THRESHOLD) {
                lowEnergyCount++;
            }
        }

        int averageEnergy = engineers.isEmpty() ? 0 : (totalEnergy / engineers.size());

        System.out.print(engineers.size() + " total " + className + "s    ");
        System.out.print("Average energy: " + averageEnergy + "    ");
        System.out.print("Low energy: " + lowEnergyCount + "    ");
        System.out.println("Status: Peacefully farming station resources");
    }

}
