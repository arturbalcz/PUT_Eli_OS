import java.util.*;
import java.lang.System;

public class Shell {

    private HashMap<String, Integer> command_table = new HashMap<String, Integer>();

    public Shell() {
        Integer size = command_table.size();
        Utils.step("created shell");
    }
}
