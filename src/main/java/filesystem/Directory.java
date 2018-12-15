package filesystem;

import java.util.Vector;

public class Directory {
    private String name;
    private Vector<Directory> dirs = new Vector<>();
    private Files files;

    Directory(String name) {
        files = new Files();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector<Directory> getDirectories() {
        return dirs;
    }

    public void addDirectory(String name){
        dirs.add(new Directory(name));
    }

    public Files getFiles() {
        return files;
    }

}
