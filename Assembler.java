

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Coordinate the translation of MAL assembly code to text-based binary.
 * 
 * @author djr41
 * @version
 */
public class Assembler {
    // The lines of the input file.
    private List<String> input;
    // Where to write the output.
    private PrintWriter output;
    private HashMap<String, String> instructions;

    /**
     * Create an assembler.
     * 
     * @param inputfile  The input file.
     * @param outputfile The output file.
     */
    public Assembler(String inputfile, String outputfile) throws IOException {
        //GET INPUT
        input = Files.readAllLines(Paths.get(inputfile));
        //GET OUTPUT
        output = new PrintWriter(new FileWriter(outputfile));
        //PUT OPERANDS INTO A HASHMAP TO CALL IT EASILY
        instructions = new HashMap<String, String>();
        instructions.put("LOADN", "0000");
        instructions.put("LOADA", "0001");
        instructions.put("ADD", "0010");
        instructions.put("SUB", "0011");
        instructions.put("JMP", "0100");
        instructions.put("JGT", "0101");
        instructions.put("JLT", "0110");
        instructions.put("JEQ", "0111");
        instructions.put("COPY", "1000");
        instructions.put("STORE", "1001");
        instructions.put("A", "01");
        instructions.put("D", "10");
    }

    /**
     * Translate the input file, line by line.
     * 
     */
    public void assemble() {
        for (String line : input) {
            translateOneInstruction(line);
        }
        output.close();
    }

    /**
     * Translate one line of MAL assembly code to text-based binary.
     * 
     * @param line The line to translate.
     */
    private void translateOneInstruction(String line) {
        String instructionBinary;
        String noSpaces = line.replaceAll("\\s", "");

        String opcode = "";

        for (String key : instructions.keySet()) {
            if  (noSpaces.startsWith(key)) {
                opcode = key;
                noSpaces = noSpaces.substring(key.length());
                break;
            }
        }

        String operands = noSpaces;

        String operand1 = "";
        String operand2 = "";

        if (operands.indexOf(",") != -1) {
            operand1 = operands.substring(0, operands.indexOf(","));
            operand2 = operands.substring(operands.indexOf(",") + 1);
        } else {
            operand1 = operands;
        }
                                                                       
        // conditionals

        // case 1: if operands==null

        if (operand1 == null) {
            // handle this properly, described in brief ("Line error NNN") and exit
            // afterwards
            System.out.println("NNN");
        } else {

            // case 2: if first operand==num
            if (operand1.matches("[0-9]+")) {
                instructionBinary = instructions.get(opcode); // gain the opcodes key (binary val)
                while (instructionBinary.length() < 8) { // has to be 8 bits (adds padding until length = 8)
                    instructionBinary += "0";
                }
                output.println(instructionBinary); // print to output.txt
                String temp = Integer.toBinaryString(Integer.parseInt(operand1));
                while (temp.length() < 8) {
                    temp = "0" + temp;
                }
                output.println(temp); // print to output.txt
            } else if (operand1.equals("A") || operand1.equals("D")) { // else if operand is register
                instructionBinary = instructions.get(opcode);

                if (operand2.equals("A") || operand2.equals("D")) { // if first operand==reg1 (A or D)
                    String instruction = instructions.get(opcode); // get opcode
                    String reg1 = instructions.get(operand1); // get reg1
                    String reg2 = instructions.get(operand2); // get reg2
                    output.println(instruction + reg1 + reg2); // print to output.txt
                }

                // case 3b: if the 2nd operand==num
                if (operand2.matches("[0-9]+")) {
                    instructionBinary = instructions.get(opcode) + instructions.get(operand1);
                    while (instructionBinary.length() < 8) { // has to be 8 bits (adds padding until length = 8)
                        instructionBinary = instructionBinary + "0";
                    }
                    String num = Integer.toBinaryString(Integer.parseInt(operand2));
                    while (num.length() < 8) {
                        num = "0" + num;
                    }
                    output.println(instructionBinary); // print to output.txt
                    output.println(num); // print to output.txt
                }
            }
        }
    }
}