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
    private static final int TIME_QUANTUM = 3;

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
            runningProcess.setExetucedOrders(0);
            runningProcess.setBasePriority();

            runningProcess.setState(ProcessState.READY);
            readyProcessQueue.add(runningProcess, runningProcess.getDynamicPriority());

            readySummary.set(runningProcess.getDynamicPriority(), true);

            runningProcess.setCPUState(Assembler.getCPUState());

            Utils.log("Process: " + runningProcess.getSignature() + " finished running");

            findReadyProcess();
        }
    }

    /** finds the first process with the highest priority */
    private void findReadyProcess()
    {
        runningProcess = readyProcessQueue.poll();
        runningProcess.setReadyTime(0);
        runningProcess.setState(ProcessState.RUNNING);
        Assembler.setCPUState(runningProcess.getCpuState());

        Utils.log("New running process: " + runningProcess.getSignature());
    }

    /** removes process from queue */
    public void removeProcess(final PCB process)
    {
        process.setState(ProcessState.TERMINATED);

        readyProcessQueue.remove(process);
        pcbList.deleteProcess(process.getPID());

        if(readyProcessQueue.isEmpty(process.getDynamicPriority()))
            readySummary.set(process.getDynamicPriority(), false);

        Utils.log("Process removed: " + process.getSignature());
        Shell.println("Process removed: " + process.getSignature());
    }

    /** adds process to the queue */
    public void addReadyProcess(PCB process, boolean modifier)
    {
        if(modifier)
        {
            process.setDynamicPriority(process.getDynamicPriority()+3);
            Utils.log("Process: " + process.getSignature() + "priority increased to: " + process.getDynamicPriority());
        }

        int priority = process.getDynamicPriority();
        readyProcessQueue.add(process, priority);
        readySummary.set(priority, true);

        Utils.log("Process added: " + process.toString());
    }

    /** finds if any process was waiting too long and sets higher priority */
    private void balanceSetManager()
    {
        PCB process;
        for(int i=1; i<15; i++)
        {
            if(readySummary.get(i))
            {
                for(int j = 0; j < readyProcessQueue.size(i); j++) {
                    process = readyProcessQueue.poll(i);

                    if (readyProcessQueue.isEmpty(process.getDynamicPriority())) {
                        readySummary.set(process.getDynamicPriority(), false);
                    }

                    process.setReadyTime(process.getReadyTime() + TIME_QUANTUM);

                    if (process.getReadyTime() >= 15) {
                        process.setDynamicPriority(process.getDynamicPriority() + 1);
                        Utils.log("Process: " + process.getSignature() + " has been waiting for: " + process.getReadyTime() + ". Priority increased to: " + process.getDynamicPriority());
                        process.setReadyTime(0);
                    }

                    readyProcessQueue.add(process, process.getDynamicPriority());
                    readySummary.set(process.getDynamicPriority(), true);
                }

            }
        }
    }

    /** executes process with the highest priority */
    public void run()
    {
        if (runningProcess == null || runningProcess.getProcessState() != ProcessState.RUNNING) findReadyProcess();
        Utils.log("Executing process: " + runningProcess.getName() + " id: " + runningProcess.getPID());

        runningProcess.setExetucedOrders(runningProcess.getExecutedOrders()+1);
        final boolean end = !runningProcess.execute();
        Utils.log("Process: " + runningProcess.getName() + " executed with result: end=" + end + ", PC=" + runningProcess.getPC());

        if (end) removeProcess(runningProcess);
        else if (runningProcess.getExecutedOrders() >= TIME_QUANTUM && pcbList.getData().size() > 1) finishRunning();

        balanceSetManager();
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
                    Utils.log(process.getSignature() + "\t" + process.getDynamicPriority());
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
                    Shell.println(process.toString());
                    readyProcessQueue.add(process, process.getDynamicPriority());
                }
            }
        }
    }
}
