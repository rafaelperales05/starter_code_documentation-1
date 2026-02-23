import java.io.*;
import java.util.Scanner;

public class Main {

    static Scanner kb;
    private static String inputFile;
    static ByteArrayOutputStream testOutputString;
    private static boolean DEBUG = false;
    static PrintStream old = System.out;

    public static void main(String[] args) {
        if (args.length != 0) {
            try {
                inputFile = args[0];
                kb = new Scanner(new File(inputFile));
            } catch (FileNotFoundException e) {
                System.out.println("USAGE: java Main OR java Main <input file> <test output>");
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.out.println("USAGE: java Main OR java Main <input file>  <test output>");
            }
            if (args.length >= 2) {
                if (args[1].equals("test")) {
                    testOutputString = new ByteArrayOutputStream();
                    PrintStream ps = new PrintStream(testOutputString);
                    old = System.out;
                    System.setOut(ps);
                }
            }
        } else {
            kb = new Scanner(System.in);
        }

        /* Do not alter the code above for your submission. */
        /* Write your code below. */

        String in = "";

        while (!in.equals("quit")) {
            System.out.print("space_station> ");
            in = kb.nextLine();
            String[] command = in.split("\\s+", -2);

            switch (command[0]) {

                //   show                  - Display the world grid
                //   make Type [count]     - Create one or more entities
                //   step [count]          - Advance simulation by one or more steps
                //   seed number           - Set the random seed
                //   stats Type            - Show statistics for an entity type
                //
                // Invalid commands should print: "invalid command: <the input>"
                // Commands with bad arguments should print: "error processing: <the input>"
                case "show": 
                    if (command.length != 1) {
                        printProcessingError(command);
                        break;
                    }
                    Entity.displayWorld();
                    break;

                case "make":  
                    makeType(command);
                    break;
                
                case "step": 
                    advanceTime(command);
                    break;

                case "seed": 
                    setSeed(command);
                    break;

                case "stats": 
                    runTypeStats(command);
                    break;
                
                case "quit":
                    if (command.length != 1) {
                        printProcessingError(command);
                    }
                    break;

                case "help":
                    if (command.length != 1) {
                        printProcessingError(command);
                        break;
                    }
                    printHelp();
                    break;

                case "?":
                    if (command.length != 1) {
                        printProcessingError(command);
                        break;
                    }
                    printCommands();
                    break;

                case "": 
                    break;

                default:
                    System.out.println("invalid command: " + errorToString(command));
            }
        }


        /* Write your code above */
        System.out.flush();
    }   

    /**
     * Handles the "stats Type" command to display statistics for a given entity type.
     * Validates the entity type and dispatches to the appropriate stats method.
     * 
     * @param command The command array where command[0] is "stats" and command[1] is the entity type name
     */
    private static void runTypeStats(String[] command) { 
         try {
            if (command.length != 2) {
                printProcessingError(command);
                return;
            }
            String entityType = command[1];

            if (entityType == null || entityType.isEmpty() || Character.isLowerCase(entityType.charAt(0))) {
                printProcessingError(command);
                return;
            }
        
            // Check if class exists and is valid
            Class<?> entityClass = Class.forName(entityType);

            if (!Entity.class.isAssignableFrom(entityClass)) {
                printProcessingError(command);
                return;
            }

            // Call static runStats and then handle type-specific output
            if ("PowerCell".equals(entityType)) {
                Entity.runStats(entityType);
                System.out.print(" entities as follows -- *:");
                Entity.runStats(entityType);
                System.out.println();
            } else if ("MaintenanceBot".equals(entityType)) {
                java.util.List<Entity> bots = Entity.getInstances(entityType);
                MaintenanceBot.runStats(bots);
            } else if ("Commander".equals(entityType)) {
                java.util.List<Entity> commanders = Entity.getInstances(entityType);
                Commander.runStats(commanders);
            } else if ("Engineer".equals(entityType)) {
                java.util.List<Entity> engineers = Entity.getInstances(entityType);
                Engineer.runStats(engineers);
            } else {
                // Default: just print count
                Entity.runStats(entityType);
                System.out.println();
            }
        
        } catch (InvalidEntityException e) {
            printProcessingError(command);
        } catch (Exception e) {
            printProcessingError(command);
        }

    } 

    /**
     * Handles the "seed number" command to set the random number generator seed.
     * Validates that the seed is a valid long integer (positive or negative).
     * 
     * @param command The command array where command[0] is "seed" and command[1] is the seed value
     */
    private static void setSeed(String[] command) {
        try {
            if (command.length != 2) {
                printProcessingError(command);
                return;
            }

            Long seed = parseLongSeed(command[1]);
            if (seed == null) {
                printProcessingError(command);
                return;
            }

            Entity.setSeed(seed); 
        } 
        catch (Exception e) { 
            printProcessingError(command);
        }
 
    }
    
    /**
     * Handles the "make Type [count]" command to create one or more entities.
     * If count is omitted, creates exactly one entity. Validates that the entity
     * type exists and the count is a positive integer.
     * 
     * @param command The command array where command[0] is "make", command[1] is the entity type,
     *                and command[2] is the optional count
     */
    private static void makeType(String[] command) {
        try {
            if (!(command.length == 2 || command.length == 3)) {
                printProcessingError(command);
                return;
            }
            
            String entityType = command[1];
            int count = 1; // default to 1 if not specified
            
            if (command.length == 3) {
                Integer parsedCount = parsePositiveInt(command[2]);
                if (parsedCount == null) {
                    printProcessingError(command);
                    return;
                }
                count = parsedCount;
            }
            
            for (int i = 0; i < count; i++){ 
                Entity.makeEntity(entityType); 
            } 
        } 
        catch (InvalidEntityException e) {
            printProcessingError(command);
        }
        catch (Exception e) { 
            printProcessingError(command);
        }
    } 

    /**
     * Handles the "step [count]" command to advance the simulation by one or more time steps.
     * If count is omitted, advances by exactly one step. Validates that count is a positive integer.
     * 
     * @param command The command array where command[0] is "step" and command[1] is the optional count
     */
    private static void advanceTime(String[] command){ 
        try {
            if (!(command.length == 1 || command.length == 2)) {
                printProcessingError(command);
                return;
            }
            
            int count = 1; // default to 1 if not specified
            
            if (command.length == 2) {
                Integer parsedCount = parsePositiveInt(command[1]);
                if (parsedCount == null) {
                    printProcessingError(command);
                    return;
                }
                count = parsedCount;
            }
            
            for (int i = 0; i < count; i++){ 
                Entity.worldTimeStep(); 
            } 
        } catch (Exception e) {
            printProcessingError(command);
        }
    }

    /**
     * Parses a string into a positive integer.
     * Returns null if the string is not a valid positive integer.
     * 
     * @param value The string to parse
     * @return The parsed positive integer, or null if invalid
     */
    private static Integer parsePositiveInt(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (char ch : value.toCharArray()) {
            if (!Character.isDigit(ch)) {
                return null;
            }
        }
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parses a string into a Long seed value (accepts positive or negative integers).
     * Returns null if the string is not a valid long integer.
     * 
     * @param value The string to parse
     * @return The parsed Long value, or null if invalid
     */
    private static Long parseLongSeed(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        String digits = value;
        if (digits.startsWith("-")) {
            digits = digits.substring(1);
        }
        if (digits.isEmpty()) {
            return null;
        }
        for (char ch : digits.toCharArray()) {
            if (!Character.isDigit(ch)) {
                return null;
            }
        }
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Prints an error message indicating that the given command had processing errors.
     * Outputs: "error processing: <command string>"
     * 
     * @param command The command array that failed to process
     */
    private static void printProcessingError(String[] command) {
        System.out.println("error processing: " + errorToString(command));
    }


    /**
     * Prints a high-level overview of the simulation
     */
    private static void printHelp() {
        System.out.println("=== Space Station Simulation ===");
        System.out.println();
        System.out.println("You are managing a space station populated by autonomous entities.");
        System.out.println("Your goal is to create a self-sustaining ecosystem where entity");
        System.out.println("populations reach a dynamic equilibrium.");
        System.out.println();
        System.out.println("Entity types:");
        System.out.println("  PowerCell (*)       - Energy producer. Gains +" + Params.solar_energy_amount + " energy per step via solar");
        System.out.println("                        charging. Does not fight. " + Params.refresh_powercell_count + " new PowerCell(s)");
        System.out.println("                        spawn automatically each step.");
        System.out.println("  MaintenanceBot (M)  - Uses a genetic algorithm to evolve movement.");
        System.out.println("                        Always fights. Reproduces with mutation.");
        System.out.println("  Commander (C)       - Always fights. Keeps population under control.");
        System.out.println("  Engineer (E)        - Keeps systems and populations healthy. Avoids fights and gives energy back to other entities.");
        System.out.println();
        System.out.println("World:");
        System.out.println("  Grid size:    " + Params.world_width + " x " + Params.world_height + " (wraps around at edges)");
        System.out.println("  Coordinates:  (0,0) is top-left");
        System.out.println();
        System.out.println("Energy:");
        System.out.println("  Starting energy:    " + Params.start_energy);
        System.out.println("  Walk cost:          " + Params.walk_energy_cost + " (move 1 square)");
        System.out.println("  Run cost:           " + Params.run_energy_cost + " (move 2 squares)");
        System.out.println("  Rest cost per step: " + Params.rest_energy_cost);
        System.out.println("  Min reproduce energy: " + Params.min_reproduce_energy);
        System.out.println();
        System.out.println("Movement directions: 0=E, 1=NE, 2=N, 3=NW, 4=W, 5=SW, 6=S, 7=SE");
        System.out.println();
        System.out.println("Encounters:");
        System.out.println("  When two entities share a location, each decides to fight or not.");
        System.out.println("  If both fight, the one with more energy wins.");
        System.out.println("  The winner absorbs half the loser's energy. The loser dies.");
        System.out.println();
        System.out.println("Type ? for a list of commands.");
    }

    /**
     * Prints a brief list of available commands
     */
    private static void printCommands() {
        System.out.println("Commands:");
        System.out.println("  show                  - Display the current world grid");
        System.out.println("  make Type [count]     - Create one or more entities of the given type");
        System.out.println("  step [count]          - Advance the simulation by one or more time steps");
        System.out.println("  seed number           - Set the random number seed for reproducibility");
        System.out.println("  stats Type            - Show statistics for an entity type");
        System.out.println("  help                  - Show simulation overview and parameters");
        System.out.println("  ?                     - Show this list of commands");
        System.out.println("  quit                  - Exit the simulation");
    }

    /**
     * Converts command array back to string for error messages
     */
    private static String errorToString(String[] command) {
        String result = "";
        for (int i = 0; i < command.length; i++) {
            if (i > 0) result += " ";
            result += command[i];
        }
        return result;
    }
}
