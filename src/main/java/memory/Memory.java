package memory;

public class Memory {
    private byte[] memory = new byte[256];


    // zapisz całą stronę w wybranej ramce

    void write(byte[] data, int frame) {
        // czekam na implementację strony - można ją przekazać w argumencie
        System.arraycopy(data, 0, memory, frame * 16, 16);
    }


    // zapisz pojedynczy bajt zgodnie z tablicą stronic danego procesu

    void write(byte data, int processID, byte address) {
        // translacja adresu
        int page = (address & 0xf0) >>> 4;
        int offset = address & 0x0f;

        // czekam na implementację PageTable; piszę przzykłąd, żeby nie zapomnieć
        // sprawdzam wartość w PageTable danego procesu
        int frame = PageTable.getFrame(processID, page);
        if (frame == -1) {
            PageTable.loadPage(processID, page);
            frame = PageTable.getFrame(processID, page);
        }

        // wpisuję do pamięci
        memory[frame * 16 + offset] = data;
    }


    // zapisz pojedynczy bajt

    void write(byte data, int i) {
        try {
            memory[i] = data;
        }
        catch (Exception e) {
            System.console().printf(e.getMessage());
        }
    }

    // odczytaj bajt spod adresu zgodnego z tablicą stronic danego procesu

    byte read(int processID, byte address) {
        // translacja adresu
        int page = address & 0xf0;
        int offset = address & 0x0f;

        // czekam na implementację PageTable; piszę przzykłąd, żeby nie zapomnieć
        // sprawdzam wartość w PageTable danego procesu
        int frame = PageTable.getFrame(processID, page);
        if (frame == -1) {
            PageTable.loadPage(processID, page);
            frame = PageTable.getFrame(processID, page);
        }

        // czytam z pamięci
        return memory[frame * 16 + offset];

    }


    // odczytaj bajt z podanej komórki pamięci

    byte read(int i) {
        try {
            return memory[i];
        }
        catch (Exception e) {
            System.console().printf(e.getMessage());
            return 0;
        }
    }
}
