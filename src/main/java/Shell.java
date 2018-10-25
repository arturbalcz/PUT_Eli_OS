import java.util.Map;
import java.lang.System;

public class Shell {

    private static Map<String, Integer> command_table;

    public Shell() {
        command_table.put("jedynka", new Integer(1));
    }

    public static void run(String command) {
        System.out.println(command_table.get(command));
    }
}
