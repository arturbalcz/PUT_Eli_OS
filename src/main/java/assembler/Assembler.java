package assembler;

import processess.PCB;
import utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class Assembler {
    public static final CPU cpu = new CPU();

    private static boolean[] getFromMemory(final byte address) {
        System.out.println("ASSEMBLER + MEMORY not yet implemented");
        return AssemblerUtils.emptyRegistry();
    }

    private static void writeToMemory(final byte address, boolean[] data) {
        System.out.println("ASSEMBLER + MEMORY not yet implemented");
    }

    private HashMap<String, Byte> labels = new HashMap<>();

    boolean hasLabel(final String label) {
        return this.labels.containsKey(label);
    }

    void addEmptyLabel(final String label) {
        this.labels.put(label, (byte) -1);
    }

    void implementLabel(final String label, final byte address) {
        this.labels.computeIfPresent(label, (k, v) -> v = address);
    }

    byte getLabelValue(final String label) {
        return this.labels.get(label);
    }

    public byte[] compile(final byte[] program) {
        if(this.validate(program)) return this.getExecutableBytes(program);
        return null;
    }

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

    private byte[] getExecutableBytes(final byte[] code) {
        Utils.log("getting executable...");
        List<Byte> bytes = new LinkedList<>();
        final String[] commands = AssemblerUtils.toCommandsArray(code);

        Arrays.stream(commands).forEach((c) -> bytes.addAll(Instruction.getExecutable(c, this, (byte) bytes.size())));

        final byte[] result = new byte[bytes.size()];
        for(int i = 0; i < bytes.size(); i++) result[i] = bytes.get(i);

        return result;
    }

    public static void execute(byte address, PCB pcb) {
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

    static void mov(final byte type1, final byte arg1, final byte type2, final byte arg2) {
        final boolean[] data = Assembler.getData(type2, arg2);
        Assembler.writeData(type1, arg1, data);
    }

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

    static void inc(final byte type, final byte arg) {
        Assembler.add(type, arg, (byte) ArgumentTypes.VALUE.ordinal(), (byte) 1, false);
    }

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

    static void dec(final byte type, final byte arg) {
        Assembler.sub(type, arg, (byte) ArgumentTypes.VALUE.ordinal(), (byte) 1);
    }

    static void mul(final byte type1, final byte arg1, final byte type2, final byte arg2) {
        final boolean[] initialValueBin = Assembler.getData(type1, arg1);
        final byte initialValue = AssemblerUtils.binToByte(initialValueBin);
        final boolean[] nBin = Assembler.getData(type2, arg2);
        final byte n = AssemblerUtils.binToByte(nBin);
        for (int i = 1; i < n; i++)
            Assembler.add(type1, arg1, (byte) ArgumentTypes.VALUE.ordinal(), initialValue, true);
    }

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

    static void and(final byte type1, final byte arg1, final byte type2, final byte arg2) {
        boolean[] source = Assembler.getData(type1, arg1);
        boolean[] value = Assembler.getData(type2, arg2);

        for(int i = 0; i < source.length; i++) source[i] = source[i] && value[i];

        Assembler.writeData(type1, arg1, source);
    }

    static void nand(final byte type1, final byte arg1, final byte type2, final byte arg2) {
        boolean[] source = Assembler.getData(type1, arg1);
        boolean[] value = Assembler.getData(type2, arg2);

        for(int i = 0; i < source.length; i++) source[i] = !(source[i] && value[i]);

        Assembler.writeData(type1, arg1, source);
    }

    static void or(final byte type1, final byte arg1, final byte type2, final byte arg2) {
        boolean[] source = Assembler.getData(type1, arg1);
        boolean[] value = Assembler.getData(type2, arg2);

        for(int i = 0; i < source.length; i++) source[i] = source[i] || value[i];

        Assembler.writeData(type1, arg1, source);
    }

    static void nor(final byte type1, final byte arg1, final byte type2, final byte arg2) {
        boolean[] source = Assembler.getData(type1, arg1);
        boolean[] value = Assembler.getData(type2, arg2);

        for (int i = 0; i < source.length; i++) source[i] = !(source[i] || value[i]);

        Assembler.writeData(type1, arg1, source);
    }

    static void xor(final byte type1, final byte arg1, final byte type2, final byte arg2) {
        boolean[] source = Assembler.getData(type1, arg1);
        boolean[] value = Assembler.getData(type2, arg2);

        for(int i = 0; i < source.length; i++) source[i] = source[i] ^ value[i];

        Assembler.writeData(type1, arg1, source);
    }

    static void not(final byte type, final byte arg) {
        boolean[] source = Assembler.getData(type, arg);

        for(int i = 0; i < source.length; i++) {
            source[i] = !source[i];
        }

        Assembler.writeData(type, arg, source);
    }

    static void jmp(final byte address, PCB pcb) {
        Utils.log("jump from " + pcb.getPC() + " to " + address + " at " + pcb.name);
        pcb.setPC(address);
    }

    static void jnz(final byte address, PCB pcb) {
        if(!Assembler.cpu.getZF()) Assembler.jmp(address, pcb);
    }

    static void prt(final byte type, final byte... arg) {
        StringBuilder output = new StringBuilder();

        if(ArgumentTypes.getType(type) == ArgumentTypes.TEXT)
            for(final byte c : arg) output.append((char) c);
        else
            output.append(AssemblerUtils.binToChar(Assembler.getData(type, arg[0])));

        System.out.println(output);

        Utils.log("PRT " + Arrays.toString(arg));
    }

    static void nop() {
        Utils.step("NOP");
    }
}
