package processess;


import processor.Processor;
import shell.Shell;
import utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Stores Process Control Blocks and manages them
 *
 * @see PCB
 */
public class PCBList {

    /** Instance of PCBList */
    public static final PCBList list = new PCBList();
  
    /**
     * Stores process control blocks
     */
    private List<PCB> data;

    /**
     * Stores pid that are used at that moment
     */
    private List<Integer> usedPids;

    /**
     * Generator for random numbers
     */
    private Random generator;

    /**
     * Reference to virtual memory manager
     */
    //private virtalmemory vram; TODO: uncomment


    public Processor processor = new Processor(this);

    /**
     * Initilaizes PCBList
     */
    public PCBList(/*virtualmemory vram*/) {
        data = new ArrayList<>();
        usedPids = new ArrayList<>();
        generator = new Random();
        //this.vram = vram; TODO: uncomment
    }

    /**
     * Generates an unique process id (pid) and adds it to used adresses arraylist
     *
     * @return generated pid
     */
    private int pidGen(){
        boolean notIn = false;
        int temp = -1;
        while (!notIn){
            notIn = true;
            temp = generator.nextInt(101) + 800;
            for (int e: usedPids){
                if (e == temp) {
                    notIn = false;
                    break;
                }
            }
        }
        usedPids.add(temp);
        return temp;
    }

    public static final int DUMMY_ID = -1;

    public void addDummy(final byte[] dummyExec) {
        final PCB dummy = new PCB(DUMMY_ID, "DUMMY", 0, dummyExec);
        data.add(dummy);
        processor.addReadyProcess(dummy, false);
    }

    /**
     * Creates a new process and adds it to
     *
     * @param name name of new process
     * @param priority base priority of new process
     */
    public void newProcess(final String name, final int priority, final byte[] exec){
        final int id = pidGen();
        final PCB newProcess = new PCB(id, name, priority, exec);
        data.add(newProcess);
        processor.addReadyProcess(newProcess, false);
    }

    public void makeProcessWait(final PCB process) {
        process.setState(ProcessState.WAITING);
        processor.removeFromQueue(process);
    }

    public void signal(final PCB process) {
        process.setState(ProcessState.READY);
        processor.addReadyProcess(process, true);
    }

    /**
     * Deletes process and frees it's pid
     *
     * @param pid id of deleted process
     */
    public void deleteProcess(int pid){
        Iterator itr = data.iterator();
        while (itr.hasNext()){
            PCB temp = (PCB) itr.next();
            if (temp.getPID() == pid){
                Utils.log("Deleted process \"" + temp.getName() +
                        "\", PID: " + temp.getPID());
                //vram.removeProcess(temp.getPID()) TODO: unncoment
                //[Gracjan] - moze w tej funkcji zmienilbys parametr z proces na pid,
                // w sumie to i tak tylko tego potrzebujesz, a bedzie prosciej
                itr.remove();
            }
        }
    }

    public PCB findByName(String name){
        for (PCB e: data){
            if (e.getName().equals(name)) return e;
        }
        return null;
    }

    public PCB findByPID(int pid){
        for (PCB e: data){
            if (e.getPID() == pid) return e;
        }
        return null;
    }

    public List<PCB> getData() {
        return data;
    }

    public void print(){
        for (final PCB pcb: data) Shell.println(pcb.toString());
    }

}
