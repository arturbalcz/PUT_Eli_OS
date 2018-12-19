package shell;

import filesystem.Disk;
import utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.Consumer;


/**
 *  Shares methods to communicate with user.
 *  Stores all commends and connected methods in map.
 *  All methods need to have one parameter - list of parameters from user.
 */
public class Shell {

    private static final int READ_TIMEOUT = 1000;
    private static final PrintStream standardOut = System.out;
    static boolean exiting = false;

    private static boolean empty = true;

    /**
     * Main map that stores all of connected commends and methods references
     * Method need to have ArrayList<String> as array of parameters from user
     */
    static HashMap<String, Consumer<ArrayList<String>>> CommandTable = new HashMap<>();
    static {
        CommandTable.put("time", Commands::time );
        CommandTable.put("help", Commands::help );
        CommandTable.put("exit", Commands::exit );
        CommandTable.put("log", Commands::logging);
        CommandTable.put("step", Commands::stepping);
        CommandTable.put("test", Commands::test);
        CommandTable.put("disk", Disk::test);
        CommandTable.put("file", Commands::file);
        CommandTable.put("com", Commands::com);
        CommandTable.put("cp", Commands::cp);
        CommandTable.put("lpq", Commands::lpq);
        CommandTable.put("lp", Commands::lp);
        CommandTable.put("rp", Commands::rp);
        CommandTable.put("dp", Commands::dp);
        CommandTable.put("update", Commands::update);
    }

    /**
     * Prints parameter to the console with new line
     * @param msg message to print for user
     */
    public static void println(String msg) {
        standardOut.println(msg);
    }

    /**
     * Prints parameter to the console without new line
     * @param msg message to print for user
     */
    public static void print(String msg) {
        standardOut.print(msg);
    }

    /**
     * Gets from console whole line inserted by user
     * @return input from user
     */
    public static String read() {
        Scanner standardIn = new Scanner(System.in);
        return standardIn.nextLine();
    }

    /**
     * Interprets user input and run method found in {@link #CommandTable}
     * @return condition to close system
     */
    public static boolean interpret( ) throws IOException {
        if (empty) {
            standardOut.print("> ");
            empty = false;
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < READ_TIMEOUT && !in.ready()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String input;
        if (in.ready()) input = in.readLine();
        else throw new IOException();

        input = input.trim();
        if (input.isEmpty()) {
            return exiting;
        }
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(input.split("\\s")));

        // printing arguments for debug
        Utils.log("Printing all arguments");
        String args = "";
        StringBuilder sB = new StringBuilder(args);
        for (String x : arguments) {
            sB.append(x);
            sB.append(", ");
        }
        Utils.log(sB.toString());
        String command = arguments.get(0);

        if (CommandTable.get(command) == null) {
            standardOut.println("\"" + input + "\" can't resolve command");
        }
        else {
            try {
                CommandTable.get(command).accept(arguments);
            }
            catch (IndexOutOfBoundsException e) {
                Utils.log("Invalid number of arguments for " + command, true);
                println("invalid number of arguments");
            }
        }

        empty = true;
        return exiting;
    }

}