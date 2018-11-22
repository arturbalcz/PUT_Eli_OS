package virtualmemory;

import java.util.Map;
import java.util.Queue;
import java.util.Vector;
import java.util.function.Function;

class Page{
    Integer pageId, processId;
    //Virtual address
    //Physical address
    //Rozmiar???
}
class PageTable{

}
class Process{
    Integer processId;
    Vector<Page> processVector;
}
public class virtualmemory {
    boolean freeFrames;
    //Queue musi mieć rozmiar rzeczywistej liości ramek w pamięci fizycznej, albo po prostu będzie licznik, który nie pozwoli na przejśćie
    Queue <Page> victimQueue;
    // Ten integer w mapie to id procesu
    Map<Integer, Process> PageFile;
    //Funkcja wywoływana przez Szymona, który będzie dzielił kod na Strony
    void putProcessToPageFile(Process proc){
        PageFile.put(proc.processId, proc);
    }

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
