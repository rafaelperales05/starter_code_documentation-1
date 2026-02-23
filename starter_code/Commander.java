import java.util.List;

/**
 * Commander — a custom entity type (your design).
 * 
 * Design a Commander entity that fills a meaningful role in the space
 * station ecosystem. Think about:
 *   - How does it move? (random, patrol pattern, toward/away from others?)
 *   - When does it fight? (always, never, conditionally?)
 *   - When does it reproduce? (at what energy threshold?)
 *   - What niche does it fill that helps the station stay self-sustaining?
 * 
 * Display character: C
 */
public class Commander extends Entity {

    private static final int REPRODUCTION_ENERGY_MIN = 220;
    private static final int REPRODUCTION_CHANCE = 20;
    private static final int PATROL_STEPS_BEFORE_TURN = 4;
    private static final int FIGHT_ENERGY_MIN = 180;
    private static final int RUN_ENERGY_THRESHOLD = 140;

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

    @Override
    public boolean fight(String opp) {
        if ("*".equals(opp)) {
            return true;
        }
        if ("M".equals(opp)) {
            return false;
        }
        return this.getEnergy() >= FIGHT_ENERGY_MIN;
    }

    @Override
    public void runStats(String className) throws InvalidEntityException {
        List<Entity> commanders = getInstances(className);
        System.out.println(commanders.size() + " total " + className + "s    Status: Leading the station efficiently");
    }

}
