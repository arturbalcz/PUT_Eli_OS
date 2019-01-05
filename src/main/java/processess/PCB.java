package processess;

import assembler.Assembler;
import assembler.CPUState;
import memory.Memory;
import utils.Utils;

/**
 * Process Control Block, represents process
 */
public class PCB implements Comparable<PCB> {

    /**
     * Unique process id
     */
    final private int PID;

    public final String name;

    /**
     * Process state (READY, RUNNING, WAITING, TERMINATED)
     *
     * @see ProcessState
     */
    private ProcessState state;

    /**
     * Program counter
     */
    private byte PC;

    /**
     * Unchangeable base priority, set in constructor,
     * Value 0 - idle process,
     * Values 1-15 - dynamic priority,
     * Values 16-17 - real-time priority
     */
    private final int basePriority;

    /**
     * Dynamically changed priority,
     * Values 1-15 - dynamic priority
     * Values 16-17 - real-time priority
     */
    private int dynamicPriority;
    private int readyTime;
    private int executedOrders;
    private final int codeLength;

    private CPUState cpuState;


    // temporary ram, see constructor
   // private final byte[] code;
    Memory ram;


    //virtalmemmory vram; TODO: unncoment

    /**
     * Creates Porcess Contol Block
     *
     * @param PID unique process id
     * @param name name of the process
     * @param priority process base priority
     * //@param exec temporary ram
     */
    PCB(int PID, String name, int priority, Memory ram, byte PC, int codeLength) {
        this.PID = PID;
        this.name = name;

        //TODO priority 0
        if (priority < 0){
            Utils.log("Priority is too low, cannot create process", true);
        }
        if (priority > 17) {
            Utils.log("Priority is too high, changed to priority max size - 17", true);
            priority = 17;
        }
        this.basePriority = priority;
        this.dynamicPriority = priority;
        this.cpuState = Assembler.getFreshCPU();
        this.readyTime=0;
        this.codeLength = codeLength;

        this.ram = ram;
        //this.vm = vm; TODO: unncoment

        // TODO: ram


        // temporary ram solution for testing assembler
        //this.code = exec;
        //this.PC = (byte) (exec[0] + 1); // sets PC after allocated values ('LETs')


        this.state = ProcessState.READY;

        Utils.log("Created process " + this.name + " with pid " + this.PID
                + " and with priority " + this.basePriority);

    }



    public int getReadyTime() { return  readyTime; }

    public void setReadyTime(int readyTime)
    {
        this.readyTime=readyTime;
    }

    public int getExecutedOrders() {return executedOrders; }

    public void setExecutedOrders(int executedOrders) {this.executedOrders = executedOrders; }

    public int getPID() {
        return PID;
    }

    public String getName() {
        return name;
    }

    public byte getPC() {
        return PC;
    }

    public void setPC(byte PC) {
        this.PC = PC;
    }

//    public void deleteProcess(){
//        pcbList.deleteProcess(this.PID);
//    }

    public ProcessState getProcessState(){
        return state;
    }

    public void setState(ProcessState state) {

        if (this.state != state){//in case of some error

            Utils.log("Changed state of proces " + this.getSignature() + " from " + this.state
                    + " to " + state);

            switch (state){
                //TODO: find some way to use it or delete
                case READY:{
                    break;
                }
                case RUNNING:{
                    break;
                }
            }

            this.state = state;
        } //else do nothing

    }

    //Priority-------------------------------------------------------

    public int getBasePriority() {
        return basePriority;
    }

    public int getDynamicPriority() {
        return dynamicPriority;
    }

    /**
     * Adds given parameter to dynamic priority, if sum is bigger than 15,
     * sum is set with value 15 and gives error log
     *
     * @param newPriority value to add for dynamic priority
     */
    public void setDynamicPriority(final int newPriority)
    {
        dynamicPriority = (newPriority < 15) ? newPriority : 15;
        if(dynamicPriority < basePriority) {dynamicPriority=basePriority;}
    }
    /**
     * Sets dynamic priority with it's base value
     */
    public void setBasePriority(){
        if(dynamicPriority != basePriority) {
            this.dynamicPriority = this.basePriority;
            Utils.log("Changed priority of " + this.getSignature() + " from " + this.dynamicPriority +
                    " to base value - " + this.basePriority);
        }
    }


    //Assembler------------------------------------------------------

    /**
     * Executes one command from PCB's program starting from current {@link PCB#PC}
     * <p>Before execution of program the state of {@link Assembler#cpu} should be updated with value from {@link PCB#cpuState}.</p>
     * <p>After execution of program the state of {@link Assembler#cpu} should be saved to {@link PCB#cpuState}.</p>
     *
     * @return {@code false} if it was the last command
     *
     * @see Assembler#setCPUState(CPUState)
     * @see Assembler#getCPUState()
     */
    public boolean execute() {
        Utils.log(toString());
        Assembler.execute(this);

        return PC < codeLength;
    }

    public byte getByteAt(final byte address) {

        return ram.read(this.getPID(), address);
        //return this.code[address];
    }
    public void writeByteAt(final byte address, final byte value) {

        ram.write(value, this.getPID(), address);
        //this.code[address] = value;
    }

    public CPUState getCpuState() {
        return cpuState;
    }

    public void setCPUState(CPUState cpuState) {
        this.cpuState.set(cpuState);
    }

    @Override
    public String toString() {
        return "PCB{" +
                "PID=" + PID +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", PC=" + PC +
                ", basePriority=" + basePriority +
                ", dynamicPriority=" + dynamicPriority +
                ", readyTime=" + readyTime +
                ", executedOrders=" + executedOrders +
                '}';
    }

    public String getSignature() {
        return name + " id: " + PID;
    }

    @Override
    public int compareTo(PCB o) {
        return 0;
    }
}