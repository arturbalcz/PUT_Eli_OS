package processess;

import assembler.Assembler;
import assembler.CPUState;
import utils.Utils;

/**
 * Process Control Block, represents process
 */
public class PCB {

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
     * Unchangeable base priority, set in constructor, min value 0, max value 15
     */
    private final int basePriority;

    /**
     * Dynamically changed priority, min value 0, max value 15
     */
    private int dynamicPriority;

    private CPUState cpuState;

    // temporary ram, see constructor
    private final byte[] code;

    /**
     * Creates Porcess Contol Block
     *
     * @param PID unique process id
     * @param name name of the process
     * @param priority process base priority
     * @param exec temporary ram
     */
    public PCB(int PID, String name, int priority, byte[] exec) {
        this.PID = PID;
        this.name = name;
        //TODO unique name
        //TODO prority <= 0 pogadac: dummy process, czy mam się tym jakoś przejmować, czy udostepnić jakieś możliwości zmainy
        if (priority > 15) {
            Utils.log("Priority is too high, changed to priority max size - 15", true);
            priority = 15;
        }
        this.basePriority = priority;
        this.dynamicPriority = priority;
        this.state = ProcessState.READY;
        this.cpuState = Assembler.getFreshCPU();

        // TODO: ram
        // temporary ram solution for testing assembler
        this.code = exec;
        this.PC = (byte) (exec[0] + 1); // sets PC after allocated values ('LETs')
    }


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

    public void setState(ProcessState state) {
        this.state = state;
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
     * @param dynamicPriority value to add for dynamic priority
     */
    public void setDynamicPriority(int dynamicPriority) {

        if (this.dynamicPriority + dynamicPriority > 15){
            Utils.log("Priority is too high, changed priority of process " + this.PID +
                    " from " + this.dynamicPriority + " to priority max size: 15", true);
            this.dynamicPriority = 15;

        } else {
            int sum = this.dynamicPriority + dynamicPriority;
            Utils.log("Changed priority of process " + this.PID + " from "
                    + this.dynamicPriority + " to " + (sum));
            this.dynamicPriority = sum;
        }

    }

    /**
     * Sets dynamic priority with it's base value
     */
    public void setBasePriority(){
        this.dynamicPriority = this.basePriority;
        Utils.log("Changed priority of process " + this.PID + " from " + this.dynamicPriority +
                " to it's base value - " + this.basePriority);
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
        Assembler.execute(this);
        //TODO terminated
        return PC < code.length;
    }

    public byte getByteAt(final byte address) {
        return this.code[address];
    }
    public void writeByteAt(final byte address, final byte value) {
        this.code[address] = value;
    }

    public CPUState getCpuState() {
        return cpuState;
    }

    public void setCPUState(CPUState cpuState) {
        this.cpuState = cpuState;
    }
}