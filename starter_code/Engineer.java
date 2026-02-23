import java.util.List;

/**
 * Engineer — a custom entity type (your design).
 * 
 * Design an Engineer entity that fills a different role from your Commander.
 * The two custom entities should complement each other in keeping the
 * ecosystem balanced. Think about:
 *   - How does it move? (random, patrol pattern, toward/away from others?)
 *   - When does it fight? (always, never, conditionally?)
 *   - When does it reproduce? (at what energy threshold?)
 *   - What happens to the ecosystem if you remove all Engineers?
 * 
 * Display character: E
 */
public class Engineer extends Entity {

    private static final int LOW_ENERGY_THRESHOLD = 25;
    private static final int RUN_ENERGY_THRESHOLD = 140;
    private static final int REPRODUCTION_ENERGY_MIN = 160;
    private static final int DONATION_ENERGY_MIN = 120;
    private static final int DONATION_AMOUNT = 5;
    private static final int PATROL_STEPS_BEFORE_TURN = 4;

    private int direction = Entity.getRandomInt(8);
    private int stepsUntilTurn = PATROL_STEPS_BEFORE_TURN;

    @Override
    public void doTimeStep() {
        if (this.getEnergy() < LOW_ENERGY_THRESHOLD) {
            return;
        }

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

        if (this.getEnergy() >= REPRODUCTION_ENERGY_MIN) {
            reproduce(this, Entity.getRandomInt(8));
        }
    }

    @Override
    public String toString() {
        return "E";
    }

    @Override
    public boolean fight(String opp) {
        return "*".equals(opp);
    }

    @Override
    protected boolean resolveSharedBlock(Entity other) {
        if (other == null || other == this) {
            return false;
        }

        if ("*".equals(other.toString())) {
            return false;
        }

        if (this.getEnergy() < DONATION_ENERGY_MIN) {
            return true;
        }

        int donation = Math.min(DONATION_AMOUNT, this.getEnergy() - LOW_ENERGY_THRESHOLD);
        if (donation <= 0) {
            return true;
        }

        this.setEnergy(this.getEnergy() - donation);
        other.setEnergy(other.getEnergy() + donation);
        return true;
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
        System.out.println("Status: Maintaining station systems");
    }

}
