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

    private static final int LOW_ENERGY_THRESHOLD = 15;
    private static final int RUN_ENERGY_THRESHOLD = 200;
    private static final int REPRODUCTION_ENERGY_MIN = 150;
    private static final int DONATION_ENERGY_MIN = 80;
    private static final int DONATION_AMOUNT = 5;
    private static final int COMMANDER_SUPPORT_DONATION = 10;
    private static final int BOT_SUPPORT_DONATION = 24;
    private static final int SELF_ENERGY_RESERVE = 25;
    private static final int HARVEST_BUFFER = 20;
    private static final int EVASIVE_STEPS = 2;

    // Smart movement state:
    // - Serpentine sweep (E/W with periodic S shift) for broad world coverage
    // - Temporary evasive movement after hostile encounters
    private int horizontalDirection = 0; // 0 = East, 4 = West
    private int stepsRemainingInRow = Math.max(1, Params.world_width - 1);
    private boolean shiftToNextRow = false;
    private int evasiveDirection = Entity.getRandomInt(8);
    private int evasiveStepsRemaining = 0;

    @Override
    public void doTimeStep() {
        performSmartMovement();

        if (this.getEnergy() >= REPRODUCTION_ENERGY_MIN) {
            reproduce(this, Entity.getRandomInt(8));
        }
    }

    private void performSmartMovement() {
        if (evasiveStepsRemaining > 0) {
            if (this.getEnergy() >= RUN_ENERGY_THRESHOLD) {
                run(evasiveDirection);
            } else {
                walk(evasiveDirection);
            }
            evasiveStepsRemaining--;
            return;
        }

        if (shiftToNextRow) {
            walk(6); // move South one row between sweep passes
            shiftToNextRow = false;
            horizontalDirection = (horizontalDirection == 0) ? 4 : 0;
            stepsRemainingInRow = Math.max(1, Params.world_width - 1);
            return;
        }

        boolean canRunInRow = this.getEnergy() >= RUN_ENERGY_THRESHOLD && stepsRemainingInRow >= 2;
        if (canRunInRow) {
            run(horizontalDirection);
            stepsRemainingInRow -= 2;
        } else {
            walk(horizontalDirection);
            stepsRemainingInRow--;
        }

        if (stepsRemainingInRow <= 0) {
            shiftToNextRow = true;
        }
    }

    @Override
    public String toString() {
        return "E";
    }

    @Override
    public boolean fight(String opp) {
        if (!"*".equals(opp)) {
            evasiveDirection = Entity.getRandomInt(8);
            evasiveStepsRemaining = EVASIVE_STEPS;
            return false;
        }

        return this.getEnergy() < (REPRODUCTION_ENERGY_MIN + HARVEST_BUFFER);
    }

    @Override
    protected boolean resolveSharedBlock(Entity other) {
        if (other == null || other == this) {
            return false;
        }

        String otherType = other.toString();

        if ("*".equals(otherType)) {
            return false;
        }

        if (this.getEnergy() < DONATION_ENERGY_MIN) {
            return false;
        }

        int baseDonation = DONATION_AMOUNT;
        if ("M".equals(otherType)) {
            baseDonation = BOT_SUPPORT_DONATION;
        } else if ("C".equals(otherType)) {
            baseDonation = COMMANDER_SUPPORT_DONATION;
        }

        int donation = Math.min(baseDonation, this.getEnergy() - SELF_ENERGY_RESERVE);
        if (donation <= 0) {
            return false;
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
