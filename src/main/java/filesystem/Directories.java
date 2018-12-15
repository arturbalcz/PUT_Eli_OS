package filesystem;

import shell.Shell;

import java.util.Stack;

public class Directories {

    private static Directory dir = new Directory("root");
    private static Directory currentDir = dir;
    private static Directory previousDir = dir;
    private static Stack<String> history = new Stack<>();

    public static Directory getDir() {
        return dir;
    }

    public static Directory getCurrentDir() {
        return currentDir;
    }

    private static Directory findDirectory(String name){
        for(Directory e:currentDir.getDirectories()){
            if(e.getName().equals(name)){
                return e;
            }
        }
        return null;
    }

    public static void setCurrentDir(String name){
        if(name.equals("..")){
            if(history.empty()){
                Shell.println("Already at top level");
                return;
            }
            history.pop();
            currentDir = previousDir;
        }else {
            Directory findDir = findDirectory(name);
            if (findDir != null) {
                history.push(currentDir.getName());
                previousDir = currentDir;
                Directories.currentDir = findDir;
            } else {
                Shell.println("No such directory");
            }
        }
    }

    public static void setDir(Directory dir) {
        Directories.dir = dir;
        //Directories.currentDir = dir;
    }

    public static Stack<String> getHistory() {
        return history;
    }
}
