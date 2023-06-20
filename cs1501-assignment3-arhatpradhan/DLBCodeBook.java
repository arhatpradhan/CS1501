/**
 * An implementation of CompressionCodeBookInterface using a DLB Trie.
 */

 public class DLBCodeBook implements CompressionCodeBookInterface {

  private static final int R = 256;        // alphabet size
  private DLBNode root;
  public StringBuilder currentPrefix;
  private DLBNode currentNode;
  private int W;       // current codeword width
  private int minW;    // minimum codeword width
  private int maxW;    // maximum codeword width
  private int L;       // maximum number of codewords with 
                       // current codeword width (L = 2^W)
  private int code;    // next available codeword value

  //creates the new codebook
  public DLBCodeBook(int minW, int maxW){
    this.maxW = maxW;
    this.minW = minW;
    currentPrefix = new StringBuilder(); 
    currentNode = null;
    initialize();
  }
  

  //TO DO: Modify the add methods to implement the codeword size incrementing logic
  public void add(String str, boolean flushIfFull){

    boolean haveRoom = false;
    if(root == null){   
      root = new DLBNode(str.charAt(0));
    }
    if(code < L){
      haveRoom = true;
    }
    if(haveRoom){
      if(str.length() > 0){
        add(root, code, str, 0);
      }
      code++;
    }
  }

  private void add(DLBNode node, int codeword, String word, int index){
    DLBNode current = node;
    char c = word.charAt(index);
    while(current != null){
      if(current.data == c){
        if(index == word.length() - 1){
          current.codeword = codeword;
        } else { //move down
          if(current.child == null){
            current.child = new DLBNode(word.charAt(index+1));
          }
          add(current.child, codeword, word, index+1);
        }
        break;
      } else {
        if(current.sibling == null){
          current.sibling = new DLBNode(c);
        }
        current = current.sibling;
      }
    }
  }

  public int getCodewordWidth(){
    return W;
  }
  //initializes the codebook
  private void initialize(){
    root = null;
    //starts with the min
    W = minW;
    //shifts the bits
    L = 1<<W;
    //sets the next codeword value
    code = 0;
    for (int i = 0; i < R; i++)
      add("" + (char) i, false);
    add("", false); //R is codeword for EOF
  }

  /**
   * appends the character c to the current prefix in O(alphabet size) time. 
   * This method doesn't modify the codebook.
   * @param c: the character to append
   * @return true if the current prefix after appending c is a word in
   * the codebook and false otherwise
   */

  public boolean advance(char c){
    boolean result = false;
    currentPrefix.append(c);
    if(currentNode == null){
      currentNode = root;
      while(currentNode != null){
        if(currentNode.data == c){
          result = true;
          break;
        }
        currentNode = currentNode.sibling;
      }
    } else {
      DLBNode curr = currentNode.child;
      while(curr != null){
        if(curr.data == c){
          currentNode = curr;
          result = true;
          break;
        }
        curr = curr.sibling;
      }
    }    
    return result;
  }
  
  public void add(boolean flushIfFull){

    boolean haveRoom = false;

    //code book is not full
    if(code < L){
      haveRoom = true;
    }else{
      //code book is full but less than max W then we can just increment the code
      //if we reached the max code book check what the user wants to do
      if(W == maxW){
        //2 options 
        //1 flushIfFull is true we dump everything and start with new codewords
        if(flushIfFull == true){
          initialize();
          haveRoom = true;
        }
        //2 flushIf Full is false
      //   if(flushIfFull == false){
      //       haveRoom = false;
      //   }
      // }
      
      // if(W < maxW){
      //     W++;
      //     L = (int) Math.pow(2, W);
      // }

      
    }

    //if there is room create the new node for the next char
    if(haveRoom){
      DLBNode newNode = 
        new DLBNode(currentPrefix.charAt(currentPrefix.length()-1));
      //set the new node codeword to the codeword value for the node
      newNode.codeword = code;
      code++;
      newNode.sibling = currentNode.child;
      currentNode.child = newNode;        
    }
    currentNode = null;
    currentPrefix = new StringBuilder();

    }
  }

  /**
   * retrieves the codeword corresponding to the current prefix in O(1) time. 
   * Useful in LZW compression.
   * @return the codeword corresponding to the current prefix
   */
  public int getCodeWord() {
    return currentNode.codeword;
  }

  //The DLB node class
  private class DLBNode{
    private char data;
    private DLBNode sibling;
    private DLBNode child;
    private Integer codeword;

    private DLBNode(char data){
        this.data = data;
        child = sibling = null;        
    }
  } 
}