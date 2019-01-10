import os.OS;
import utils.Utils;

import java.io.IOException;

/**
 * Entry point of system
 */
public class AppStart {
    public static void main(String[] args) {
        Utils.log("OS starting...");

        try {
            OS os = new OS();
            os.run();
        }
        catch(Exception e){
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                new ProcessBuilder("cmd", "/c", "color 17").inheritIO().start().waitFor();
                for (int i = 0; i < 20; i++) {
                    System.out.println();
                }
                String msg = "\t\t\tPUT Elita OS \n" +
                        "\n" +
                        "\tA fatal exception " + e.hashCode() + " has occured. \n" +
                        "\tThe current application will now be terminated. \n" +
                        "\n";
                System.out.println(msg);
                e.printStackTrace();
                System.out.println();
                System.out.println("\tPress return key to continue ");
                System.out.println();
                System.out.println();
                System.out.println();
                System.in.read();
                new ProcessBuilder("cmd", "/c", "color 07").inheritIO().start().waitFor();
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (IOException | InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        //Utils.stepOn();
        Utils.step("quitting...");
        Utils.closeLogs();
    }
}
