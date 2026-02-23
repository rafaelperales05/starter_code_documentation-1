import java.util.List;

/**
 * Engineer — a custom entity type (your design).
 * 
 * Engineers are efficient farmers who tend to the station's resources.
 * They move systematically in rows like tending organized crop fields.
 * They are completely peaceful toward all entities EXCEPT other Engineers,
 * with whom they compete for resources (fight 1/5 times for population control).
 * 
 * Engineers also maintain MaintenanceBots by transferring energy to them when
 * they encounter each other, creating a symbiotic relationship where the efficient
 * farming behavior of Engineers helps support the bot population.
 * 
 * Movement pattern: Travels East across a row, moves South, then travels
 * West across the next row, creating efficient coverage like plowing fields.
 * 
 * Display character: E
 */
public class Engineer extends Entity {

    private static final int REPRODUCTION_ENERGY_MIN = 150;
    private static final int ENGINEER_FIGHT_CHANCE = 3;
    private static final int ROW_LENGTH = Math.max(8, Params.world_width / 3);

    // Smart farming movement: systematic row-based pattern
    private int horizontalDirection = 0; // 0 = East, 4 = West
    private int stepsInCurrentRow = 0;

    @Override
    public void doTimeStep() {
        performFarmingMovement();

        // Reproduce more readily than fighting entities
        if (this.getEnergy() >= REPRODUCTION_ENERGY_MIN) {
            reproduce(this, Entity.getRandomInt(8));
        }
    }

    /**
     * Simplified farming movement pattern:
     * Move systematically in rows like tending organized crop fields.
     * Always walk to conserve energy.
     */
    private void performFarmingMovement() {
        // Smart systematic farming pattern - always walk
        stepsInCurrentRow++;
        
        // Move along the current row
        if (stepsInCurrentRow < ROW_LENGTH) {
            walk(horizontalDirection);
        } else {
            // End of row: move South to next row and switch direction
            walk(6); // 6 = South
            stepsInCurrentRow = 0;
            horizontalDirection = (horizontalDirection == 0) ? 4 : 0;
        }
    }

    @Override
    public String toString() {
        return "E";
    }

    /**
     * Engineers are efficient farmers with simple behavior:
     * - Always harvest PowerCells (food source)
     * - Compete with other Engineers (1/3 chance)
     * - Peaceful toward all other entities
     */
    @Override
    public boolean fight(String opp) {
        // Always harvest PowerCells for energy
        if ("*".equals(opp)) {
            return true;
        }
        
        // Fight other Engineers 1/3 of the time for population control
        if ("E".equals(opp) || "C".equals(opp)) {
            return Entity.getRandomInt(ENGINEER_FIGHT_CHANCE) == 0;
        }

        
        // Peaceful toward everything else (bots, commanders, etc.)
        return false;
    }

    /**
     * Engineers maintain MaintenanceBots by transferring energy to them.
     * This creates a symbiotic relationship where Engineers help bots survive.
     */
    @Override
    public boolean resolveSharedBlock(Entity otherEntity) {
        if (otherEntity instanceof MaintenanceBot) {
            // Transfer energy if we have enough to reproduce
            if (this.getEnergy() >= REPRODUCTION_ENERGY_MIN) {
                int transferAmount = 30;
                this.setEnergy(this.getEnergy() - transferAmount);
                otherEntity.setEnergy(otherEntity.getEnergy() + transferAmount);
                return true;
            }
        }
        return false;
    }

    @Override
    public void runStats(String className) throws InvalidEntityException {
        List<Entity> engineers = getInstances(className);
        int totalEnergy = 0;
        int lowEnergyCount = 0;
        for (Entity entity : engineers) {
            totalEnergy += entity.getEnergy();
        }
        int averageEnergy = engineers.isEmpty() ? 0 : (totalEnergy / engineers.size());
        System.out.println(engineers.size() + " total " + className + "s    Average energy: " + averageEnergy + "    Status: Peacefully farming station resources");
    }

}
