import java.util.ArrayList;
import java.util.List;

/**
 * MaintenanceBot — an autonomous robot that evolves its movement over time.
 *
 * MaintenanceBots use a simple genetic algorithm to determine how they turn.
 * Each bot has 8 genes (one per direction offset 0-7) that sum to GENE_TOTAL.
 * At each step, a random roll against the gene distribution determines
 * how much the bot turns relative to its current direction.
 *
 * When a MaintenanceBot reproduces, the offspring inherits the parent's genes
 * with a small random mutation: one gene loses a point and another gains one.
 *
 * Display character: M
 */
public class MaintenanceBot extends Entity {

    private static final int GENE_TOTAL = 24;
    private static final int GENE_AMOUNT = 8;

    // Indices into aggregated distribution buckets
    private static final int STRAIGHT = 0;
    private static final int RIGHT = 1;
    private static final int BACK = 2;
    private static final int LEFT = 3;
    private static final int DIRECTION_BUCKETS = 4;

    private static final int REPRODUCTION_ENERGY_MIN = 150;

    private final int[] genes = new int[GENE_AMOUNT];
    private int dir;

    public MaintenanceBot() {
        for (int i = 0; i < GENE_AMOUNT; i++) {
            genes[i] = GENE_TOTAL / GENE_AMOUNT;
        }
        dir = Entity.getRandomInt(GENE_AMOUNT);
    }

    @Override
    public void doTimeStep() {
        // Move forward
        walk(dir);

        // Reproduce only when energy > 150
        if (this.getEnergy() > REPRODUCTION_ENERGY_MIN) {
            reproduce(this, Entity.getRandomInt(8));
            // Access the newly added baby directly from babies list
            if (!babies.isEmpty()) {
                Entity child = babies.get(babies.size() - 1);
                geneMutation(this, child);
            }
        }

        // Update direction based on genes distribution
        int roll = Entity.getRandomInt(GENE_TOTAL);
        int cumulative = 0;
        int offset = 0;
        for (int i = 0; i < GENE_AMOUNT; i++) {
            cumulative += genes[i];
            if (roll < cumulative) {
                offset = i;
                break;
            }
        }
        dir = (dir + offset) % 8;
    }

    private void geneMutation(Entity parent, Entity offspring) {
        if (!(parent instanceof MaintenanceBot) || !(offspring instanceof MaintenanceBot)) {
            return;
        }

        MaintenanceBot maintenanceParent = (MaintenanceBot) parent;
        MaintenanceBot maintenanceOffspring = (MaintenanceBot) offspring;

        // Copy genes
        for (int i = 0; i < GENE_AMOUNT; i++) {
            maintenanceOffspring.genes[i] = maintenanceParent.genes[i];
        }

        // Pick a non-zero gene to decrement
        ArrayList<Integer> candidates = new ArrayList<>();
        for (int i = 0; i < GENE_AMOUNT; i++) {
            if (maintenanceOffspring.genes[i] > 0) {
                candidates.add(i);
            }
        }
        if (candidates.isEmpty()) {
            return;
        }

        int loseIdx = candidates.get(Entity.getRandomInt(candidates.size()));
        maintenanceOffspring.genes[loseIdx]--;

        // Pick any gene to increment
        int gainIdx = Entity.getRandomInt(GENE_AMOUNT);
        maintenanceOffspring.genes[gainIdx]++;
    }

    @Override
    public String toString() {
        return "M";
    }

    @Override
    public boolean fight(String opp) {
        return true;
    }

    /**
     * Autograder-style stats entry point.
     *
     * Prints:
     *   <count> total MaintenanceBots    <p>% straight   <p>% back   <p>% right   <p>% left
     */
    public static void runStats(List<Entity> entities) {
        int count = (entities == null) ? 0 : entities.size();

        int[] geneSum = new int[DIRECTION_BUCKETS];
        int totalSum = 0;

        if (entities != null) {
            for (Entity entity : entities) {
                MaintenanceBot bot = (MaintenanceBot) entity;
                for (int i = 0; i < GENE_AMOUNT; i++) {
                    if (i == 0) {
                        geneSum[STRAIGHT] += bot.genes[i];
                    } else if (i >= 1 && i <= 3) {
                        geneSum[RIGHT] += bot.genes[i];
                    } else if (i == 4) {
                        geneSum[BACK] += bot.genes[i];
                    } else {
                        geneSum[LEFT] += bot.genes[i];
                    }
                    totalSum += bot.genes[i];
                }
            }
        }

        if (totalSum == 0) {
            System.out.print(count + " total MaintenanceBots    0.0% straight   ");
            System.out.print("0.0% back   ");
            System.out.print("0.0% right   ");
            System.out.println("0.0% left   ");
            return;
        }

        double straightPercent = (geneSum[STRAIGHT] * 100.0) / totalSum;
        double backPercent = (geneSum[BACK] * 100.0) / totalSum;
        double rightPercent = (geneSum[RIGHT] * 100.0) / totalSum;
        double leftPercent = (geneSum[LEFT] * 100.0) / totalSum;

        System.out.print(count + " total MaintenanceBots    " + straightPercent + "% straight   ");
        System.out.print(backPercent + "% back   ");
        System.out.print(rightPercent + "% right   ");
        System.out.println(leftPercent + "% left   ");
    }
}
