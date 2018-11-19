package assembler;

/**
 * Internal utility functions for assembler module
 *
 * @see Assembler
 */
interface AssemblerUtils {
    static boolean isZero(final boolean[] arg) {
        for (final boolean anArg : arg) if (anArg) return false;
        return true;
    }

    static boolean greaterOrEqual(final boolean[] a, final boolean[] b) {
        boolean[] A = AssemblerUtils.emptyRegistry();
        System.arraycopy(a, 0, A, 0, a.length);

        boolean[] B = AssemblerUtils.emptyRegistry();
        System.arraycopy(b, 0, B, 0, a.length);

        boolean equal = true;
        for(int i = 7; i >= 0; i--) {
            if(A[i] && !B[i]) return true;
            else if(A[i] != B[i]) equal = false;
        }
        return equal;
    }

    static boolean[] emptyRegistry() {
        return AssemblerUtils.emptyRegistry(true);
    }

    static boolean[] emptyRegistry(final boolean fullSize) {
        return fullSize
                ? new boolean[]{false, false, false, false, false, false, false, false}
                : new boolean[]{false, false, false, false};
    }

    static boolean[] hexToBin(String hex) {
        return AssemblerUtils.hexToBin(hex, true);
    }

    static boolean[] hexToBin(String hex, final boolean fullSize) {
        hex = hex.toUpperCase();
        if (hex.endsWith("H")) hex = hex.substring(0, hex.length() - 1);

        final boolean[] bin = AssemblerUtils.emptyRegistry(fullSize);
        int number = Integer.parseInt(hex, 16);
        String binStr = Integer.toBinaryString(number);

        for (int i = binStr.length() - 1, j = 0; i >= 0; i--, j++) bin[j] = binStr.charAt(i) == '1';

        return bin;
    }

    static byte hexToByte(char hex) {
       return AssemblerUtils.hexToByte(String.valueOf(hex));
    }

    static byte hexToByte(String hex) {
        if (hex.endsWith("H")) hex = hex.substring(0, hex.length() - 1);
        else if(hex.startsWith("[") && hex.endsWith("]")) hex = hex.substring(1, hex.length() - 2);
        return (byte) Short.parseShort(hex, 16);
    }

    static String byteToString(byte b) {
        String value = Integer.toHexString((int) b);
        if(value.length() == 1) value = "0" + value;
        return value.toUpperCase();
    }

    static boolean[] byteToBin(final byte data) {
        String digits = Integer.toBinaryString(data >= 0 ? data : 256 + data);
        boolean[] result = emptyRegistry();
        for (int i = digits.length() - 1, j = 0; i >= 0; i--, j++) result[j] = digits.charAt(i) == '1';
        return result;
    }

    static String[] toCommandsArray(byte[] code) {
        String commands = new String(code);
        return commands.split("\n");
    }

    static byte binToByte(final boolean[] bin) {
        byte result = 0;
        for(int i = 0; i < bin.length; i++) if(bin[i]) result += Math.pow(2, i);
        return result;
    }

    static char binToChar(final boolean[] bin) {
        return (char) AssemblerUtils.binToByte(bin);
    }
}
