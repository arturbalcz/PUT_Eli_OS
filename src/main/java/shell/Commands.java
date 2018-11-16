package shell;

import utils.Utils;

import java.util.ArrayList;

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
                    Utils.log("Wrong argument");
                    Shell.print(help);
                    break;
            }
        }
    }

    /**
     * Used for testing shell
     * @param argv anything but {@code null}
     */
    static void test(ArrayList<String> argv) {
        Shell.print("command working " + argv.get(1));
    }

}
