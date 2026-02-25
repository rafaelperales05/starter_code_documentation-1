import java.util.*;
/** * 
 * Engineers are efficient farmers who tend to the station's resources.
 * They move systematically in rows like tending organized crop fields.
 * They are completely peaceful toward all entities EXCEPT other Engineers 
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
    private static final int ROW_LENGTH = Math.max(8, Params.world_width / 3);

    // Smart farming movement: systematic row-based pattern
    private int horizontalDirection = 0; // 0 = East, 4 = West
    private int stepsInCurrentRow = 0;
    private boolean restStep = false; // walk every other step to conserve energy

    /**
     * Engineers move in a systematic row pattern and reproduce at 150 energy.
     */
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
     * Walks every other step to conserve energy (rest cost 1 vs move cost 3).
     */
    private void performFarmingMovement() {
        restStep = !restStep;
        if (restStep) {
            return; // rest this step, only pay rest_energy_cost
        }

        stepsInCurrentRow++;
        
        if (stepsInCurrentRow < ROW_LENGTH) {
            walk(horizontalDirection);
        } else {
            // End of row: move South to next row and switch direction
            walk(6); 
            stepsInCurrentRow = 0;
            horizontalDirection = (horizontalDirection == 0) ? 4 : 0;
        }
    }

    /**
     * Display character is 'E' for Engineer.
     */
    @Override
    public String toString() {
        return "E";
    }

    /**
     * Engineers are efficient farmers with simple behavior:
     * - Always harvest PowerCells
     * - Never fight other entities (peaceful herbivores)
     */
    @Override
    public boolean fight(String opp) {
        return "*".equals(opp)  || "E".equals(opp)  ;
    }

    /**
     * Print statistics for Engineer entities.
     */
    public static void runStats(List<Entity> entities) {
        int count = (entities == null) ? 0 : entities.size();
        int totalEnergy = 0;
        
        if (entities != null) {
            for (Entity entity : entities) {
                totalEnergy += entity.getEnergy();
            }
        }
        
        int averageEnergy = (count == 0) ? 0 : (totalEnergy / count);
        System.out.println(count + " total Engineers    Average energy: " + averageEnergy + "    Status: Peacefully farming station resources");
    }

}
