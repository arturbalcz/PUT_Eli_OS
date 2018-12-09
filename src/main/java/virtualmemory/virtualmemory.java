package virtualmemory;

import java.util.Map;
import java.util.Queue;
import java.util.Vector;
import java.util.function.Function;

public class virtualmemory {
    public
    //Jak Szymon zrobi to wezmę tą klasę od niego
    class Frame{
        Integer id;
    }

    //Tymczasowa klasa, jak będę mial dokładniejsze info to po prostu wezmę klase z innego modułu (prawd, PCB)
    class Process{
        Integer processId;
        Vector<byte[]> code;
    }

    //Funkcja otrzymująca proces od PCB
    void getProcess(Process p){
        processProcessing(p);
        startProcessZero(p.processId);
    }

    //Mapa wszystkich Tablic Stron, int to ProcessID, a PgTable to pojedyncza tablica stron
    Map<Integer,  Vector<PageEntry>> PageTables;

    // $#$##$#$#$##$#$#$#$#$#$#$#$#$#$#$#$#$#$##$#$#$#$#$#$##$#$#$##$#$#$#$#$#$#$#$#$#$#$#$#$#$#$ //

    private
    //Pojedynczy wpis do tablicy stronic
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
    //Pojedyncza tablica stron
    Vector<PageEntry> pageTable = null;

    //inicjalizacja mapy PageFile, Integer to PID, a vecvec to 2 wymiarowy wektor z programem
    Map<Integer, Vector<Vector <byte[]>>> PageFile = null;

    //Queue musi mieć rozmiar rzeczywistej liości ramek w pamięci fizycznej, albo po prostu będzie licznik, który nie pozwoli na przejśćie
    Queue <Integer> victimQueue;

    //Co w RAMie piszczy?
    WhatsInside[] RamStatus = new WhatsInside[16];

    //Funkcja dzieląca program na stronice, tworząca tablice stronic i umieszczająca je w odpowiednich wektorach
    void processProcessing(Process proc){

        Vector <byte[]> Page;
        Vector<Vector <byte[]>> program = new Vector<Vector<byte[]>>();
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
        Vector <byte[]> Page = PageFile.get(procID).get(pageID);
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

    void removeProcess(Process proc){
        PageFile.remove(proc.processId, proc);
        PageTables.remove(proc.processId);
    }

    void takePageOut(Integer fID){
        //Funkcja, która zabiera ofiarę z ramu do pliku stronicowania
        Vector <byte[]> page = null;
        //page = getPageFromRAM(FrameID);

        Integer prID = RamStatus[fID].ProcessID;
        Integer pgID = RamStatus[fID].PageID;

        putPageInPageFile(pgID, prID, page);
        updatePageTables(prID, pgID, -1, false);
        updateRamStatus(-1, -1, fID);
    }

    boolean putPageIn(Integer FrameID, Vector<byte[]> Page){
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
    void putProcessToPageFile(Integer pID, Vector<Vector <byte[]>> pr){
        PageFile.put(pID, pr);
    }
    void putPageInPageFile(Integer pageID,Integer procID,Vector <byte[]> page){
        Vector<Vector <byte[]>> tmp =  PageFile.get(procID);
        tmp.set(pageID, page);
        PageFile.put(procID, tmp);
    }
    void putInfoToPageTable(Integer pID, Vector <PageEntry> pT){
        PageTables.put(pID, pT);
    }
    Integer findVictim(){
        return victimQueue.poll();
    }

    //Program address space - klasa, która przechowuje cały program w pliku stronicowania
}
