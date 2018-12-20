package shell;

import filesystem.Directories;
import filesystem.Directory;
import filesystem.Disk;
import utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;


/**
 *  Shares methods to communicate with user.
 *  Stores all commends and connected methods in map.
 *  All methods need to have one parameter - list of parameters from user.
 */
public class Shell {

    private static final int READ_TIMEOUT = 1000;
    private static final PrintStream standardOut = System.out;
    private static Queue<String> threadedInput = new ConcurrentLinkedQueue<>();
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
        CommandTable.put("rm", Commands::rm);
        CommandTable.put("rmdir", Commands::rmdir);
        CommandTable.put("more", Commands::more);
        CommandTable.put("cd", Commands::cd);
        CommandTable.put("mkdir", Commands::mkdir);
        CommandTable.put("dir", Commands::dir);
        CommandTable.put("copy", Commands::copy);
        CommandTable.put("tree", Commands::tree);
        CommandTable.put("edit", Commands::edit);
        CommandTable.put("com", Commands::com);
        CommandTable.put("cp", Commands::cp);
        CommandTable.put("lpq", Commands::lpq);
        CommandTable.put("lp", Commands::lp);
        CommandTable.put("rp", Commands::rp);
        CommandTable.put("dp", Commands::dp);
        CommandTable.put("update", Commands::update);

        //Creating thread with input from console
        Thread inputT = new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            String a;
            while (true)
            {
                a = sc.nextLine();

                try {
                    if (threadedInput.size() > 0) {
                        //System.out.println("Queue is full");
                        Thread.sleep(1000);
                    }
                    else  {
                        threadedInput.add(a);
                        if (a.equals("exit")) break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
            }


        });
        inputT.start();
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
            String history = "";
	        for (Directory e: Directories.getHistory()){
	            history += e.getName() + "/";
	        }
        	print(history + Directories.getCurrentDir().getName() + "> ");
            empty = false;
        }

        String input;

        try {
            String mes = threadedInput.poll();
            if (mes == null) {
                //queue empty, skipping
                throw new IOException();
            }
            //System.out.println("Response: " + mes);
            else input = mes;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            throw new IOException();
        }


        if (input.isEmpty()) {
            return exiting;
        }
        input = input.trim();
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