package virtualmemory;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;
import java.util.function.Function;

public class virtualmemory {

     //Jak Szymon zrobi to wezmę tą klasę od niego
     public class Frame{
        Integer id;
    }

    //Tymczasowa klasa, jak będę mial dokładniejsze info to po prostu wezmę klase z innego modułu (prawd, PCB)
    public class Process{
        Integer processId;
        Vector<Byte> code;
    }

    //Funkcja otrzymująca proces od PCB
    public void getProcess(Process p){
        processProcessing(p);
        startProcessZero(p.processId);
    }
    //Funkcja wywoływana w momencie, gdy dana strona nie jest w ramie - wywolanie stronicowania na żądanie
    public void demandPage(Integer ProcessID, Integer PageID){
        putPageInRam(ProcessID, PageID);
    }
    //Mapa wszystkich Tablic Stron, int to ProcessID, a Vector to tablica stron
    Map<Integer,  Vector<PageEntry>> PageTables;

    //funkcja sprzątająca po zakonczeniu procesu, wszystkie niezbedne wartośći ustawia na -1 i usuwa wpisy w PageFile i PageTable
    public void removeProcess(Integer processID){
        Queue <Integer> tmpQueue = victimQueue, beginQ = null;
        Integer frame, Qsize;
        for(int i = 0; i<16; i++){
            if(RamStatus[i].ProcessID == processID){
                Qsize = tmpQueue.size();
                //Updating queue
                //szukamy w calej kolejce ramko, ktora zajmowal proces
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
            }
        }
        //czyszczenie kolejki glownej
        victimQueue.removeAll(victimQueue);
        //aktualizacja zawartosci kolejki glownej
        victimQueue.addAll(tmpQueue);
        PageFile.remove(processID);
        PageTables.remove(processID);
    }
    //Funkcje związane z wypisywaniem tego co jest gdziekolwiek, do pracy krokowej.
    public void printPageTable (Integer processID){
        System.out.println("#### Printing page table, process ID: " + processID + " ####");
        for(int i=0; i<PageTables.get(processID).size(); i++){
            System.out.println("Page no."+ i + " " + PageTables.get(processID).get(i).frame + " " + PageTables.get(processID).get(i).valid);
        }
        System.out.println();
    }

    public void printQueue(){
        Queue<Integer> tmp = victimQueue;
        System.out.println("#### Printing victimQueue ####");
        for(int i=0; i<tmp.size(); i++){
            System.out.print(tmp.poll() + " ");
        }
        System.out.println();
    }

    public void printProcessPages(Integer processID){
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
    public void printPage(Integer processID, Integer pageID){
        System.out.println("#### Printing process page, process ID: " + processID + " pageID: " + pageID + " ####");
        for(int j=0; j<PageFile.get(processID).get(pageID).size(); j++){
            System.out.println(PageFile.get(processID).get(pageID).get(j));
        }
        System.out.println();
    }
    public void printRamStatus(){
        System.out.println("#### Printing current RAM status ####");
        for(int i=0; i<16; i++){
            System.out.println("Frame ID: " + i + " PageID " + RamStatus[i].PageID + " ProcessID " + RamStatus[i].ProcessID);
        }
        System.out.println();
    }
    public void printNextVictim(){
        //Mozna uzyc tez .peek()
        System.out.println("#### Printing next victim page ####");
        Queue<Integer> tmp = victimQueue;
        System.out.println(tmp.poll());
    }

    // $#$##$#$#$##$#$#$#$#$#$#$#$#$#$#$#$#$#$##$#$#$#$#$#$##$#$#$##$#$#$#$#$#$#$#$#$#$#$#$#$#$#$ //
    // Pojedynczy wpis w tablicy stronic
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
    Map<Integer, Vector<Vector <Byte>>> PageFile = null;

    //Queue - kolejka w której są zawarte informacje o kolejnych stronach, które zostały umieszczone w ramie
    //Element przydatny przy wymianie stronic na żądanie metodą FIFO
    Queue <Integer> victimQueue;

    //Tablica o rozmiarze 16 pozwalająca na kontrolowanie tego co jest w ramie
    WhatsInside[] RamStatus = new WhatsInside[16];

    //Funkcja dzieląca program na stronice, tworząca tablice stronic i umieszczająca je w odpowiednich wektorach
    void processProcessing(Process proc){

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
        putProcessToPageFile (proc.processId, program);
    }

    void startProcessZero(Integer procID){
        putPageInRam(procID, 0);
    }

    void putPageInRam(Integer procID, Integer pageID){
        Vector <Byte> Page = PageFile.get(procID).get(pageID);
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
                    }
                }
            }
        }
        //That means ram is full, need to kill some victims
        else
        {
            Integer fID = findVictim();
            takePageOut(fID);
            if(putPageIn(fID, Page)){
                //If sent correctly modify all tables connected with status and pages
                //Updating pageTable and ram status
                updatePageTables(procID, pageID, fID, true);
                updateRamStatus(procID, pageID, fID);
                victimQueue.add(fID);
            }
        }
    }

    void takePageOut(Integer fID){
        //Funkcja, która zabiera ofiarę z ramu do pliku stronicowania
        Vector <Byte> page = null;
        //page = getPageFromRAM(FrameID);

        Integer prID = RamStatus[fID].ProcessID;
        Integer pgID = RamStatus[fID].PageID;

        putPageInPageFile(pgID, prID, page);
        updatePageTables(prID, pgID, -1, false);
        updateRamStatus(-1, -1, fID);
    }

    boolean putPageIn(Integer FrameID, Vector<Byte> Page){
        //Funkcja, która wprowadza stronicę do ramu z pliku stronicowania
        //if(writeIN(FrameID, Page)){return true;}
        return false;
    }

    void updatePageTables(Integer procID, Integer pageID, Integer frameID, boolean value){
        PageTables.get(procID).get(pageID).frame = frameID;
        PageTables.get(procID).get(pageID).valid = value;
    }
    void updateRamStatus(Integer procID, Integer pageID, Integer fID) {
        RamStatus[fID].ProcessID = procID;
        RamStatus[fID].PageID = pageID;
    }
    void putProcessToPageFile(Integer pID, Vector<Vector <Byte>> pr){
        PageFile.put(pID, pr);
    }
    void putPageInPageFile(Integer pageID,Integer procID,Vector <Byte> page){
        Vector<Vector <Byte>> tmp =  PageFile.get(procID);
        tmp.set(pageID, page);
        PageFile.put(procID, tmp);
    }
    void putInfoToPageTable(Integer pID, Vector <PageEntry> pT){
        PageTables.put(pID, pT);
    }
    Integer findVictim(){
        return victimQueue.poll();
    }
}