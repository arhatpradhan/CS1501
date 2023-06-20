/**
 * An implementation of ExpansionCodeBookInterface using an array.
 */

public class ArrayCodeBook implements ExpansionCodeBookInterface {
    private static final int R = 256; // alphabet size
    private String[] codebook;
    private int W; // current codeword width
    private int minW; // minimum codeword width
    private int maxW; // maximum codeword width
    private int L; // maximum number of codewords with
                   // current codeword width (L = 2^W)
    private int code; // next available codeword value

    public ArrayCodeBook(int minW, int maxW) {
        this.maxW = maxW;
        this.minW = minW;
        initialize();
    }

    public int size() {
        return code;
    }

    // TODO: modify this to return the effective codword size so that the expand is
    // in sync with the compress
    public int getCodewordWidth(boolean flushIfFull) {
        // we should be returning in some cases W + 1
        // if the codebook is full right now return W + 1
        // if W is 16 go back to 9 or you have to use 16 stil

        // if codebook is full and W < maxW return W + 1
        // if (code == L && W < maxW) {
        //     return W + 1;
        // }
        // if (code == L && W == maxW) {

        //     if (flushIfFull == true)
        //         return minW;

        //     if (flushIfFull == false)
        //         return 16;
        // }
        return W;
    }

    private void initialize() {
        codebook = new String[1 << maxW];
        W = minW;
        L = 1 << W;
        code = 0;
        // initialize symbol table with all 1-character strings
        for (int i = 0; i < R; i++)
            add("" + (char) i, false);
        add("", false); // R is codeword for EOF
    }

    public void add(String str, boolean flushIfFull) {
        boolean haveRoom = false;

        if (code < L) {
            haveRoom = true;
        } else {
            // if we reached the max code book check what the user wants to do
            if (W == maxW) {
                // 2 options
                // 1 flushIfFull is true we dump everything and start with new codewords
                if (flushIfFull == true) {
                    initialize();
                    haveRoom = true;
                }
            //     // 2 flushIf Full is false
            //     if (flushIfFull == false) {
            //         haveRoom = false;
            //     }
            // }else if (W < maxW) {
            //     W++;
            //     L = (int) Math.pow(2, W);
            // }
            
         }

        if(haveRoom) {
            codebook[code] = str;
            code++;
        }
     }
    }
    public String getString(int codeword) {
        return codebook[codeword];
    }

}
