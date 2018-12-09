package processor;

import processess.PCB;
import processor.data_structures.Multilevel_queue;
import java.util.ArrayList;

public class processor
{

    private int executedOrders=0;

    private Multilevel_queue<PCB> ReadyProcessQueue = new Multilevel_queue<>(18);

    private ArrayList<Boolean> ReadySummary = new ArrayList<>(18);

    private PCB RunningProcess;

    public PCB getRunningProcess() //returns currently running process
    {
        return this.RunningProcess;
    }

    public processor() //nothing but constructor
    {
        for (Boolean e: ReadySummary)
        {
            e=false;
        }
    }

    private void FindReadyProcess() //finds the first process with the highest priority
    {
        RunningProcess = ReadyProcessQueue.poll();

        if(ReadyProcessQueue.isEmpty(RunningProcess.getDynamicPriority()))
        {
            ReadySummary.set(RunningProcess.getDynamicPriority(), false);
        }

        RunningProcess.setReadyTime(0);

        RunningProcess.setDynamicPriority(RunningProcess.getBasePriority());

        ReadyProcessQueue.add(RunningProcess, RunningProcess.getDynamicPriority());

        ReadySummary.set(RunningProcess.getDynamicPriority(), true);
    }

    public boolean RemoveProcess(PCB process) //removes process from queue
    {
        boolean result = ReadyProcessQueue.remove(process);
        if(result)
        {
            if(ReadyProcessQueue.isEmpty(process.getDynamicPriority()))
            {
                ReadySummary.set(process.getDynamicPriority(), false);
            }

            if (RunningProcess==process)
            {
                this.FindReadyProcess();
            }
        }

        return result;
    }

    public void AddReadyProcess(PCB process, boolean modifier) //adds process to the queue
    {
        if(modifier)
        {
            process.setDynamicPriority(process.getDynamicPriority()+3);
        }
        int priority = process.getDynamicPriority();
        ReadyProcessQueue.add(process, priority);
        ReadySummary.set(priority, true);

        if(priority > RunningProcess.getDynamicPriority())
        {
            this.FindReadyProcess();
        }
    }

    private void BalanceSetManager() //finds if any process was waiting too long and sets higher priority
    {
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
                    }

                    ReadyProcessQueue.add(process, process.getDynamicPriority());
                    ReadySummary.set(process.getDynamicPriority(), true);
                }

            }
        }

    }

    public void run() //executes process with the highest priority
    {
        int breakpoint = 0;

        FindReadyProcess();

        while(executedOrders<3)
        {
            for(int i=0; i<3; i++)
            {
                executedOrders++;
                if (RunningProcess.execute() == false) {
                    RemoveProcess(RunningProcess);
                    executedOrders=0;
                }
            }

            BalanceSetManager();

            breakpoint++;
            if(breakpoint >= 2)
            {
                break;
            }
        }

        executedOrders=0;
    }

    //TODO: work-by-step
    //TODO: logs
    //TODO: displaying stuff

}
