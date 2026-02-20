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

                // TODO: Implement the remaining commands:
                //   show                  - Display the world grid
                //   make Type [count]     - Create one or more entities
                //   step [count]          - Advance simulation by one or more steps
                //   seed number           - Set the random seed
                //   stats Type            - Show statistics for an entity type
                //
                // Invalid commands should print: "invalid command: <the input>"
                // Commands with bad arguments should print: "error processing: <the input>"

                case "quit":
                    break;

                case "help":
                    printHelp();
                    break;

                case "?":
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
        System.out.println("  Commander (C)       - Custom entity (your design).");
        System.out.println("  Engineer (E)        - Custom entity (your design).");
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
