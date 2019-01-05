package processess;


import processor.Processor;
import shell.Shell;
import memory.Memory;
import virtualmemory.virtualmemory;
import utils.Utils;

import java.util.*;

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
    private Memory ram;
    private virtualmemory vm;


    public Processor processor = new Processor(this);

    /**
     * Initilaizes PCBList
     */
    public PCBList(/*virtualmemory vm*/) {
        data = new ArrayList<>();
        usedPids = new ArrayList<>();
        generator = new Random();

        this.ram = new Memory();
        this.vm = new virtualmemory(ram);
        ram.GetReference(vm);
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
        final PCB dummy = new PCB(DUMMY_ID, "DUMMY", 0, ram, (byte)0, dummyExec.length);
        vm.createProcess(DUMMY_ID, toObjects(dummyExec));
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
        byte PC = (byte) (exec[0] + 1);
        final PCB newProcess = new PCB(id, name, priority, ram, PC, exec.length);
        vm.createProcess(id, toObjects(exec));
        data.add(newProcess);
        processor.addReadyProcess(newProcess, false);
    }

    /**
     * Changes byte array to Byte vector
     *
     * @param bytesPrim byte array
     * @return Byte vector
     */
    public Vector<Byte> toObjects(byte[] bytesPrim) {
        Vector<Byte> bytes = new Vector<>();

        int i = 0;
        for (byte b : bytesPrim)  bytes.add(i++, b); // Autoboxing

        return bytes;
    }

    /**
     * Changes Byte vector to byte array
     *
     * @param oBytes Byte vector
     * @return byte array
     */
    public byte[] toPrimitives(Vector<Byte> oBytes)
    {
        byte[] bytes = new byte[oBytes.size()];

        for(int i = 0; i < oBytes.size(); i++) {
            bytes[i] = oBytes.elementAt(i);
        }

        return bytes;
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
                vm.removeProcess(temp.getPID());

                itr.remove();
            }
        }
    }

    //for testing
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
