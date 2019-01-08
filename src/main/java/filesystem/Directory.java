package filesystem;

import shell.Shell;
import utils.Utils;

import java.util.Vector;

public class Directory {
    private String name;
    private Vector<Directory> dirs = new Vector<>();
    private Files files;

    Directory(String name) {
        files = new Files();
        this.name = name;
    }

    Directory(Directory newDir){
        this.name = newDir.name;
        this.dirs = newDir.dirs;
        this.files = newDir.files;
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

    public boolean directoryExists(String name) {
        for (Directory e : dirs) {
            if (e.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void addDirectory(String name){
        if(directoryExists(name)){
            Shell.println("Directory " + name + " already exists");
            Utils.log("Directory " + name + " already exists");
            return;
        }
        dirs.add(new Directory(name));

    }

    public Files getFiles() {
        return files;
    }

    public void removeDir(String name) {
        for(Directory e:dirs){
            if(e.getName().equals(name)){
                dirs.remove(e);
                return;
            }
        }
        Shell.println("No directory named: " + name);
    }

    public void tree(int level){
        String temp = "";
        //if(level==0) { temp = " "; }
        for (int i = 0; i < level; i++) {
            temp += "\t";
        }
        Shell.println(temp + name + " ");
        for(Directory e:dirs){
            e.tree(level+1);
        }
    }

}
