package virtualmemory;

import java.util.Map;
import java.util.Queue;
import java.util.Vector;
import java.util.function.Function;

class Process{
    Integer processId;
    Vector<virtualmemory.Page> processVector;
}
public class virtualmemory {
    public
    class Page{
        boolean valid;
        Integer frame;
        Page(Boolean v, Integer f){
            valid = v;
            frame = f;
        }
    }
    void getProcess(Process p){
        processSpliter(p);
        putProcessToPageFile(p, program);

    }
    private
    char[] bajt = new char [8];
    Vector <char[]> chunk;
    Vector<Vector <char[]>> program;

    void processSpliter(Process p){
        int size = p.processVector.size(); //size of program code
        while (size != 0){
            for(int i=0; i<16; i++){
                for(int j=0; j<8; j++){
                bajt[j]= 1; //Tutaj przypisuję każdy kolejny znak do tablicy charów i następnie wpisuję je do vectora zbiorczego
                }
                chunk.add(bajt);
            }
            program.add(chunk);
        }
    }


    //Queue musi mieć rozmiar rzeczywistej liości ramek w pamięci fizycznej, albo po prostu będzie licznik, który nie pozwoli na przejśćie
    Queue <Page> victimQueue;

    // Ten integer w mapie to id procesu
    Map<Integer, Vector<Vector <char[]>>> PageFile;


    void putProcessToPageFile(Process proc, Vector<Vector <char[]>> prog){
        PageFile.put(proc.processId, prog);
    }
    void removeProcessFormPageFile(Process proc) { PageFile.remove(proc.processId, proc);}
    void pageSwap(Integer iTF, Integer prId){
        Page adin = findVictim();
        Page dwa = findSpecificPage(iTF, prId);
    }
    void pageOut(){
        //Funkcja, która zabiera ofiarę z ramu do pliku stronicowania
    }

    void pageIn(){
        //Funkcja, która wprowadza stronicę do ramu z pliku stronicowania
    }
    void updateTranslationMap(){
        //Funkcja, która aktualizuje informacje w mapie odpowiedzialnej za translację z adresów irtualnych a adresy fizyczne
    }

    Page findVictim(){
        Page victimPage;
        victimPage = victimQueue.poll();
        return victimPage;
    }

    Page findSpecificPage(Integer idToFind, Integer processId){
        Process tmp = PageFile.get(processId);
        Page found = tmp.processVector.get(idToFind);
        return found;
    }

    //free frame list - lista, w której są wolne ramki
    //Program address space - klasa, która przechowuje cały program w pliku stronicowania
    int procId;


}
