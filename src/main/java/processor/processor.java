package processor;

import processess.PCB;
import processess.PCBList;
import processess.ProcessState;
import processor.data_structures.Multilevel_queue;
import utils.Utils;

//import javax.rmi.CORBA.Util;
import java.util.ArrayList;

public class processor
{
    private Multilevel_queue<PCB> ReadyProcessQueue = new Multilevel_queue<>(18);

    private ArrayList<Boolean> ReadySummary = new ArrayList<>(18);

    //<Mateusz>
    //private PCB RunningProcess;
    private PCB RunningProcess = null;
    //</Mateusz>

    public PCB getRunningProcess() /** *returns currently running process */
    {
        return this.RunningProcess;
    }

    public processor() /** *nothing but constructor */
    {
        for (Boolean e: ReadySummary)
        {
            e=false;
        }
    }

    private void FinishRunning() /** *finishes running process executing after 3 orders */
    {
        //  TO>Artur|Jakub< proces który był używany, nie miał zmienionego stanu na running i
        //       wciąż znajdował się w kolejce ready, zmieniłem to tak aby usuwało z kolejki ten proces
        //       TODO: sprawdzić czy nic nie popsułem przy tym


        //<Mateusz>
        /*
        RunningProcess = ReadyProcessQueue.poll();

        if(ReadyProcessQueue.isEmpty(RunningProcess.getDynamicPriority()))
        {
            ReadySummary.set(RunningProcess.getDynamicPriority(), false);
        }

        if(RunningProcess.getExecutedOrders() >= 2)
        {
            RunningProcess.setExetucedOrders(0);
            RunningProcess.setDynamicPriority(RunningProcess.getBasePriority());
        }
        //TODO: change state
        ReadyProcessQueue.add(RunningProcess, RunningProcess.getDynamicPriority());

        ReadySummary.set(RunningProcess.getDynamicPriority(), true);

        Utils.log("Process: " + RunningProcess.getName() + "finished running, priority decreased to" + RunningProcess.getDynamicPriority());
*/
        //----------------------------------------------

        if (RunningProcess == null){
            return;
        }
       /* else if (RunningProcess.getProcessState() == ProcessState.TERMINATED){

            if(ReadyProcessQueue.isEmpty(RunningProcess.getDynamicPriority()))
            {
                ReadySummary.set(RunningProcess.getDynamicPriority(), false);
            }


        } */

        else {
            if(ReadyProcessQueue.isEmpty(RunningProcess.getDynamicPriority()))
            {
                ReadySummary.set(RunningProcess.getDynamicPriority(), false);
            }

            if(RunningProcess.getExecutedOrders() >= 2)
            {
                RunningProcess.setExetucedOrders(0);
                RunningProcess.setBasePriority();
                //RunningProcess.setDynamicPriority(RunningProcess.getBasePriority());
            }

            RunningProcess.setState(ProcessState.READY);
            ReadyProcessQueue.add(RunningProcess, RunningProcess.getDynamicPriority());

            ReadySummary.set(RunningProcess.getDynamicPriority(), true);

            Utils.log("Process: " + RunningProcess.getName() + "finished running, priority decreased to" + RunningProcess.getDynamicPriority());



        }

    }

    private void FindReadyProcess() /** *finds the first process with the highest priority */
    {
        FinishRunning();

        System.out.println("Running process: ");
        RunningProcess = ReadyProcessQueue.poll();
        System.out.println(RunningProcess.getName());
        RunningProcess.setReadyTime(0);
        RunningProcess.setState(ProcessState.RUNNING); //[Mateusz]

        Utils.log("New ready process found: " + RunningProcess.getName() + ". Priority: " + RunningProcess.getDynamicPriority());
    }



    public boolean RemoveProcess(PCB process) /** *removes process from queue */
    {

        //  TO>Artur|Jakub< proces który był używany, nie miał zmienionego stanu na running i
        //       wciąż znajdował się w kolejce ready, zmieniłem to tak aby usuwało z kolejki ten proces
        //       TODO: sprawdzić czy nic nie popsułem przy tym
        //       TODO: zobaczyc czy wciąż nie da się zrobić kontroli błędów (chodzi o tamto return true)


        //<Mateusz>


       /*  boolean result = ReadyProcessQueue.remove(process);
        if(result)
        {
            if(ReadyProcessQueue.isEmpty(process.getDynamicPriority()))
            {
                ReadySummary.set(process.getDynamicPriority(), false);
            }

            Utils.log("Process removed: " + process.getName());

            if (RunningProcess==process)
            {
                this.FindReadyProcess();
            }
        }

        else
        {
            Utils.log("Process: " + process.getName() + "was not removed. Not in queue.");
        }

        return result;
        */

        //------------------------------------------------



            if(ReadyProcessQueue.isEmpty(process.getDynamicPriority()))
            {
                ReadySummary.set(process.getDynamicPriority(), false);
            }

            Utils.log("Process removed: " + process.getName());


            //TO>Artur|Jakub< czy tu jest możliwość że RunningProcess nie będzie procesem?
            if (RunningProcess==process)
            {
                this.FindReadyProcess();
            }

            process.deleteProcess();


        return true;



        //</Mateusz>
    }

    public void AddReadyProcess(PCB process, boolean modifier) /** *adds process to the queue */
    {
        if(modifier)
        {
            process.setDynamicPriority(process.getDynamicPriority()+3);
            Utils.log("Process: " + process.getName() + "priority increased to: " + process.getDynamicPriority());
        }

        //TODO: Debug
        Utils.log("Adding process: " + process.getName());

        int priority = process.getDynamicPriority();
        ReadyProcessQueue.add(process, priority);
        ReadySummary.set(priority, true);

        Utils.log("Process added: " + process.getName());

        if(priority > RunningProcess.getDynamicPriority())
        {
            this.FindReadyProcess();
        }
    }

    private void BalanceSetManager() /** *finds if any process was waiting too long and sets higher priority */
    {
        Utils.log("Balance Set Manager started");

        PCB process = null;
        for(int i=1; i<15; i++)
        {
            if(ReadySummary.get(i)==true)
            {
                for(int j=0; j < ReadyProcessQueue.size(i); j++) {
                    process = ReadyProcessQueue.poll(i);

                    if (ReadyProcessQueue.isEmpty(process.getDynamicPriority())) {
                        ReadySummary.set(process.getDynamicPriority(), false);
                    }

                    process.setReadyTime(process.getReadyTime() + 3);

                    if (process.getReadyTime() >= 15) {
                        process.setDynamicPriority(15);
                        Utils.log("Process: " + process.getName() + "has been waiting for: " + process.getReadyTime() + " orders. Priority increased to: " + process.getDynamicPriority());
                    }

                    ReadyProcessQueue.add(process, process.getDynamicPriority());
                    ReadySummary.set(process.getDynamicPriority(), true);
                }

            }
        }

        Utils.log("Balance Set Manager finished");

    }

    public void run() /** *executes process with the highest priority */
    {
        FindReadyProcess();

        System.out.println("Executing process: " + RunningProcess.getName()); //[Mateusz]

        while(RunningProcess.getExecutedOrders() < 3)
        {
            for(int i=0; i<3; i++)
            {
                RunningProcess.setExetucedOrders(RunningProcess.getExecutedOrders()+1);
                boolean result=RunningProcess.execute();

                Utils.log("Process: " + RunningProcess.getName() + "executed with result: " + result + ". PC: " + RunningProcess.getPC());

                if (result == false)
                {
                    RemoveProcess(RunningProcess);
                }


            }
            BalanceSetManager();
        }
    }

    public void run_by_step() /** *work-by-step run method*/
    {
        FindReadyProcess();

        System.out.println("Executing process: " + RunningProcess.getName()); //[Mateusz]
        while(RunningProcess.getExecutedOrders() < 3)
        {
            for(int i=0; i<3; i++)
            {
                RunningProcess.setExetucedOrders(RunningProcess.getExecutedOrders()+1);
                boolean result=RunningProcess.execute();

                Utils.step("Process: " + RunningProcess.getName() + "executed with result: " + result + ". PC: " + RunningProcess.getPC());

                if (result == false)
                {
                    RemoveProcess(RunningProcess);
                }
            }

            BalanceSetManager();
        }
    }

    public void displayQueue() /** *displays not empty queues */
    {
        PCB process = null;

        for(int i=0; i<18; i++)
        {
            if(ReadySummary.get(i)==true)
            {
                Utils.log("Priority: " + i);
                for(int j=0; j<ReadyProcessQueue.size(i); j++)
                {
                    process = ReadyProcessQueue.poll(i);
                    Utils.log(process.getName() + "\t" + process.getDynamicPriority());
                    ReadyProcessQueue.add(process, process.getDynamicPriority());
                }
            }
        }

    }

    public void displayQueue(int priority) /** *displays specified queue */
    {
        PCB process = null;

        Utils.log("Priority: " + priority);

        if(ReadySummary.get(priority)==true)
        {
            for(int j=0; j<ReadyProcessQueue.size(priority); j++)
            {
                process = ReadyProcessQueue.poll(priority);
                Utils.log(process.getName() + "\t" + process.getDynamicPriority());
                ReadyProcessQueue.add(process, process.getDynamicPriority());
            }
        }

        else
        {
            Utils.log("Queue is empty");
        }
    }
}
