package shell;

import assembler.Assembler;
import filesystem.Directories;
import filesystem.Directory;
import filesystem.Files;
import processess.PCB;
import utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

/**
 * Stores implementation of every shell command with dependencies on external modules
 * <p>Every method should consume exactly one argument {@code ArrayList<String> args}.
 * For using your command checkout {@link Shell#CommandTable}</p>
 *
 * @see Shell
 */
public interface Commands {

    /**
     * Turns logging on or off
     *
     * @param args "on" or "off"
     */
    static void logging(ArrayList<String> args) {
        String help = "LOG - turns logging on or off\nLOG ON/OFF";
        if (args.size() != 2) {
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
     * Used for testing shell
     *
     * @param argv anything but {@code null}
     */
    static void test(ArrayList<String> argv) {
        Shell.println("command working " + argv.get(1));
    }

    static void file(ArrayList<String> args) {

        String help = "FILE - create and modify files \n" +
                "   Options: \n" +
                "       CREATE - \n" +
                "       SHOW - \n" +
                "       DELETE - \n";
        if (args.size() != 2 && args.size() != 3 && args.size() != 4) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        }
        else {
            String param = args.get(1);
            switch (param.toUpperCase()) {
                case "CREATE":
                    Scanner scan = new Scanner(System.in);
                    System.out.print(": ");
                    String input = scan.nextLine();
                    Directories.getCurrentDir().getFiles().createFile(args.get(2), input.getBytes());
                    break;
                default:
                    Utils.log("Wrong argument");
                    Shell.println(help);
                    break;
            }

        }
    }

    static void more(ArrayList<String> args) {
        String help = "MORE - print file content \n";
        if (args.size() != 2) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        } else try {
            byte[] temp = Directories.getCurrentDir().getFiles().getFileClean(args.get(1));
            if (temp[0] != -1) {
                String result = "";
                for (byte e : temp) {
                    result += (char)e;
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
            if(dirs != null) {
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
            System.out.println((Directories.getCurrentDir().getFiles().getFile(args.get(1))));
            Directories.getCurrentDir().getFiles().createFile(args.get(2), Directories.getCurrentDir().getFiles().getFile(args.get(1)));
        }
        catch(IndexOutOfBoundsException e){
            Shell.println("Cant copy file");
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
            final byte[] code = Directories.getCurrentDir().getFiles().getFileClean(fileName);

            Assembler assembler = new Assembler();
            final byte[] exec = assembler.compile(code);
            if (exec == null) Shell.println("compilation failed");
            else Directories.getCurrentDir().getFiles().createFile(fileName.substring(0, fileName.indexOf(".")) + ".exe", exec);
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
            final byte[] exec = Directories.getCurrentDir().getFiles().getFileClean(args.get(1));
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

    static void rm(ArrayList<String> args) {
        String help = "RM - remove file \n";
        if (args.size() != 2) {
            Utils.log("Wrong numbers of arguments");
            Shell.println(help);
        } else try {
            Directories.getCurrentDir().getFiles().deleteFile(args.get(1));
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
            Directories.getCurrentDir().removeDir(args.get(1));
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
            Directories.getCurrentDir().addDirectory(args.get(1));
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
            //Directories.setCurrentDir(args.get(1));
            String[] path = args.get(1).split("[/]");
            for(String e:path){
                Directories.setCurrentDir(e);
            }
        } catch (IndexOutOfBoundsException e) {
            Shell.println("Invalid index");
        }
    }
}
