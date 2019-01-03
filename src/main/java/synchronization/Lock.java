package synchronization;

import processess.PCB;
import processess.PCBList;
import processess.ProcessState;
import shell.Shell;
import utils.Utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Lock {
    private static final Map<String, Lock> locks = new HashMap<>();

    public static boolean lockFile(final String name, final PCB pcb) {
        final Lock lock = (locks.containsKey(name)) ? locks.get(name) : new Lock(name);
        if (!locks.containsKey(name)) locks.put(name, lock);
        return lock.lock(pcb);
    }

    public static void unlockFile(final String name) {
        final Lock lock = locks.get(name);
        if(!lock.unlock()) locks.remove(name);
    }

    public static void printLocks() {
        for (final Lock lock : locks.values()) {
            Shell.print(lock.name + ": locked: " + lock.locked + " queue: ");
            for (final PCB pcb : lock.kolejka) Shell.print(pcb.getSignature() + ", ");
            Shell.println("");
        }
    }

    /** initilize locked which is open */
    private boolean locked;

    /** kolejka uzywana do lock */
    private List<processess.PCB> kolejka = new LinkedList<>();

    private final String name;

    private Lock(final String name) {
        locked = false;
        this.name = name;
    }

    // @param function lock block the locked to security the data
    private boolean lock(PCB process) {
        if (locked) {
            process.setState(ProcessState.WAITING);
            Utils.log("Lock " + name + " is already locked. "+process.name+" has to wait");
            kolejka.add(process);
            PCBList.list.makeProcessWait(process);
            return false;
        } else {
            Utils.log(process.name+" has taken the lock " + name);
            locked = true;
            return true;
        }
    }

    //@param function unlock, unlocking the locked and check if is another process waiting
    private boolean unlock() {
        this.locked = false;
        Utils.log("Lock " + name + " has been released");

        if (kolejka.isEmpty()) {
            Utils.log("No more processes waiting for it");
            return false;
        }
        else {
            final PCB process = kolejka.remove(0);
            lock(process);
            PCBList.list.signal(process);
            return true;
        }
    }
}



