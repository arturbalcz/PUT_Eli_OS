package processess;

import processor.Processor;
import utils.Utils;

import java.util.*;

/**
 * Stores Process Control Blocks and manages them
 *
 * @see PCB
 */
public class PCBList {

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


    public Processor CPU = new Processor();


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

    /**
     * Creates a new process and adds it to
     *
     * @param name name of new process
     * @param priority base priority of new process
     */
    public void newProcess(String name, int priority, byte[] exec){
        int tempPid = pidGen();
        data.add(new PCB(tempPid, name, priority, exec, this));
        //TO>Artur|Jakub< TODO: correct use of this modifier
        CPU.addReadyProcess(findByPID(tempPid), false);

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
        for (PCB e: data){
            System.out.println("Process " + e.getPID() + " (Name: " + e.getName() + ", Priority: " + e.getDynamicPriority() + ")" );
        }
    }

}
