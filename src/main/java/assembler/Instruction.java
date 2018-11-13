package assembler;

import processess.PCB;
import utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

abstract class Instruction {

    private static final HashMap<String, Byte> codes = new HashMap<>();
    private static final HashMap<Byte, Instruction> instructions = createInstructions();

    private static HashMap<Byte, Instruction> createInstructions() {
        Utils.log("creating instructions");
        
        HashMap<Byte, Instruction> c = new HashMap<>();
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
        c.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "ADD",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.add(args[0], args[1], args[2], args[3], false);
            }
        };
        c.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "ADC",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.add(args[0], args[1], args[2], args[3], true);
            }
        };
        c.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "INC",
                1,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.inc(args[0],args[1]);
            }
        };
        c.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "SUB",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.sub(args[0], args[1], args[2], args[3]);
            }
        };
        c.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "DEC",
                1,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.dec(args[0], args[1]);
            }
        };
        c.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "MUL",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.mul(args[0], args[1], args[2], args[3]);
            }
        };
        c.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "DIV",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.div(args[0], args[1], args[2], args[3]);
            }
        };
        c.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "MOV",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.mov(args[0], args[1], args[2], args[3]);
            }
        };
        c.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "AND",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.and(args[0], args[1], args[2], args[3]);
            }
        };
        c.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "NAND",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.nand(args[0], args[1], args[2], args[3]);
            }
        };
        c.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "OR",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.or(args[0], args[1], args[2], args[3]);
            }
        };
        c.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "NOR",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.nor(args[0], args[1], args[2], args[3]);
            }
        };
        c.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "XOR",
                2,
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY),
                ArgumentTypes.getTypes(ArgumentTypes.FULL_REGISTRY, ArgumentTypes.REGISTRY, ArgumentTypes.MEMORY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.xor(args[0], args[1], args[2], args[3]);
            }
        };
        c.put(code, instruction);
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
        c.put(code, instruction);
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
        c.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        instruction = new Instruction(
                "PRT",
                1,
                ArgumentTypes.getTypes(ArgumentTypes.TEXT, ArgumentTypes.CHARACTER, ArgumentTypes.REGISTRY, ArgumentTypes.VALUE)
        ) {
            @Override
            void command(PCB pcb, byte... args) {
                Assembler.prt(args[0], Arrays.copyOfRange(args, 1, args.length));
            }
        };
        c.put(code, instruction);
        Instruction.codes.put(instruction.name, code++);

        return c;
    }

    static Instruction getByCode(final byte code) {
        return Instruction.instructions.get(code);
    }

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

    private static List<Byte> getArgumentExecutable(final ArgumentTypes type, final List<String> pieces, int begin, Assembler assembler) {
        List<Byte> executable = new ArrayList<>();
        switch (type) {
            case FULL_REGISTRY:
            case REGISTRY:
                executable.add(CPU.getRegistryId(pieces.get(begin)));
                break;
            case MEMORY:
                System.out.println("MEMORY EXEC NOT IMPLEMENTED");
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
                for(int i = begin; i < pieces.size(); i++)
                    for(int j = 1; j < pieces.get(i).length()-1; j++) executable.add((byte) pieces.get(i).charAt(j));
                executable.add((byte) -1);
                break;
        }
        return executable;
    }

    final String name;
    final int argsNumber;
    private final ArgumentTypes[][] argTypes;

    Instruction(final String name, final int argsNumber, ArgumentTypes[]... argsTypes) {
        this.name = name;
        this.argsNumber = argsNumber;
        this.argTypes = argsTypes;
    }

    void execute(PCB pcb, byte... args) {
        Utils.log("before " + name + ":");
        Assembler.cpu.print();

        command(pcb, args);

        Utils.log("after " + name + ":");
        Assembler.cpu.print(true);
    }

    abstract void command(PCB pcb, byte... args);

    ArgumentTypes[][] getArgTypes() {
        return argTypes;
    }

}

