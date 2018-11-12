package shell;

import utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;
import java.lang.System;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * shell.Shell share methods  to communicate with user
 */
public class Shell {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SS");

    //private HashMap<String, Integer> CommandTable;
    private static final PrintStream standardOut = System.out;

    public Shell() {
        HashMap<String, Integer> CommandTable = new HashMap<String, Integer>();
        Integer size = CommandTable.size();
        standardOut.println("CommandTable created");
        Utils.log("created shell");
    }

    public static void print(String msg) {
        standardOut.println(msg);
    }

    private void populateCommandTable() {
        File commandsFile = new File("komendy.ini");
        try {
            Scanner commands = new Scanner(commandsFile);
            String text = commands.nextLine();
            standardOut.println(text);
        } catch (FileNotFoundException e) {
            standardOut.println("pliku nie znaleziono :(");
            //e.printStackTrace();
        }
    }

    public void time() {
        String time = formatter.format(LocalDateTime.now());
        Utils.step("Printing time for user.", time);
    }

    public boolean userInput( ) {
        Scanner standardIn = new Scanner(System.in);
        String input = standardIn.nextLine();
        if(input.equals("pc")) {
            populateCommandTable();
            return false;
        }
        else if(input.equals("time")) {
            time();
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