import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;
public class SmallVM {

    // memory
    private static Object[] memory = new Object[500];
    // registers
    private static Integer pc;
    private static String stringcumulator; // it's an accumulator for strings

    public static void main(String[] args) {
        String filename = "src/test.asm";
        try { // load the program to memory and catch FileNotFoundException
            load_program(filename);
        } catch (FileNotFoundException e) {
            System.err.println(e);
            System.exit(1);
        }
        fetchExecute(pc);
    }

    public static void load_program(String filename) throws FileNotFoundException {
        Scanner scnr = new Scanner(new File(filename));
        pc = 1; // use program counter to iterate along file (im using pc here to save resources)
        while (scnr.hasNext()) {
            memory[pc] = scnr.nextLine(); // save line in memory
            if (memory[pc].toString().charAt(0) == ';') { // skip lines starting with ';'
                continue;
            }
            pc++; // iterate pc
        }
        memory[0] = pc;
        pc = 0;
    }

    public static void fetchExecute(int startIndex) {
        pc = startIndex + 1;
        // this is the fetch execute cycle, all opcodes must be handled here
        // the first line of a program (startIndex) stores the first memory location after the program
        while (pc < (int) memory[startIndex]) {
            executeLine(new Scanner(memory[pc].toString())); // create a scanner and pass it into the executeLine() method
            pc++;
        }
    }

    public static void executeLine(Scanner cmd) {
        String opcode = cmd.next();
        switch (opcode) { // opcode switch
            case "ADD"  -> ADD(cmd);
            case "DIV"  -> DIV(cmd);
            case "HALT" -> HALT();
            case "IN"   -> IN(cmd);
            case "MUL"  -> MUL(cmd);
            case "OUT"  -> OUT(cmd);
            case "STO"  -> STO(cmd);
            case "SUB"  -> SUB(cmd);
            case "DUMP" -> dumpmem();
            default     -> throw new SyntaxErrorException("Opcode " + opcode + " not supported");
        }
    }

    // methods corresponding to opcodes
    private static void SUB(Scanner cmd) {
        String destination = cmd.next();
        changeValue(destination, (getValue(cmd.next()) - getValue(cmd.next())));
    }
    private static void MUL(Scanner cmd) {
        String destination = cmd.next();
        changeValue(destination, (getValue(cmd.next()) * getValue(cmd.next())));
    }
    private static void DIV(Scanner cmd) {
        String destination = cmd.next();
        changeValue(destination, (getValue(cmd.next()) / getValue(cmd.next())));
    }
    private static void ADD(Scanner cmd) {
        String destination = cmd.next();
        changeValue(destination, (getValue(cmd.next()) + getValue(cmd.next())));
    }

    private static void STO(Scanner cmd) {
        String destination = cmd.next();
        if (identifierExists(destination)) {
            changeValue(destination, getValue(cmd.next()));
            return;
        }
        int i = (int) memory[0];
        while (memory[i] != null) {i++;} // move to free memory space
        // cmd.next is destination, and cmd.nextInt is value
        memory[i] = destination + " " + getValue(cmd.next());
    }

    private static void OUT(Scanner cmd) {
        stringcumulator = cmd.next();
        if (stringcumulator.charAt(0) == (char) 34) { // if the first character is " then we print the string literal
            stringcumulator += " "; // scanner ignores spaces, so we need to put those back in
            while (cmd.hasNext()) { // accumulate the text in the string literal
                stringcumulator += cmd.next() + " ";
            }
            // print string literal - quotation marks
            System.out.println(stringcumulator.substring(1, stringcumulator.length()-2));
        }
        else {
            try {
                System.out.println(getValue(stringcumulator));
            } catch (IdentifierNotFoundException e) {
                System.err.println(e);
            }
        }
    }

    private static void IN(Scanner cmd) {
        Scanner scnr = new Scanner(System.in);
        changeValue(cmd.next(), scnr.nextInt());
    }

    private static void HALT() {
        System.exit(0);
    }

    // methods for dealing with identifiers
    private static int getValue(String token) throws IdentifierNotFoundException {
        // check if first character of token is a number, if it is return it as int
        if (token.getBytes()[0] >= 48 && token.getBytes()[0] <= 57) { // 47 < all int chars < 58 in ASCII
            return new Scanner(token).nextInt(); // idk how else to do it
        }
        for (int i = (int) memory[0]; memory[i] != null; i++) { // iterate along vitual memory
            Scanner scnr = new Scanner(memory[i].toString());
            if (scnr.next().equals(token)) { // compare against all stored identifiers
                return scnr.nextInt();
            }
        }
        throw new IdentifierNotFoundException("identifier " + token + " not found");
    }

    private static void changeValue(String identifier, int value) throws IdentifierNotFoundException {
        int i;
        for (i = (int) memory[0]; memory[i] != null; i++) { // iterate along virtual memory
            Scanner scnr = new Scanner(memory[i].toString());
            if (scnr.next().equals(identifier)) { // compare against all stored identifiers
                memory[i] = identifier + " " + value; // store that value
                return;
            }
        }
        memory[i] = identifier + " " + value; // if identifier is not found, it will store a new one
    }

    private static boolean identifierExists(String identifier) {
        for(int i = (int) memory[0]; memory[i] != null; i++) {
            Scanner scnr = new Scanner(memory[i].toString());
            if (Objects.equals(scnr.next(), identifier)) { // just comparing identifier against stored identifiers
                return true;
            }
        }
        return false;
    }
    private static void dumpmem() {
        System.out.println("\u001B[32m############ Dumping memory ############\u001B[0m");
        for (Object x: memory) { // use this to dump memory to STDOUT
            System.out.println(x);
        }
    }
}
