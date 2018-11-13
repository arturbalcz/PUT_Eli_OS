package assembler;

import utils.Utils;

public class CPU {
    // registers
    private final Registry A = new Registry();
    private final Registry B = new Registry();
    private final Registry C = new Registry();
    private final Registry D = new Registry();

    private static final byte AL_ID = 1;
    private static final byte AH_ID = 2;
    private static final byte AX_ID = 3;
    private static final byte BL_ID = 4;
    private static final byte BH_ID = 5;
    private static final byte BX_ID = 6;
    private static final byte CL_ID = 7;
    private static final byte CH_ID = 8;
    private static final byte CX_ID = 9;
    private static final byte DL_ID = 10;
    private static final byte DH_ID = 11;
    private static final byte DX_ID = 12;

    // flags
    private boolean CF = false; // carry flag
    private boolean ZF = false; // zero flag

    public Registry getA() {
        return A;
    }

    public Registry getB() {
        return B;
    }

    public Registry getC() {
        return C;
    }

    public Registry getD() {
        return D;
    }

    public boolean getCF() {
        return this.CF;
    }

    public boolean getZF() {
        return ZF;
    }

    static byte getRegistryId(final String registry) {
        switch (registry) {
            case "AL":
                return AL_ID;
            case "AH":
                return AH_ID;
            case "AX":
                return AX_ID;
            case "BL":
                return BL_ID;
            case "BH":
                return BH_ID;
            case "BX":
                return BX_ID;
            case "CL":
                return CL_ID;
            case "CH":
                return CH_ID;
            case "CX":
                return CX_ID;
            case "DL":
                return DL_ID;
            case "DH":
                return DH_ID;
            case "DX":
                return DX_ID;
        }

        return -1;
    }

    private Registry getRegistryInstanceByStr(final String str) {
        Registry registry = null;

        switch (str.charAt(0)) {
            case 'A':
                registry = this.getA();
                break;
            case 'B':
                registry = this.getB();
                break;
            case 'C':
                registry = this.getC();
                break;
            case 'D':
                registry = this.getD();
                break;
        }

        return registry;
    }

    boolean[] getRegistryByStr(final String str) {
        Registry registry = this.getRegistryInstanceByStr(str);

        return str.endsWith("X")
                ? registry.get()
                : str.endsWith("H") ? registry.getH() : registry.getL();
    }

    boolean[] getRegistryById(final byte id) {
        switch (id) {
            case AL_ID:
                return Assembler.cpu.A.getL();
            case AH_ID:
                return Assembler.cpu.A.getH();
            case AX_ID:
                return Assembler.cpu.A.get();
            case BL_ID:
                return Assembler.cpu.B.getL();
            case BH_ID:
                return Assembler.cpu.B.getH();
            case BX_ID:
                return Assembler.cpu.B.get();
            case CL_ID:
                return Assembler.cpu.C.getL();
            case CH_ID:
                return Assembler.cpu.C.getH();
            case CX_ID:
                return Assembler.cpu.C.get();
            case DL_ID:
                return Assembler.cpu.D.getL();
            case DH_ID:
                return Assembler.cpu.D.getH();
            case DX_ID:
                return Assembler.cpu.D.get();
            default:
                return null;
        }
    }

    void setRegistryByStr(final String str, boolean[] value) {
        Registry registry = this.getRegistryInstanceByStr(str);

        assert registry != null;
        if(str.endsWith("X")) registry.set(value);
        else if(str.endsWith("H")) registry.setH(value);
        else registry.setL(value);
    }

    void setRegistryById(final byte id, final boolean[] data) {
        switch (id) {
            case AL_ID:
                Assembler.cpu.A.setL(data);
                break;
            case AH_ID:
                Assembler.cpu.A.setH(data);
                break;
            case AX_ID:
                Assembler.cpu.A.set(data);
                break;
            case BL_ID:
                Assembler.cpu.B.setL(data);
                break;
            case BH_ID:
                Assembler.cpu.B.setH(data);
                break;
            case BX_ID:
                Assembler.cpu.B.set(data);
                break;
            case CL_ID:
                Assembler.cpu.C.setL(data);
                break;
            case CH_ID:
                Assembler.cpu.C.setH(data);
                break;
            case CX_ID:
                Assembler.cpu.C.set(data);
                break;
            case DL_ID:
                Assembler.cpu.D.setL(data);
                break;
            case DH_ID:
                Assembler.cpu.D.setH(data);
                break;
            case DX_ID:
                Assembler.cpu.D.set(data);
                break;
        }
    }

    public void setCF(boolean CF) {
        this.CF = CF;
    }

    public void setZF(boolean ZF) {
        this.ZF = ZF;
    }

    void print() {
        print(false);
    }

    void print(final boolean step) {
        String msg = "CPU:\n";
        StringBuilder value = new StringBuilder();
        for(final boolean b : this.A.get()) value.insert(0, b ? 1 : 0);
        msg += "\tA: " + value;
        value = new StringBuilder();
        for(final boolean b : this.B.get()) value.insert(0, b ? 1 : 0);
        msg += "\tB: " + value;
        value = new StringBuilder();
        for(final boolean b : this.C.get()) value.insert(0, b ? 1 : 0);
        msg += "\tC: " + value;
        value = new StringBuilder();
        for(final boolean b : this.D.get()) value.insert(0, b ? 1 : 0);
        msg += "\tD: " + value;
        msg += " CF: " + (this.CF ? "1" : 0) + " ZF: " + (this.ZF ? 1 : 0);

        if (step) Utils.step(msg);
        else  Utils.log(msg);
    }
}
