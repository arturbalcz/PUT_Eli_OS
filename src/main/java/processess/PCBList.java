package processess;

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
     * Initilaizes PCBList
     */
    public PCBList() {
        data = new ArrayList<>();
        usedPids = new ArrayList<>();
        generator = new Random();
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
            temp = generator.nextInt(1500001) + 1000000;
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
    public void newProcess(String name, int priority){
        data.add(new PCB(pidGen(), name, priority, null));
        //dać znać aby przenieść z pamięci do ram TODO usunac exec z construktora
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
                itr.remove();
            }
        }
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
