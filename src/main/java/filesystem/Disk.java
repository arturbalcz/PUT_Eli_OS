package filesystem;

import shell.Shell;
import utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class Disk {
    static byte physicalDisk[] = new byte[1024];
    static int blockSize = 32;
    static boolean blockTaken[] = new boolean[physicalDisk.length/blockSize];
    static int currentBlock = 0;

    public Disk(){
        System.out.println("Hello world!");
        Arrays.fill(physicalDisk, (byte)0);

    }

    public static byte[] getBlock(int index){
        byte[] temp = Arrays.copyOfRange(physicalDisk, index*blockSize, index*blockSize + blockSize);
        return temp;
    }

    public static void setBlock(int index, byte[] content){
        int max = content.length < blockSize ? blockSize : content.length;
        int currentIndex = index;
        for (int j = 0; j < content.length; j+=blockSize) {
            while(blockTaken[currentIndex + j/blockSize]){
                currentIndex++;
                if(currentIndex > blockTaken.length){
                    break;
                }
            }
            Utils.log(currentIndex + " ");
            blockTaken[currentIndex + j/blockSize] = true;
            for (int i = 0; i < max; i++) {
                if(i + j >= content.length) {
                    physicalDisk[currentIndex*blockSize + j + i] = 0;
                }
                else {
//                    while(blockTaken[currentIndex + (j+1)/blockSize]){
//                        currentIndex++;
//                    }

                    physicalDisk[currentIndex*blockSize + j + i] = content[i + j];
                }
            }

        }
    }

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
            for(byte y : getBlock(i)) {
                System.out.print((y == 0 ? "_" : (char)y) + " ");
            }
            System.out.println("  " + blockTaken[i]);
        }
        System.out.println();
    }

    public static boolean run(){
        Scanner scan = new Scanner(System.in);
        System.out.print(">");
        String input = scan.nextLine();
        setBlock(currentBlock, input.getBytes());
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
                    default:
                        Utils.log("Wrong argument");
                        Shell.print(help);
                        break;
                }
            }
        }
    }
}