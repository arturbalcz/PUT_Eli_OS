import shell.Shell;
import utils.Utils;

class System {

    System() {
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
