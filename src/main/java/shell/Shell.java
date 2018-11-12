package shell;

import utils.Utils;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;
import java.lang.System;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;


/**
 * shell.Shell share methods  to communicate with user
 */
public class Shell {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SS");
    private static final PrintStream standardOut = System.out;
    private static HashMap<String, Supplier<Boolean>> CommandTable = new HashMap<>();

    static {
        CommandTable.put("time", Shell::time );
    }
    private static Integer size = CommandTable.size();

    public static void print(String msg) {
        standardOut.println(msg);
    }

    public static boolean time() {
        String times = formatter.format(LocalDateTime.now());
        Utils.step("Printing time for user.");
        print(times);
        return true;
    }

    public static boolean interpret( ) {
        Scanner standardIn = new Scanner(System.in);
        String input = standardIn.nextLine();
        if (CommandTable.get(input) == null) {
            standardOut.println("\""+input+"\" can't resolve command");
        }


        if(input.equals("time")) {
            CommandTable.get("time").get();
            return false;
        }
        else if(input.equals("exit")) {
            return true;
        }
        else {
            standardOut.println("\""+input+"\" cant resolve command");
            return false;
        }


    }

}