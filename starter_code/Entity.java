import java.util.*;
import java.lang.reflect.Method;

public abstract class Entity {
    private static List<Entity> population = new java.util.ArrayList<Entity>();
    private static List<Entity> babies = new java.util.ArrayList<Entity>();
    
    // 2D world representation as 1D list for efficiency
    // Each grid cell holds a list of entities at that location
    private static List<List<Entity>> world = new ArrayList<>(Params.world_width * Params.world_height);
    private static List<List<Boolean>> hasWalked = new ArrayList<>(Params.world_width * Params.world_height);
    private static Entity[] fighters = new Entity[2];

    static {
        for (int i = 0; i < Params.world_width * Params.world_height; i++) {
            world.add(new ArrayList<Entity>());
            hasWalked.add(new ArrayList<Boolean>());
        }
    }

    /* ========================================================================
     * Random number generation — DO NOT MODIFY
     * ======================================================================== */

    private static java.util.Random rand = new java.util.Random();
    public static int getRandomInt(int max) {
        return rand.nextInt(max);
    }

    public static void setSeed(long new_seed) {
        rand = new java.util.Random(new_seed);
    }

    /* ========================================================================
     * Instance fields — DO NOT MODIFY these field declarations
     * ======================================================================== */

    private int energy = 0;
    protected int getEnergy() { return energy; }
    protected void setEnergy(int energy) { this.energy = energy; }

    private int x_coord;
    private int y_coord;

    /* ========================================================================
     * Movement
     * 
     * Entities can walk (move 1 square) or run (move 2 squares) in one of
     * 8 directions: 0=E, 1=NE, 2=N, 3=NW, 4=W, 5=SW, 6=S, 7=SE
     * 
     * The world wraps around at the edges (toroidal topology).
     * Movement costs energy as defined in Params.
     * 
     * The moveConditionals method handles the special case where an entity
     * is currently in an encounter (fight). During encounters, movement is
     * restricted — an entity that has already moved cannot move again, and
     * fighters can only move to occupied cells.
     * ======================================================================== */

    protected final void walk(int direction) {
        moveConditionals(direction, 1, Params.walk_energy_cost);
    }

    protected final void run(int direction) {
        moveConditionals(direction, 2, Params.run_energy_cost);
    }

    private void moveConditionals(int direction, int distance, int energyCost) {
        boolean isFighter = (fighters[0] == this || fighters[1] == this);
        
        if (isFighter && checkIfWalked(this)) {
            energy -= energyCost;
            return;
        } else if (isFighter) {
            int prevX = x_coord;
            int prevY = y_coord;
            move(this, direction, distance);
            if (world.get(convertTo1D(x_coord, y_coord)).isEmpty()) {
                x_coord = prevX;
                y_coord = prevY;
            } else {
                x_coord = prevX;
                y_coord = prevY;
                energy -= energyCost;
                return;
            }
        }
        
        removeFromWorld(this);
        move(this, direction, distance);
        energy -= energyCost;
        addToWorld(this);
        markAsWalked(this);
    }

    private static void move(Entity entity, int direction, int distance) {
        // Direction: 0=E, 1=NE, 2=N, 3=NW, 4=W, 5=SW, 6=S, 7=SE
        if (direction == 7 || direction == 0 || direction == 1) {
            entity.x_coord = (entity.x_coord + distance) % Params.world_width;
        }
        if (direction == 3 || direction == 4 || direction == 5) {
            entity.x_coord -= distance;
            if (entity.x_coord < 0) {
                entity.x_coord += Params.world_width;
            }
        }
        if (direction == 5 || direction == 6 || direction == 7) {
            entity.y_coord = (entity.y_coord + distance) % Params.world_height;
        }
        if (direction == 1 || direction == 2 || direction == 3) {
            entity.y_coord -= distance;
            if (entity.y_coord < 0) {
                entity.y_coord += Params.world_height;
            }
        }
    }

    /* ========================================================================
     * Reproduction
     * 
     * When an entity reproduces, the parent's energy is split between parent
     * and offspring. The offspring is placed adjacent to the parent in the
     * given direction.
     * 
     * The parent keeps ceil(energy/2) and the offspring gets floor(energy/2).
     * ======================================================================== */

    /**  
     * Reproduce takes in a parent entity and creates a baby/offspring entity 
     * Divides the energy with the baby and places the baby adjacent to the parent depending 
     * on input direction. 
     * It has special conditions for MaintenanceBots. It copies their gene distribution and rearranges them. 
     * @param Parent - Parent entity that creates the offspring
     * @param Direction - The direciton indicates in which block the child will move to be adjacent
     *  
     */  
    protected static void reproduce(Entity parent, int direction){   
        
        //null check and check for minumum energy 
        if (parent == null || parent.getEnergy() < Params.min_reproduce_energy){ 
            return;
        } 

        try {

        String parentClass = parent.getClass().getSimpleName(); 
        Class<?> genClass = Class.forName(parentClass);
        Entity child = (Entity) genClass.getDeclaredConstructor().newInstance();

        //set energy 
        int parentEnergy = parent.getEnergy();  
        int childEnergy = parentEnergy / 2;
        int newParentEnergy = parentEnergy - childEnergy;
        child.setEnergy(childEnergy);  
        parent.setEnergy(newParentEnergy); 

        // set offspring coordinates
        child.x_coord = parent.x_coord; 
        child.y_coord = parent.y_coord;  
        move(child,direction,1);

        // Handle MaintenanceBot gene mutation
        if (parent instanceof MaintenanceBot && child instanceof MaintenanceBot) {
            MaintenanceBot parentBot = (MaintenanceBot) parent;
            MaintenanceBot childBot = (MaintenanceBot) child;
            
            int[] parentGenes = parentBot.getGenes();
            int[] childGenes = childBot.getGenes();
            for (int i = 0; i < parentGenes.length; i++) {
                childGenes[i] = parentGenes[i];
            }
            
            // Find non-zero genes to decrement
            List<Integer> nonZeroGenes = new ArrayList<>();
            for (int i = 0; i < childGenes.length; i++) {
                if (childGenes[i] > 0) {
                    nonZeroGenes.add(i);
                }
            }
            
            if (!nonZeroGenes.isEmpty()) {

                int loseIndex = nonZeroGenes.get(getRandomInt(nonZeroGenes.size()));
                childGenes[loseIndex]--;
                
                int gainIndex = getRandomInt(childGenes.length);
                childGenes[gainIndex]++;
            }
        }
            
        babies.add(child);

            
        } catch (Exception e) { 
            e.printStackTrace();
        }
    }


    /* ========================================================================
     * Abstract methods
     * 
     * Think carefully about what behaviors differ between entity types and
     * must be implemented by each subclass.
     * ======================================================================== */


    
    /**
     * Entity specfic time step
     */
    protected  abstract void doTimeStep();   

    /**
     * Returns display character
     */
    @Override
    public  abstract String toString(); 
     

    /**
     * return true or false depending on fight behavior
     * @param Opp - Opponent entity
     * @return - true or false depending on fight behavior
     */
    protected  abstract boolean fight(String Opp); 


    /* ========================================================================
     * Entity creation and lookup
     * 
     * makeEntity creates a new entity of the given class name using reflection.
     * It should:
     *   - Reject names that start with a lowercase letter
     *   - Use Class.forName() to find the class
     *   - Create an instance and set its starting energy and random position
     *   - Add it to the population and the world grid
     *   - Throw InvalidEntityException if anything goes wrong
     * 
     * getInstances returns a list of all living entities that are instances
     * of the given class name.
     * ======================================================================== */


    /** 
     * MakeEntity creates a new entity of the given class name using reflection. 
     * It rejects null or invalid names, initializes the energy/poistions, and adds 
     * it to the population and world grid
     * @param Name 
    */  
    public static void makeEntity(String className) throws InvalidEntityException {
             // (1) check null/empty/lowercase 
        if (className == null || className.isEmpty() || Character.isLowerCase(className.charAt(0))){ 
                throw new InvalidEntityException(className);  
        }     
        try {  

            // (2) find class 
            Class<?> genClass = Class.forName(className);  

            // (3) instiate and set energy/random position
            Entity genEntity = (Entity) genClass.getDeclaredConstructor().newInstance();
            genEntity.setEnergy(Params.start_energy);  
            genEntity.x_coord = Entity.getRandomInt(Params.world_width);  
            genEntity.y_coord = Entity.getRandomInt(Params.world_height);  
            
            // (4) add to population and world 
            population.add(genEntity);  
            addToWorld(genEntity); 

        } 
        catch ( ReflectiveOperationException e) { 
            throw new InvalidEntityException(className); 
        } 
        catch (ClassCastException e){ 
            throw new InvalidEntityException(className); 
        } 
        catch (Exception e) { 
            throw new InvalidEntityException(className);  
        }
    }



    /**
    * The getinstances class retrieves all the instances/entites of a give type in the population
    * @Param className - string variable of instances requested 
    * @return instances - Array holding all entitys of same class
    * */
    public static List<Entity> getInstances(String className) throws InvalidEntityException {   

        // check null/empty/lowercase 
        if (className == null || className.isEmpty() || Character.isLowerCase(className.charAt(0))){ 
                throw new InvalidEntityException(className);  
        }       

        try { 
            List<Entity> instances = new ArrayList<>();  
            Class<?> genClass = Class.forName(className);  

            // error check for invalid type
            if (!Entity.class.isAssignableFrom(genClass)){ 
                throw new InvalidEntityException(className); 
            } 

            // check for population
            for (Entity entity: population){ 

                if (genClass.isInstance(entity)){ 
                    instances.add(entity); 
                }
            } 
            return instances;
            
        } 
        catch (ReflectiveOperationException e) {
            throw new InvalidEntityException(className); 
        } 
        catch (Exception e){ 
            throw new InvalidEntityException(className); 
        }
    }



    /* ========================================================================
     * Statistics
     * 
     * runStats prints a summary of the given entity list. The default
     * implementation counts how many of each display character exist.
     * Individual entity classes may provide their own runStats method
     * with more detailed information.
     * ======================================================================== */


    /**
     * Prints a summary of the given entity type. Subclasses can provide
     * their own runStats method with more detailed information.
     */
    public static void runStats(String className) throws InvalidEntityException { 
        if (className == null || className.isEmpty() || Character.isLowerCase(className.charAt(0))) {
            throw new InvalidEntityException(className);
        }

        List<Entity> instances = getInstances(className);
        try {
            Class<?> genClass = Class.forName(className);
            if (!Entity.class.isAssignableFrom(genClass)) {
                throw new InvalidEntityException(className);
            }
            Method stats = genClass.getMethod("runStats", List.class);
            stats.invoke(null, instances);
        } catch (NoSuchMethodException e) {
            System.out.print(instances.size());
        } catch (InvalidEntityException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidEntityException(className);
        }
    }

    /* ========================================================================
     * World grid helpers — DO NOT MODIFY
     * ======================================================================== */

    private static int convertTo1D(int x, int y) {
        return y * Params.world_width + x;
    }

    private static void addToWorld(Entity entity) {
        world.get(convertTo1D(entity.x_coord, entity.y_coord)).add(entity);
        hasWalked.get(convertTo1D(entity.x_coord, entity.y_coord)).add(false);
    }

    private static void removeFromWorld(Entity entity) {
        List<Entity> location = world.get(convertTo1D(entity.x_coord, entity.y_coord));
        int index = location.indexOf(entity);
        if (index >= 0) {
            location.remove(index);
            hasWalked.get(convertTo1D(entity.x_coord, entity.y_coord)).remove(index);
        }
    }

    private static void markAsWalked(Entity entity) {
        List<Entity> location = world.get(convertTo1D(entity.x_coord, entity.y_coord));
        int index = location.indexOf(entity);
        if (index >= 0) {
            hasWalked.get(convertTo1D(entity.x_coord, entity.y_coord)).set(index, true);
        }
    }

    private static boolean checkIfWalked(Entity entity) {
        List<Entity> location = world.get(convertTo1D(entity.x_coord, entity.y_coord));
        int index = location.indexOf(entity);
        if (index >= 0) {
            return hasWalked.get(convertTo1D(entity.x_coord, entity.y_coord)).get(index);
        }
        return false;
    }

    /* ========================================================================
     * TestEntity — DO NOT MODIFY
     * Used internally for testing. You do not need to use this class.
     * ======================================================================== */

    static abstract class TestEntity extends Entity {

        protected void setX_coord(int new_x_coord) {
            super.x_coord = new_x_coord;
        }

        protected void setY_coord(int new_y_coord) {
            super.y_coord = new_y_coord;
        }

        protected int getX_coord() {
            return super.x_coord;
        }

        protected int getY_coord() {
            return super.y_coord;
        }

        protected static List<Entity> getPopulation() {
            return population;
        }

        protected static List<Entity> getBabies() {
            return babies;
        }
    }

    /* ========================================================================
     * World management
     * ======================================================================== */

    public static void clearWorld() {
        population.clear();
        babies.clear();
        for (List<Entity> location : world) {
            location.clear();
        }
        for (List<Boolean> walkStatus : hasWalked) {
            walkStatus.clear();
        }
    }

    /**
     * Advances the simulation by one time step. The order of operations
     * within a single time step is:
     *   1. Each existing entity performs its action for this step
     *   2. Resolve encounters — when multiple entities share a location
     *   3. Deduct rest energy cost and remove dead entities (energy <= 0)
     *   4. Add any offspring born this step to the world
     *   5. Reset movement tracking for the next step
     *   6. Spawn new PowerCells (Params.refresh_powercell_count per step)
     */
    public static void worldTimeStep() { 
        try {
            // 1. Each existing entity performs its action for this step
            runTimesteps(); 

            // 2. Resolve encounters — when multiple entities share a location 
            
            solveEncounters(); 

            // 3. Deduct rest energy cost and remove dead entities (energy <= 0)
            removeDeadEntities(); 

            // 4. Add any offspring born this step to the world
            addOffspring();  

            //  5. Reset movement tracking for the next step  
            resetMovement();

            // 6. Spawn powercells 
            spawnPowerCells(); 
            
        }
         catch (Exception e) {
        }
    }  

    /**
     * Resolves all encounters where multiple entities occupy the same location.
     * For each location with 2+ entities, the resolution order is:
     * Check if either is a powercell and can be consumed. 
     * Check if pair is engineer -> MaintenaceBot. Engineers donate energy at 115. 
     * Fight, winner will be the Entity with greater energy and will collect 
     * half the losers energy.
     * 
     */
    private static void solveEncounters(){
        if (world == null || world.isEmpty()) {
            return;
        }

        for (List<Entity> location : world) {
            if (location == null || location.isEmpty()) {
                continue;
            }

            while (location.size() > 1) {
                Entity first = location.get(0);
                Entity second = location.get(1);

                if (first == null) {
                    location.remove(0);
                    continue;
                }
                if (second == null) {
                    location.remove(1);
                    continue;
                }

                if (first.getEnergy() <= 0) {
                    removeFromWorld(first);
                    population.remove(first);
                    continue;
                }
                if (second.getEnergy() <= 0) {
                    removeFromWorld(second);
                    population.remove(second);
                    continue;
                }


                // PowerCells automatically give energy to opponent
                if (first instanceof PowerCell) {
                    second.setEnergy(second.getEnergy() + (first.getEnergy() / 2));
                    first.setEnergy(0);
                    removeFromWorld(first);
                    population.remove(first);
                    continue;
                }
                if (second instanceof PowerCell) {
                    first.setEnergy(first.getEnergy() + (second.getEnergy() / 2));
                    second.setEnergy(0);
                    removeFromWorld(second);
                    population.remove(second);
                    continue;
                }

                // Engineer energy transfer to MaintenanceBot
                boolean handledWithoutFight = false;
                if (first instanceof Engineer && second instanceof MaintenanceBot) {
                    if (first.getEnergy() >= 115) {
                        int transferAmount = 10;
                        first.setEnergy(first.getEnergy() - transferAmount);
                        second.setEnergy(second.getEnergy() + transferAmount);
                        // Engineer walks away after giving energy
                        removeFromWorld(first);
                        move(first, getRandomInt(8), 1);
                        first.energy -= Params.walk_energy_cost;
                        addToWorld(first);
                        handledWithoutFight = true;
                    }
                } else if (second instanceof Engineer && first instanceof MaintenanceBot) {
                    if (second.getEnergy() >= 115) {
                        int transferAmount = 10;
                        second.setEnergy(second.getEnergy() - transferAmount);
                        first.setEnergy(first.getEnergy() + transferAmount);
                        // Engineer walks away after giving energy
                        removeFromWorld(second);
                        move(second, getRandomInt(8), 1);
                        second.energy -= Params.walk_energy_cost;
                        addToWorld(second);
                        handledWithoutFight = true;
                    }
                }

                if (handledWithoutFight) {
                    break;
                }

                fighters[0] = first;
                fighters[1] = second;

                boolean firstWillFight = first.fight(second.toString());
                boolean secondWillFight = second.fight(first.toString());

                fighters[0] = null;
                fighters[1] = null;

                // check again in case entity dies from running
                if (first.getEnergy() <= 0) {
                    removeFromWorld(first);
                    population.remove(first);
                    continue;
                }
                if (second.getEnergy() <= 0) {
                    removeFromWorld(second);
                    population.remove(second);
                    continue;
                }

                if (first.x_coord != second.x_coord || first.y_coord != second.y_coord) {
                    continue;
                }

                if (!firstWillFight && !secondWillFight) {
                    boolean moved = attemptEncounterEscape(second);
                    if (!moved) {
                        moved = attemptEncounterEscape(first);
                    }
                    if (moved) {
                        continue;
                    }
                }

                // The one with more energy wins
                Entity winner;
                Entity loser;
                if (first.getEnergy() >= second.getEnergy()) {
                    winner = first;
                    loser = second;
                } else {
                    winner = second;
                    loser = first;
                }

                winner.setEnergy(winner.getEnergy() + (loser.getEnergy() / 2));
                loser.setEnergy(0);
                removeFromWorld(loser);
                population.remove(loser);
            }
        }
    }

    /**
     * Attempts to make an entity escape from an encounter by moving to
     * an adjacent occupied cell in a random direction. Used for entities that
     * are sometimes peaceful. 
     * 
     * @param boolean- true if the entity successfully moved to a different location.
     * false if no valid escape location exists (all adjacent cells empty).
     * 
     */
    private static boolean attemptEncounterEscape(Entity entity) {
        if (entity == null || entity.getEnergy() <= 0) {
            return false;
        }

        int originalX = entity.x_coord;
        int originalY = entity.y_coord;

        fighters[0] = entity;
        fighters[1] = null;
        entity.walk(getRandomInt(8));
        fighters[0] = null;
        fighters[1] = null;

        return entity.x_coord != originalX || entity.y_coord != originalY;
    }


    /**
     * Call do timestep for all Entities in the world.
     */
    private static void runTimesteps(){ 
        try {  
            // (1) all existing do specfic timestpe
            for(Entity entity: population){ 
                entity.doTimeStep(); 
            }

        } 
        catch (Exception e) {  
            e.printStackTrace();
        }
    } 

    /**
     * Remove all entities that are dead. 
     */
    private static void removeDeadEntities() { 
        if (population == null || population.isEmpty()) {
            return;
        }

        Iterator<Entity> iterator = population.iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            if (entity == null) {
                iterator.remove();
                continue;
            }

            entity.setEnergy(entity.getEnergy() - Params.rest_energy_cost);
            if (entity.getEnergy() <= 0) {
                removeFromWorld(entity);
                iterator.remove();
            }
        }
    }

    /**
     * Add all current offspring in the babies buffer to the world. 
     */
    private static void addOffspring(){  
        if (babies == null || babies.isEmpty()) {
            return;
        }
        if (population == null) {
            return;
        }

        for (Entity entity : new ArrayList<>(babies)) {
            if (entity == null) {
                continue;
            }
            population.add(entity);
            addToWorld(entity);
        }
        babies.clear();
    }  

    /** 
     *  Reset movement for all entities in the world. 
     * */ 
    private static void resetMovement(){ 
        try {  
            for (List<Boolean> walked : hasWalked){ 
                for (int i = 0; i < walked.size(); i++){ 
                    walked.set(i,false); 
                }
            }
        } 
        catch (Exception e) { 
            e.printStackTrace();
        }
    }  

    /**
     * Spawn powercells based on parameters given. 
     */
    private static void spawnPowerCells(){    
        try { 
            for (int i = 0; i < Params.refresh_powercell_count; i++){ 
                makeEntity("PowerCell"); 

            }
        } catch (Exception e) {
        }
    } 


    
    

    /* ========================================================================
     * Display — DO NOT MODIFY
     * ======================================================================== */

    public static void displayWorld() {
        System.out.print("+");
        for (int i = 0; i < Params.world_width; i++) {
            System.out.print("-");
        }
        System.out.println("+");

        for (int y = 0; y < Params.world_height; y++) {
            System.out.print("|");
            for (int x = 0; x < Params.world_width; x++) {
                List<Entity> location = world.get(convertTo1D(x, y));
                if (location.isEmpty()) {
                    System.out.print(" ");
                } else {
                    System.out.print(location.get(0).toString());
                }
            }
            System.out.println("|");
        }

        System.out.print("+");
        for (int i = 0; i < Params.world_width; i++) {
            System.out.print("-");
        }
        System.out.println("+");
    }
}
