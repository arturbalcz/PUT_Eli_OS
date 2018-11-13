package assembler;

import assembler.AssemblerUtils;

import java.util.Arrays;

class Registry {
    public static final int length = 8;

    private boolean[] registry = AssemblerUtils.emptyRegistry();

    public final boolean[] get() {
        return this.registry;
    }

    public void set(boolean [] value) {
//        if(value.length != length) throw new Exception("Illegal length");
        this.registry = value;
    }

    public final boolean[] getH() {
        return Arrays.copyOfRange(this.registry, 4, 8);
    }

    public void setH(boolean [] value) {
//        if(value.length != length / 2) throw new Exception("Illegal length");
        this.registry[7] = value[3];
        this.registry[6] = value[2];
        this.registry[5] = value[1];
        this.registry[4] = value[0];
    }

    public final boolean[] getL() {
        return Arrays.copyOfRange(this.registry, 0, 4);
    }

    public void setL(boolean [] value) {
//        if(value.length != length / 2) throw new Exception("Illegal length");
        this.registry[3] = value[3];
        this.registry[2] = value[2];
        this.registry[1] = value[1];
        this.registry[0] = value[0];
    }
}
