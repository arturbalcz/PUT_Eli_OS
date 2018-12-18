import assembler.Assembler;
import filesystem.Files;
import processess.PCBList;
import shell.Shell;
import utils.Utils;

import java.io.IOException;

/**
 * Collects together all modules and run system
 */
public class OS {

    /** code of DUMMY process */
    private static final String DUMMY = "loop: MOV AX FFH\n" +
                                        "JNZ loop";

    /**
     * Code of program counting 16bit factorial
     */
    private static final String FACTORIAL_16_ASM =  "LET 08H\n" +
                                                    "LET 00H\n" +
                                                    "LET 00H\n" +
                                                    "MOV AL [01]\n" +
                                                    "MOV [03] AX\n" +
                                                    "DEC AL\n" +
                                                    "loop: MOV AH AL\n" +
                                                    "MOV CX [03]\n" +
                                                    "MOV BX [02]\n" +
                                                    "MOV [03] 00H\n" +
                                                    "MOV [02] 00H\n" +
                                                    "mul: ADD [03] CX\n" +
                                                    "ADC [02] BX\n" +
                                                    "DEC AH\n" +
                                                    "JNZ mul\n" +
                                                    "MOV CX [03]\n" +
                                                    "MOV BX [02]\n" +
                                                    "DEC AL\n" +
                                                    "JNZ loop";

    /**
     * Code of program counting 8bit factorial (for debug)
     */
    private static final String FACTORIAL_8_ASM =   "LET 03H\n" +
                                                    "MOV AL [01]\n" +
                                                    "MOV BX AX\n" +
                                                    "DEC AL\n" +
                                                    "loop: MUL BX AL\n" +
                                                    "DEC AL\n" +
                                                    "JNZ loop";

    private static final String CP_ASM = "CP \"f16.exe F16\" 07H\n" +
                                        "CP \"f8.exe F8-1\" 01H\n" +
                                        "CP \"f8.exe F8-2\" 01H\n" +
                                        "CP \"f8.exe F8-3\" 05H\n" +
                                        "CP \"f8.exe F8-4\" 01H\n" +
                                        "PRT \"end\"";

    private static final String LOGO =  "    ____  __  ________   _________ __           ____  _____\n" +
                                        "   / __ \\/ / / /_  __/  / ____/ (_) /_____ _   / __ \\/ ___/\n" +
                                        "  / /_/ / / / / / /    / __/ / / / __/ __ `/  / / / /\\__ \\ \n" +
                                        " / ____/ /_/ / / /    / /___/ / / /_/ /_/ /  / /_/ /___/ / \n" +
                                        "/_/    \\____/ /_/    /_____/_/_/\\__/\\__,_/   \\____//____/  \n" +
                                        "                                                           ";

    OS() {
        createInitialFiles();

        final byte[] dummyExec = Files.getFile("dummy.exe");
        PCBList.list.addDummy(dummyExec);
    }

    /**
     * starting the system
     */
    void run() {
        Utils.log("system started");
        Shell.println(LOGO);
        boolean closing = false;
        while(!closing) {
            Utils.log("os step");
            PCBList.list.processor.run();
            try {
                closing = Shell.interpret();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Creates files with initial program codes and executables
     */
    private static void createInitialFiles() {
        Utils.log("creating initial programs");
        createAndCompile(DUMMY, "dummy", false);
        createAndCompile(FACTORIAL_8_ASM, "f8", true);
        createAndCompile(FACTORIAL_16_ASM, "f16", true);
        createAndCompile(CP_ASM, "cp", true);
    }

    /**
     * Creates file with given code and second with its executable
     * @param codeText assembler code
     * @param fileName name of file to create
     */
    private static void createAndCompile(final String codeText, final String fileName, final boolean withSource) {
        final byte[] code = codeText.getBytes();
        if (withSource) Files.createFile(fileName + ".asm", code);

        Assembler assembler = new Assembler();
        final byte[] exec = assembler.compile(code);
        Files.createFile(fileName + ".exe", exec);
    }

}
