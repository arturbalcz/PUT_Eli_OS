package processor;

import processess.PCB;
import processess.ProcessState;
import processor.data_structures.MultilevelQueue;
import utils.Utils;

//import javax.rmi.CORBA.Util;
import java.util.ArrayList;

public class Processor
{
    private MultilevelQueue<PCB> readyProcessQueue = new MultilevelQueue<>(18);

    private ArrayList<Boolean> readySummary = new ArrayList<>(18);

    //<Mateusz>
    //private PCB runningProcess;
    private PCB runningProcess = null;
    //</Mateusz>

    /** returns currently running process */
    public PCB getRunningProcess()
    {
        return this.runningProcess;
    }

    /** nothing but constructor */
    public Processor()
    {
        for (Boolean e: readySummary)
        {
            e=false;
        }
    }

    /** finishes running process executing after 3 orders */
    private void finishRunning()
    {
        //  TO>Artur|Jakub< proces który był używany, nie miał zmienionego stanu na running i
        //       wciąż znajdował się w kolejce ready, zmieniłem to tak aby usuwało z kolejki ten proces
        //       TODO: sprawdzić czy nic nie popsułem przy tym


        //<Mateusz>
        /*
        runningProcess = readyProcessQueue.poll();

        if(readyProcessQueue.isEmpty(runningProcess.getDynamicPriority()))
        {
            readySummary.set(runningProcess.getDynamicPriority(), false);
        }

        if(runningProcess.getExecutedOrders() >= 2)
        {
            runningProcess.setExetucedOrders(0);
            runningProcess.setDynamicPriority(runningProcess.getBasePriority());
        }
        //TODO: change state
        readyProcessQueue.add(runningProcess, runningProcess.getDynamicPriority());

        readySummary.set(runningProcess.getDynamicPriority(), true);

        Utils.log("Process: " + runningProcess.getName() + "finished running, priority decreased to" + runningProcess.getDynamicPriority());
*/
        //----------------------------------------------

        if (runningProcess == null){
            return;
        }
       /* else if (runningProcess.getProcessState() == ProcessState.TERMINATED){

            if(readyProcessQueue.isEmpty(runningProcess.getDynamicPriority()))
            {
                readySummary.set(runningProcess.getDynamicPriority(), false);
            }


        } */

        else {
            if(readyProcessQueue.isEmpty(runningProcess.getDynamicPriority()))
            {
                readySummary.set(runningProcess.getDynamicPriority(), false);
            }

            if(runningProcess.getExecutedOrders() >= 2)
            {
                runningProcess.setExetucedOrders(0);
                runningProcess.setBasePriority();
                //runningProcess.setDynamicPriority(runningProcess.getBasePriority());
            }

            runningProcess.setState(ProcessState.READY);
            readyProcessQueue.add(runningProcess, runningProcess.getDynamicPriority());

            readySummary.set(runningProcess.getDynamicPriority(), true);

            Utils.log("Process: " + runningProcess.getName() + "finished running, priority decreased to" + runningProcess.getDynamicPriority());



        }

    }

    /** finds the first process with the highest priority */
    private void findReadyProcess()
    {
        finishRunning();

        System.out.println("Running process: ");
        runningProcess = readyProcessQueue.poll();
        System.out.println(runningProcess.getName());
        runningProcess.setReadyTime(0);
        runningProcess.setState(ProcessState.RUNNING); //[Mateusz]

        Utils.log("New ready process found: " + runningProcess.getName() + ". Priority: " + runningProcess.getDynamicPriority());
    }

    /** removes process from queue */
    public boolean removeProcess(PCB process)
    {

        //  TO>Artur|Jakub< proces który był używany, nie miał zmienionego stanu na running i
        //       wciąż znajdował się w kolejce ready, zmieniłem to tak aby usuwało z kolejki ten proces
        //       TODO: sprawdzić czy nic nie popsułem przy tym
        //       TODO: zobaczyc czy wciąż nie da się zrobić kontroli błędów (chodzi o tamto return true)


        //<Mateusz>


       /*  boolean result = readyProcessQueue.remove(process);
        if(result)
        {
            if(readyProcessQueue.isEmpty(process.getDynamicPriority()))
            {
                readySummary.set(process.getDynamicPriority(), false);
            }

            Utils.log("Process removed: " + process.getName());

            if (runningProcess==process)
            {
                this.findReadyProcess();
            }
        }

        else
        {
            Utils.log("Process: " + process.getName() + "was not removed. Not in queue.");
        }

        return result;
        */

        //------------------------------------------------



            if(readyProcessQueue.isEmpty(process.getDynamicPriority()))
            {
                readySummary.set(process.getDynamicPriority(), false);
            }

            Utils.log("Process removed: " + process.getName());


            //TO>Artur|Jakub< czy tu jest możliwość że runningProcess nie będzie procesem?
            if (runningProcess ==process)
            {
                this.findReadyProcess();
            }

            process.deleteProcess();


        return true;



        //</Mateusz>
    }

    /** adds process to the queue */
    public void addReadyProcess(PCB process, boolean modifier)
    {
        if(modifier)
        {
            process.setDynamicPriority(process.getDynamicPriority()+3);
            Utils.log("Process: " + process.getName() + "priority increased to: " + process.getDynamicPriority());
        }

        //TODO: Debug
        Utils.log("Adding process: " + process.getName());

        int priority = process.getDynamicPriority();
        readyProcessQueue.add(process, priority);
        readySummary.set(priority, true);

        Utils.log("Process added: " + process.getName());

        if(priority > runningProcess.getDynamicPriority())
        {
            this.findReadyProcess();
        }
    }

    /** finds if any process was waiting too long and sets higher priority */
    private void balanceSetManager()
    {
        Utils.log("Balance Set Manager started");

        PCB process = null;
        for(int i=1; i<15; i++)
        {
            if(readySummary.get(i)==true)
            {
                for(int j = 0; j < readyProcessQueue.size(i); j++) {
                    process = readyProcessQueue.poll(i);

                    if (readyProcessQueue.isEmpty(process.getDynamicPriority())) {
                        readySummary.set(process.getDynamicPriority(), false);
                    }

                    process.setReadyTime(process.getReadyTime() + 3);

                    if (process.getReadyTime() >= 15) {
                        process.setDynamicPriority(15);
                        Utils.log("Process: " + process.getName() + "has been waiting for: " + process.getReadyTime() + " orders. Priority increased to: " + process.getDynamicPriority());
                    }

                    readyProcessQueue.add(process, process.getDynamicPriority());
                    readySummary.set(process.getDynamicPriority(), true);
                }

            }
        }

        Utils.log("Balance Set Manager finished");

    }

    /** executes process with the highest priority */
    public void run()
    {
        findReadyProcess();

        System.out.println("Executing process: " + runningProcess.getName()); //[Mateusz]

        while(runningProcess.getExecutedOrders() < 3)
        {
            for(int i=0; i<3; i++)
            {
                runningProcess.setExetucedOrders(runningProcess.getExecutedOrders()+1);
                boolean result= runningProcess.execute();

                Utils.log("Process: " + runningProcess.getName() + "executed with result: " + result + ". PC: " + runningProcess.getPC());

                if (result == false)
                {
                    removeProcess(runningProcess);
                }


            }
            balanceSetManager();
        }
    }

    /** work-by-step run method*/
    public void run_by_step()
    {
        findReadyProcess();

        System.out.println("Executing process: " + runningProcess.getName()); //[Mateusz]
        while(runningProcess.getExecutedOrders() < 3)
        {
            for(int i=0; i<3; i++)
            {
                runningProcess.setExetucedOrders(runningProcess.getExecutedOrders()+1);
                boolean result= runningProcess.execute();

                Utils.step("Process: " + runningProcess.getName() + "executed with result: " + result + ". PC: " + runningProcess.getPC());

                if (result == false)
                {
                    removeProcess(runningProcess);
                }
            }

            balanceSetManager();
        }
    }

    /** displays not empty queues */
    public void displayQueue()
    {
        PCB process = null;

        for(int i=0; i<18; i++)
        {
            if(readySummary.get(i)==true)
            {
                Utils.log("Priority: " + i);
                for(int j = 0; j< readyProcessQueue.size(i); j++)
                {
                    process = readyProcessQueue.poll(i);
                    Utils.log(process.getName() + "\t" + process.getDynamicPriority());
                    readyProcessQueue.add(process, process.getDynamicPriority());
                }
            }
        }

    }

    /** displays specified queue */
    public void displayQueue(int priority)
    {
        PCB process = null;

        Utils.log("Priority: " + priority);

        if(readySummary.get(priority)==true)
        {
            for(int j = 0; j< readyProcessQueue.size(priority); j++)
            {
                process = readyProcessQueue.poll(priority);
                Utils.log(process.getName() + "\t" + process.getDynamicPriority());
                readyProcessQueue.add(process, process.getDynamicPriority());
            }
        }

        else
        {
            Utils.log("Queue is empty");
        }
    }
}
