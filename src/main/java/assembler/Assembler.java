package assembler;

import processess.PCB;
import utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *  Core class of assembler module. Validates, parses and executes whole programs.
 *
 * @see Instruction
 */
public class Assembler {
    public static final CPU cpu = new CPU();

    /**
     *  Returns current state od processor to save in {@link PCB}
     *
     * @see CPU
     * @see processess.PCB
     * @see PCB#execute()
     * @return current state od processor
     */
    public static CPUState getCPUState() {
        return new CPUState(
                cpu.getA(),
                cpu.getB(),
                cpu.getC(),
                cpu.getD(),
                cpu.getCF(),
                cpu.getZF()
        );
    }

    /**
     * Sets {@link Assembler#cpu} to given state
     * @param cpuState new state of cpu
     *
     * @see PCB#execute()
     */
    public static void setCPUState(CPUState cpuState) {
        cpu.getA().set(cpu.getA().get());
        cpu.getB().set(cpu.getB().get());
        cpu.getC().set(cpu.getC().get());
        cpu.getD().set(cpu.getD().get());
        cpu.setCF(cpuState.getCF());
        cpu.setZF(cpuState.getZF());
    }

    /**
     * Compiles given program to executable
     *
     * @param program code to compile
     * @return executable of given program or {@code null} if compilation errors occurred
     */
    public byte[] compile(final byte[] program) {
        if(this.validate(program)) return this.getExecutable(program);
        return null;
    }

    /**
     * Validates if given program is valid and can parsed to executable
     *
     * @param code program to validate
     * @return {@code true} if program is valid
     */
    private boolean validate(byte[] code) {
        Utils.log("validating...");
        String[] commands = AssemblerUtils.toCommandsArray(code);

        int i = 0;
        try{
            for (; i < commands.length; i++) Instruction.validate(commands[i], this);
        } catch (Exception e) {
            System.out.println(e.getMessage() + " at line " + (i+1));
            Utils.log("not valid");
            return false;
        }

        Utils.log("validated");
        return true;
    }

    /**
     * Parses given program to executable
     *
     * @see #compile
     * @see #validate
     * @param code program to parse
     * @return executable
     */
    private byte[] getExecutable(final byte[] code) {
        Utils.log("getting executable...");
        List<Byte> bytes = new LinkedList<>();
        final String[] commands = AssemblerUtils.toCommandsArray(code);

        Arrays.stream(commands).forEach((c) -> bytes.addAll(Instruction.getExecutable(c, this, (byte) bytes.size())));

        final byte[] result = new byte[bytes.size()];
        for(int i = 0; i < bytes.size(); i++) result[i] = bytes.get(i);

        return result;
    }

    /**
     * Executes one instruction starting at {@code pcb.PC} and change {@code pcb.PC} to beginning of next instruction
     *
     * @param pcb {@link PCB} of process with instruction to execute
     */
    public static void execute(PCB pcb) {
        byte address = pcb.getPC();
        Utils.log("executing instruction at " + address + " from " + pcb.name);

        Instruction instruction = Instruction.getByCode(pcb.getByteAt(address++));
        byte[] args = null;
        if(instruction.argsNumber > 0) {
            byte argTypesBin = pcb.getByteAt(address++);
            String argTypes = AssemblerUtils.byteToString(argTypesBin);
            byte arg1Type = AssemblerUtils.hexToByte(argTypes.charAt(0));
            if(argTypes.charAt(1) != 'F'){
                byte arg2Type = AssemblerUtils.hexToByte(argTypes.charAt(1));
                args = new byte[] {arg1Type, pcb.getByteAt(address++), arg2Type, pcb.getByteAt(address++)};
            }
            else {
                if(ArgumentTypes.getType(arg1Type) == ArgumentTypes.TEXT) {
                    List<Byte> temp = new ArrayList<>();
                    temp.add(arg1Type);
                    byte letter = pcb.getByteAt(address++);
                    while (letter != -1) {
                        temp.add(letter);
                        letter = pcb.getByteAt(address++);
                    }
                    args = new byte[temp.size()];
                    for (int i = 0; i < temp.size(); i++) {
                        args[i] = temp.get(i);
                    }
                }
                else args = new byte[] {arg1Type, pcb.getByteAt(address++)};
            }
        }

        pcb.setPC(address);
        instruction.execute(pcb, args);
    }

    /**
     *  Labels used in currently parsed program
     */
    private HashMap<String, Byte> labels = new HashMap<>();

    /**
     * Checks if label is already added to current program
     *
     * @see Assembler::labels
     * @param label label name to look for
     * @return true if label exists
     */
    boolean hasLabel(final String label) {
        return this.labels.containsKey(label);
    }

    /**
     * Adds new label to the program
     *
     * @see Assembler::labels
     * @see Assembler::implementLabel
     * @param label label name to add
     */
    void addEmptyLabel(final String label) {
        this.labels.put(label, (byte) -1);
    }

    /**
     * Adds location witch given label points to
     *
     * @see Assembler::labels
     * @see Assembler::addEmptyLabel
     * @param label label to implement
     * @param address address for label to point to
     */
    void implementLabel(final String label, final byte address) {
        this.labels.computeIfPresent(label, (k, v) -> v = address);
    }

    /**
     * Returns locations witch given label points to
     *
     * @see Assembler::labels
     * @see Assembler::addEmptyLabel
     * @param label label to check
     * @return address witch labels to point to
     */
    byte getLabelValue(final String label) {
        return this.labels.get(label);
    }

    /**
     * Set {@code cpu.ZF} if given value is equal to 0. In other case unset the flag.
     * @param data value to test
     */
    private static void checkZF(final boolean[] data) {
        if(AssemblerUtils.isZero(data)) {
            Utils.log("ZF set");
            cpu.setZF(true);
        }
        else {
            Utils.log("ZF unset");
            cpu.setZF(false);
        }
    }

    /**
     * Retrieves data from given argument
     *
     * @see ArgumentTypes
     * @see Assembler#writeData(byte, byte, boolean[])
     * @param type type of argument to retrieve
     * @param arg argument to retrieve
     * @return binary representation of argument value
     */
    private static boolean[] getData(final byte type, final byte arg) {
        final ArgumentTypes argumentType = ArgumentTypes.getType(type);
        boolean [] data = AssemblerUtils.emptyRegistry();
        switch (argumentType) {
            case FULL_REGISTRY:
            case REGISTRY:
                data = Assembler.cpu.getRegistryById(arg);
                break;
            case MEMORY:
                data = Assembler.getFromMemory(arg);
                break;
            case VALUE:
            case CHARACTER:
                data = AssemblerUtils.byteToBin(arg);
                break;
        }

        Utils.log("getting data from " + argumentType.name() + " " + arg);
        return Arrays.copyOf(data, data.length);
    }

    /**
     * Gets single byte from system memory
     * <p>NOT IMPLEMENTED</p>
     *
     * @param address address of memory cell to read data from
     * @return retrieved data
     */
    private static boolean[] getFromMemory(final byte address) {
        System.out.println("ASSEMBLER + MEMORY not yet implemented");
        return AssemblerUtils.emptyRegistry();
    }

    /**
     * Writes single byte from system memory
     * <p>NOT IMPLEMENTED</p>
     *
     * @param address address of memory cell to write data to
     */
    private static void writeToMemory(final byte address, boolean[] data) {
        System.out.println("ASSEMBLER + MEMORY not yet implemented");
    }

    /**
     * Writes given data to location specified by given argument and type
     *
     * @see ArgumentTypes
     * @see Assembler#getData(byte, byte)
     * @param type type of argument
     * @param address address of location to write data
     * @param data data to write
     */
    private static void writeData(final byte type, final byte address, boolean[] data) {
        data = Arrays.copyOf(data, data.length);
        if(type == ArgumentTypes.REGISTRY.ordinal() || type == ArgumentTypes.FULL_REGISTRY.ordinal()){
            Utils.log("writing data to registry");
            Assembler.cpu.setRegistryById(address, data);
        }
        else {
            Utils.log("writing data to memory");
            Assembler.writeToMemory(address, data);
        }
        Assembler.checkZF(data);
    }

    /**
     * Copies data from second to first argument
     *
     * @param type1 type of first argument
     * @param arg1 first argument
     * @param type2 type of second argument
     * @param arg2 second argument
     */
    static void mov(final byte type1, final byte arg1, final byte type2, final byte arg2) {
        final boolean[] data = Assembler.getData(type2, arg2);
        Assembler.writeData(type1, arg1, data);
    }

    /**
     * Adds two arguments and save the result to first one.
     * If the result exceeds 8bit range, sets CF
     *
     * @param type1 type of first argument
     * @param arg1 first argument
     * @param type2 type of second argument
     * @param arg2 second argument
     * @param useCF if {@code true} uses carry from past operations
     */
    static void add(final byte type1, final byte arg1, final byte type2, final byte arg2, final boolean useCF) {
        boolean[] source = Assembler.getData(type1, arg1);
        boolean[] value = Assembler.getData(type2, arg2);

        boolean carry = false;
        if(useCF && Assembler.cpu.getCF()) {
            carry = true;
            Assembler.cpu.setCF(false);
        }

        for(int i = 0; i < source.length; i++) {
            if(carry && !source[i]) {
                carry = false;
                source[i] = true;
            }
            if(source[i] && value[i]) {
                source[i] = carry;
                carry = true;
            }
            else if(source[i] && !value[i]) {
                if(carry) {
                    source[i] = false;
                    carry = true;
                }
            }
            else if(!source[i] && value[i]) source[i] = true;
        }

        if(carry) Assembler.cpu.setCF(true);
        Assembler.writeData(type1, arg1, source);
    }

    /**
     * Increments given argument
     * If the result exceeds 8bit range, sets CF
     *
     * @see Assembler#add(byte, byte, byte, byte, boolean)
     * @param type type of argument
     * @param arg argument
     */
    static void inc(final byte type, final byte arg) {
        Assembler.add(type, arg, (byte) ArgumentTypes.VALUE.ordinal(), (byte) 1, false);
    }

    /**
     * Subtracts value of second argument from first and saves the result to first one.
     * If the result exceeds 8bit range, sets CF
     *
     * @param type1 type of first argument
     * @param arg1 first argument
     * @param type2 type of second argument
     * @param arg2 second argument
     */
    static void sub(final byte type1, final byte arg1, final byte type2, final byte arg2) {
        boolean[] source = Assembler.getData(type1, arg1);
        boolean[] value = Assembler.getData(type2, arg2);

        for(int i = 0; i < source.length; i++) {
            if(source[i] && value[i]) source[i] = false;
            else if(!source[i] && value[i]) {
                for (int j = i+1; j < source.length; j++) if(source[j]) {
                    source[j] = false;
                    for(int k = j-1; k >= i; k--) source[k] = true;
                    break;
                }
                if(!source[i]) for (int j = i; j < source.length; j++) source[j] = true;
            }
        }

        Assembler.writeData(type1, arg1, source);
    }

    /**
     * Decrements given argument
     *
     * @see Assembler#sub(byte, byte, byte, byte)
     * @param type type of argument
     * @param arg argument
     */
    static void dec(final byte type, final byte arg) {
        Assembler.sub(type, arg, (byte) ArgumentTypes.VALUE.ordinal(), (byte) 1);
    }

    /**
     * Multiplies two arguments and saves the result to first one.
     * If the result exceeds 8bit range, sets CF
     *
     * @param type1 type of first argument
     * @param arg1 first argument
     * @param type2 type of second argument
     * @param arg2 second argument
     */
    static void mul(final byte type1, final byte arg1, final byte type2, final byte arg2) {
        final boolean[] initialValueBin = Assembler.getData(type1, arg1);
        final byte initialValue = AssemblerUtils.binToByte(initialValueBin);
        final boolean[] nBin = Assembler.getData(type2, arg2);
        final byte n = AssemblerUtils.binToByte(nBin);
        for (int i = 1; i < n; i++)
            Assembler.add(type1, arg1, (byte) ArgumentTypes.VALUE.ordinal(), initialValue, true);
    }

    /**
     * Divides two arguments and saves the quotient to H part of first argument and the remainder to L part.
     * <p>Consumes only X registers as arg1 and arg2 </p>
     *
     * @param type1 type of first argument
     * @param arg1 first argument
     * @param type2 type of second argument
     * @param arg2 second argument
     */
    static void div(final byte type1, final byte arg1, final byte type2, final byte arg2) {
        final boolean[] divisorBin = Assembler.getData(type2, arg2);
        final byte divisor = AssemblerUtils.binToByte(divisorBin);
        byte quotient = 0;

        while (AssemblerUtils.greaterOrEqual(Assembler.getData(type1, arg1), divisorBin)) {
            Assembler.sub(type1, arg1, (byte) ArgumentTypes.VALUE.ordinal(), divisor);
            quotient++;
        }

        final byte quotientDestination;
        if(arg1 == CPU.getRegistryId("AX")) quotientDestination = CPU.getRegistryId("AH");
        else if(arg1 == CPU.getRegistryId("BX")) quotientDestination =  CPU.getRegistryId("BH");
        else if(arg1 == CPU.getRegistryId("CX")) quotientDestination =  CPU.getRegistryId("CH");
        else /*if(arg1 == CPU.getRegistryId("DX"))*/ quotientDestination =  CPU.getRegistryId("DH");
        Assembler.writeData(type1, quotientDestination, AssemblerUtils.byteToBin(quotient));
    }

    /**
     * Performs logical AND on given arguments and saves the result to the first one
     *
     * @param type1 type of first argument
     * @param arg1 first argument
     * @param type2 type of second argument
     * @param arg2 second argument
     */
    static void and(final byte type1, final byte arg1, final byte type2, final byte arg2) {
        boolean[] source = Assembler.getData(type1, arg1);
        boolean[] value = Assembler.getData(type2, arg2);

        for(int i = 0; i < source.length; i++) source[i] = source[i] && value[i];

        Assembler.writeData(type1, arg1, source);
    }

    /**
     * Performs logical NAND on given arguments and saves the result to the first one
     *
     * @param type1 type of first argument
     * @param arg1 first argument
     * @param type2 type of second argument
     * @param arg2 second argument
     */
    static void nand(final byte type1, final byte arg1, final byte type2, final byte arg2) {
        boolean[] source = Assembler.getData(type1, arg1);
        boolean[] value = Assembler.getData(type2, arg2);

        for(int i = 0; i < source.length; i++) source[i] = !(source[i] && value[i]);

        Assembler.writeData(type1, arg1, source);
    }

    /**
     * Performs logical OR on given arguments and saves the result to the first one
     *
     * @param type1 type of first argument
     * @param arg1 first argument
     * @param type2 type of second argument
     * @param arg2 second argument
     */
    static void or(final byte type1, final byte arg1, final byte type2, final byte arg2) {
        boolean[] source = Assembler.getData(type1, arg1);
        boolean[] value = Assembler.getData(type2, arg2);

        for(int i = 0; i < source.length; i++) source[i] = source[i] || value[i];

        Assembler.writeData(type1, arg1, source);
    }

    /**
     * Performs logical NOR on given arguments and saves the result to the first one
     *
     * @param type1 type of first argument
     * @param arg1 first argument
     * @param type2 type of second argument
     * @param arg2 second argument
     */
    static void nor(final byte type1, final byte arg1, final byte type2, final byte arg2) {
        boolean[] source = Assembler.getData(type1, arg1);
        boolean[] value = Assembler.getData(type2, arg2);

        for (int i = 0; i < source.length; i++) source[i] = !(source[i] || value[i]);

        Assembler.writeData(type1, arg1, source);
    }

    /**
     * Performs logical XOR on given arguments and saves the result to the first one
     *
     * @param type1 type of first argument
     * @param arg1 first argument
     * @param type2 type of second argument
     * @param arg2 second argument
     */
    static void xor(final byte type1, final byte arg1, final byte type2, final byte arg2) {
        boolean[] source = Assembler.getData(type1, arg1);
        boolean[] value = Assembler.getData(type2, arg2);

        for(int i = 0; i < source.length; i++) source[i] = source[i] ^ value[i];

        Assembler.writeData(type1, arg1, source);
    }

    /**
     * Performs logical NOT on given argument and saves the result to it
     *
     * @param type type of argument
     * @param arg argument
     */
    static void not(final byte type, final byte arg) {
        boolean[] source = Assembler.getData(type, arg);

        for(int i = 0; i < source.length; i++) {
            source[i] = !source[i];
        }

        Assembler.writeData(type, arg, source);
    }

    /**
     * Performs unconditional jump to given address by setting PC of given {@link PCB}
     *
     * @see Assembler#jnz(byte, PCB)
     * @param address address to jump to
     * @param pcb {@link PCB} of current process
     */
    static void jmp(final byte address, PCB pcb) {
        Utils.log("jump from " + pcb.getPC() + " to " + address + " at " + pcb.name);
        pcb.setPC(address);
    }

    /**
     * Performs jump to given address if ZF is set
     *
     * @see Assembler#jmp(byte, PCB)
     * @param address address to jump to
     * @param pcb {@link PCB} of current process
     */
    static void jnz(final byte address, PCB pcb) {
        if(!Assembler.cpu.getZF()) Assembler.jmp(address, pcb);
    }

    /**
     * Prints given value to the console
     *
     * @param type type of value to print
     * @param arg value to print
     */
    static void prt(final byte type, final byte... arg) {
        StringBuilder output = new StringBuilder();

        if(ArgumentTypes.getType(type) == ArgumentTypes.TEXT)
            for(final byte c : arg) output.append((char) c);
        else
            output.append(AssemblerUtils.binToChar(Assembler.getData(type, arg[0])));

        System.out.println(output);

        Utils.log("PRT " + Arrays.toString(arg));
    }

    /**
     * Does nothing as instruction
     */
    static void nop() {
        Utils.step("NOP");
    }
}
