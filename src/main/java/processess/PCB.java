package processess;

public class PCB {
    final private int PID;
    final String name;
    private ProcessState state;
    private byte PC;
    //@Max(15)
    private final int basePriority;
    //@Max(15)
    private int dynamicPriority;



    public PCB(int PID, String name, int priority) {
        this.PID = PID;
        this.name = name;
        this.basePriority = priority;
        this.dynamicPriority = priority;
        //TODO: ram
        state = ProcessState.READY;
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

    /*
    public boolean execute(){
        Assembler.execute(PC, this);
        return PC != code.length;   TODO sprawdzanie czy program się nie skończył
    }
    */
}
