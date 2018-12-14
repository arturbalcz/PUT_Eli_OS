package filesystem;

import shell.Shell;

import java.util.Vector;

public class Files {

    private static Vector<File> allFiles = new Vector<>();

    private static boolean fileExists(String name) {
        for (File e : allFiles) {
            if (e.getName().equals(name)) {
                Shell.print("Name taken");
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
            Shell.print("File already exists");
            return;
        }
        String temp = name;
        if (name.equals("")) {
            temp = "test.txt";
        }
        File newF = new File(temp);
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
        Shell.print("No such file");
        return Disk.invalid();
    }

    /***
     *
     *
     */
    public static void showFiles(){
        for(File e: allFiles){
            Shell.print(e.getSize() + "\t" + e.getName() );
        }
    }


}
