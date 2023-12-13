import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

/**
 * @author syntex
 * comprehensive tester for the a3 assembler
 */
public class MALTester {
    @Test
    public void testADD() {
        File[] files = setup("ADD A,D");
        File output = files[1];
        List<String> lines = getLines(output);
        cleanup(files);
        assertEquals("00100110", lines.get(0));
    }

    @Test
    public void testSUB() {
        File[] files = setup("SUB A,D");
        File output = files[1];
        List<String> lines = getLines(output);
        cleanup(files);
        assertEquals("00110110", lines.get(0));
    }

    @Test
    public void testLOADN() {
        File[] files = setup("LOADN 3");
        List<String> lines = getLines(files[1]);
        cleanup(files);
        assertEquals("00000000", lines.get(0));
        assertEquals("00000011", lines.get(1));
    }

    @Test
    public void testLOADA() {
        File[] files = setup("LOADA 254");
        List<String> lines = getLines(files[1]);
        cleanup(files);
        assertEquals("00010000", lines.get(0));
        assertEquals("11111110", lines.get(1));
    }

    @Test
    public void testJLT() {
        File[] files = setup("JLT A,3");
        List<String> lines = getLines(files[1]);
        cleanup(files);
        assertEquals("01100100", lines.get(0));
        assertEquals("00000011", lines.get(1));
    }

    @Test
    public void testJEQ() {
        File[] files = setup("JEQ A,3");
        List<String> lines = getLines(files[1]);
        cleanup(files);
        assertEquals("01110100", lines.get(0));
        assertEquals("00000011", lines.get(1));
    }

    @Test
    public void testJGT() {
        File[] files = setup("JGT A,3");
        List<String> lines = getLines(files[1]);
        cleanup(files);
        assertEquals("01010100", lines.get(0));
        assertEquals("00000011", lines.get(1));
    }

    @Test
    public void testCOPY() {
        File[] files = setup("COPY D,A");
        List<String> lines = getLines(files[1]);
        cleanup(files);
        assertEquals("10001001", lines.get(0));
    }

    @Test
    public void testJMP() {
        File[] files = setup("JMP 9");
        List<String> lines = getLines(files[1]);
        cleanup(files);
        assertEquals("01000000", lines.get(0));
        assertEquals("00001001", lines.get(1));
    }

    @Test
    public void testSTORE() {
        File[] files = setup("STORE 16");
        List<String> lines = getLines(files[1]);
        cleanup(files);
        assertEquals("10010000", lines.get(0));
        assertEquals("00010000", lines.get(1));
    }


    @Test
    public void testADDObfuscated() {
        File[] files = setup("  ADD A,    D");
        File output = files[1];
        List<String> lines = getLines(output);
        cleanup(files);
        assertEquals("00100110", lines.get(0));
    }

    @Test
    public void testSUBObfuscated() {
        File[] files = setup("  SUB A,  D");
        File output = files[1];
        List<String> lines = getLines(output);
        cleanup(files);
        assertEquals("00110110", lines.get(0));
    }

    @Test
    public void testLOADNObfuscated() {
        File[] files = setup("  LOADN   3 ");
        List<String> lines = getLines(files[1]);
        cleanup(files);
        assertEquals("00000000", lines.get(0));
        assertEquals("00000011", lines.get(1));
    }

    @Test
    public void testLOADAObfuscated() {
        File[] files = setup("  LOADA    254    ");
        List<String> lines = getLines(files[1]);
        cleanup(files);
        assertEquals("00010000", lines.get(0));
        assertEquals("11111110", lines.get(1));
    }

    @Test
    public void testJLTObfuscated() {
        File[] files = setup("  JLT  A ,   3 ");
        List<String> lines = getLines(files[1]);
        cleanup(files);
        assertEquals("01100100", lines.get(0));
        assertEquals("00000011", lines.get(1));
    }

    @Test
    public void testJEQObfuscated() {
        File[] files = setup("  JEQ A   ,  3");
        List<String> lines = getLines(files[1]);
        cleanup(files);
        assertEquals("01110100", lines.get(0));
        assertEquals("00000011", lines.get(1));
    }

    @Test
    public void testJGTObfuscated() {
        File[] files = setup("  JGT A ,    3");
        List<String> lines = getLines(files[1]);
        cleanup(files);
        assertEquals("01010100", lines.get(0));
        assertEquals("00000011", lines.get(1));
    }

    @Test
    public void testCOPYObfuscated() {
        File[] files = setup("  COPY D, A");
        List<String> lines = getLines(files[1]);
        cleanup(files);
        assertEquals("10001001", lines.get(0));
    }

    @Test
    public void testJMPObfuscated() {
        File[] files = setup("  JMP 9 ");
        List<String> lines = getLines(files[1]);
        cleanup(files);
        assertEquals("01000000", lines.get(0));
        assertEquals("00001001", lines.get(1));
    }

    @Test
    public void testSTOREObfuscated() {
        File[] files = setup("  STORE 16    ");
        List<String> lines = getLines(files[1]);
        cleanup(files);
        assertEquals("10010000", lines.get(0));
        assertEquals("00010000", lines.get(1));
    }

    @Test
    public void testAllRegisters() {
        List<String> instructions = new ArrayList<>();

        instructions.add("ADD A,D");
        instructions.add("LOADN 64");
        instructions.add("LOADA 192");
        instructions.add("JLT D,3");
        instructions.add("COPY D,A");
        instructions.add("JMP 9");
        instructions.add("STORE 254");

        instructions.add("JEQ A,3");
        instructions.add("JGT A,3");
        instructions.add("SUB D,A");

        String content = instructions.stream().reduce("", (a, b) -> a + System.lineSeparator() + b);
        File[] files = setup(content);
        List<String> lines = getLines(files[1]);

        //rename the files
        File file = files[0];
        File output = files[1];

        File expectedFile = new File("testAllRegistersExpected.bin");
        File expectedMalFile = new File("testAllRegisters.mal");

        if (expectedFile.exists()) {
            expectedFile.delete();
        }

        if (expectedMalFile.exists()) {
            expectedMalFile.delete();
        }

        file.renameTo(expectedFile);
        output.renameTo(expectedMalFile);

        int pc = 0;

        assertEquals(lines.get(pc++), "00100110"); // ADD A,D

        assertEquals(lines.get(pc++), "00000000"); // LOADN 64
        assertEquals(lines.get(pc++), "01000000");

        assertEquals(lines.get(pc++), "00010000"); // LOADA 192
        assertEquals(lines.get(pc++), "11000000");

        assertEquals(lines.get(pc++), "01101000"); // JLT D,3
        assertEquals(lines.get(pc++), "00000011");
        
        assertEquals(lines.get(pc++), "10001001"); // COPY D,A

        assertEquals(lines.get(pc++), "01000000"); // JMP 9
        assertEquals(lines.get(pc++), "00001001");

        assertEquals(lines.get(pc++), "10010000"); // STORE 254
        assertEquals(lines.get(pc++), "11111110");

        assertEquals(lines.get(pc++), "01110100"); // JEQ A,3
        assertEquals(lines.get(pc++), "00000011");

        assertEquals(lines.get(pc++), "01010100"); // JGT A,3
        assertEquals(lines.get(pc++), "00000011");

        assertEquals(lines.get(pc++), "00111001"); // SUB D,A
    }

    @Test
    public void testAllRegistersObfuscated() {
        List<String> instructions = new ArrayList<>();

        instructions.add("ADD      A,D      ");
        instructions.add("    LOADN  64 ");
        instructions.add(" LOADA    192 ");
        instructions.add("  JLT    D,     3 ");
        instructions.add("COPY    D   ,A"   );
        instructions.add("  JMP 9");
        instructions.add("STORE     254 ");

        instructions.add("JEQ   A       ,  3");
        instructions.add("  JGT A   ,  255   ");
        instructions.add("  SUB D,  A   ");

        String content = instructions.stream().reduce("", (a, b) -> a + System.lineSeparator() + b);
        File[] files = setup(content);
        List<String> lines = getLines(files[1]);

        //rename the files
        File file = files[0];
        File output = files[1];

        //delete the files if they exist

        File expectedFile = new File("testAllRegistersObfuscatedExpected.bin");
        File expectedMalFile = new File("testAllRegistersObfuscated.mal");

        if (expectedFile.exists()) {
            expectedFile.delete();
        }

        if (expectedMalFile.exists()) {
            expectedMalFile.delete();
        }

        file.renameTo(expectedFile);
        output.renameTo(expectedMalFile);

        int pc = 0;

        assertEquals(lines.get(pc++), "00100110"); // ADD A,D

        assertEquals(lines.get(pc++), "00000000"); // LOADN 64
        assertEquals(lines.get(pc++), "01000000");

        assertEquals(lines.get(pc++), "00010000"); // LOADA 192
        assertEquals(lines.get(pc++), "11000000");

        assertEquals(lines.get(pc++), "01101000"); // JLT D,3
        assertEquals(lines.get(pc++), "00000011");
        
        assertEquals(lines.get(pc++), "10001001"); // COPY D,A

        assertEquals(lines.get(pc++), "01000000"); // JMP 9
        assertEquals(lines.get(pc++), "00001001");

        assertEquals(lines.get(pc++), "10010000"); // STORE 254
        assertEquals(lines.get(pc++), "11111110");

        assertEquals(lines.get(pc++), "01110100"); // JEQ A,3
        assertEquals(lines.get(pc++), "00000011");

        assertEquals(lines.get(pc++), "01010100"); // JGT A,3
        assertEquals(lines.get(pc++), "11111111");

        assertEquals(lines.get(pc++), "00111001"); // SUB D,A
    }

    private File[] setup(String line) {
        File file = makeFile(line);
        File output = new File(file.getName().replace(".mal", ".bin"));
        Main.main(new String[] { file.getName() });
        return new File[] { file, output };
    }

    private void cleanup(File[] files) {
        for (File file : files) {
            if (file != null && file.exists()) {
                System.out.println("Deleting file: " + file.getName());
                file.delete();
            }
        }
    }

    private List<String> getLines(File file) {
        List<String> lines = null;
        try {
            lines = java.nio.file.Files.readAllLines(file.toPath());
        } catch (Exception e) {
            System.out.println("Error reading file");
        }
        return lines;
    }

    private File makeFile(String line) {
        File file = new File(UUID.randomUUID().toString() + ".mal");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println("Error creating file");
            }
        }

        List<String> lines = Arrays.asList(line);
        try {
            java.nio.file.Files.write(file.toPath(), lines);
        } catch (Exception e) {
            System.out.println("Error writing to file");
        }

        return file;
    }
}
