package shell;

import assembler.Assembler;
import filesystem.Files;
import filesystem.Directories;
import filesystem.Directory;
import os.OS;
import processess.PCB;
import processess.PCBList;
import utils.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

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
            final byte[] code = Directories.getCurrentDir().getFiles().getFileClean(fileName); //error, non-static method in static context

            Assembler assembler = new Assembler();
            final byte[] exec = assembler.compile(code);
            if (exec == null) Shell.println("compilation failed");
            else Directories.getCurrentDir().getFiles().createFile(fileName.substring(0, fileName.indexOf(".")) + ".exe", exec); //error, non-static method in static context
        }
    }

    /**
     * Creates process with given program, name and priority
     * @param args name of .exe file
     */
    static void cp(ArrayList<String> args) {
        if (args.size() != 4) Shell.println("invalid number of arguments");

        final byte[] exec = Directories.getCurrentDir().getFiles().getFileClean(args.get(1)); //error, non-static method in static context
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

	/* filesystem commands */
	
	static void file(ArrayList<String> args) {
        String help = "FILE - create and modify files \n";
        if (args.size() != 2) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        } else {
            String name = Directories.path(args.get(1));
            Scanner scan = new Scanner(System.in);
            Shell.println("Enter content, --- to finish");
            Shell.print(": ");
            String input = scan.nextLine();
            String result = "";
            while (!input.equals("---")) {
                Shell.print(": ");
                result += input + "\n";
                input = scan.nextLine();
            }
            result = result.substring(0, result.length() - 1); //get rid of last newline char
            Directories.getTargetDir().getFiles().createFile(name, result.getBytes());
        }

    }

    static void more(ArrayList<String> args) {
        String help = "MORE - print file content \n";
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

    static void dir(ArrayList<String> args) {
        String help = "DIR - print directory content \n";
        if (args.size() != 1) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        } else {
            Vector<Directory> dirs = Directories.getCurrentDir().getDirectories();
            if (dirs != null) {
                for (Directory e : dirs) {
                    if (e != null) {
                        Shell.println("<dir>\t" + e.getName());
                    }
                }
                Directories.getCurrentDir().getFiles().showFiles();
            }
        }
    }

    static void copy(ArrayList<String> args) {
        String help = "COPY - copies file \n" +
                "   COPY [source] [target]";
        if (args.size() != 3) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        } else try {
            String[] name = Directories.path(args.get(2), args.get(1));
            Directories.getTargetDir().getFiles().createFile(name[0], Directories.getSourceDir().getFiles().getFile(name[1]));
        } catch (IndexOutOfBoundsException e) {
            Shell.println("Cant copy file");
        }
    }
	
	static void rm(ArrayList<String> args) {
        String help = "RM - remove file \n";
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

    static void rmdir(ArrayList<String> args) {
        String help = "RM - remove file \n";
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

    static void mkdir(ArrayList<String> args) {
        String help = "MKDIR - creates a directory \n";
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

    static void cd(ArrayList<String> args) {
        String help = "CD - changes current directory \n";
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

    static void tree(ArrayList<String> args) {
        String help = "TREE - displays all directories \n";
        if (args.size() != 1) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        } else try {
            Directories.getCurrentDir().tree(0);
        } catch (IndexOutOfBoundsException e) {
            Shell.println("Invalid index");
        }
    }

    static void edit(ArrayList<String> args) {
        String help = "EDIT - append content to file \n";
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
            Scanner scan = new Scanner(System.in);
            Shell.print(": ");
            String input = scan.nextLine();
            String modify = "";
            while (!input.equals("---")) {
                Shell.print(": ");
                modify += input + "\n";
                input = scan.nextLine();
            }
            modify = modify.substring(0, modify.length() - 1); //get rid of last newline char


            current.createFile(args.get(1), (result + modify).getBytes());
        } catch (IndexOutOfBoundsException e) {
            Shell.println("Invalid index");
        }
    }
		
}
