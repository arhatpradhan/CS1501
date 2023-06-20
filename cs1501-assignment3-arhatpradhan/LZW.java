/*************************************************************************
 * Compilation: javac LZWmod.java
 * Execution: java LZWmod - < input.txt > output.lzw (compress input.txt
 * into output.lzw)
 * Execution: java LZWmod + < output.lzw > input.rec (expand output.lzw
 * into input.rec)
 * Dependencies: BinaryStdIn.java BinaryStdOut.java
 *
 * Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/
// TO DO: modify so that the codebooks are initialized with the correct minimum
// and maximum codeword sizes
public class LZW {
    private static final int R = 256; // alphabet size
    private static boolean flushIfFull = false;

    public static void compress() {
        // creates new compression
        // with min and max with 12

        // TO DO: min should be 9, max should be 16
        CompressionCodeBookInterface codebook = new DLBCodeBook(12, 12);

        // reads the next character
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            // advance appends to the currentprefix
            // does not change the actual codebook
            // return true if after adding the char if it is in the codebook
            // false otherwise
            if (!codebook.advance(c)) { // found longest match
                int codeword = codebook.getCodeWord();
                BinaryStdOut.write(codeword, codebook.getCodewordWidth());
                // checks if there is room
                codebook.add(flushIfFull);
                // advances if there is room
                codebook.advance(c);
            }
        }
        // if the codeword is already there just set it to that
        int codeword = codebook.getCodeWord();
        // write out the codeword
        BinaryStdOut.write(codeword, codebook.getCodewordWidth());

        BinaryStdOut.write(R, codebook.getCodewordWidth());
        BinaryStdOut.close();
    }

    public static void expand() {
        ExpansionCodeBookInterface codebook = new ArrayCodeBook(12, 12);

        //read 1st bit 
        flushIfFull = BinaryStdIn.readBoolean();

        int codeword = BinaryStdIn.readInt(codebook.getCodewordWidth(flushIfFull));
        String val = codebook.getString(codeword);

        while (true) {
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(codebook.getCodewordWidth(flushIfFull));

            if (codeword == R)
                break;
            String s = codebook.getString(codeword);
            if (codebook.size() == codeword)
                s = val + val.charAt(0); // special case hack
            
            codebook.add(val + s.charAt(0), flushIfFull);
            val = s;

        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
          if (args[0].equals("-")) {

            // user input for reset or no
            if (args[1].equals("r"))
                flushIfFull = true;
            if (args[1].equals("n"))
                flushIfFull = false;
  
            // write the first bit
            BinaryStdOut.write(flushIfFull);
            compress();

        } else if (args[0].equals("+"))
            expand();
        else
            throw new RuntimeException("Illegal command line argument");

    }

}
