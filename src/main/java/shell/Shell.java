package shell;

import filesystem.Directories;
import filesystem.Directory;
import filesystem.Disk;
import utils.Utils;


import java.io.IOException;
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
    private static boolean echo = false;

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
        CommandTable.put("echo", Commands::echo);
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
        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            String a;
            while (true)
            {
                a = sc.nextLine();

                try {
                    if (threadedInput.size() > 0) {
                        // Queue is full
                        Thread.sleep(READ_TIMEOUT);
                    }
                    else  {
                        threadedInput.add(a);
                        if (a.equals("exit")) break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
            }


        }).start();
    }

    public static void echoOn() { echo = true; }
    public static void echoOff() { echo = false; }

    /**
     * Prints parameter to the console with new line
     * @param msg message to print for user
     */
    public static void println(String msg) {
        if (echo) standardOut.println(msg);
    }

    /**
     * Prints parameter to the console without new line
     * @param msg message to print for user
     */
    public static void print(String msg) {
        if (echo) standardOut.print(msg);
    }

    /**
     * Tries to get from console whole line inserted by user
     * @return input from user
     */
    private static String readOnce() throws IOException {
        final long startTime = System.currentTimeMillis();
        String mes;
        do {
            mes = threadedInput.poll();

            // slow down a bit ;)
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while ((System.currentTimeMillis() - startTime) < READ_TIMEOUT && mes == null);
        if (mes == null) {throw new IOException(); }
        return mes;
    }

    public static String read() {
        String result = "";
        while(result.isEmpty()) {
            try {
                result = readOnce();
            } catch (IOException ignored) {
            }
        }
        return result;
    }

    /**
     * Interprets user input and run method found in {@link #CommandTable}
     * @return condition to close system
     */
    public static boolean interpret( ) throws IOException {
        //check if current line is empty and ready for new interpreting session
        if (empty) {
            String history = "";
	        for (Directory e: Directories.getHistory()){
	            history += e.getName() + "\\";
	        }
	        String dirName = Directories.getCurrentDir().getName();
	        if (dirName.charAt(dirName.length()-1) == ':') dirName += "\\";
        	print(history + dirName + "> ");
            empty = false;
        }

        String input = readOnce().trim().toLowerCase();
        if (input.isEmpty()) return exiting;

        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(input.split("\\s")));

        // printing arguments for debug
        Utils.log("Printing all arguments");
        String args = "";
        StringBuilder sB = new StringBuilder(args);
        sB.append("[");
        for (String x : arguments) {
            sB.append(x).append(", ");
        }
        sB.delete(sB.length()-2, sB.length()-1);
        sB.append("]");
        Utils.log(sB.toString());

        //finding command in CommandTable
        String command = arguments.get(0);
        if (CommandTable.get(command) == null) {
            //if not found
            standardOut.println("\'" + input + "\'  " +
                    "is not recognized as an internal or external command,\n" +
                    "operable program or batch file.");
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