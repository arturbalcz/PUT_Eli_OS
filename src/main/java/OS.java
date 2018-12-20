import assembler.Assembler;
import filesystem.Directories;
import filesystem.Files;
import shell.Shell;
import utils.Utils;

/**
 * Collects together all modules and run system
 */
class OS {

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
     * Creates files with initial program codes and executables
     */
    private static void createInitialFiles() {
        Utils.log("creating initial programs");
        createAndCompile(FACTORIAL_16_ASM, "f16");
    }

    /**
     * Creates file with given code and second with its executable
     * @param codeText assembler code
     * @param fileName name of file to create
     */
    private static void createAndCompile(final String codeText, final String fileName) {
        final byte[] code = codeText.getBytes();
        Directories.getCurrentDir().getFiles().createFile(fileName + ".asm", code);

        Assembler assembler = new Assembler();
        final byte[] exec = assembler.compile(code);
        Directories.getCurrentDir().getFiles().createFile(fileName + ".exe", exec);
    }

    OS() {
        createInitialFiles();
    }

    /**
     * starting the system
     */
    void run() {
        Utils.log("system started");
        Shell.println("    ____  __  ________   _________ __           ____  _____\n" +
                "   / __ \\/ / / /_  __/  / ____/ (_) /_____ _   / __ \\/ ___/\n" +
                "  / /_/ / / / / / /    / __/ / / / __/ __ `/  / / / /\\__ \\ \n" +
                " / ____/ /_/ / / /    / /___/ / / /_/ /_/ /  / /_/ /___/ / \n" +
                "/_/    \\____/ /_/    /_____/_/_/\\__/\\__,_/   \\____//____/  \n" +
                "                                                           ");
        boolean closing = false;
        while(!closing) {
            closing = Shell.interpret();
        }
    }

}
