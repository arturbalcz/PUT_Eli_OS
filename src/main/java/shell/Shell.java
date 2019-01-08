package shell;

import filesystem.Directories;
import filesystem.Directory;
import filesystem.Disk;
import utils.Utils;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Queue;
import java.util.Scanner;
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
    static HashMap<String, String> HelpingTable = new HashMap<>();
    static {
        CommandTable.put("time", Commands::time );
        HelpingTable.put("time", "Displays the system time.\n");
        CommandTable.put("help", Commands::help );
        HelpingTable.put("help", "Provides help information for PUT Elita OS commands.\n\nHELP [command]\n\n\tcommand - displays help information on that command.\n");
        CommandTable.put("exit", Commands::exit );
        HelpingTable.put("exit", "Quits the system.\n\nEXIT [t]\n\n\tt - if set, quits the system without confirm in step log\n");
        CommandTable.put("log", Commands::logging);
        HelpingTable.put("log", "Turns logging on or off.\n\nLOG [/ON | /OFF]\n");
        CommandTable.put("step", Commands::stepping);
        HelpingTable.put("step", "Turns step work on or off.\n\nSTEP [/ON][/OFF]\n");
        CommandTable.put("echo", Commands::echo);
        HelpingTable.put("echo", "Turns printing on or off.\n\nECHO [/ON][/OFF]\n");
        CommandTable.put("test", Commands::test);
        HelpingTable.put("test", "Testing!!.\n");
        CommandTable.put("disk", Disk::test);
        HelpingTable.put("disk", "Displays the raw contents of a local disk.\n");
        CommandTable.put("file", Commands::file);
        HelpingTable.put("file", "Create and modify file.\n");
        CommandTable.put("rm", Commands::rm);
        HelpingTable.put("rm", "Deletes one or more files.\n");
        CommandTable.put("rmdir", Commands::rmdir);
        HelpingTable.put("rmdir", "Removes a directory.\n");
        CommandTable.put("more", Commands::more);
        HelpingTable.put("more", "Displays the contents of a text file.\n");
        CommandTable.put("cd", Commands::cd);
        HelpingTable.put("cd", "Displays the name of or changes the current directory.\n");
        CommandTable.put("mkdir", Commands::mkdir);
        HelpingTable.put("mkdir", "Creates a directory.\n");
        CommandTable.put("dir", Commands::dir);
        HelpingTable.put("dir", "Displays a list of files and subdirectories in a directory.\n");
        CommandTable.put("copy", Commands::copy);
        HelpingTable.put("copy", "Copies one files to another location.\n");
        CommandTable.put("tree", Commands::tree);
        HelpingTable.put("tree", "Graphically displays the directory structure of a drive or path.\n");
        CommandTable.put("edit", Commands::edit);
        HelpingTable.put("edit", "Opens editor with given file.\n");
        CommandTable.put("com", Commands::com);
        HelpingTable.put("com", "Compiles given program.\n\nCOM filename\n\n\tfilename - Specifies the file to compile.\n");
        CommandTable.put("cp", Commands::cp);
        HelpingTable.put("cp", "Creates process or application.\n");
        CommandTable.put("lpq", Commands::lpq);
        HelpingTable.put("lpq", "Displays all ready tasks.\n");
        CommandTable.put("lp", Commands::lp);
        HelpingTable.put("lp", "Displays all tasks.\n");
        CommandTable.put("tasklist", Commands::tasklist);
        HelpingTable.put("tasklist", "Displays tasks.\n\nTAKSLIST [/N name][/S stateName]\n\n\t/N\tname\t\tSpecifies the name of displayed processes\n\t/S\tstateName\tSpecifies the state of displayed processes\n");
        CommandTable.put("rp", Commands::rp);
        HelpingTable.put("rp", "Displays all currently running tasks.\n");
        CommandTable.put("dp", Commands::dp);
        HelpingTable.put("dp", "Kill or stop a running process or application.\n");
        CommandTable.put("update", Commands::update);
        HelpingTable.put("update", "Updates code of initial programs.\n");
		CommandTable.put("lck", Commands::lck);
		HelpingTable.put("lck", "empty.\n");
		CommandTable.put("vm", Commands::vm);
		HelpingTable.put("vm", "empty.\n");
        
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
        	print(history + dirName + ">");
            empty = false;
        }

        String input = readOnce().trim().toLowerCase();
        if (input.isEmpty()) return exiting;

        boolean echoPrevState = echo;
        if (input.charAt(0) == '@') {
            echoOff();
            input = input.substring(1, input.length());
        }

        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(input.split("\\s")));

        // printing arguments for debug
        Utils.log("Shell.interprete(): arguments="+arguments.toString());

        if (Commands.localExe(arguments)) {
            empty = true;
            return exiting;
        }

        //finding command in CommandTable
        String command = arguments.get(0);
        if (CommandTable.get(command) == null) {
            //if not found
            standardOut.println("\'" + command + "\' " +
                    "is not recognized as an internal or external command,\n" +
                    "operable program or batch file.");
        }
        else {
            try {
                CommandTable.get(command).accept(arguments);
            }
            catch (IndexOutOfBoundsException e) {
                Utils.log("Shell.interprete(): Shell.CommandTable::IndexOutOfBoundsException::"+command+"::"+arguments, true);
                println("invalid number of arguments");
            }
        }

        echo = echoPrevState;
        empty = true;
        return exiting;
    }

}