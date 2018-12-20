package os;

import assembler.Assembler;
import filesystem.Files;
import processess.PCBList;
import shell.Shell;
import utils.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Collects together all modules and run system
 */
public class OS {

    /** code of DUMMY process */
    private static final String DUMMY = "loop: JMP loop";

    private static final String LOGO =  "    ____  __  ________   _________ __           ____  _____\n" +
                                        "   / __ \\/ / / /_  __/  / ____/ (_) /_____ _   / __ \\/ ___/\n" +
                                        "  / /_/ / / / / / /    / __/ / / / __/ __ `/  / / / /\\__ \\ \n" +
                                        " / ____/ /_/ / / /    / /___/ / / /_/ /_/ /  / /_/ /___/ / \n" +
                                        "/_/    \\____/ /_/    /_____/_/_/\\__/\\__,_/   \\____//____/  \n" +
                                        "                                                           ";

    public OS() {
        updateInitialFiles();

        createAndCompile(DUMMY, "dummy", false);
        final byte[] dummyExec = Files.getFile("dummy.exe"); //error, non-static method in static context
        PCBList.list.addDummy(dummyExec);
    }

    /**
     * starting the system
     */
    public void run() {
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

    private static final String PATH_TO_FILES = "src/main/resources/assembler/";
    private static final String[] INITIAL_PROGRAMS = new String[] { "f16", "f8", "cp" };
    private static final String INITIAL_PROGRAMS_DIR = "samples";

    public static void updateInitialFiles() {
        Utils.log("updating initial programs");
        for (final String filename : INITIAL_PROGRAMS) {
            try {
                createAndCompile(getFileContent(filename), filename, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getFileContent(final String filename) throws IOException {
        final BufferedReader reader = new BufferedReader(new FileReader(PATH_TO_FILES + filename + ".asm"));
        final StringBuilder fileBuilder = new StringBuilder();
        while (true) {
            final String currentLine = reader.readLine();
            if (currentLine != null) fileBuilder.append(currentLine).append("\n");
            else break;
        }
        final String file = fileBuilder.toString();
        reader.close();
        return file;
    }

    /**
     * Creates file with given code and second with its executable
     * @param codeText assembler code
     * @param fileName name of file to create
     */
    private static void createAndCompile(final String codeText, final String fileName, final boolean withSource) {
        final byte[] code = codeText.getBytes();
        if (withSource) Files.createFile(fileName + ".asm", code); //error, non-static method in static context

        Assembler assembler = new Assembler();
        final byte[] exec = assembler.compile(code);
        Files.createFile(fileName + ".exe", exec); //error, non-static method in static context
    }

}
