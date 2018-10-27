import java.io.IOException;
import java.lang.System;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility methods used across all classes
 */
public class Utils {
    /**
     * If set to true, logs are printed
     */
    static private boolean loggingOn = true;

    /**
     * Turns logging on
     */
    static void logginOn() { Utils.loggingOn = true; }

    /**
     * Turns logging on
     */
    static void logginOff() { Utils.loggingOn = false; }

    static private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SS");

    /**
     * If {@link Utils#loggingOn} is set to true, logs message to the console
     *
     * @param  msg  the message to log
     */
    static void log(String msg) {
        Utils.log(msg, false);
    }

    /**
     * If loggingOn is set to true, logs message to the console.
     * Optionally adds error tah to the message
     *
     * @param  msg  the message to log
     * @param  error  if true the message is printed as an error
     */
    static void log(String msg, boolean error) {
        if(loggingOn) {
            if(error) msg = "ERR: " + msg;
            String time = formatter.format(LocalDateTime.now());
            System.out.println(time + " - " + msg);
        }
    }

    /**
     * If loggingOn is set to true, logs message to the console and
     * wait for user to click ENTER
     * @param msg the message to log
     */
    static void step(String msg) {
        Utils.log(msg, false);
        try {
            Utils.log("Click ENTER to continue..." , false);
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
