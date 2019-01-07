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
    /**Funkcja otrzymująca proces od PCB*/
    void getProcess(Process p)
    {
        processProcessing(p);
        startProcessZero(p.processId);
        Utils.log("got Process " + p.processId);
    }
    //Funkcja wywoływana w momencie, gdy dana strona nie jest w ramie - wywolanie stronicowania na żądanie
    static void demandPage(int ProcessID, int PageID)
    {
        putPageInRam(ProcessID, PageID);
    }
    //Mapa wszystkich Tablic Stron, int to ProcessID, a Vector to tablica stron
    static Map<Integer, Vector<PageEntry>>  PageTables = new HashMap<>();
    //funkcja sprzątająca po zakonczeniu procesu, wszystkie niezbedne wartośći ustawia na -1 i usuwa wpisy w PageFile i PageTable
    public void removeProcess(int pID)
    {
        Utils.log("removing Process " + pID);
        Queue<Integer> tmpQueue = victimQueue, beginQ = null;
        int frame, Qsize;
        for(int i = 0; i<16; i++)
        {
            if(RamStatus[i].ProcessID == pID)
            {
                Qsize = tmpQueue.size();
                //Updating queue
                //szukamy w calej kolejce ramki, ktora zajmowal proces
                for(int j=0; j<Qsize; j++)
                {
                    frame = tmpQueue.poll();
                    if(frame != i)
                    {
                        //Poczatek calej kolejki jest zapisywany do innej kolejki jezeli ramka jest rozna od szukanej
                        beginQ.add(frame);
                    } else break;
                }
                Qsize = tmpQueue.size();
                //Do kolejki poczatkowej przypisujemy pozostale elementy kolejki oprocz szukanej ramki
                for(int a=0; a<Qsize; a++)
                {
                    beginQ.addAll(tmpQueue);
                }
                //czyszczenie kolejki tymczasowej
                tmpQueue.removeAll(tmpQueue);
                //zapisanie do tymczasowej kolejki kolejki zmodyfikowanej, bez szukanej ramki
                tmpQueue.addAll(beginQ);
                //czyszczenie kolejki z poczatkiem kolejki tymczasowej
                beginQ.removeAll(beginQ);
                RamStatus[i].ProcessID = -1;
                RamStatus[i].PageID = -1;
                Utils.log("Victim queue updated, removed: pID:" + pID + " from queue position: " + i);
            }
        }
        //czyszczenie kolejki glownej
        victimQueue.removeAll(victimQueue);
        //aktualizacja zawartosci kolejki glownej
        victimQueue.addAll(tmpQueue);
        PageFile.remove(pID);
        PageTables.remove(pID);
        Utils.log("Process " + pID + " has been removed");
    }
    //Funkcje związane z wypisywaniem tego co jest gdziekolwiek, do pracy krokowej.
    public void printPageTable (int processID)
    {
        System.out.println("#### Printing page table, process ID: " + processID + " ####");
        for(int i=0; i<PageTables.get(processID).size(); i++)
        {
            System.out.println("Page no."+ i + " " + PageTables.get(processID).get(i).frame + " " + PageTables.get(processID).get(i).valid);
        }
        System.out.println();
    }

    public void printQueue()
    {
        Queue<Integer> tmp = victimQueue;
        System.out.println("#### Printing victimQueue ####");
        for(int i=0; i<tmp.size(); i++)
        {
            System.out.print(tmp.poll() + " ");
        }
        System.out.println();
    }

    public void printProcessPages(int processID)
    {
        System.out.println("#### Printing process pages, process ID: " + processID + " ####");
        Vector<Vector<Byte>> pages = PageFile.get(processID);
        for(int i=0; i<pages.size(); i++)
        {
            System.out.println("Page no."+i);
            for(int j=0; j<pages.get(i).size(); j++)
            {
                System.out.println(pages.get(i).get(j));
            }
        }
        System.out.println();
    }
    public void printPage(int processID, int pageID)
    {
        System.out.println("#### Printing process page, process ID: " + processID + " pageID: " + pageID + " ####");
        for(int j=0; j<PageFile.get(processID).get(pageID).size(); j++)
        {
            System.out.println(PageFile.get(processID).get(pageID).get(j));
        }
        System.out.println();
    }
    public void printRamStatus()
    {
        System.out.println("#### Printing current RAM status ####");
        for(int i=0; i<16; i++)
        {
            Utils.log("Frame ID: " + i + " PageID " + RamStatus[i].PageID + " ProcessID " + RamStatus[i].ProcessID);
            System.out.println("Frame ID: " + i + " PageID " + RamStatus[i].PageID + " ProcessID " + RamStatus[i].ProcessID);
        }
        System.out.println();
    }
    public void printNextVictim()
    {
        System.out.println("#### Printing next victim page ####");
        System.out.println(victimQueue.peek());
    }

    // $#$##$#$#$##$#$#$#$#$#$#$#$#$#$#$#$#$#$##$#$#$#$#$#$##$#$#$##$#$#$#$#$#$#$#$#$#$#$#$#$#$#$ //

    private
            //Pojedynczy wpis w tablicy stronic
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

    //klasa pomocnicza aby sie orientowac co jest w ramie
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
    //Pojedyncza tablica stron, vector pomocniczy w którym tymczasowo znajdują się stronu programu
//    Vector<PageEntry> pageTable = new Vector<>();

    //Plik wymiany
    //inicjalizacja mapy PageFile, Integer to PID, a vec-vec to 2 wymiarowy wektor z programem
    static Map<Integer, Vector<Vector<Byte>>> PageFile = new HashMap<>();

    //Queue - kolejka w której są zawarte informacje o kolejnych stronach, które zostały umieszczone w ramie
    //Element przydatny przy wymianie stronic na żądanie metodą FIFO
    static Queue<Integer> victimQueue = new LinkedList<>();

    //Tablica o rozmiarze 16 pozwalająca na kontrolowanie tego co jest w ramie
    static WhatsInside[] RamStatus = new WhatsInside[16];
    void setRamStatus()
    {
        for(int i=0; i<16; i++)
        {
            RamStatus[i] = new WhatsInside();
        }
    }

    //Funkcja dzieląca program na stronice, tworząca tablice stronic i umieszczająca je w odpowiednich wektorach
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
                    Shell.print(String.format("%d", proc.code.get(j + currentPageID * 16)));
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

    void startProcessZero(int procID)
    {
        putPageInRam(procID, 0);
    }

    static void putPageInRam(int procID, int pageID)
    {
        Vector <Byte> Page = PageFile.get(procID).get(pageID);
        Utils.log("Putting page: " + pageID + " processID: " + procID + " to RAM");
        if(victimQueue.size()<16)
        {
            for(int fID = 0; fID < 16; fID++)
            {
                if(RamStatus[fID].ProcessID == -1)
                {
                    if(putPageIn(fID, Page))
                    {
                        //If sent correctly modify all tables connected with status and pages
                        //Updating pageTable and ram status
                        updatePageTables(procID, pageID, fID, true);
                        updateRamStatus(procID, pageID, fID);
                        victimQueue.add(fID);
                        Utils.log("Page: " + pageID + " processID: " + procID + " had been put into RAM");
                        break;
                    }
                }
            }
        }
        //That means ram is full, need to kill some victims
        else
        {
            Utils.log("RAM is full, searching for victim...");
            int fID = findVictim();
            takePageOut(fID);
            if(putPageIn(fID, Page))
            {
                //If sent correctly modify all tables connected with status and pages
                //Updating pageTable and ram status
                updatePageTables(procID, pageID, fID, true);
                updateRamStatus(procID, pageID, fID);
                victimQueue.add(fID);
                Utils.log("Page: " + pageID + " processID: " + procID + " had been put into RAM");
            }
        }
    }

    static void takePageOut(int fID)
    {
        //Funkcja, która zabiera ofiarę z ramu do pliku stronicowania
        Utils.log("Taking out victimPage from frame: " + fID);
        Vector <Byte> page;
        page = Ram.readFrame(fID);

        int prID = RamStatus[fID].ProcessID;
        int pgID = RamStatus[fID].PageID;
        Utils.log("Taking out victimPage from frame: " + fID);
        putPageInPageFile(pgID, prID, page);
        updatePageTables(prID, pgID, -1, false);
        updateRamStatus(-1, -1, fID);
    }

    static boolean putPageIn(int FrameID, Vector<Byte> Page)
    {
        //Funkcja, która wprowadza stronicę do ramu z pliku stronicowania
        Utils.log("Putting page in frame: " + FrameID);
        return Ram.write(Page, FrameID);
    }

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
        System.out.print("Putting info into PageTable, processID: " + pID);
        PageTables.put(pID, pT);
    }
    static int findVictim()
    {
        return victimQueue.poll();
    }
    // (wykorzystywane przez RAM) dostęp do tablicy stronic

    public static int getFrame(int processID, int page) 
    {
        if (PageTables.get(processID).get(page).valid == false) 
        {
            demandPage(processID, page);
        }
        return PageTables.get(processID).get(page).frame;
    }
}
