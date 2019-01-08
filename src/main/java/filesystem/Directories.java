package filesystem;

import shell.Shell;
import java.util.Stack;

public class Directories {

    private static final String ROOT_NAME = "P:";
    private static final String DIR_SEPARATOR_REGEX = "[\\\\/]";

    private static Directory dir = new Directory(ROOT_NAME);
    private static Directory rootDir = dir;
    private static Directory currentDir = dir;
    private static Directory targetDir = dir;
    private static Directory sourceDir = dir;
    private static Stack<Directory> history = new Stack<>();
    private static Stack<Directory> targetHistory = new Stack<>();
    private static Stack<Directory> sourceHistory = new Stack<>();

    public static Directory getDir() {
        return dir;
    }

    public static Directory getCurrentDir() {
        return currentDir;
    }

    public static Directory getTargetDir() {
        return targetDir;
    }

    public static Directory getSourceDir() {
        return sourceDir;
    }

    public static Directory findDirectory(String name, String src) {
        switch(src){
            case "current":
                for (Directory e : currentDir.getDirectories()) {
                    if (e.getName().equals(name)) {
                        return e;
                    }
                }
                break;
            case "target":
                for (Directory e : targetDir.getDirectories()) {
                    if (e.getName().equals(name)) {
                        return e;
                    }
                }
                break;
            case "source":
                for (Directory e : sourceDir.getDirectories()) {
                    if (e.getName().equals(name)) {
                        return e;
                    }
                }
                break;
        }
        return null;
    }

    public static String path(String filePath) {
        targetHistory = (Stack<Directory>) history.clone();
        targetDir = new Directory(currentDir);
        String[] path = filePath.split(DIR_SEPARATOR_REGEX);
        for (String e : path) {
            if (e == path[path.length - 1]) {
                break;
            }

            Directories.setTargetDir(e);
        }
        return path[path.length - 1];
    }

    public static String[] path(String targetPath, String sourcePath) {
        targetHistory = (Stack<Directory>) history.clone();
        targetDir = new Directory(currentDir);
        sourceHistory = (Stack<Directory>) history.clone();
        sourceDir = new Directory(currentDir);

        String[] tPath = targetPath.split(DIR_SEPARATOR_REGEX);
        String[] sPath = sourcePath.split(DIR_SEPARATOR_REGEX);
        for (String e : tPath) {
            if (e == tPath[tPath.length - 1]) {
                break;
            }
            Directories.setTargetDir(e);
        }
        for (String e : sPath) {
            if (e == sPath[sPath.length - 1]) {
                break;
            }
            Directories.setSourceDir(e);
        }
        String pathName[] = new String[2];
        pathName[0] = tPath[tPath.length - 1];
        pathName[1] = sPath[sPath.length - 1];
        return pathName;
    }

    public static String getPath() {
        String history = "";
        for (Directory e: Directories.getHistory()){
            history += e.getName() + "\\";
        }
        String dirName = Directories.getCurrentDir().getName();
        if (dirName.charAt(dirName.length()-1) == ':') dirName += "\\";
        return history + dirName;
    }

    public static void setCurrentDir(String name) {
        if (name.equals("..")) {
            if (history.empty()) {
                Shell.println("Already at top level");
                return;
            }
            currentDir = history.pop();
        } else if (name.equals(ROOT_NAME)){
            history = new Stack<>();
            currentDir = rootDir;

        } else {
            Directory findDir = findDirectory(name, "current");
            if (findDir != null) {
                history.push(currentDir);
                Directories.currentDir = findDir;
            } else {
                Shell.println("No such directory");
            }
        }
    }

    public static void setTargetDir(String name) {
        if (name.equals("..")) {
            if (targetHistory.empty()) {
                Shell.println("Already at top level");
                return;
            }
            targetDir = targetHistory.pop();
        } else if (name.equals(ROOT_NAME)){
            targetHistory = new Stack<>();
            targetDir = rootDir;

        } else {
            Directory findDir = findDirectory(name, "target");
            if (findDir != null) {
                targetHistory.push(targetDir);
                Directories.targetDir = findDir;
            } else {
                Shell.println("No such target directory");
            }
        }
    }

    public static void setSourceDir(String name) {
        if (name.equals("..")) {
            if (sourceHistory.empty()) {
                Shell.println("Already at top level");
                return;
            }
            sourceDir = sourceHistory.pop();
        } else if(name.equals(ROOT_NAME)){
            sourceHistory = new Stack<>();
            sourceDir = rootDir;
        } else {
            Directory findDir = findDirectory(name, "source");
            if (findDir != null) {
                sourceHistory.push(sourceDir);
                Directories.sourceDir = findDir;
            } else {
                Shell.println("No such source directory");
            }
        }
    }

    public static void setDir(Directory dir) {
        Directories.dir = dir;
    }

    public static Stack<Directory> getHistory() {
        return history;
    }
}
