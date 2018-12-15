package filesystem;

import shell.Shell;

public class Directories {

    private static Directory dir = new Directory("root");
    private static Directory currentDir = dir;

    public static Directory getDir() {
        return dir;
    }

    public static Directory getCurrentDir() {
        return currentDir;
    }

    private static Directory findDirectory(String name){
        for(Directory e:currentDir.getDirectories()){
            Shell.println("---" + e.getName());
            if(e.getName().equals(name)){
                return e;
            }
        }
        return null;
    }

    public static void setCurrentDir(String name){
        Directory findDir = findDirectory(name);
        if(findDir != null) {
            Directories.currentDir = findDir;
        }
        else{
            Shell.println("No such directory");
        }
    }

    public static void setDir(Directory dir) {
        Directories.dir = dir;
        //Directories.currentDir = dir;
    }
}
