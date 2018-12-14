import shell.Shell;
import utils.Utils;

/**
 * Collects together all modules and run system
 */
class OS {

    OS() {
    }

    /**
     * starting the system
     */
    void run() {
        Utils.log("system started");
        boolean closing = false;
        while(!closing) {
            closing = Shell.interpret();
        }
    }

}
