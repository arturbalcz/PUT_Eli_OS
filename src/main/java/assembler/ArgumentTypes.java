package assembler;

/**
 * Types of arguments used by programs and utility methods for them
 */
public enum ArgumentTypes {
    REGISTRY,
    FULL_REGISTRY,
    MEMORY,
    VALUE,
    LABEL,
    TEXT,
    CHARACTER;

    static final String REGISTRY_REGEX = "[A-D][LHX]";
    static final String FULL_REGISTRY_REGEX = "[A-D]X";
    static final String MEMORY_REGEX = "\\[[0-9A-F]{2}+H?\\]";
    static final String VALUE_REGEX = "[0-9A-F]{2}+H?";
    static final String LABEL_REGEX = "[a-z]+";
    static final String TEXT_REGEX = "\".*\"";
    static final String CHARACTER_REGEX = "\'.\'";

    static ArgumentTypes[] getTypes(ArgumentTypes...types) {
        return types;
    }

    static boolean validateArg(final String arg, final ArgumentTypes[] types, final int lets) {
        boolean valid = false;

        for(ArgumentTypes i : types) {
            switch (i) {
                case FULL_REGISTRY:
                    if(ArgumentTypes.isFullRegistry(arg)) valid = true;
                    break;
                case REGISTRY:
                    if(ArgumentTypes.isRegistry(arg)) valid = true;
                    break;
                case MEMORY:
                    if(ArgumentTypes.isMemory(arg))
                        if (Integer.parseInt(arg.substring(1, arg.length()-1), 16) <= lets) valid = true;
                    break;
                case VALUE:
                    if(ArgumentTypes.isValue(arg)) valid = true;
                    break;
                case LABEL:
                    if(ArgumentTypes.isLabel(arg)) valid = true;
                    break;
                case TEXT:
                    if(ArgumentTypes.isText(arg)) valid = true;
                    break;
                case CHARACTER:
                    if(ArgumentTypes.isCharacter(arg)) valid = true;
                    break;
            }
        }

        return valid;
    }

    static ArgumentTypes getArgumentType(final String arg) {
        if(ArgumentTypes.isFullRegistry(arg)) return ArgumentTypes.FULL_REGISTRY;
        else if(ArgumentTypes.isRegistry(arg)) return ArgumentTypes.REGISTRY;
        else if(ArgumentTypes.isMemory(arg)) return ArgumentTypes.MEMORY;
        else if(ArgumentTypes.isLabel(arg)) return ArgumentTypes.LABEL;
        else if(ArgumentTypes.isValue(arg)) return ArgumentTypes.VALUE;
        else if(ArgumentTypes.isText(arg)) return ArgumentTypes.TEXT;
        else return ArgumentTypes.CHARACTER;
    }

    static boolean isRegistry(final String str) {
        return str.matches(REGISTRY_REGEX);
    }

    static boolean isFullRegistry(final String str) {
        return str.matches(FULL_REGISTRY_REGEX);
    }

    static boolean isMemory(final String str) {
        return str.matches(MEMORY_REGEX);
    }

    static boolean isValue(final String str) {
        return str.matches(VALUE_REGEX);
    }

    static boolean isLabel(final String str) {
        return str.matches(LABEL_REGEX);
    }

    static boolean isText(final String str) {
        return str.matches(TEXT_REGEX);
    }

    static boolean isCharacter(final String str) {
        return str.matches(CHARACTER_REGEX);
    }

    static ArgumentTypes getType(final int type) {
        if (type == ArgumentTypes.FULL_REGISTRY.ordinal()) return ArgumentTypes.FULL_REGISTRY;
        if (type == ArgumentTypes.REGISTRY.ordinal()) return ArgumentTypes.REGISTRY;
        if (type == ArgumentTypes.MEMORY.ordinal()) return ArgumentTypes.MEMORY;
        if (type == ArgumentTypes.VALUE.ordinal()) return ArgumentTypes.VALUE;
        if (type == ArgumentTypes.LABEL.ordinal()) return ArgumentTypes.LABEL;
        if (type == ArgumentTypes.TEXT.ordinal()) return ArgumentTypes.TEXT;
        else /*(type == ArgumentTypes.CHARACTER.ordinal())*/ return ArgumentTypes.CHARACTER;
    }
}
