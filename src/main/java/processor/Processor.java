package processor;

import assembler.Assembler;
import processess.PCB;
import processess.PCBList;
import processess.ProcessState;
import processor.data_structures.MultilevelQueue;
import shell.Shell;
import utils.Utils;

import java.util.ArrayList;

public class Processor
{
    private static final int LEVEL_NUMBER = 18;

    private final MultilevelQueue<PCB> readyProcessQueue = new MultilevelQueue<>(LEVEL_NUMBER);
    private final ArrayList<Boolean> readySummary = new ArrayList<>(LEVEL_NUMBER);
    private PCB runningProcess = null;
    private final PCBList pcbList;

    /** returns currently running process */
    public PCB getRunningProcess()
    {
        return this.runningProcess;
    }

    /** nothing but constructor */
    public Processor(PCBList pcbList)
    {
        this.pcbList = pcbList;

        for (int i = 0; i < LEVEL_NUMBER; i++) readySummary.add(false);
    }

    /** finishes running process executing after 3 orders */
    private void finishRunning()
    {
        if (runningProcess != null) {
            if(readyProcessQueue.isEmpty(runningProcess.getDynamicPriority()))
                readySummary.set(runningProcess.getDynamicPriority(), false);

            runningProcess.setExetucedOrders(0);
            runningProcess.setBasePriority();

            runningProcess.setState(ProcessState.READY);
            readyProcessQueue.add(runningProcess, runningProcess.getDynamicPriority());

            readySummary.set(runningProcess.getDynamicPriority(), true);

            runningProcess.setCPUState(Assembler.getCPUState());

            Utils.log("Process: " + runningProcess.getName() + " finished running, priority decreased to" + runningProcess.getDynamicPriority());
        }

    }

    /** finds the first process with the highest priority */
    private void findReadyProcess()
    {
        runningProcess = readyProcessQueue.poll();
        runningProcess.setReadyTime(0);
        runningProcess.setState(ProcessState.RUNNING); //[Mateusz]
        Assembler.setCPUState(runningProcess.getCpuState());

        Utils.log("New ready process found: " + runningProcess.getName() + ". Priority: " + runningProcess.getDynamicPriority());
    }

    /** removes process from queue */
    private void removeProcess(PCB process)
    {
        readyProcessQueue.remove(process);
        if(readyProcessQueue.isEmpty(process.getDynamicPriority()))
            readySummary.set(process.getDynamicPriority(), false);
        if (runningProcess==process) findReadyProcess();
        pcbList.deleteProcess(runningProcess.getPID());

        Utils.log("Process removed: " + process.getName());
        Shell.println("Process removed: " + process.getName());
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
    public boolean run()
    {
        findReadyProcess();

        Shell.println("Running process: " + runningProcess.getName());
        Utils.log("Running process: " + runningProcess.getName());

        while(runningProcess.getExecutedOrders() < 3)
        {
            runningProcess.setExetucedOrders(runningProcess.getExecutedOrders()+1);
            final boolean end = !runningProcess.execute();
            Utils.log("Process: " + runningProcess.getName() + " executed with result: end=" + end + ", PC=" + runningProcess.getPC());

            if (end) {
                removeProcess(runningProcess);
                break;
            }
        }
        finishRunning();
        balanceSetManager();

        return !runningProcess.name.equals("DUMMY");
    }

    /** work-by-step run method*/
    public void runByStep()
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

    /** logs not empty queues */
    public void logQueue()
    {
        PCB process;
        for(int i=0; i<18; i++)
        {
            if(readySummary.get(i))
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

    /** prints not empty queues */
    public void printQueue()
    {
        PCB process;
        for(int i=0; i<18; i++)
        {
            if(readySummary.get(i))
            {
                Shell.println("Priority: " + i);
                for(int j = 0; j< readyProcessQueue.size(i); j++)
                {
                    process = readyProcessQueue.poll(i);
                    Shell.println(process.getName() + "\t" + process.getDynamicPriority());
                    readyProcessQueue.add(process, process.getDynamicPriority());
                }
            }
        }
    }
}
