package shell;

import assembler.Assembler;
import filesystem.Files;
import processess.PCB;
import utils.Utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;

/**
 * Stores implementation of every shell command with dependencies on external modules
 * <p>Every method should consume exactly one argument {@code ArrayList<String> args}.
 * For using your command checkout {@link Shell#CommandTable}</p>
 * @see Shell
 */
public interface Commands {

    /**
     * Turns logging on or off
     * @param args "on" or "off"
     */
    static void logging(ArrayList<String> args) {
        String help = "LOG - turns logging on or off\nLOG ON/OFF";
        if(args.size() != 2) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        }
        else {
            String param = args.get(1);
            switch (param.toUpperCase()) {
                case "ON":
                    Utils.loggingOn();
                    break;
                case "OFF":
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
     * Prints time to user console
     * @param args no effect
     */
    static void time(ArrayList<String> args) {
        String times = Shell.formatter.format(LocalDateTime.now());
        Utils.step("Printing time for user.");
        Shell.print(times);
    }

    /*
    TODO
    Create help for all commends
     */
    /**
     * Prints all available commends
     * @param args no effect
     */
    static void help(ArrayList<String> args) {
        Set<String> keys = Shell.CommandTable.keySet();
        Utils.step("Printing help for user.");
        for (String command : keys) {
            Shell.print(command.toUpperCase());
        }
    }

    /**
     * Exits from system
     * @param args no effect
     */
    static void exit(ArrayList<String> args) {
        Utils.log("Exiting by user");
        Shell.exiting = true;
    }

    /**
     * Used for testing shell
     * @param argv anything but {@code null}
     */
    static void test(ArrayList<String> argv) {
        Shell.println("command working " + argv.get(1));
    }

    static void file(ArrayList<String> args){

        String help = "FILE - create and modify files \n" +
                "   Options: \n" +
                "       CREATE - \n" +
                "       SHOW - \n" +
                "       DELETE - \n";
        if(args.size() != 2 && args.size() != 3 && args.size() != 4) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        }
        else {
            String param = args.get(1);
            switch (param.toUpperCase()) {
                case "CREATE":
                    Scanner scan = new Scanner(System.in);
                    System.out.print(">");
                    String input = scan.nextLine();
                    Files.createFile(args.get(2), input.getBytes());
                    break;
                case "GET":
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
        if (args.size() == 1) Shell.println("no program file specified");
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
     * <p>IN DEVELOPMENT</p>
     * @param args name of .exe file
     */
    static void cp(ArrayList<String> args) {
        // TODO: implement proper process creation
        if (args.size() == 1) Shell.println("no exe file specified");
        else {
            Utils.log("running program in dev environment");
            final byte[] exec = Files.getCleanFile(args.get(1));
            if(exec[0] == -1) {
                Shell.println("Program does not exist");
                return;
            }
            Utils.log(Arrays.toString(exec));
            PCB process = new PCB(1,"p1", 10, exec);
            //noinspection StatementWithEmptyBody
            while(process.execute());
        }
    }

}
