package memory;

import shell.Shell;
import utils.Utils;
import virtualmemory.virtualmemory;
import java.util.Vector;

public class Memory {
    private static byte[] memory = new byte[256];
    virtualmemory VM;


    // połącz z pamięcią wirtualną
    public void GetReference(virtualmemory vm){
        VM = vm;
    }


    // zapisz całą stronę w wybranej ramce
    public static boolean write(Vector<Byte> data, int frame) {
        if (frame < 0 || frame > 15) {
            return false;
        }

        for (int i = 0; i < data.size(); i++) {
            memory[frame * 16 + i] = data.get(i);
        }

        Utils.log(String.format("loaded page to frame %d", frame));
        return true;
    }


    // zapisz pojedynczy bajt zgodnie z tablicą stronic danego procesu
    public void write(byte data, int processID, byte address) {
        // translacja adresu
        int page = (address & 0xf0) >>> 4;
        int offset = address & 0x0f;

        // sprawdzam wartość w PageTable danego procesu
        int frame = VM.getFrame(processID, page);

        // wpisuję do pamięci
        memory[frame * 16 + offset] = data;

        Utils.log(String.format("loaded page %d (PID = %d) to frame %d", page, processID, frame));
    }


    // zapisz pojedynczy bajt
    void write(byte data, int i) {
        try {
            memory[i] = data;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Utils.log(String.format("loaded byte to address %d", i));
    }



    // odczytaj zawartość całej ramki
    public static Vector<Byte> readFrame(int frame) {
        Utils.log(String.format("reading page from frame %d", frame));

        // czytam z pamięci
        Vector<Byte> tmp = new Vector<Byte>();
        try {
            for (int i = 0; i < 15; i++) {
                tmp.add(memory[frame * 16 + i]);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return tmp;
    }

    // odczytaj bajt spod adresu zgodnego z tablicą stronic danego procesu
    public byte read(int processID, byte address) {
        // translacja adresu
        int page = address & 0xf0;
        int offset = address & 0x0f;

        // sprawdzam wartość w PageTable danego procesu
        int frame = VM.getFrame(processID, page);

        // czytam z pamięci
        Utils.log(String.format("reading byte from address %d", frame * 16 + offset));
        return memory[frame * 16 + offset];
    }


    // odczytaj bajt z podanej komórki pamięci
    public byte read(int i) {
        Utils.log(String.format("reading byte from address %d", i));

        try {
            return memory[i];
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }

    }


    // wyświetl surową zawartość pamięci
    void print() {
        for (int i = 0; i < 16; i++) {
            Shell.print(String.format("%n%d:", i));
            for (int j = 0; j < 16; j++) {
                Shell.print(String.format(" %c", memory[i * 16 + j]));
            }
        }
        Shell.println("");

        Utils.log("showing raw contents of RAM");
    }
}