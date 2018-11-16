package assembler;

/**
 * Container for storing processor state in {@link processess.PCB}
 *
 * @see processess.PCB
 * @see CPU
 */
public class CPUState {
    private final Registry A;
    private final Registry B;
    private final Registry C;
    private final Registry D;

    private final boolean CF;
    private final boolean ZF;

    public CPUState(Registry a, Registry b, Registry c, Registry d, boolean CF, boolean ZF) {
        A = a;
        B = b;
        C = c;
        D = d;
        this.CF = CF;
        this.ZF = ZF;
    }

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
        return CF;
    }

    public boolean getZF() {
        return ZF;
    }
}
