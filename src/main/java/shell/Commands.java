package shell;

import assembler.Assembler;
import filesystem.Files;
import os.OS;
import processess.PCB;
import processess.PCBList;
import utils.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Set;

/**
 * Stores implementation of every shell command with dependencies on external modules
 * <p>Every method should consume exactly one argument {@code ArrayList<String> args}.
 * For using your command checkout {@link Shell#CommandTable}</p>
 * @see Shell
 */
public interface Commands {

    /** updates code of initial programs */
    static void update(ArrayList<String> args) {
        OS.updateInitialFiles();
    }

    /**
     * Turns logging on or off
     * @param args "on" or "off"
     */
    static void logging(ArrayList<String> args) {
        String help = "Turns logging on or off\n\n" +
                "LOG [/ON][/OFF]\n";
        if(args.size() != 2) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        }
        else {
            String param = args.get(1);
            switch (param.toUpperCase()) {
                case "/ON":
                    Utils.loggingOn();
                    break;
                case "/OFF":
                    Utils.loggingOff();
                    break;
                default:
                    Utils.log("Wrong argument");
                    Shell.println(help);
                    break;
            }
        }
    }

    /**
     * Turns step work on or off
     * @param args "on" or "off"
     */
    static void stepping(ArrayList<String> args) {
        String help = "Turns step work on or off\n\n" +
                "STEP [/ON][/OFF]\n";
        if(args.size() != 2) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        }
        else {
            String param = args.get(1);
            switch (param.toUpperCase()) {
                case "/ON":
                    Utils.stepOn();
                    break;
                case "/OFF":
                    Utils.stepOff();
                    break;
                default:
                    Utils.log("Wrong argument");
                    Shell.println(help);
                    break;
            }
        }
    }

    /**
     * Prints time to user console
     * @param args no effect
     */
    static void time(ArrayList<String> args) {
        String help = "Prints time to user console\n";
        if (args.size() == 2 && args.get(1).equals("/?")) {
            Shell.println(help);
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SS");
        String times = formatter.format(LocalDateTime.now());
        Utils.log("Printing time for user.");
        Shell.println(times);
    }

    /**
     * Prints all available commends
     * @param args no effect
     */
    static void help(ArrayList<String> args) {
        Set<String> keys = Shell.CommandTable.keySet();
        Utils.log("Printing help for user.");
        for (String command : keys) {
            Shell.println(command.toUpperCase());
        }
    }

    /**
     * Exits from system
     * @param args no effect
     */
    static void exit(ArrayList<String> args) {
        String help = "Quits the system.\n";
        if (args.size() == 2 && args.get(1).equals("/?")) {
            Shell.println(help);
            return;
        }
        Utils.log("Exiting by user");
        Shell.exiting = true;
    }

    /**
     * Used for testing shell
     * @param args anything but {@code null}
     */
    static void test(ArrayList<String> args) {
        Utils.step("step work is working" + args.get(1));
        Utils.log("log is working" +  args.get(1));
        Shell.println("command is working " + args.get(1));
    }

    static void file(ArrayList<String> args){

        String help = "Creates and modifies text files.\n\n" +
                "FILE [/C] [/S] [/D] filename\n" +
                "\t/C - Create empty file.\n" +
                "\t/S - Display the content of a text file.\n" +
                "\t/D - Delete specified file.\n";
        if(args.size() != 2 && args.size() != 3 && args.size() != 4) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        }
        else {
            String param = args.get(1);
            switch (param.toUpperCase()) {
                case "/C":
                    Shell.print("@");
                    String input = Shell.read();
                    Files.createFile(args.get(2), input.getBytes());
                    break;
                case "/S":
                    try{
                    byte[] temp= Files.getFile(args.get(2));
                    if(temp[0] != -1){
                        String result = "";
                        for(byte e: temp){
                            result += (char)e + " ";
                        }
                        Shell.println(result);
                    }
                    }
                    catch(IndexOutOfBoundsException e){
                        Shell.println("Invalid index");
                    }
                    break;
                case "/?":
                    Shell.println(help);
                    break;
                case "-?":
                    Shell.println(help);
                    break;
                default:
                    Utils.log("Wrong argument");
                    Shell.println(help);
                    break;
            }

        }
    }

    /**
     * Compiles given program
     * @param args asm program to compile
     */
    static void com(ArrayList<String> args) {
        String help = "Compiles given program\n\n" +
                "COM filename\n";
        if (args.size() == 1) Shell.println("no program file specified");
        if (args.get(1).equals("/?")) Shell.println(help);
        else {
            final String fileName = args.get(1);
            final byte[] code = Files.getCleanFile(fileName);

            Assembler assembler = new Assembler();
            final byte[] exec = assembler.compile(code);
            if (exec == null) Shell.println("compilation failed");
            else Files.createFile(fileName.substring(0, fileName.indexOf(".")) + ".exe", exec);
        }
    }

    /**
     * Creates process with given program, name and priority
     * @param args name of .exe file
     */
    static void cp(ArrayList<String> args) {
        if (args.size() != 4) Shell.println("invalid number of arguments");

        final byte[] exec = Files.getCleanFile(args.get(1));
        if (exec[0] == -1) Shell.println("Program does not exist");
        else PCBList.list.newProcess(args.get(2), Integer.parseInt(args.get(3)), exec);
    }

    /**
     * List all processes
     */
    static void lp(ArrayList<String> args) {
        PCBList.list.print();
    }

    /**
     * List ready processes
     */
    static void lpq(ArrayList<String> args) {
        PCBList.list.processor.printQueue();
    }

    /**
     * Prints running process
     */
    static void rp(ArrayList<String> args) {
        Shell.println(PCBList.list.processor.getRunningProcess().toString());
    }

    /**
     * Deletes selected process
     */
    static void dp(ArrayList<String> args) {
        final int processId = Integer.parseInt(args.get(1));
        final PCB process = PCBList.list.findByPID(processId);
        if (process != null) PCBList.list.processor.removeProcess(process);
        else Shell.println("process does not exist");
    }

}
