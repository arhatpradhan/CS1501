/**
 * An implementation of the AutoCompleteInterface using a DLB Trie.
 */


 public class AutoComplete implements AutoCompleteInterface {

  private DLBNode root;
  private StringBuilder currentPrefix;
  private DLBNode currentNode;
  //TODO: Add more instance variables if you need to
  private DLBNode prevNode;
  private int prevPos;
  private StringBuilder prediction;
  

  public AutoComplete(){
    root = null;
    currentPrefix = new StringBuilder();
    currentNode = null;
    prevPos = -1;
  }

  /**
   * Adds a word to the dictionary in O(word.length()) time
   * @param word the String to be added to the dictionary
   * @return true if add is successful, false if word already exists
   * @throws IllegalArgumentException if word is the empty string
   */
    public boolean add(String word) throws IllegalArgumentException{
      //TODO: implement this method

      //if word is null throw a illegal Argument 
      if(word == null || word == "") throw new IllegalArgumentException();
      //keep track of currentNode by keeping at the end of the string
      prevNode = root;
      currentNode = getNode(root, word, 0);
      //check if the current string is already a word
      if(isWord()) return false;
      //new stringBuilder to add word
      StringBuilder addWord = new StringBuilder(word);
    
      ///add to the dictionary
      if(addWord(root, addWord, 0)){
        return true;  
      }
      return false;
    } 

    private boolean addWord(DLBNode x, StringBuilder String, int pos){
    //if we have reached the end of the word return true 
    if(pos == String.length()){
      prevNode.isWord = true;
      return true;
    }
    //if currentNode is null then create a new node
    if(x == null){
       //create new node at currentNode
       x = new DLBNode(String.charAt(pos));
       //check if we are at the not at the last char keep going
        if(pos < String.length()){
              if(pos == prevPos){
                prevNode.nextSibling = x;
                x.parent = prevNode.parent;
                x.previousSibling = prevNode;
              }else{
                //if root is null then set root as the first node added
                if(root == null) root = x;
                 else{
                 //sets the previous node child to the current node
                 prevNode.child = x;  
                 //set current node parent to the previous node
                 x.parent = prevNode;
               }
              }
           //increment size
           x.size++;
           prevNode = x;
           prevPos = pos;
           if(addWord(x.child, String, ++pos)) return true;
          }
        }else if(x.data == String.charAt(pos)){
            if(pos < String.length()){
              if(root == x){ 
                prevPos = pos;
                x.size++;
                if(addWord(x.child, String, ++pos)) return true; 
              } 
              prevNode = x;
              prevPos = pos;
              x.size++;
              if(addWord(x.child, String, ++pos)) return true; 
            }
        }else{
      prevNode = x;
      prevPos = pos;
			if(addWord(x.nextSibling, String, pos)){
        return true;
      }
    }
     return false;
    }


  /**
   * appends the character c to the current prefix in O(1) time. This method 
   * doesn't modify the dictionary.
   * @param c: the character to append
   * @return true if the current prefix after appending c is a prefix to a word 
   * in the dictionary and false otherwise
   */
    public boolean advance(char c){
      //TODO: implement this method
      //true if the curr prefix after c is a prefix to a word in the dict 
      currentPrefix.append(c);
      if(root != null){
        currentNode = getNode(root, currentPrefix.toString(), 0);
      }
      //if it is not a word, it is a prefix and currentNode.child is not null return true also check the sibling
      if(!isWord()){
        if(currentNode == null) return false;
        if(currentNode.child != null) return true; 
        else if(currentNode.nextSibling != null ) return true;
      }
      if(isWord()) return true;
      return false;
    }

    public void appendChar(char c){
      //TODO: implement this method 
      prediction.append(c);
      
    }

  /**
   * removes the last character from the current prefix in O(1) time. This 
   * method doesn't modify the dictionary.
   * @throws IllegalStateException if the current prefix is the empty string
   */
    public void retreat() throws IllegalStateException{
      //TODO: implement this method
      if(currentPrefix == null || currentPrefix.length() == 0) throw new IllegalStateException();
      currentPrefix = currentPrefix.deleteCharAt(currentPrefix.length() - 1);
      currentNode = getNode(root, currentPrefix.toString(), 0);
    }

  /**
   * resets the current prefix to the empty string in O(1) time
   */
    public void reset(){
      //TODO: implement this method
      currentPrefix.setLength(0);
    }
    
  /**
   * @return true if the current prefix is a word in the dictionary and false
   * otherwise
   */
    public boolean isWord(){
    //TODO: implement this method
      if(currentNode == null) return false; 
        else if(currentNode.isWord){
          return true;  
        }
    
      return false;
    }
    

  /**
   * adds the current prefix as a word to the dictionary (if not already a word)
   * The running time is O(length of the current prefix). 
   */
    public void add(){
      //TODO: implement this method
      //check if it is a word already, if it is a word then return 
      add(currentPrefix.toString());
      currentNode = getNode(root, currentPrefix.toString(), 0);
    }


  /** 
   * @return the number of words in the dictionary that start with the current 
   * prefix (including the current prefix if it is a word). The running time is 
   * O(1).
   */
    public int getNumberOfPredictions(){
      //TODO: implement this method
      if(currentNode != null) return currentNode.size;
      
      return 0;
      
    }
  
  /**
   * retrieves one word prediction for the current prefix. The running time is 
   * O(prediction.length()-current prefix.length())
   * @return a String or null if no predictions exist for the current prefix
   */
    public String retrievePrediction(){
      //TODO: implement this method
      if(currentNode == null ) return null;

      
      DLBNode temp = currentNode;
      prediction = new StringBuilder("");
      //append the current prefix to the the prediction
      prediction.append(currentPrefix.toString()); 

      //if the prediction is already a valid word just return that
      if(isWord()) return prediction.toString();
      //if not a valid word then 
      //if the currentNode's child is not null check if it is a word else keep going
     if(temp.child != null){
        while(temp.isWord != true){
          appendChar(temp.child.data);
          temp = temp.child;
        }
        return prediction.toString();
      }else if(temp.nextSibling != null){
        appendChar(temp.nextSibling.data);
        return prediction.toString();
      }
     
      return null;
    }
  /* ==============================
   * Helper methods for debugging.
   * ==============================
   */

  //print the subtrie rooted at the node at the end of the start String
  public void printTrie(String start){
    System.out.println("==================== START: DLB Trie Starting from \""+ start + "\" ====================");
    if(start.equals("")){
      printTrie(root, 0);
    } else {
      DLBNode startNode = getNode(root, start, 0);
      if(startNode != null){
        printTrie(startNode.child, 0);
      }
    }
    
    System.out.println("==================== END: DLB Trie Starting from \""+ start + "\" ====================");
  }

  //a helper method for printTrie
  private void printTrie(DLBNode node, int depth){
    if(node != null){
      for(int i=0; i<depth; i++){
        System.out.print(" ");
      }
      System.out.print(node.data);
      if(node.isWord){
        System.out.print(" *");
      }
      System.out.println(" (" + node.size + ")");
      printTrie(node.child, depth+1);
      printTrie(node.nextSibling, depth);
    }
  }

  //return a pointer to the node at the end of the start String.
  private DLBNode getNode(DLBNode node, String start, int index){
    if(start.length() == 0){
      return node;
    }
    DLBNode result = node;
    if(node != null){
      if((index < start.length()-1) && (node.data == start.charAt(index))) {
          result = getNode(node.child, start, index+1);
      } else if((index == start.length()-1) && (node.data == start.charAt(index))) {
          result = node;
      } else {
          result = getNode(node.nextSibling, start, index);
      }
    }
    return result;
  } 

  //The DLB node class
  private class DLBNode{
    private char data;
    private int size;
    private boolean isWord;
    private DLBNode nextSibling;
    private DLBNode previousSibling;
    private DLBNode child;
    private DLBNode parent;

    private DLBNode(char data){
        this.data = data;
        size = 0;
        isWord = false;
        nextSibling = previousSibling = child = parent = null;
    }
  }
}
