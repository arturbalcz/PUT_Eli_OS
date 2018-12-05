package shell;

import utils.Utils;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.Scanner;
import java.lang.System;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;


/**
 *  Shares methods to communicate with user.
 *  Stores all commends and connected methods in map.
 *  All methods need to have one parameter - list of parameters from user.
 */
public class Shell {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SS");
    private static final PrintStream standardOut = System.out;

    /**
     * Main map that stores all of connected commends and methods references
     * Method need to have ArrayList<String> as array of parameters from user
     */
    private static HashMap<String, Consumer<ArrayList<String>>> CommandTable = new HashMap<>();
    static {
        CommandTable.put("time", Shell::time );
        CommandTable.put("help", Shell::help );
        CommandTable.put("exit", Shell::exit );
        CommandTable.put("log", Commands::logging);
        CommandTable.put("test", Commands::test);
    }
    private static Integer size = CommandTable.size();

    /**
     * Prints parameter to the console
     * @param msg message to print for user
     */
    public static void print(String msg) {
        standardOut.println(msg);
    }

    /**
     * Gets from console whole line inserted by user
     * @return input from user
     */
    public static String read() {
        Scanner standardIn = new Scanner(System.in);
        return standardIn.nextLine();
    }

    private static void time(ArrayList<String> argv) {
        String times = formatter.format(LocalDateTime.now());
        Utils.step("Printing time for user.");
        print(times);
    }

    private static void help(ArrayList<String> argv) {
        Set<String> keys = CommandTable.keySet();
        Utils.step("Printing help for user.");
        for (String command : keys) {
            print(command.toUpperCase());
        }
    }

    private static void exit(ArrayList<String> argv) {
        Utils.log("Exiting by user");
    }

    /**
     * Interprets user input and run method found in {@link #CommandTable}
     * @return condition to close system
     */
    public static boolean interpret( ) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add("ex");
        String input = read();
        arguments = new ArrayList<>(Arrays.asList(input.split("\\s")));
        for (String x : arguments) {
            Utils.log(x);
        }
        String command = arguments.get(0);

        if (CommandTable.get(command) == null) {
            standardOut.println("\"" + input + "\" can't resolve command");
        }
        else {
            try {
                CommandTable.get(command).accept(arguments);
            }
            catch (IndexOutOfBoundsException e) {
                Utils.step("Invalid number of arguments for " + command);
                print("invalid number of arguments");
            }
            return command.equals("exit");
        }
        return false;

    }

}