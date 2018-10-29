import java.io.PrintStream;
import java.util.HashMap;
import java.lang.System;

/**
 * Shell share methods  to communicate with user
 */
public class Shell {

    private HashMap<String, Integer> command_table = new HashMap<String, Integer>();
    private PrintStream standardOut;

    public Shell() {
        standardOut = System.out;
        Integer size = command_table.size();
        standardOut.println("command_map created");
        Utils.step("created shell");
    }
}