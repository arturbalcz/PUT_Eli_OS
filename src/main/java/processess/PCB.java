package processess;

import assembler.Assembler;
import assembler.CPUState;

public class PCB {
    final private int PID;
    public final String name;
    private ProcessState state;
    private byte PC;
    //@Max(17)
    private final int basePriority;
    //@Max(17)
    //real-time priority: 16, 17 [Artur]
    //dynamic priority 1-15 [Artur]
    private int dynamicPriority;
    private int readyTime;
    private int executedOrders;
    private CPUState cpuState;

    // temporary ram, see constructor
    private final byte[] code;

    public PCB(int PID, String name, int priority, byte[] exec) {
        this.PID = PID;
        this.name = name;
        this.basePriority = priority;
        this.dynamicPriority = priority;
        this.state = ProcessState.READY;
        this.cpuState = Assembler.getFreshCPU();
        this.readyTime=0;
        // TODO: ram
        // temporary ram solution for testing assembler
        this.code = exec;
        this.PC = (byte) (exec[0] + 1); // sets PC after allocated values ('LETs')
    }


    public int getReadyTime() { return  readyTime; }

    public void setReadyTime(int readyTime)
    {
        this.readyTime=readyTime;
    }

    public int getExecutedOrders() {return executedOrders; }

    public void setExetucedOrders(int executedOrders) {this.executedOrders = executedOrders; }

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

    public void setDynamicPriority(int dynamicPriority) {

        if(basePriority < 16 && dynamicPriority > 15) dynamicPriority = 15; //[Artur]
        if(basePriority > 15 && dynamicPriority > 17) dynamicPriority = 17; //[Artur]

        //[Artur]: if(dynamicPriority < basePriority) throw Exception;


        this.dynamicPriority = dynamicPriority;

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