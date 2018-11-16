package assembler;

//import processess.PCB;
//import utils.Utils;

import shell.Shell;

import java.util.Arrays;

public class Sandbox {
    public static void main(String[] args) {

        String msg = Shell.read();
        System.out.println(msg);
      /*  System.out.println("assembler sandbox environment");
        Utils.step("assembler sandbox environment started");

        Assembler a = new Assembler();

        Assembler.cpu.print();


        // "MOV CX 01H\nMOV BX 01H\nADD CX BX";
//                "MOV AL 0CH\nMOV BX 00H\nMOV CX 01H\n" +
//                "loop: MOV DX CX\n" +
//                      "ADD CX BX\n" +
//                      "MOV BX DX\n" +
//                      "DEC AL\n" +
//                      "JNZ loop\n" +
//                "PRT CX";//"PRT \"Hello World!\"";//"MOV AX 41H\nMOV BX 1AH\nloop: PRT AX\nINC AX\nDEC BX\nJNZ loop";//loop: DEC AL\nINC BL\nJNZ loop"; //\"MOV AL 01H\nMOV BL 0AH\nADD AL BL\nDEC AL\nNOP";
        String program = "MOV AL 05H\nMOV BX AX\nDEC AL\nloop: MUL BX AL\nDEC AL\nJNZ loop";
        Utils.log(program);
        final byte[] bin = program.getBytes();
        final byte[] exec = a.compile(bin);
        Utils.log(Arrays.toString(exec));
        if(exec != null) {
            PCB process = new PCB(1,"p1", 10, exec);
            //noinspection StatementWithEmptyBody
            while(process.execute());
        }

        Assembler.cpu.print();

        Utils.step("assembler sandbox environment closed");
        Utils.closeLogs();
    */}
}
