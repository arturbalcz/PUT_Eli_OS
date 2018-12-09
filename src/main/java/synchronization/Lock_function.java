package synchronization;
import processess.PCB;


import java.util.LinkedList;
import java.util.List;
import processess.ProcessState;

public class Lock_function {
    //initilize lock which is open
    private Boolean lock;
    PCB pcb = new PCB();
    private List<PCB> kolejka = new LinkedList<String>();
    //initialize queue FIFO;

    String name;
    Object fileName;

    //it can be usfull we will see(never enough variable :D )
    Lock_function(Object fileName) {
        this.fileName = fileName;
        this.lock = false;
    }

    void lock(PCB proces) {
        if (lock == true) {
            proces.setState(ProcessState.WAITING);
            System.out.println("Ktos juz uzywa ten plik. " + name + "musi czekac");
            kolejka.add(proces);
        } else {
            System.out.println("LOCK zostal zablokowany " + name);
            lock = true;
            name = name;
            //this function take the lock
            //procces is been stopped
        }
    }

    void unlock() {
        System.out.println(name + " Zakonczyl swoje zadanie");
        this.lock = false;
        if (kolejka.size() != 0) {
            System.out.println("Kolejka lock jest pusta");
            //Procces which is in quer will be start
        } else {
            PCB process = kolejka.get(0);
            System.out.println("Process" + process.name + "rozpoaczac swoja akcje");
        }
        //reclaim the lock
        //procces is been activate from the stopped
    }


    



}

