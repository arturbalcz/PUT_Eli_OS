package virtualmemory;

import java.util.Map;
import java.util.Queue;
import java.util.Vector;
import java.util.function.Function;
import memory.Memory;
import utils.Utils;

public class virtualmemory {
    class Process{
        Integer processId;
        Vector<Byte> code;
}

    //Funkcja otrzymująca proces od PCB
    void getProcess(Process p){
        processProcessing(p);
        startProcessZero(p.processId);
        Utils.log("got Process " + p.processId);
    }
    //Funkcja wywoływana w momencie, gdy dana strona nie jest w ramie - wywolanie stronicowania na żądanie
    static void demandPage(Integer ProcessID, Integer PageID){
        putPageInRam(ProcessID, PageID);
    }
    //Mapa wszystkich Tablic Stron, int to ProcessID, a Vector to tablica stron
    static Map<Integer,  Vector<PageEntry>> PageTables;

    //funkcja sprzątająca po zakonczeniu procesu, wszystkie niezbedne wartośći ustawia na -1 i usuwa wpisy w PageFile i PageTable
    void removeProcess(Integer pID){
        Utils.log("removing Process " + pID);
        Queue <Integer> tmpQueue = victimQueue, beginQ = null;
        Integer frame, Qsize;
        for(int i = 0; i<16; i++){
            if(RamStatus[i].ProcessID == pID){
                Qsize = tmpQueue.size();
                //Updating queue
                //szukamy w calej kolejce ramki, ktora zajmowal proces
                for(int j=0; j<Qsize; j++){
                    frame = tmpQueue.poll();
                    if(frame != i) {
                        //Poczatek calej kolejki jest zapisywany do innej kolejki jezeli ramka jest rozna od szukanej
                        beginQ.add(frame);
                    } else break;
                }
                Qsize = tmpQueue.size();
                //Do kolejki poczatkowej przypisujemy pozostale elementy kolejki oprocz szukanej ramki
                for(int a=0; a<Qsize; a++){
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
    void printPageTable (Integer processID){
        System.out.println("#### Printing page table, process ID: " + processID + " ####");
        for(int i=0; i<PageTables.get(processID).size(); i++){
            System.out.println("Page no."+ i + " " + PageTables.get(processID).get(i).frame + " " + PageTables.get(processID).get(i).valid);
        }
        System.out.println();
    }

    void printQueue(){
        Queue<Integer> tmp = victimQueue;
        System.out.println("#### Printing victimQueue ####");
        for(int i=0; i<tmp.size(); i++){
            System.out.print(tmp.poll() + " ");
        }
        System.out.println();
    }

    void printProcessPages(Integer processID){
        System.out.println("#### Printing process pages, process ID: " + processID + " ####");
        Vector<Vector<Byte>> pages = PageFile.get(processID);
        for(int i=0; i<pages.size(); i++){
            System.out.println("Page no."+i);
            for(int j=0; j<pages.get(i).size(); j++){
                System.out.println(pages.get(i).get(j));
            }
        }
        System.out.println();
    }
    void printPage(Integer processID, Integer pageID){
        System.out.println("#### Printing process page, process ID: " + processID + " pageID: " + pageID + " ####");
        for(int j=0; j<PageFile.get(processID).get(pageID).size(); j++){
            System.out.println(PageFile.get(processID).get(pageID).get(j));
        }
        System.out.println();
    }
    void printRamStatus(){
        System.out.println("#### Printing current RAM status ####");
        for(int i=0; i<16; i++){
            System.out.println("Frame ID: " + i + " PageID " + RamStatus[i].PageID + " ProcessID " + RamStatus[i].ProcessID);
        }
        System.out.println();
    }
    void printNextVictim(){
        System.out.println("#### Printing next victim page ####");
        System.out.println(victimQueue.peek());
    }

    // $#$##$#$#$##$#$#$#$#$#$#$#$#$#$#$#$#$#$##$#$#$#$#$#$##$#$#$##$#$#$#$#$#$#$#$#$#$#$#$#$#$#$ //

    private
    //Pojedynczy wpis w tablicy stronic
    class PageEntry
    {
        boolean valid;
        Integer frame;
        PageEntry(){
            valid = false;
            frame = -1;
        }
    }

    //klasa pomocnicza aby sie orientowac co jest w ramie
    class WhatsInside{
        Integer ProcessID;
        Integer PageID;
        WhatsInside(){
            this.PageID = -1;
            this.ProcessID = -1;
        }
    }
    //Pojedyncza tablica stron, vector pomocniczy w którym tymczasowo znajdują się stronu programu
    Vector<PageEntry> pageTable = null;

    //Plik wymiany
    //inicjalizacja mapy PageFile, Integer to PID, a vec-vec to 2 wymiarowy wektor z programem
    static Map<Integer, Vector<Vector <Byte>>> PageFile = null;

    //Queue - kolejka w której są zawarte informacje o kolejnych stronach, które zostały umieszczone w ramie
    //Element przydatny przy wymianie stronic na żądanie metodą FIFO
    static Queue <Integer> victimQueue;

    //Tablica o rozmiarze 16 pozwalająca na kontrolowanie tego co jest w ramie
    static WhatsInside[] RamStatus = new WhatsInside[16];

    //Funkcja dzieląca program na stronice, tworząca tablice stronic i umieszczająca je w odpowiednich wektorach
    void processProcessing(Process proc){
        Utils.log("Got process " + proc.processId + " that will be added to pageFile and pageTable");
        Vector <Byte> Page;
        Vector<Vector <Byte>> program = new Vector<Vector<Byte>>();
        pageTable.clear();
        Integer progSize = proc.code.size();
        Integer AddedValue = 0, stepCounter=0;
        PageEntry tmpPE;
        for(int i = 0; stepCounter<progSize; i++)
        {
            Page = null;
            for(Integer j=0; j<16; j++)
            {
                tmpPE = new PageEntry();
                Page.add(j, proc.code.get(j + AddedValue));
                pageTable.add(j+AddedValue, tmpPE);
                stepCounter++;
            }
            program.add(i, Page);
            AddedValue+=16;
        }
        putInfoToPageTable(proc.processId, pageTable);
        Utils.log("Added process info: " + proc.processId + " to pageTable");
        putProcessToPageFile (proc.processId, program);
        Utils.log("Added process: " + proc.processId + " to pageFile");
    }

    void startProcessZero(Integer procID){
        putPageInRam(procID, 0);
    }

    static void putPageInRam(Integer procID, Integer pageID){
        Vector <Byte> Page = PageFile.get(procID).get(pageID);
        Utils.log("Putting page: " + pageID + " processID: " + procID + " to RAM");
        if(victimQueue.size()<16)
        {
            for(int fID=0; fID<16; fID++)
            {
                if(RamStatus[fID].ProcessID == -1)
                {
                    if(putPageIn(fID, Page)){
                        //If sent correctly modify all tables connected with status and pages
                        //Updating pageTable and ram status
                        updatePageTables(procID, pageID, fID, true);
                        updateRamStatus(procID, pageID, fID);
                        victimQueue.add(fID);
                        Utils.log("Page: " + pageID + " processID: " + procID + " had been put into RAM");
                    }
                }
            }
        }
        //That means ram is full, need to kill some victims
        else
        {
            Utils.log("RAM is full, searching for victim...");
            Integer fID = findVictim();
            takePageOut(fID);
            if(putPageIn(fID, Page)){
                //If sent correctly modify all tables connected with status and pages
                //Updating pageTable and ram status
                updatePageTables(procID, pageID, fID, true);
                updateRamStatus(procID, pageID, fID);
                victimQueue.add(fID);
                Utils.log("Page: " + pageID + " processID: " + procID + " had been put into RAM");
            }
        }
    }

    static void takePageOut(Integer fID){
        //Funkcja, która zabiera ofiarę z ramu do pliku stronicowania
        Utils.log("Taking out victimPage from frame: " + fID);
        Vector <Byte> page = null;
        page = Memory.read(fID);

        Integer prID = RamStatus[fID].ProcessID;
        Integer pgID = RamStatus[fID].PageID;
        Utils.log("Taking out victimPage from frame: " + fID);
        putPageInPageFile(pgID, prID, page);
        updatePageTables(prID, pgID, -1, false);
        updateRamStatus(-1, -1, fID);
    }

    static boolean putPageIn(Integer FrameID, Vector<Byte> Page){
        //Funkcja, która wprowadza stronicę do ramu z pliku stronicowania
        Utils.log("Putting page in frame: " + FrameID);
        return Memory.write(Page, FrameID);
    }

    static void updatePageTables(Integer procID, Integer pageID, Integer frameID, boolean value){
        Utils.log("Updating page table with: pageID: " + pageID +" frameID "+ frameID);
        PageTables.get(procID).get(pageID).frame = frameID;
        PageTables.get(procID).get(pageID).valid = value;
    }
    static void updateRamStatus(Integer procID, Integer pageID, Integer fID) {
        Utils.log("Updating RAMstatus with: processID: "+ procID +" pageID: " + pageID +" frameID "+ fID);
        RamStatus[fID].ProcessID = procID;
        RamStatus[fID].PageID = pageID;
    }
    void putProcessToPageFile(Integer pID, Vector<Vector <Byte>> pr){
        Utils.log("Putting process in PageFile, processID: " + pID);
        PageFile.put(pID, pr);
    }
    static void putPageInPageFile(Integer pageID,Integer procID,Vector <Byte> page){
        Utils.log("Putting page in PageFile, processID: " + procID + " pageID: "+pageID);
        Vector<Vector <Byte>> tmp =  PageFile.get(procID);
        tmp.set(pageID, page);
        PageFile.put(procID, tmp);
    }
    void putInfoToPageTable(Integer pID, Vector <PageEntry> pT){
        Utils.log("Putting info into PageTable, processID: " + pID);
        PageTables.put(pID, pT);
    }
    static Integer findVictim(){
        return victimQueue.poll();
    }
    // (wykorzystywane przez RAM) dostęp do tablicy stronic
    public static int getFrame(int processID, int page) {
        boolean valid = PageTables.get(processID).get(page).valid;
        if (!valid) {
            demandPage(processID, page);
        }
        return PageTables.get(processID).get(page).frame;
    }
}
