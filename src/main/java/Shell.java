import java.util.*;
import java.lang.System;

public class Shell {

    private HashMap<String, Integer> command_table = new HashMap<String, Integer>();

    public Shell() {
        Utils.log("shell started");
        //command_table = new HashMap<String, Integer>();
        Integer size = command_table.size();

        Utils.log(size.toString());
    }
}
