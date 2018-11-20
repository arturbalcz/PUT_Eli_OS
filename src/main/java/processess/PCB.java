package processess;

import assembler.Assembler;
import assembler.CPUState;

public class PCB {
    final private int PID;
    public final String name;
    private ProcessState state;
    private byte PC;
    //@Max(15)
    private final int basePriority;
    //@Max(15)
    private int dynamicPriority;
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

        //TODO: ram
        // temporary ram solution for testing assembler
        this.code = exec;
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

    public void setDynamicPriority(int dynamicPriority) {
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

    public CPUState getCpuState() {
        return cpuState;
    }

    public void setCPUState(CPUState cpuState) {
        this.cpuState = cpuState;
    }
}