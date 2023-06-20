import java.io.*;
import java.util.*;

public final class Crossword implements WordPuzzleInterface {

    //create an empty board
    private char[][] board;
    //create a varible for the dictionary
    private DictInterface dictionary;

    //keeping track words of row and col
    private StringBuilder[] colStr;
    private StringBuilder[] rowStr;
    //keeping track of last position
    private int[] rowlastMinusPos;
    private int[] colLastMinusPos;
    //depth of recursion
    private int depth = 0;

    //main method
    public static void main(String[] args){
      String dictionary = args[0];
      String file = args[1];

      Crossword CW = new Crossword();
    }
    //String dict, String file
    public Crossword(){

  }
    //if solution is found
  private boolean solve(int row, int col, int depth){
      //filling the board
      // for (int i = 0 ; i < depth ; i++ ) {
      //  System.out.println(" ");
      // }
     // System.out.println(depth);
     // System.out.println(row + " , " + col + " " + rowStr[row].toString() + "  " + colStr[col].toString());
      //1st fill puzzle
      boolean found= false;
      //for each cell in the crossword puzzle
      System.out.println();
      switch(board[row][col]){
          case '+':
          //go through each letter in the crossword puzzle;
            for(char c = 'a'; c <= 'z' && !found; c++){
              //check if the letter can be place in the cell
              // System.out.println("in 109");
              if(isValid(row, col, c)){
                  //if in last last spot then it is a true solution
                  //base case  ;
                  if(row == board.length -1 && col == board.length -1){
                    colStr[col].append(c);
                    rowStr[row].append(c);

                     found = true;

                    return true;
                  }else{
                    colStr[col].append(c);
                    rowStr[row].append(c);

                   System.out.println();
                   StringBuilder rowStrBuild = new StringBuilder();
                   rowStrBuild = rowStr[row];
                   System.out.println("Final Append Row: " + rowStrBuild.toString());

                   StringBuilder colStrBuild = new StringBuilder();
                   colStrBuild = colStr[col];
                   System.out.println("Final Append Col: " + colStrBuild.toString());
                   System.out.println();
                  }
                    //moving onto the next cells
                    colLastMinusPos[col] = col;
                    rowlastMinusPos[row] = row;

                    //keeping track if last col was solved
                    boolean Sol = false;

                    //if the col is ending move to next row
                    if(col == board.length - 1){
                       found = solve(row + 1 , 0, depth + 1);
                    }else{
                      found = solve(row, col + 1, depth + 1);
                    }
                    //if the next cell returns that no solution is found, we need to unplace the letter
                    if(found == false){

                      int rLast = rowlastMinusPos[row];
                      StringBuilder rowStrBuild = new StringBuilder();

                      int cLast = colLastMinusPos[col];
                      StringBuilder colStrBuild = new StringBuilder();

                      //if on the last row, we should delete the string for both the col and the row

                      //delete last character for row
                      //stores last spot of char
                      rowStrBuild = rowStr[rLast];
                      rowStrBuild.deleteCharAt(rowStrBuild.length() - 1);
                      rowStr[rLast] = rowStrBuild;

                      colStrBuild = colStr[cLast];
                      colStrBuild.deleteCharAt(colStrBuild.length() - 1);
                      colStr[cLast] = colStrBuild;


                    }
                    //if the next cell returns that a solution was found then return true
                    if(found == true){
                      return true;
                  }
              }//end switch case
            //otherwise if more letters to try
          if(c <= 'z'){
            continue;
          }else{
            break;
          }
        }//end for loop
              break;
          case '-':
            //move on to next cell
            //if the col is ending move to next row
            boolean Sol2 = false;
            if(col == board.length - 1){
               Sol2 = solve(row + 1 , 0, depth + 1);
            }else{
              Sol2 = solve(row, col + 1, depth + 1);
            }

            //if the solution is false, inform previous cell that solution was not found
            //else if the solution is found, inform previous cell that the solution was found
            if (Sol2 == false) {
              found = false;
            }else{
              found = true;
            }

             break;

          default:
          //place the letter
        if(isValid(row, col, board[row][col])) {
          colStr[col].append(board[row][col]);
          rowStr[row].append(board[row][col]);

          if(col == board.length - 1){
             found = solve(row + 1 , 0, depth + 1);
          }else{
            found = solve(row, col + 1, depth + 1);
          }
          //if the next cell returns that no solution is found, we need to unplace the letter
          if(found == false){
            int rLast = rowlastMinusPos[row];
            StringBuilder rowStrBuild = new StringBuilder();

            int cLast = colLastMinusPos[col];
            StringBuilder colStrBuild = new StringBuilder();

            //if on the last row, we should delete the string for both the col and the row

            //delete last character for row
            //stores last spot of char
            rowStrBuild = rowStr[rLast];
            rowStrBuild.deleteCharAt(rowStrBuild.length() - 1);
            rowStr[rLast] = rowStrBuild;

            colStrBuild = colStr[cLast];
            colStrBuild.deleteCharAt(colStrBuild.length() - 1);
            colStr[cLast] = colStrBuild;
          }
          //if the next cell returns that a solution was found then return true
          if(found == true){
            return true;
        }
      }
            break;
      }//end switch statement
      return false;
    }

    private boolean isValid(int row, int col, char c){

     int currRow = row;
     int currCol = col;

    StringBuilder rowStrBuild = new StringBuilder();
    rowStrBuild = rowStr[currRow];

    StringBuilder colStrBuild = new StringBuilder();
    colStrBuild = colStr[currCol];

    if(rowStrBuild.length() > board.length || colStrBuild.length() > board.length){
      return false;
    }

    rowStrBuild.append(c);
    colStrBuild.append(c);

    //check also depends on whether the cell is followed by a filled cell
    //before a filled cell
    //the row segment starting from the previous filled cell(if any) has to be a word
    if(currCol != 0 && board[currRow][currCol - 1] == '-' ){
      int r = dictionary.searchPrefix(colStrBuild);
      if(r == 2 || r == 1){
        return true;
      }
    }

     boolean unfilledRow = false;
     boolean unfilledCol = false;
     //if it is not at the end of the current row, row so far has to be a prefix
	   if(currCol != board.length - 1){
       int r = dictionary.searchPrefix(rowStrBuild);
       // so if it is a prefix, set true
       if(r == 1 || r == 3 ){
         unfilledRow = true;
       }
     }
     //if not at the end of current col, col so far has to be a prefix
     if(currRow != board.length -1){
       int r = dictionary.searchPrefix(colStrBuild);
       // System.out.println(r2);
       if(r == 1 || r == 3 ){
         unfilledCol = true;
       }
     }


     //if at the end of the row and col both have to be complete words
     boolean filledRow = false;
     boolean checkCol = false;

     //if at the end of the row, the row has to be a complete word and col has to be a prefix
     if(currCol == board.length - 1){
       int r = dictionary.searchPrefix(rowStrBuild);
       System.out.println(r);
      //if a prefix and word
      if(r == 2 || r == 3){
        filledRow = true;
      }
      System.out.println(filledRow);
    }
    int r2 = dictionary.searchPrefix(colStrBuild);
    if(r2 == 1 || r2 == 3){
      checkCol = true;
    }

    //if at the end of the col, the col has to be a complete word and row has to be a prefix
    boolean filledCol = false;
    boolean checkRow = false;
    if(currRow == board.length -1){
      int r = dictionary.searchPrefix(colStrBuild);
      if(r == 2 || r == 3){
        filledCol = true;
      }
    } int r3 = dictionary.searchPrefix(rowStrBuild);
    if(r3 == 1 || r3 == 3){
      checkRow = true;
    }

    //if at the last spot row and col have to be words
    if(currCol == board.length - 1 && currRow == board.length -1){
      if (filledCol == true && filledRow == true){
        rowStrBuild.deleteCharAt(rowStrBuild.length() - 1);
        colStrBuild.deleteCharAt(colStrBuild.length() - 1);
        return true;
      }else{
      rowStrBuild.deleteCharAt(rowStrBuild.length() - 1);
      colStrBuild.deleteCharAt(colStrBuild.length() - 1);
      return false;
      }
    }
    //1st check
    if(unfilledRow == true && unfilledCol == true) {
      rowStrBuild.deleteCharAt(rowStrBuild.length() - 1);
      colStrBuild.deleteCharAt(colStrBuild.length() - 1);
      return true;
    }
    //2nd check
    if(filledRow == true && checkCol == true){
      rowStrBuild.deleteCharAt(rowStrBuild.length() - 1);
      colStrBuild.deleteCharAt(colStrBuild.length() - 1);
      return true;
    }

    //3rd check
    if(filledCol == true && checkRow == true){
      rowStrBuild.deleteCharAt(rowStrBuild.length() - 1);
      colStrBuild.deleteCharAt(colStrBuild.length() - 1);
      return true;
    }

      rowStrBuild.deleteCharAt(rowStrBuild.length() - 1);
      colStrBuild.deleteCharAt(colStrBuild.length() - 1);
     return false;
  }//end of valid


	/*
     * fills out a word puzzle defined by an empty board.
     * The characters in the empty board can be:
     *    '+': any letter can go here
     *    '-': no letter is allowed to go here
     *     a letter: this letter has to remain in the filled puzzle
     *  @param board is a 2-d array representing the empty board to be filled
     *  @param dictionary is the dictinary to be used for filling out the puzzle
     *  @return a 2-d array representing the filled out puzzle
     */
    public char[][] fillPuzzle(char[][] board, DictInterface dictionary){

      this.dictionary = dictionary;
      this.board = board;
      char[][] crosswordBoard = board;
      rowStr = new StringBuilder[board.length];
      colStr = new StringBuilder [board.length];
      for(int i = 0; i < board.length; i++){
        rowStr[i] = new StringBuilder("");
        colStr[i] = new StringBuilder("");
      }
      colLastMinusPos = new int[board.length];
      rowlastMinusPos = new int[board.length];

      if(solve(0, 0, 0)){
        for(int i=0; i< crosswordBoard.length; i++){
            StringBuilder rowB = new StringBuilder();
            rowB = rowStr[i];
            for(int j=0; j<crosswordBoard.length; j++){
                    String s = rowB.toString();
                    char c = s.charAt(j);
                    crosswordBoard[i][j] = c;
            }
          }
        return crosswordBoard;
      }
    System.out.println("343");
		return null;
  }

    /*
     * checks if filledBoard is a correct fill for emptyBoard
     * @param emptyBoard is a 2-d array representing an empty board
     * @param filledBoard is a 2-d array representing a filled out board
     * @param dictionary is the dictinary to be used for checking the puzzle
     * @return true if rules defined in fillPuzzle has been followed and
     *  that every row and column is a valid word in the dictionary. If a row
     *  a column has one or more '-' in it, then each segment separated by
     * the '-' should be a valid word in the dictionary
     */
    public boolean checkPuzzle(char[][] emptyBoard, char[][] filledBoard, DictInterface dictionary){
      //every solution is filled correctly

      //every cell in the puzzle was filled correctly
      //make sure the 2 boards are the same dimensions
      if(emptyBoard.length != filledBoard.length){
        return false;
      }
      for(int i = 0; i < filledBoard.length; i++){
        for (int j = 0; j < filledBoard[i].length; j ++ ) {
          //if plus it has to be filled by a letter
          if(emptyBoard[i][j] == '+' && Character.isLetter(filledBoard[i][j]) == false){
            return false;
          }
          //if the empty board has a minus filled board has to be a minus
          if(emptyBoard[i][j] == '-' && filledBoard[i][j] != '-'){
            return false;
          }
          //if the empty board had a character, filled board has to have the same character
          if(Character.isLetter(emptyBoard[i][j]) && emptyBoard[i][j] != filledBoard[i][j]){
            return false;
          }
        }
      }

      for(int i = 0; i < filledBoard.length; i++){
        //everytime going through rows make new string builder
        StringBuilder row = new StringBuilder();
       for (int j = 0; j < filledBoard.length; j ++ ) {
        row.append(filledBoard[i][j]);
      }
      //check for every row that it is a valid word
      for (String s : row.toString().split("") ) {
            //needs new StringBuilder
            int res = dictionary.searchPrefix(new StringBuilder(s));
            if(res != 2 && res !=3) {
              return false;
            }
       }
     }
    return true;
	}
}
