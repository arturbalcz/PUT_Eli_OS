package assembler;

import processess.PCB;
import utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Stores assembler instructions representation and metadata
 *
 * @see Assembler
 */
abstract class Instruction {

    /**
     * Byte codes of all assembler instructions used in the system
     */
    private static final HashMap<String, Byte> codes = new HashMap<>();

    /**
     * All assembler instructions used in the system with metadata and implementation
     */
    private static final HashMap<Byte, Instruction> instructions = createInstructions();

    /**
     * Creates map of all assembler instructions used in the system
     *
     * @return map of all assembler instructions used in the system
     */
    private static HashMap<Byte, Instruction> createInstructions() {
        HashMap<Byte, Instruction> instructionsMap = new HashMap<>();
        byte code = 11;

        Instruction instruction = new Instruction(
                "NOP",
                0,
                (ArgumentTypes[]) null
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.nop();
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "ADD",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.add(args[0], args[1], args[2], args[3], false, pcb);
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "ADC",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.add(args[0], args[1], args[2], args[3], true, pcb);
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "INC",
                1,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.inc(args[0],args[1], pcb);
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "SUB",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.sub(args[0], args[1], args[2], args[3], pcb);
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "DEC",
                1,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.dec(args[0], args[1], pcb);
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "MUL",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.mul(args[0], args[1], args[2], args[3], pcb);
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "DIV",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.div(args[0], args[1], args[2], args[3], pcb);
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "MOV",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.mov(args[0], args[1], args[2], args[3], pcb);
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "AND",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.and(args[0], args[1], args[2], args[3], pcb);
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "NAND",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.nand(args[0], args[1], args[2], args[3], pcb);
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "OR",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.or(args[0], args[1], args[2], args[3], pcb);
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "NOR",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.nor(args[0], args[1], args[2], args[3], pcb);
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "XOR",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.xor(args[0], args[1], args[2], args[3], pcb);
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "JMP",
                1,
                ArgumentTypes.getTypes(ArgumentTypes.LABEL)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.jmp(args[1], pcb);
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "JNZ",
                1,
                ArgumentTypes.getTypes(ArgumentTypes.LABEL)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.jnz(args[1], pcb);
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "PRT",
                1,
                ArgumentTypes.getTypes(ArgumentTypes.TEXT, ArgumentTypes.CHARACTER, ArgumentTypes.REGISTRY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.prt(args[0], pcb , Arrays.copyOfRange(args, 1, args.length-1));
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "FLC",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.TEXT),
                ArgumentTypes.getTypes(ArgumentTypes.TEXT)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                int firstArgEnd = 0;
                while (args[firstArgEnd] != -1) firstArgEnd++;

                Assembler.flc(
                        Arrays.copyOfRange(args, 1, firstArgEnd),
                        Arrays.copyOfRange(args, firstArgEnd+2, args.length-1)
                );
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "FLG",
                1,
                ArgumentTypes.getTypes(ArgumentTypes.TEXT)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.flg(Arrays.copyOfRange(args, 1, args.length-1));
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "CP",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.TEXT),
                ArgumentTypes.getTypes(ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                int argEnd = 0;
                while (args[argEnd] != -1) argEnd++;
                final byte[] textArgs = Arrays.copyOfRange(args, 1, argEnd);

                argEnd = 0;
                while (textArgs[argEnd] != ' ') argEnd++;
                final byte[] exeName =  Arrays.copyOfRange(textArgs, 0, argEnd);
                final byte[] processName =  Arrays.copyOfRange(textArgs, argEnd+1, textArgs.length);

                Assembler.cp(exeName, processName, args[args.length-1]);
            }
        };
        instructionsMap.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        return instructionsMap;
    }

    static Instruction getByCode(final byte code) {
        return Instruction.instructions.get(code);
    }

    /**
     * Validates if given line of assembler code is a valid instruction with arguments
     *
     * @param line instruction to validate
     * @param assembler assembler witch compiles given instruction
     * @throws Exception when validation error occurs
     */
    static void validate(final String line, Assembler assembler) throws Exception {
        List<String> pieces = Instruction.getPieces(line);

        final boolean hasLabel = pieces.get(0).endsWith(":");
        int begin = hasLabel ? 1 : 0;

        if(hasLabel) {
            String label = pieces.get(0).substring(0, pieces.get(0).length()-1);
            if(assembler.hasLabel(label)) throw new Exception("Duplicated label name: " + label);
            if(!ArgumentTypes.isLabel(label)) throw new Exception("Invalid label name: " + label);
            assembler.addEmptyLabel(label);
        }

        Instruction instruction = Optional.ofNullable(
                Instruction.instructions.get(codes.get(pieces.get(begin)))
        ).orElseThrow(() -> new Exception("Invalid instruction: " + pieces.get(begin)));

        if(instruction.argsNumber != pieces.size()-(begin+1))
            throw new Exception("Invalid number of arguments: " + instruction.name);

        for(int i = begin+1; i <= instruction.argsNumber; i++) {
            if(instruction.getArgTypes()[i-1].length == 1 && instruction.getArgTypes()[i-1][0] == ArgumentTypes.LABEL) {
                if(!assembler.hasLabel(pieces.get(i))) throw new Exception("Label not declared: " + pieces.get(i));
            }
            else if(!ArgumentTypes.validateArg(pieces.get(i), instruction.getArgTypes()[i-1]))
                throw new Exception("Invalid " + i + ". argument: " + pieces.get(i) + " for " + instruction.name);
        }
    }

    /**
     * Parses given line of assembler code to executable
     *
     * @param line instruction to parse
     * @param assembler assembler witch compiles given instruction
     * @param address address of given line in executable
     * @return executable of given line
     */
    static List<Byte> getExecutable(final String line, Assembler assembler, final byte address) {
        List<String> pieces = Instruction.getPieces(line);
        List<Byte> executable = new LinkedList<>();

        final boolean hasLabel = pieces.get(0).endsWith(":");
        final int begin = hasLabel ? 1 : 0;

        if(hasLabel) assembler.implementLabel(pieces.get(0).substring(0, pieces.get(0).length()-1), address);

        final byte code = Instruction.codes.get(pieces.get(begin));
        Instruction instruction = Instruction.instructions.get(code);
        executable.add(code);

        if(instruction.argsNumber > 0) {
            final ArgumentTypes type1 = ArgumentTypes.getArgumentType(pieces.get(begin+1));
            String type1Code = String.valueOf(type1.ordinal());
            String type2Code = instruction.argsNumber > 1 ? String.valueOf(ArgumentTypes.getArgumentType(pieces.get(begin+2)).ordinal()) : "F";
            executable.add(AssemblerUtils.hexToByte(type1Code + type2Code));

            executable.addAll(Instruction.getArgumentExecutable(type1, pieces, begin+1, assembler));
            if(instruction.argsNumber == 2) {
                final ArgumentTypes type2 = ArgumentTypes.getArgumentType(pieces.get(begin+2));
                executable.addAll(Instruction.getArgumentExecutable(type2, pieces, begin+2, assembler));
            }
        }

        return executable;
    }

    /**
     * Splits given line of assembler code into pieces of one instruction or argument
     *
     * @param line line to split
     * @return pieces of one instruction or argument
     */
    private static List<String> getPieces(final String line) {
        final String[] piecesPre = line.split(" ");
        List<String> pieces = new ArrayList<>();
        for (int i = 0; i < piecesPre.length; i++) {
            if(piecesPre[i].startsWith("\"")) {
                StringBuilder buffer = new StringBuilder();
                for (int j = i; j < piecesPre.length; j++) {
                    buffer.append(piecesPre[j]);
                    if(piecesPre[j].endsWith("\"")) {
                        i = j;
                        pieces.add(buffer.toString());
                        break;
                    }
                    else buffer.append(" ");
                }
            }
            else pieces.add(piecesPre[i]);
        }

        return pieces;
    }

    /**
     * Parses given argument to executable
     *
     * @param type type of argument to parse
     * @param pieces pieces of line of code with argument
     * @param begin index of first piece after argument
     * @param assembler assembler witch compiles given argument
     * @return executable of argument
     */
    private static List<Byte> getArgumentExecutable(final ArgumentTypes type, final List<String> pieces, int begin, Assembler assembler) {
        List<Byte> executable = new ArrayList<>();
        switch (type) {
            case FULL_REGISTRY:
            case REGISTRY:
                executable.add(CPU.getRegistryId(pieces.get(begin)));
                break;
            case MEMORY:
                executable.add(AssemblerUtils.memoryAddressToByte(pieces.get(begin)));
                break;
            case VALUE:
                executable.add(AssemblerUtils.hexToByte(pieces.get(begin).substring(0, pieces.get(begin).length()-1)));
                break;
            case LABEL:
                executable.add(assembler.getLabelValue(pieces.get(begin)));
                break;
            case CHARACTER:
                executable.add((byte) pieces.get(begin).charAt(1));
                break;
            case TEXT:
                String piece = pieces.get(begin);
                for(int i = 1; i < piece.length()-1; i++) executable.add((byte) piece.charAt(i));
                executable.add((byte) -1);
                break;
        }
        return executable;
    }

    /**
     * Mnemonic name of instruction
     */
    final String name;

    /**
     * Number of arguments it consumes
     */
    final int argsNumber;

    /**
     * Array of {@link ArgumentTypes} possible for each argument
     */
    private final ArgumentTypes[][] argTypes;

    Instruction(final String name, final int argsNumber, ArgumentTypes[]... argsTypes) {
        this.name = name;
        this.argsNumber = argsNumber;
        this.argTypes = argsTypes;
    }

    /**
     * Executes instruction with given arguments
     *
     * @param pcb {@link PCB} of current process
     * @param args arguments to use
     */
    void execute(PCB pcb, byte... args) {
        Utils.log("before " + name + ":");
        Assembler.cpu.print();

        command(pcb, args);

        Utils.log("after " + name + ":");
        Assembler.cpu.print(true);
    }

    /**
     * Implementation of the instruction
     *
     * @param pcb {@link PCB} of current process
     * @param args arguments to use
     */
    abstract void command(PCB pcb, byte... args);

    private ArgumentTypes[][] getArgTypes() {
        return argTypes;
    }

}

