package virtualmemory;

import java.util.*;

import memory.Memory;
import shell.Shell;
import utils.Utils;

public class virtualmemory
{
    static Memory Ram;
    public virtualmemory(Memory ram)
    {
        this.Ram = ram;
        setRamStatus();
    }

    class Process
    {
        int processId;
        Vector<Byte> code;
    }
    public void createProcess(int pid, Vector<Byte> exec)
    {
        Process p = new Process();
        p.processId = pid;
        p.code = exec;
        getProcess(p);
    }
    /**Method that was given by PCB*/
    void getProcess(Process p)
    {
        processProcessing(p);
        putPageInRam(p.processId, 0);
        Utils.log("Got Process " + p.processId);
    }
    /**Method used by RAM to get a specific page*/
    public static int getFrame(int processID, int page)
    {
        if (!PageTables.get(processID).get(page).valid)
        {
            demandPage(processID, page);
        }
        return PageTables.get(processID).get(page).frame;
    }
    /**When page is missing in RAM, this method is used.*/
    static void demandPage(int ProcessID, int PageID)
    {
        putPageInRam(ProcessID, PageID);
    }
   /**Method that removes process from all containers when it's done.*/
    public void removeProcess(int pID)
    {
        Utils.log("Removing Process " + pID);
        Queue<Integer> tmpQueue = victimQueue, beginQ = new LinkedList<>();
        int frame, Qsize;
        for(int i = 0; i<16; i++)
        {
            if(RamStatus[i].ProcessID == pID)
            {
                Qsize = tmpQueue.size();
                /**Updating queue
                 * We are looking in queue for frame that process was occupying
                 */

                for(int j=0; j<Qsize; j++)
                {
                    frame = tmpQueue.poll();
                    if(frame != i)
                    {
                        /**If frame is different from the looking one it's added to the new "begin" queue*/
                        beginQ.add(frame);
                    } else break;
                }
                /**When the frame is found, the rest of records form queue is added to the tmp queue with the begging of the previous*/
                beginQ.addAll(tmpQueue);

                /**Removing all records from tmpQueue to prevent putting trash into the main Queue*/
                tmpQueue.removeAll(tmpQueue);
                /**Saving the queue without found frame-to-remove to the cleaned queue*/
                tmpQueue.addAll(beginQ);
                /**Removing all records from the begin Queue*/
                beginQ.removeAll(beginQ);
                RamStatus[i].ProcessID = -1;
                RamStatus[i].PageID = -1;
                Utils.log("Victim queue updated, removed: pID:" + pID + " from queue position: " + i);
            }
        }
        //czyszczenie kolejki glownej
        //victimQueue.removeAll(victimQueue);
        //aktualizacja zawartosci kolejki glownej
        //victimQueue.addAll(tmpQueue);
        /**Removing all records from PageFile and PageTables maps*/
        PageFile.remove(pID);
        PageTables.remove(pID);
        Utils.log("Process " + pID + " has been removed");
    }

    /**
     * Methods used for printing containers in shell
     * */
    public static void printPageTable (int processID)
    {
        if(processExists(processID))
        {
       Shell.println("#### Printing page table, process ID: " + processID + " ####");
            Shell.println("Page\tframe\tvalid");
        for(int i=0; i<PageTables.get(processID).size(); i++)
        {
            Shell.println(i + "   \t" + PageTables.get(processID).get(i).frame + "   \t" + PageTables.get(processID).get(i).valid);
        }
        } else Shell.println("Error: process with given ID doesn't exist.");
    }

    public static void printQueue()
    {
        Queue<Integer> tmp = new LinkedList<>(victimQueue);
        Shell.println("#### Printing victimQueue ####");
        for(int i=0; i<tmp.size(); i++)
        {
            Shell.print(tmp.poll() + " ");
        }
        Shell.println("");
    }

    public static void printProcessPages(int processID)
    {
        if(processExists(processID))
        {
        Shell.println("#### Printing process pages, process ID: " + processID + " ####");
        Vector<Vector<Byte>> pages = PageFile.get(processID);
        for(int i=0; i<pages.size(); i++)
        {
            Shell.println("Page no."+i);
            for(int j=0; j<pages.get(i).size(); j++)
            {
                Shell.print(String.format("%d", pages.get(i).get(j))+"\t");
            }
        }
        Shell.println("\n");
        } else Shell.println("Error: process with given ID doesn't exist.");
    }
    public static void printPage(int processID, int pageID)
    {
        if(processExists(processID))
        {
            if(pageExists(processID, pageID))
            {
            Shell.println("#### Printing process page, process ID: " + processID + "\t pageID: " + pageID + " ####");
            for(int j=0; j<PageFile.get(processID).get(pageID).size(); j++)
                {
                 Shell.print(String.format("%d",PageFile.get(processID).get(pageID).get(j))+"\t");
                }
            Shell.println("");
            } else Shell.println("Error: page with given ID doesn't exist.");
        } else Shell.println("Error: process with given ID doesn't exist.");
    }
    public static void printRamStatus()
    {
        Shell.println("#### Printing current RAM status ####");
        for(int i=0; i<16; i++)
        {
            Utils.log("Frame ID: " + i + " PageID " + RamStatus[i].PageID + "\t ProcessID " + RamStatus[i].ProcessID);
            Shell.println("Frame ID: " + i + " PageID " + RamStatus[i].PageID + "\t ProcessID " + RamStatus[i].ProcessID);
        }
    }
    public static void printNextVictim()
    {
        Shell.println("#### Printing next victim frame ####");
        Shell.println(String.format("%d",victimQueue.peek()));
    }
    static boolean processExists(int procID){
        return PageFile.containsKey(procID);
    }

    static boolean pageExists(int procID, int pageID){

        try{
            PageTables.get(procID).get(pageID);
            System.out.println(PageTables.get(procID).get(pageID));
            return true;
        }  catch (Exception ignored) {}
        return false;
    }

    private
    /**Single record in PageTable*/
    class PageEntry
    {
        boolean valid;
        int frame;
        PageEntry()
        {
            valid = false;
            frame = -1;
        }
    }

    /**Class that helps program to get info about what's inside of RAM*/
    class WhatsInside
    {
        int ProcessID;
        int PageID;
        WhatsInside()
        {
            this.PageID = -1;
            this.ProcessID = -1;
        }
    }

    /**Initialisation of PageFile.
     * Integer is processID
     * 2-dimensional vector contains program code*/
    static Map<Integer, Vector<Vector<Byte>>> PageFile = new HashMap<>();

    /**Map that contains all page tables that are: in use/RAM/not removed*/
    static Map<Integer, Vector<PageEntry>>  PageTables = new HashMap<>();

    /**Queue that contains the next victim to remove when RAM is full*/
    static Queue<Integer> victimQueue = new LinkedList<>();

   /**An array that contains information of what's in RAM atm*/
    static WhatsInside[] RamStatus = new WhatsInside[16];
    void setRamStatus()
    {
        for(int i=0; i<16; i++)
        {
            RamStatus[i] = new WhatsInside();
        }
    }

    /**Method that is processing process. It prepares the process to use it, put in PageFile, create PageTable and send it to RAM*/
    void processProcessing(Process proc)
    {
        Utils.log("Got process " + proc.processId + " that will be added to pageFile and pageTable");
        Vector<Vector<Byte>> program = new Vector<>(new Vector<>());
        Vector<PageEntry> pageTable = new Vector<>();
        int maxPageID = proc.code.size() / 16;
        for (int currentPageID = 0; currentPageID <= maxPageID; currentPageID++)
        {
            Vector<Byte> page = new Vector<>();
            pageTable.add(new PageEntry());

            for (int j = 0; j < 16; j++)
            {
                try {
                    page.add(proc.code.get(j + currentPageID * 16));
                }
                catch (Exception ignored) {}
            }
            program.add(page);
        }
        putInfoToPageTable(proc.processId, pageTable);
        Utils.log("Added process info: " + proc.processId + " to pageTable");
        putProcessToPageFile (proc.processId, program);
        Utils.log("Added process: " + proc.processId + " to pageFile");
    }

    /**Method that puts certain page in RAM*/
    static void putPageInRam(int procID, int pageID)
    {
        Vector <Byte> Page = PageFile.get(procID).get(pageID);
        Utils.log("Putting page: " + pageID + " processID: " + procID + " to RAM");
        /**Checking if RAM is full*/
        if(victimQueue.size()<16)
        {
            for(int fID = 0; fID < 16; fID++)
            {
                if(RamStatus[fID].ProcessID == -1)
                {
                    if(putPageIn(fID, Page))
                    {
                        /**If sent correctly modify all tables connected with status and pages
                         * Updating pageTable and ramStatus*/
                        updatePageTables(procID, pageID, fID, true);
                        updateRamStatus(procID, pageID, fID);
                        victimQueue.add(fID);
                        Utils.log("Page: " + pageID + " processID: " + procID + " had been put into RAM ");
                        break;
                    }
                }
            }
        }
        /**That means ram is full, there is need to kill the victim*/
        else
        {
            Utils.log("RAM is full, searching for victim...");
            int fID = findVictim();
            takePageOut(fID);
            if(putPageIn(fID, Page))
            {
                /**If sent correctly modify all tables connected with status and pages
                 * Updating pageTable and ramStatus*/
                updatePageTables(procID, pageID, fID, true);
                updateRamStatus(procID, pageID, fID);
                victimQueue.add(fID);
                Utils.log("Page: " + pageID + " processID: " + procID + " had been put into RAM");
            }
        }
    }

    /**Method that removes certain page from RAM*/
    static void takePageOut(int fID)
    {
        Vector <Byte> page;
        page = Ram.readFrame(fID);
        int prID = RamStatus[fID].ProcessID;
        int pgID = RamStatus[fID].PageID;
        Utils.log("Taking out victimPage from frame: " + fID);
        putPageInPageFile(pgID, prID, page);
        updatePageTables(prID, pgID, -1, false);
        updateRamStatus(-1, -1, fID);
    }

    /**Method that puts certain page from pageFile into RAM*/
    static boolean putPageIn(int FrameID, Vector<Byte> Page)
    {
        Utils.log("Putting page in frame: " + FrameID);
        return Ram.write(Page, FrameID);
    }

    /**Methods that update PageTables and RamStatus with specific changes*/
    static void updatePageTables(int procID, int pageID, int frameID, boolean value)
    {
        Utils.log("Updating page table with: pageID: " + pageID +" frameID "+ frameID);
        PageTables.get(procID).get(pageID).frame = frameID;
        PageTables.get(procID).get(pageID).valid = value;
    }
    static void updateRamStatus(int procID, int pageID, int fID)
    {
        Utils.log("Updating RAMstatus with: processID: "+ procID +" pageID: " + pageID +" frameID "+ fID);
        RamStatus[fID].ProcessID = procID;
        RamStatus[fID].PageID = pageID;
    }

    void putProcessToPageFile(int pID, Vector<Vector<Byte>> pr)
    {
        Utils.log("Putting process in PageFile, processID: " + pID);
        PageFile.put(pID, pr);
    }
    static void putPageInPageFile(int pageID, int procID, Vector<Byte> page)
    {
        Utils.log("Putting page in PageFile, processID: " + procID + " pageID: "+pageID);
        Vector<Vector<Byte>> tmp =  PageFile.get(procID);
        tmp.set(pageID, page);
        PageFile.put(procID, tmp);
    }
    void putInfoToPageTable(int pID, Vector<PageEntry> pT)
    {
        Utils.log("Putting info into PageTable, processID: " + pID);
        //Shell.println("Putting info into PageTable, processID: " + pID);
        PageTables.put(pID, pT);
    }
    static int findVictim()
    {
        return victimQueue.poll();
    }
}