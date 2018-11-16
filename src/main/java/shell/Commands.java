package shell;

import utils.Utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
            Utils.log("Wrong numbers of arguments", true);
            Shell.print(help);
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
                    Utils.log("Wrong argument", true);
                    Shell.print(help);
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
        Shell.print("command working " + argv.get(1));
    }

}
