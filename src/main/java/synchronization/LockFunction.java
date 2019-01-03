package synchronization;
import processess.PCB;
import utils.Utils;
import processor.Processor;
import java.util.LinkedList;
import java.util.List;
import processess.ProcessState;

import javax.print.DocFlavor;

public class LockFunction {
    //initilize locked which is open
    private boolean locked;
   // kolejka uzywana do lock
    private List<processess.PCB> kolejka = new LinkedList<>();
    //kolejka uzywana do zmiennych warunkowyc
    private List<processess.PCB> Condition=new LinkedList<>();

    LockFunction()
    {
        locked=false;
    }
    //it can be usfull we will see(never enough variable :D )

    // @param function lock block the locked to security the data

    void lock(PCB proces) {
        if (locked) {
            proces.setState(ProcessState.WAITING);
            Utils.log("Lock has been already taken."+proces.name+" has to wait");
            kolejka.add(proces);
        } else {
            //if()//something which unable to use)
            //{
            //    wait(proces);
            //}
            //jesli cos nie dziala to wait;
            Utils.log(proces.name+" has taken the lock");
            locked = true;
            Utils.log(proces.name+" has started executing");
            //this function take the lock
            //procces is been stopped
        }
    }

    // @param function wait stop the process which has to wait and the move it to the queed
    void wait(PCB proces)
    {
            proces.setState(ProcessState.WAITING);
            Condition.add(proces);
            locked=false;
    }

    //@param function unlock, unlocking the locked and check if is another process waiting
    public void unlock() {
        Utils.log("The process has relased the lock");

        if (kolejka.size() == 0) {
            System.out.println("Kolejka locked jest pusta");
            this.locked = false;
        } else {
            PCB process = kolejka.get(0);
            System.out.println("Process" + process.name + "rozpoaczac swoja akcje");
            process.setState(ProcessState.READY);
          //  processor.AddReadyProcess(process,0);
        }
        //reclaim the locked
        //procces is been activate from the stopped
    }

//@param get the signal from the previousl procces to execute the other

     void signal()
     {
         if(!Condition.isEmpty())
         {
            PCB pcb=Condition.get(0);
            pcb.setState(ProcessState.READY);
          //cos nie dziala
            // processor.AddReadyProcess(pcb,0);
            Condition.remove(0);
            locked=false;
         }
        else
         {
            locked =false;
         }
     }



}



