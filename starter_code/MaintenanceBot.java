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
 * Over many generations, bots that move efficiently (finding more PowerCells)
 * survive longer and reproduce more, so the population evolves toward
 * better movement strategies.
 * 
 * MaintenanceBots always fight when they encounter another entity.
 * 
 * Display character: M
 */
public class MaintenanceBot extends Entity {

    private static final int GENE_TOTAL = 24;
    private int[] genes = new int[8];
    private int dir;

    public MaintenanceBot() {
        for (int k = 0; k < 8; k += 1) {
            genes[k] = GENE_TOTAL / 8;
        }
        dir = Entity.getRandomInt(8);
    }

    // TODO: Implement the required methods for this entity.
    //
    // Behavior summary:
    //   - Each step: walk forward in the current direction
    //   - If energy > 150: reproduce with gene mutation
    //     (copy genes to child, randomly subtract 1 from a nonzero gene,
    //      randomly add 1 to any gene)
    //   - After moving: pick a new direction by rolling against the gene
    //     distribution. The roll determines a turn offset (0-7) which is
    //     added to the current direction (mod 8).
    //
    // Also implement a static runStats method that reports:
    //   - Total number of bots
    //   - Percentage of gene weight allocated to straight, right, back, left
    //     (straight = genes[0], right = genes[1]+[2]+[3],
    //      back = genes[4], left = genes[5]+[6]+[7])

}
