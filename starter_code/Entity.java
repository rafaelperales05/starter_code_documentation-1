import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;

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
     * it divides its energy with the baby and it is placed adjacent to the parent depending 
     * on input direction.
     * @param Parent - Parent entity that creates the offspring
     * @param Direction - The direciton indicates in which block the child will move to be adjacent
     *  
     */  
    protected static Entity reproduce(Entity parent, int direction){   
        
        //null check and check for minumum energy 
        if (parent == null || parent.getEnergy() < Params.min_reproduce_energy){ 
            return null;
        } 

        try {

        String parentClass = parent.getClass().getSimpleName(); 
        Class<?> genClass = Class.forName(parentClass);
        Entity child = (Entity) genClass.getDeclaredConstructor().newInstance();

        //set energy 
        int parentEnergy = parent.getEnergy();  
        child.setEnergy((int) Math.floor(parentEnergy/2));  
        parent.setEnergy((int) Math.ceil(parentEnergy/2)); 

        // set offspring coordinates
        child.x_coord = parent.x_coord; 
        child.y_coord = parent.y_coord;  
        move(child,direction,1);

            
        // add to babies  
        babies.add(child);  
        return child; 

            
        } catch (Exception e) { 
            e.printStackTrace();
            return null;
        }
    }
    

    /* ========================================================================
     * Abstract methods
     * 
     * Think carefully about what behaviors differ between entity types and
     * must be implemented by each subclass.
     * ======================================================================== */


    protected  abstract void doTimeStep();  
    @Override
    public  abstract String toString(); 

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
    protected static List<Entity> getInstances(String className) throws InvalidEntityException {   

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
     * runStats prints a summary of the given entity list, this defualt version 
     * counts how many of each display character exist. 
     * @param className - string variable of instances requested
     * 
     */
    public void runStats(String className) throws InvalidEntityException { 
        
        if (className == null || className.isEmpty() || Character.isLowerCase(className.charAt(0))){ 
                throw new InvalidEntityException(className);  
        }       
        try { 
            Class<?> genClass = Class.forName(className);  

            // error check for invalid type
            if (!Entity.class.isAssignableFrom(genClass)){ 
                throw new InvalidEntityException(className); 
            } 
            List<Entity> instances = getInstances(className); 
            System.out.println(instances.size() + " total " + className + "    ");
        } 
        catch (ReflectiveOperationException e) {
            throw new InvalidEntityException(className); 
        } 
        catch (Exception e){ 
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

    // Implement worldTimeStep and any helper methods it needs  

    /** 
     * Advances the simulation by one time step. 
     */
    public static void worldTimeStep() { 
        try {
            // 1. Each existing entity performs its action for this step
            runTimesteps(); 

            // 2. Resolve encounters — when multiple entities share a location 
            // TODO
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

    // TODO
    private static void solveEncounters(){  

        try {   
            for (List<Entity> location : world){ 

            }
        } 
        catch(Exception e) { 
            e.printStackTrace();
        }
    }


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
    private static void removeDeadEntities() { 

        try { 
            // (3) deduct rest energy cost 
            for (Entity entity : population) {  
                entity.setEnergy(entity.getEnergy() - Params.rest_energy_cost); 
                if (entity.getEnergy() <= 0){ 
                    population.remove(entity); 
                    removeFromWorld(entity); 
                }
            } 
            
        } catch (Exception e) { 
            e.printStackTrace();
        }
    }

    private static void addOffspring(){  
        try {   
            // (4) add offspring  
            for (Entity entity : babies){ 
                population.add(entity); 
                addToWorld(entity); 
                babies.remove(entity);
            }
        } 
        catch (Exception e) { 
            e.printStackTrace();
        }
    } 
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
