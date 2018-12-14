package shell;

import filesystem.Files;
import utils.Utils;

import java.util.ArrayList;
import java.util.Scanner;

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
            Shell.print(help);
        } else {
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
                    Shell.print(help);
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
        Shell.print("command working " + argv.get(1));
    }

    static void file(ArrayList<String> args) {

        String help = "FILE - create and modify files \n" +
                "   Options: \n" +
                "       CREATE - \n" +
                "       SHOW - \n" +
                "       DELETE - \n";
        if (args.size() != 2 && args.size() != 3 && args.size() != 4) {
            Utils.log("Wrong numbers of arguments");
            Shell.print(help);
        } else {
            String param = args.get(1);
            switch (param.toUpperCase()) {
                case "CREATE":
                    Scanner scan = new Scanner(System.in);
                    System.out.print(">");
                    String input = scan.nextLine();
                    Files.createFile(args.get(2), input.getBytes());
                    break;
                case "GET":
                    try {
                        byte[] temp = Files.getFile(args.get(2));
                        if (temp[0] != -1) {
                            String result = "";
                            for (byte e : temp) {
                                result += (char) e + " ";
                            }
                            Shell.print(result);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        Shell.print("Invalid index");
                    }
                    break;
                case "LIST":
                    Files.showFiles();
                    break;
                default:
                    Utils.log("Wrong argument");
                    Shell.print(help);
                    break;
            }

        }
    }

    static void more(ArrayList<String> args) {
        String help = "MORE - print file content \n";
        if (args.size() != 2) {
            Utils.log("Wrong numbers of arguments");
            Shell.print(help);
        } else try {
            byte[] temp = Files.getFile(args.get(1));
            if (temp[0] != -1) {
                String result = "";
                for (byte e : temp) {
                    result += e != 0 ? (char) e : "";
                }
                Shell.print(result);
            }
        } catch (IndexOutOfBoundsException e) {
            Shell.print("Invalid index");
        }
    }

    static void dir(ArrayList<String> args) {
        String help = "DIR - print directory content \n";
        if (args.size() != 1) {
            Utils.log("Wrong numbers of arguments");
            Shell.print(help);
        } else {
            Files.showFiles();
        }
    }
    static void copy(ArrayList<String> args) {
        String help = "COPY - copies file \n" +
                "   COPY [source] [target]";
        if (args.size() != 3) {
            Utils.log("Wrong numbers of arguments");
            Shell.print(help);
        } else try {
            System.out.println((Files.getFile(args.get(1))));
            Files.createFile(args.get(2), Files.getFile(args.get(1)));
        }
        catch(IndexOutOfBoundsException e){
            Shell.print("Cant copy file");
        }
    }
}
