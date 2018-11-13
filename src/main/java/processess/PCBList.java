package processess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PCBList {
    private List<PCB> data;
    private int pid = 1000;

    public PCBList() {
        data = new ArrayList<>();
    }

    //TODO better generator
    private int pidGen(){
        pid++;
        return pid;
    }
    public void newProcess(String name, int priority){
        data.add(new PCB(pidGen(), name, priority));
        //dać znać aby przenieść z pamięci do ram
    }

    public void terminateProcess(String name){
        Iterator itr = data.iterator();
        while (itr.hasNext()){
            PCB temp = (PCB) itr.next();
            if (temp.getName().equals(name)){
                itr.remove();
            }
        }
    }

    public List<PCB> getData() {
        return data;
    }

    public void print(){
        for (PCB e: data){
            System.out.println("Process " + e.getPID() + " (Name: " + e.getName() + ", Priority: " + e.getDynamicPriority() + ")" );
        }
    }

}
