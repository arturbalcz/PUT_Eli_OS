package filesystem;

import shell.Shell;
import utils.Utils;

import java.util.Arrays;
import java.util.Vector;

public class Files {

    private Vector<File> allFiles = new Vector<>();

    public boolean fileExists(String name) {
        for (File e : allFiles) {
            if (e.getName().equals(name)) {
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
    public void createFile(String name, byte[] content) {
        if (fileExists(name)) {
            Utils.log("File " + name + " already exists");
            Shell.println("File " + name + " already exists");
            return;
        }
        if (name.equals("")) {
            Utils.log("No file name given");
            Shell.println("No file name given");
            return;
        }
        File newF = new File(name);
        newF.setIndexBlock(Disk.addContent(content, 10));
        newF.setSize(content.length);
        allFiles.add(newF);
        Utils.log("New file: " + Directories.getCurrentDir().getName() + "\\" + name);
    }

    /***
     * Gets the file content by file name
     *
     * @param name      from which file to get data
     * @return byte[] of corresponding files data
     */
    public byte[] getFile(String name) {
        Utils.log("Getting file: " + name);
        for (File e : allFiles) {
            if (e.getName().equals(name)) {
                return Disk.getBlockByIndex(e.getIndexBlock());
            }
        }
        Utils.log("No such file");
        Shell.println("No such file");
        return Disk.invalid();
    }

    public byte[] getFileClean(final String name) {
        final byte[] rawExecFile = this.getFile(name);
        int last =  rawExecFile.length - 1;
        while (rawExecFile[last] == Disk.EMPTY_CELL) last--;
        return Arrays.copyOfRange(rawExecFile, 0, last+1);
    }

    /***
     *
     *
     */
    public void showFiles(){
        for(File e: allFiles){
            Shell.println(e.getSize() + "\t" + e.getName() );
        }
    }


    public void deleteFile(String name){
        for (File e : allFiles){
            if (e.getName().equals(name)){
                Disk.remove(e.getIndexBlock());
                allFiles.remove(e);
                Utils.log("removed file: " + Directories.getCurrentDir().getName() + "\\" + name);
                return;
            }
        }
        Shell.println("No file named: " + name);
    }

}
