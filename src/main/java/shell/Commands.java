package shell;

import assembler.Assembler;
import filesystem.Directories;
import filesystem.Directory;
import filesystem.Files;
import os.OS;
import processess.PCB;
import processess.PCBList;
import synchronization.Lock;
import utils.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Stores implementation of every shell command with dependencies on external modules
 * <p>Every method should consume exactly one argument {@code ArrayList<String> args}.
 * For using your command checkout {@link Shell#CommandTable}</p>
 * @see Shell
 */
public interface Commands {

    /* shell commands */

    /**
     * Turns logging on or off.
     * @param args parameters list to handle by command
     */
    static void logging(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
        if(args.size() != 2) {
            Utils.log("Commands.logging(): Wrong numbers of arguments");
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
                    Shell.println(help);
                    break;
            }
        }
    }

    /**
     * Turns step work on or off.
     * @param args parameters list to handle by command
     */
    static void stepping(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
        if(args.size() != 2) {
            Utils.log("Commands.stepping(): Wrong numbers of arguments");
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
                    //Utils.log("Commands.stepping(): Wrong argument");
                    Shell.println(help);
                    break;
            }
        }
    }

    /**
     * Prints time to user console.
     * @param args parameters list to handle by command
     */
    static void time(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
        if (args.size() == 2 && args.get(1).equals("/?")) {
            Shell.println(help);
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String times = formatter.format(LocalDateTime.now());
        Utils.log("Printing time for user.");
        Shell.println(times);
    }

    /**
     * Prints all available commends or help for specified command.
     * @param args parameters list to handle by command
     */
    static void help(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
        if (args.size() > 2) Shell.println(help);
        else if(args.size() == 2) {
            String command = args.get(1);
            if (Shell.CommandTable.get(command) == null) {
                //if not found
                Shell.println("This command is not supported by the help utility.  Try \""+command+" /?\".");
            }
            else {
                try {
                    Shell.println(Shell.HelpingTable.get(command));
                }
                catch (IndexOutOfBoundsException e) {
                    Utils.log("Commands.help(): HelpingTable::IndexOutOfBoundsException::"+command, true);
                    Shell.println("invalid number of arguments");
                }
            }
        }
        else {
            List<String> sortedKeys = new ArrayList<>(Shell.CommandTable.keySet());
            Collections.sort(sortedKeys);
            int elementsOfHelp = 0;
            for (String command : sortedKeys) {
                Shell.print(String.format("%-12s", command.toUpperCase()));
                //if (command.length() < 4) Shell.print("\t\t");
                //Shell.print("\t");
                String commandHelp = Shell.HelpingTable.get(command);
                int endIndex = commandHelp.indexOf('\n');
                Shell.println(commandHelp.substring(0, endIndex));
                elementsOfHelp++;
            }
            Shell.println("");
            Utils.log("Commands.help(): elementsOfHelp="+elementsOfHelp);
        }
    }

    /**
     * Exits from system.
     * @param args parameters list to handle by command
     */
    static void exit(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
        if (args.size() > 2 || (args.size() == 2 && args.get(1).equals("help")))  {
            Shell.println(help);
            return;
        }
        else if (args.size() == 2 && args.get(1).equals("t")) Utils.stepOff();
        else Utils.stepOn();

        Utils.log("Commands.exit(): Exiting by user");
        Shell.exiting = true;
    }

    /**
     * Turns printing on or off.
     * @param args parameters list to handle by command
     */
    static void echo(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
        if(args.size() != 2) {
            Utils.log("Commands.echo(): Wrong numbers of arguments");
            Shell.println(help);
        }
        else {
            String param = args.get(1);
            switch (param.toUpperCase()) {
                case "/ON":
                    Shell.echoOn();
                    break;
                case "/OFF":
                    Shell.echoOff();
                    break;
                default:
                    //Utils.log("Wrong argument");
                    Shell.println(help);
                    break;
            }
        }
    }

    /**
     * Displays a list of currently running processes.
     * @param args parameters list to handle by command
     */
    static void tasklist(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
        boolean isStateFiltered = false, isNameFiltered = false;
        String stateFilter = "", nameFilter = "";

        if (args.size() > 5) { Shell.println(help); return; }
        else if (args.size() == 5) {
            if(args.get(1).toUpperCase().equals("/N")) {
                isNameFiltered = true;
                nameFilter = args.get(2).toUpperCase();
                if (args.get(3).toUpperCase().equals("/S")) {
                    isStateFiltered = true;
                    stateFilter = args.get(4).toUpperCase();
                }
                else {
                    //error of second argument
                    Shell.println("Wrong arguments");
                    return;
                }
            }
            else {
                //error of first argument
                Shell.println("Wrong arguments");
                return;
            }
        }
        else if (args.size() == 3) {
            switch (args.get(1).toUpperCase()) {
                case "/N":
                    isNameFiltered = true;
                    nameFilter = args.get(2).toUpperCase();
                    break;
                case "/S":
                    isStateFiltered = true;
                    stateFilter = args.get(2).toUpperCase();
                    break;
                default:
                    Shell.println(help);
                    return;
            }
        }
        else if (args.size() == 2) {
            if (args.get(1).toUpperCase().equals("/S")) {
                Shell.println("You need to specified name of state you want filter");
            } else {
                Shell.println(help);
            }
            return;
        }

        String alignFormat = "| %-15s | %4d | %-8s | %4d | %4d | %4d | %4d | %4d |";
        String header = "|-----------------|------|----------|------|------|------|------|------|\n" +
                "| Name            |  PID | State    | Base |  Dyn |   PC |   RT |   EO |\n" +
                "|-----------------|------|----------|------|------|------|------|------|\n";

        Shell.print(header);
        for (PCB entry : PCBList.list.getData()) {
            String name = entry.getName();
            if (isNameFiltered) {
                if (!name.toUpperCase().equals(nameFilter)) {
                    continue;
                }
            }
            int pid = entry.getPID();
            String state = entry.getProcessState().toString();
            if (isStateFiltered) {
                if (!state.toUpperCase().equals(stateFilter)) {
                    continue;
                }
            }
            int basePriority = entry.getBasePriority();
            int dynamicPriority = entry.getDynamicPriority();
            int pc = entry.getPC();
            int readyTime = entry.getReadyTime();
            int executeOrders = entry.getExecutedOrders();
            String res = String.format(alignFormat, name, pid, state, basePriority, dynamicPriority, pc, readyTime, executeOrders);
            Shell.println(res);
        }
        Shell.println("|-----------------|------|----------|------|------|------|------|------|\n");

    }

    /**
     * Used for testing shell.
     * @param args parameters list to handle by command
     */
    static void test(ArrayList<String> args){
        String help = Shell.HelpingTable.get(args.get(0));
        if (args.get(1).equals("/?")) { Shell.println(help); return; }
        Utils.step("Commands.test(): step work is working" + args.get(1));
        Utils.log("Commands.test(): log is working" +  args.get(1));
        Shell.println("command is working " + args.get(1));

    }

    /**
     * Checks if current user input isn't local executable file.
     * @param args parameters list to handle by command
     * @return true if the file was found and started
     */
    static boolean localExe(ArrayList<String> args) {
        String inputFromInterpret = args.get(0);
        Utils.log("Commands.localEXE(): inputFromInterpret="+inputFromInterpret);
        String fileName = Directories.path(inputFromInterpret);
        Utils.log("Commands.localEXE(): fileName="+fileName);
        if (!fileName.matches("\\w+\\.\\w+")){
            fileName=fileName+".exe";
            inputFromInterpret = inputFromInterpret + ".exe";
            Utils.log("Commands.localEXE(): no extension; adding \".exe\"; inputFromInterpret=" + inputFromInterpret);
        }
        if (Directories.getTargetDir().getFiles().fileExists(fileName)) {
            String[] file = fileName.split("\\.");
            Utils.log("Commands.localEXE(): file found, creating process \'"+file[0]+"\'");
            if (file[1].equals("exe")) {
                ArrayList<String> arga = new ArrayList<>();
                arga.add("cp");
                arga.add(inputFromInterpret);
                arga.add(file[0]);
                arga.add("7");
                Utils.log("Commands.localEXE(): arga="+arga.toString());
                cp(arga);
                return true;
            }
            else return false;
        }
        return false;
    }

    /* assembler commands */

    /**
     * Compiles given program.
     * @param args parameters list to handle by command
     */
    static void com(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
        if (args.size() == 1) Shell.println("no program file specified");
        if (args.get(1).equals("/?")) Shell.println(help);
        else {
            final String fileName = args.get(1);
            final byte[] code = Directories.getCurrentDir().getFiles().getFileClean(fileName); //error, non-static method in static context

            Assembler assembler = new Assembler();
            final byte[] exec = assembler.compile(code);
            if (exec == null) Shell.println("compilation failed");
            else Directories.getCurrentDir().getFiles().createFile(fileName.substring(0, fileName.indexOf(".")) + ".exe", exec); //error, non-static method in static context
        }
    }

    /**
     * updates code of initial programs
     * @param args parameters list to handle by command
     */
    static void update(ArrayList<String> args) {
        OS.updateInitialFiles();
    }

    /* processes commands */

    /**
     * Creates process with given program, name and priority.
     * @param args parameters list to handle by command
     */
    static void cp(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));

        if (args.size() != 4) Shell.println("invalid number of arguments");

        String fileName = Directories.path(args.get(1));
        final byte[] exec = Directories.getTargetDir().getFiles().getFileClean(fileName);
        if (exec[0] == -1) Shell.println("Program does not exist");
        else PCBList.list.newProcess(args.get(2), Integer.parseInt(args.get(3)), exec);
    }

    /**
     * List all processes.
     * @param args parameters list to handle by command
     */
    static void lp(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
        PCBList.list.print();
    }


    /**
     * List ready processes.
     * @param args parameters list to handle by command
     */
    static void lpq(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
        PCBList.list.processor.printQueue();
    }

    /**
     * Prints running process.
     * @param args parameters list to handle by command
     */
    static void rp(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
        Shell.println(PCBList.list.processor.getRunningProcess().toString());
    }

    /**
     * Deletes selected process.
     * @param args parameters list to handle by command
     */
    static void dp(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
        final int processId = Integer.parseInt(args.get(1));
        final PCB process = PCBList.list.findByPID(processId);
        if (process != null) PCBList.list.processor.removeProcess(process);
        else Shell.println("process does not exist");
    }

	/* filesystem commands */

    /**
     * Create and modify file.
     * @param args parameters list to handle by command
     */
	static void file(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
        //String help = "FILE - create and modify files \n";
        if (args.size() != 2) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        } else {
            String name = Directories.path(args.get(1));
            //Scanner scan = new Scanner(System.in);

            Shell.println("Enter content, --- to finish");
            Shell.print(": ");
            String input = Shell.read();
            String result = "";
            while (!input.equals("---")) {
                Shell.print(": ");
                result += input + "\n";
                input = Shell.read();
            }
            result = result.substring(0, result.length() - 1); //get rid of last newline char
            Directories.getTargetDir().getFiles().createFile(name, result.getBytes());
        }

    }

    /**
     * Displays the contents of a text file.
     * @param args parameters list to handle by command
     */
    static void more(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
	    //String help = "MORE - print file content \n";
        if (args.size() != 2) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        } else try {
            String name = Directories.path(args.get(1));
            byte[] temp = Directories.getTargetDir().getFiles().getFileClean(name);
            if (temp[0] != -1) {
                String result = "";
                for (byte e : temp) {
                    result += (char) e;
                }
                Shell.println(result);
            }
        } catch (IndexOutOfBoundsException e) {
            Shell.println("Invalid index");
        }
    }

    /**
     * Displays a list of files and subdirectories in a directory.
     * @param args parameters list to handle by command
     */
    static void dir(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
        if (args.size() != 1) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        } else {
            String path = Directories.getCurrentDir().getName();
            Shell.println("\n Directory of "+path+"\n");
            Vector<Directory> dirs = Directories.getCurrentDir().getDirectories();
            if (dirs != null) {
                for (Directory e : dirs) {
                    if (e != null) {
                        Shell.println("<DIR>\t\t" + e.getName());
                    }
                }
                Directories.getCurrentDir().getFiles().showFiles();
            }
            Shell.println("");
        }
    }

    /**
     * Copies one files to another location.
     * @param args parameters list to handle by command
     */
    static void copy(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
//        String help = "COPY - copies file \n" +
//                "   COPY [source] [target]";
        if (args.size() != 3) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        } else try {
            String[] name = Directories.path(args.get(2), args.get(1));
            if( !Directories.getSourceDir().getFiles().fileExists(name[1]) ){
                Shell.println("No source file named " + name[1]);
                return;
            }
            Directories.getTargetDir().getFiles().createFile(name[0], Directories.getSourceDir().getFiles().getFile(name[1]));
        } catch (IndexOutOfBoundsException e) {
            Shell.println("Cant copy file");
        }
    }

    /**
     * Deletes one or more files.
     * @param args parameters list to handle by command
     */
	static void rm(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
	    //String help = "RM - remove file \n";
        if (args.size() != 2) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        } else try {
            String name = Directories.path(args.get(1));
            Directories.getTargetDir().getFiles().deleteFile(name);
        } catch (IndexOutOfBoundsException e) {
            Shell.println("Invalid index");
        }
    }

    /**
     * Removes a directory.
     * @param args parameters list to handle by command
     */
    static void rmdir(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
	    //String help = "RM - remove file \n";
        if (args.size() != 2) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        } else try {
            String name = Directories.path(args.get(1));
            Directories.getTargetDir().removeDir(name);
        } catch (IndexOutOfBoundsException e) {
            Shell.println("Invalid index");
        }
    }

    /**
     * Creates a directory.
     * @param args parameters list to handle by command
     */
    static void mkdir(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
	    //String help = "MKDIR - creates a directory \n";
        if (args.size() != 2) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        } else try {
            String name = Directories.path(args.get(1));
            Directories.getTargetDir().addDirectory(name);
        } catch (IndexOutOfBoundsException e) {
            Shell.println("Invalid index");
        }
    }

    /**
     * Displays the name of or changes the current directory.
     * @param args parameters list to handle by command
     */
    static void cd(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
	    //String help = "CD - changes current directory \n";
        if (args.size() != 2) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        } else try {
            String[] path = args.get(1).split("[/]");
            for (String e : path) {
                Directories.setCurrentDir(e);
            }
        } catch (IndexOutOfBoundsException e) {
            Shell.println("Invalid index");
        }
    }

    /**
     * Graphically displays the directory structure of a drive or path.
     * @param args parameters list to handle by command
     */
    static void tree(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
	    //String help = "TREE - displays all directories \n";
        if (args.size() != 1) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        } else try {
            Directories.getCurrentDir().tree(0);
        } catch (IndexOutOfBoundsException e) {
            Shell.println("Invalid index");
        }
    }

    /**
     * Opens editor with given file.
     * @param args parameters list to handle by command
     */
    static void edit(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
	    //String help = "EDIT - append content to file \n";
        if (args.size() != 2) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        } else try {
            String name = Directories.path(args.get(1));
            Files current = Directories.getTargetDir().getFiles();
            byte[] temp = current.getFileClean(name);
            if (temp[0] == -1) {
                return;
            }
            current.deleteFile(name);
            String result = "";
            for (byte e : temp) {
                result += (char) e;
            }
            Shell.println("Enter content, --- to finish");
            Shell.print(result);
            //Scanner scan = new Scanner(System.in);
            Shell.print(": ");
            String input = Shell.read();
            String modify = "";
            while (!input.equals("---")) {
                Shell.print(": ");
                modify += input + "\n";
                input = Shell.read();
            }
            modify = modify.substring(0, modify.length() - 1); //get rid of last newline char


            current.createFile(args.get(1), (result + modify).getBytes());
        } catch (IndexOutOfBoundsException e) {
            Shell.println("Invalid index");
        }
    }

    /* sync commands */

    /**
     * Prints locks.
     * @param args parameters list to handle by command
     */
    static void lck(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
        if (args.size() > 1) Shell.println(help);
        else Lock.printLocks();
    }

    /* vm commands */

    /**
     * Prints content of virtual memory containers.
     * @param args parameters list to handle by command
     */
    static void vm(ArrayList<String> args) {
        String help = Shell.HelpingTable.get(args.get(0));
        String param = args.get(1);
        try {
            switch (param) {
                case "pnv":
                    virtualmemory.virtualmemory.printNextVictim();
                    break;
                case "prs":
                    virtualmemory.virtualmemory.printRamStatus();
                    break;
                case "pq":
                    virtualmemory.virtualmemory.printQueue();
                    break;
                case "ppt":
                    virtualmemory.virtualmemory.printPageTable(Integer.parseInt(args.get(2)));
                    break;
                case "ppp":
                    virtualmemory.virtualmemory.printProcessPages(Integer.parseInt(args.get(2)));
                    break;
                case "pp":
                    virtualmemory.virtualmemory.printPage(Integer.parseInt(args.get(2)), Integer.parseInt(args.get(3)));
                    break;
                default:
                    Utils.log("Wrong argument");
                    Shell.println(help);
                    break;
            }
        } catch (NullPointerException e) {
            Utils.log(e.getMessage(), true);
            Shell.println("ERROR: " + e.getMessage());
        }
    }
}

