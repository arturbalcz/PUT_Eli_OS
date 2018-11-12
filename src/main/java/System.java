import shell.Shell;
import utils.Utils;

class System {

    System() {
    }

    /**
     * starting the system
     */
    void run() {
        Shell shell = new Shell();
        Utils.log("system started");
        boolean breake = new Boolean( false );
        while(!breake) {
            breake = shell.userInput();
        }



    }
}
