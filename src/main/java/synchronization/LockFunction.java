package synchronization;
import processess.PCB;


import java.util.LinkedList;
import java.util.List;

import processess.ProcessState;

public class LockFunction {
    //initilize locked which is open
    private boolean locked;
   // PCB pcb = new PCB();
    private List<processess.PCB> kolejka = new LinkedList<processess.PCB>();
    //initialize queue FIFO;

    String name;
    Object fileName;

    //it can be usfull we will see(never enough variable :D )
    LockFunction(Object fileName) {
        this.fileName = fileName;
        this.locked = false;
    }

    void lock(PCB proces) {
        if (locked == true) {
            proces.setState(ProcessState.WAITING);
            System.out.println("Ktos juz uzywa ten plik. " + name + "musi czekac");
            kolejka.add(proces);
        } else {
            System.out.println("LOCK zostal zablokowany " + name);
            locked = true;
            name = name;
            //this function take the lock
            //procces is been stopped
        }
    }

    void unlock(boolean lockers) { // TODO co to jest lockers?
        System.out.println(name + " Zakonczyl swoje zadanie");
        this.locked = false;
        if (kolejka.size() != 0) {
            System.out.println("Kolejka locked jest pusta");
            //Procces which is in quer will be start
        } else {
            PCB process = kolejka.get(0);
            System.out.println("Process" + process.name + "rozpoaczac swoja akcje");
        }
        //reclaim the locked
        //procces is been activate from the stopped
    }




     void signal()
     {
         if(!kolejka.isEmpty())
         {
            PCB pcb=kolejka.get(0);
            pcb.setState(ProcessState.READY);
            kolejka.remove(0);
         }
        else
         {
            locked =true;
         }

     }

}

