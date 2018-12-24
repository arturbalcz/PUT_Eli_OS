package memory;

import utils.Utils;

public class Memory {
    private byte[] memory = new byte[256];
    private static virtualmemory VM;


    // ładowanie virtualmemory
    Memory(virtualmemory virtual) {
        VM = virtual;
    }


    // metoda pomocnicza do wyciągania informacji z tablicy stronic
    private int getFrame(int processID, int page) {
        int tmp = VM.PageTables.get(processID).get(page);
        if (tmp == -1) {
            VM.demandPage(processID, page);
            tmp = VM.PageTables[processID][page];
        }
        return tmp;
    }


    // zapisz całą stronę w wybranej ramce
    void write(byte[] data, int frame) {
        System.arraycopy(data, 0, memory, frame * 16, 16);

        String tmp = String.format("loaded page to frame %d", frame);
        Utils.log(tmp);
    }


    // zapisz pojedynczy bajt zgodnie z tablicą stronic danego procesu
    void write(byte data, int processID, byte address) {
        // translacja adresu
        int page = (address & 0xf0) >>> 4;
        int offset = address & 0x0f;

        // sprawdzam wartość w PageTable danego procesu
        int frame = getFrame(processID, page);

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


    // odczytaj bajt spod adresu zgodnego z tablicą stronic danego procesu
    byte read(int processID, byte address) {
        // translacja adresu
        int page = address & 0xf0;
        int offset = address & 0x0f;

        // sprawdzam wartość w PageTable danego procesu
        int frame = getFrame(processID, page);

        // czytam z pamięci
        return memory[frame * 16 + offset];

        Utils.log(String.format("reading byte from address %d", frame * 16 + offset));
    }


    // odczytaj bajt z podanej komórki pamięci
    byte read(int i) {
        try {
            return memory[i];
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }

        Utils.log(String.format("reading byte from address %d", i));
    }


    // wyświetl surową zawartość pamięci
    void print() {
        for (int i = 0; i < 16; i++) {
            System.out.printf("%n%d:", i);
            for (int j = 0; j < 16; j++) {
                System.out.printf(" %c", memory[i * 16 + j]);
            }
        }
        System.out.println();

        Utils.log("showing raw contents of RAM");
    }
}