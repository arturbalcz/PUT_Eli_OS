package assembler;


import java.util.Arrays;

/**
 * Representation of 8 bit registry used by {@link CPU}
 */
class Registry {
    public static final int length = 8;

    private boolean[] registry = AssemblerUtils.emptyRegistry();

    public final boolean[] get() {
        return this.registry;
    }

    public void set(boolean [] value) {
        this.registry = value;
    }

    final boolean[] getH() {
        return Arrays.copyOfRange(this.registry, 4, 8);
    }

    void setH(boolean[] value) {
        this.registry[7] = value[3];
        this.registry[6] = value[2];
        this.registry[5] = value[1];
        this.registry[4] = value[0];
    }

    final boolean[] getL() {
        return Arrays.copyOfRange(this.registry, 0, 4);
    }

    void setL(boolean [] value) {
        this.registry[3] = value[3];
        this.registry[2] = value[2];
        this.registry[1] = value[1];
        this.registry[0] = value[0];
    }
}
