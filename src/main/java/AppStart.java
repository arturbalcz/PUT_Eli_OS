import utils.Utils;

/**
 * Entry point of system
 */
public class AppStart {
    public static void main(String[] args) {
        Utils.log("OS starting...");

        OS os = new OS();
        os.run();

        Utils.log("quitting...");
        Utils.closeLogs();
    }
}
