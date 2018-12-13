package filesystem;

import shell.Shell;
import utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

import static java.lang.Integer.parseInt;

public class Disk {
    static byte physicalDisk[] = new byte[1024];
    static int blockSize = 32;
    static boolean blockTaken[] = new boolean[physicalDisk.length/blockSize];
    static int blockAmount = blockTaken.length;
    static int currentBlock = 0;

    public Disk(){
        Arrays.fill(physicalDisk, (byte)0);
    }


    public static byte[] empty() {
        return new byte[]{0};
    }

    public static byte[] invalid() {
        return new byte[]{-1};
    }

    public static byte[] index() {
        return new byte[]{-2};
    }


    /**
     * Gets the value of specified block
     *
     * @param index block to be returned
     * @return byte[] corresponding to specified block
     */
    public static byte[] getBlock(int index){
        return Arrays.copyOfRange(physicalDisk, index*blockSize, index*blockSize + blockSize);
    }

    /***
     * Gets all blocks assigned to an index block
     *
     * @param index     which index block to get content from
     * @return          byte[] of all data blocks
     */
   public static byte[] getBlockByIndex(int index){
       byte[] indexBlock = getBlock(index);
       int blockAmount = 0;
       Vector<Byte> temp = new Vector<>();
       for(int i = 1; i < indexBlock.length; i++) {
           if (indexBlock[i] == 0) break;
           blockAmount++;
       }
       for (int i = 1; i <= blockAmount; i++) {
            for(byte e: getBlock((char)indexBlock[i])) {
                temp.add(e);
            }
       }
       byte[] result = new byte[blockAmount*blockSize];
       for (int i = 0; i < blockAmount * blockSize; i++) {
           result[i] = temp.get(i);
       }
       return result;
   }


    /**
     * Inserts data into a block. Does not divide data, does not check for empty block.
     *
     * @param index     which block to insert into
     * @param content   content ot be inserted, no longer than {@value blockSize}
     */
    static void setBlock(int index, byte[] content){
        int max = content.length < blockSize ? blockSize : content.length;
        blockTaken[index] = true;
        for (int i = 0; i < max; i++) {
                if(i >= content.length) {
                    physicalDisk[index*blockSize + i] = 0;
                }
                else {
                    physicalDisk[index * blockSize + i] = content[i];
                }
        }
    }


    static boolean isTaken(int index){
       return blockTaken[index];
   }

    /**
     * Finds free block on disk, checking whole disk
     *
     * @param index from which index to start looking
     * @return next free index
     */
    static int findNextFree(int index){
        for (int i = index; i < blockAmount; i++) {
            if (!isTaken(i)) {
                return i;
            }
        }
        for (int i = 0; i < index; i++) {
            if(!isTaken(i)){
                return i;
            }
        }
        return -1;
    }

    static byte[] divideContent(byte content[]){
        if( content.length < 32){ return content; }
        return Arrays.copyOfRange(content, 0, blockSize);
    }

     /**
     * Writes data onto disk searching for free space, dividing as needed.
     *
     * @param content   content to be written
     * @param index     from which index to start searching
     * @return          number of the assigned index block`
     */
    public static int addContent(byte[] content, int index){
        int currentIndex = findNextFree(index);
        int x = 0;
        byte currentContent[] = content;
        byte indexBlock[] = new byte[blockSize];
        indexBlock[0] = -2;
        do{
            currentIndex = findNextFree(currentIndex);
            currentContent = Arrays.copyOfRange(content, blockSize*x, content.length);
            setBlock(currentIndex, divideContent(currentContent));
            x++;
            indexBlock[x] = Byte.parseByte(Integer.toString(currentIndex));
        }while(currentContent.length > blockSize);
        System.out.println(Arrays.toString(indexBlock));
        int freeIndex = findNextFree(0);
        setBlock(freeIndex, indexBlock);
        return freeIndex;
    }

    /***
     * Shows disk content in a formatted table
     */
    private static void show(){
        System.out.print("    ");
        for (int i = 0; i <= blockSize/10; i++) {
            System.out.print(i + "                   ");
        }
        System.out.println();
        System.out.print("    ");
        for (int i = 0; i < blockSize; i++) {
            System.out.print(i%10 + " ");
        }
        System.out.print("  taken");
        System.out.println();
        for (int i = 0; i < physicalDisk.length / blockSize; i++) {
            System.out.print((i<10 ? " " + i: i) + "  ");
            boolean iBlock = false;
            for(byte y : getBlock(i)) {
                char temp = (char)y;
                if (iBlock) {
                    System.out.print((int)temp + " ");
                    continue;
                }
                if(Character.isSpaceChar(temp)){ temp = ' '; }
                else if (temp == '\n'){ temp = '⏎'; }
                else if (temp == '\t'){ temp = '⇥';}
                else if (y == -2){
                    temp = '!';
                    iBlock = true;
                }
                System.out.print((y == 0 ? "_" : temp) + " ");
            }
            System.out.println("  " + blockTaken[i]);
        }
        System.out.println();
    }

    public static boolean run(){
        Scanner scan = new Scanner(System.in);
        System.out.print(">");
        String input = scan.nextLine();
        addContent(input.getBytes(), currentBlock);
        show();

        return true;
    }

    public static void test(ArrayList<String> args){

        String help = "DISK - tests if disk is working";
        if(args.size() != 1 && args.size() != 2 && args.size() != 3) {
            Utils.log("Wrong numbers of arguments");
            Shell.print(help);
        }
        else {
            if(args.size() == 1){
                try {
                    run();
                }
                catch (IndexOutOfBoundsException e) {
                    Utils.step("Disk out of bounds");
                }
            }
            else{
                String param = args.get(1);
                switch (param.toUpperCase()) {
                    case "CLEAR":
                        physicalDisk = new byte[1024];
                        blockTaken = new boolean[physicalDisk.length/blockSize];
                        break;
                    case "SHOW":
                        show();
                        break;
                    case "INSERT":
                        currentBlock = parseInt(args.get(2));
                        break;
                    case "NEWLINE":
                        addContent("Testowy string\n w nowej lini :)\n z\ttabem".getBytes(),10);
                        break;
                    case "GET":
                        byte[] temp;
                        temp = getBlockByIndex(Integer.parseInt(args.get(2)));
                        for(byte e:temp){
                            System.out.print((char)e);
                        }
                        break;
                    default:
                        Utils.log("Wrong argument");
                        Shell.print(help);
                        break;
                }
            }
        }
    }
}