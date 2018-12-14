package filesystem;

import shell.Shell;

import java.util.Arrays;
import java.util.Vector;

public class Files {

    private static Vector<File> allFiles = new Vector<>();

    private static boolean fileExists(String name) {
        for (File e : allFiles) {
            if (e.getName().equals(name)) {
                Shell.println("Name taken");
                return true;
            }
        }
        return false;
    }


    /***
     * Creates a file with specified name and content
     *
     * @param name      name to be given to the file
     * @param content   content to be written
     */
    public static void createFile(String name, byte[] content) {
        if (fileExists(name)) {
            Shell.println("File already exists");
            return;
        }
        if (name.equals("")) {
            Shell.println("No file name given");
            return;
        }
        File newF = new File(name);
        newF.setIndexBlock(Disk.addContent(content, 10));
        newF.setSize(content.length);
        allFiles.add(newF);

    }

    /***
     * Gets the file content by file name
     *
     * @param name      from which file to get data
     * @return byte[] of corresponding files data
     */
    public static byte[] getFile(String name) {
        for (File e : allFiles) {
            if (e.getName().equals(name)) {
                return Disk.getBlockByIndex(e.getIndexBlock());
            }
        }
        Shell.println("No such file");
        return Disk.invalid();
    }

    public static byte[] getFileClean(final String name) {
        final byte[] rawExecFile = Files.getFile(name);
        int last =  rawExecFile.length - 1;
        while (rawExecFile[last] == Disk.EMPTY_CELL) last--;
        return Arrays.copyOfRange(rawExecFile, 0, last+1);
    }

    /***
     *
     *
     */
    public static void showFiles(){
        for(File e: allFiles){
            Shell.println(e.getSize() + "\t" + e.getName() );
        }
    }


    public static void deleteFile(String name){
        for (File e : allFiles){
            if (e.getName().equals(name)){
                Disk.remove(e.getIndexBlock());
                allFiles.remove(e);
                break;
            }
        }
    }

}
