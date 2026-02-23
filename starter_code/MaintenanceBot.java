import java.util.ArrayList;

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
    private static final int GENE_AMOUNT = 8;
    private static final int STRAIGHT = 0;
    private static final int RIGHT = 1;
    private static final int BACK = 2;
    private static final int LEFT = 3; 
    private static final int DIRECTIONAMOUNT = 4;  
    private static final int REPRODUCTION_ENERGY_MIN = 150;

    private int[] genes = new int[8];
    private int dir;

    public MaintenanceBot() {
        for (int k = 0; k < 8; k += 1) {
            genes[k] = GENE_TOTAL / 8;
        }
        dir = Entity.getRandomInt(8);
    }

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


    /** 
     * doTimestep advances the maintenancebot through the walk function,
     * reproduces and mutates the genes of the child if the energy is above 150, 
     * and updates the gene distribution. 
     */
    @Override 
    public void doTimeStep(){ 
        try {
            // walk forward 
            walk(this.dir);
                
            // check for reproduction 
            if (this.getEnergy() > REPRODUCTION_ENERGY_MIN){  
                Entity child =  reproduce(this,this.dir); 
                geneMutation(this, child);
            }  
            // pick a new direction by rolling against gene distribution
            int r = Entity.getRandomInt(GENE_TOTAL);
            int cumSum = 0;
            int offset = 0;
            for (int i = 0; i < GENE_AMOUNT; i++) {
                cumSum += genes[i];
                if (r < cumSum) { offset = i; break; }
            }
            this.dir = (this.dir + offset) % 8;
            
        } catch (Exception e) { 
            e.printStackTrace();
        }
      
    }   

    /** 
     * geneMutation is a helper function for doTimeStep. 
     * It takes in the parent and child, copies the genes , decrements a random 
     * non zero gene, and randomly increments a gene.  
     * @param parent - MaintenanceBot that is reproducing 
     * @param offpspring - Resulting Maintenance Bot that is having their genes modified
     * */

    private void geneMutation(Entity parent, Entity offspring) {
        if (!(parent instanceof MaintenanceBot) || !(offspring instanceof MaintenanceBot)) {
            return;
        }
        MaintenanceBot maintenanceParent = (MaintenanceBot) parent;
        MaintenanceBot maintenanceOffspring = (MaintenanceBot) offspring;

        // copy genes, and check if 0 
        ArrayList<Integer> indexZero = new ArrayList<>();  

        for (int i = 0; i < GENE_AMOUNT; i++) {
            maintenanceOffspring.genes[i] = maintenanceParent.genes[i];  
            // create an array of indexs > 0, so we can sample 
            if (maintenanceOffspring.genes[i] > 0 ){ 
                indexZero.add(i);
            } 
        }  

        Integer indexDec = indexZero.get(Entity.getRandomInt(indexZero.size())); 
        maintenanceOffspring.genes[indexDec]--; 
        maintenanceOffspring.genes[Entity.getRandomInt(GENE_AMOUNT)]++;
    
    }



    /**
     * MaintenanceBot's toString returns its display name "M" 
     */ 
    @Override
    public String toString() {
        return "M";
    }

    /**
     * MaintenanceBot's fight always returns true as 
     * the bots are programmed to always fight
     */ 
    @Override
    public boolean fight(String opp) {
        return true;
    }  
  
    /**
     * runStats is an overwritten method that calls its parent function to print the
     * amount of bots and it print the gene distribution. 
     * @param className - string variable of instances requested
     */   
    @Override 
    public void runStats(String className) throws InvalidEntityException {  
        // print class name 
        super.runStats(className); 
        // print dist stats   
        getDistribution(className); 

    }  

    // TODO- edge case 0 bots
    /**
     * getDistribution sums up the genes from all active bots and then prints 
     * the distribution.  
     * @param className - string of instaces requested (Bots)
     */  
    private void getDistribution (String className){ 
        ArrayList<Entity> bots = (ArrayList<Entity>) getInstances(className);   

        int[] geneSum = new int[DIRECTIONAMOUNT]; 
        int totalSum = 0;

        for (Entity bot : bots){ 
            MaintenanceBot maintenanceBotTemp = (MaintenanceBot) bot; 
            for (int i = 0; i < GENE_AMOUNT; i ++){ 
                if (i == 0){  
                    //straight 
                    geneSum[STRAIGHT] += maintenanceBotTemp.genes[i];

                } 
                else if ( i >= 1 && i <= 3 ){ 
                    // right
                    geneSum[RIGHT] += maintenanceBotTemp.genes[i];

                } 
                else if  (i == 4){ 
                    //back
                    geneSum[BACK] += maintenanceBotTemp.genes[i];

                } 
                else { 
                    //left 
                    geneSum[LEFT] += maintenanceBotTemp.genes[i];

                } 
                totalSum += maintenanceBotTemp.genes[i];
            }
        } 
        double straightPercent = (geneSum[STRAIGHT] * 100.0) / totalSum;
        double rightPercent = (geneSum[RIGHT] * 100.0) / totalSum;
        double backPercent = (geneSum[BACK] * 100.0) / totalSum;
        double leftPercent = (geneSum[LEFT] * 100.0) / totalSum;
        
        System.out.print(" total " + className + "s    " + straightPercent + "%" +  " straight   ");
        System.out.print(backPercent + "%" +  " back   ");
        System.out.print(rightPercent + "%" +  " right   ");
        System.out.println(leftPercent + "%" +  " left   ");
    }


}
