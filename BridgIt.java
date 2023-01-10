import java.util.ArrayList; 
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;


// represents a cell in the game
class Cell {
  Color c;
  int size;
  Cell top;
  Cell left;
  Cell right;
  Cell bottom;
  boolean isEdge;
  boolean isTopOrLeft;

  Cell(Color c, int s, Cell top, Cell left, Cell right, Cell bottom) {
    this.c = c;
    this.size = 400 / s;
    this.top = top;
    this.left = left;
    this.right = right;
    this.bottom = bottom;
    this.isEdge = false;
    this.isTopOrLeft = false;
  }

  Cell(Color c, int s) {
    this.c = c;
    this.size = 400 / s;
    this.top = null;
    this.left = null;
    this.right = null;
    this.bottom = null;
    this.isEdge = false;
    this.isTopOrLeft = false;
  }

  Cell(Color c) {
    this.c = c;
    this.size = 20;
    this.top = null;
    this.left = null;
    this.right = null;
    this.bottom = null;
    this.isEdge = false;
    this.isTopOrLeft = false;
  }


  /*
   * fields:
   *  c...color
   *  size...int
   *  top...cell
   *  left...cell
   *  right...cell
   *  bottom...cell
   * methods:
   *  this.draw(int size)...worldImage
   *  this.cellColor()...String
   *  this.changeToOrange()...void
   *  this.changeToPurple()...void
   *  this.link()...void
   *  this.makeEdge()...void
   *  this.makeTopOrLeft()...void
   *  this.checkThrough(BridgItGame, int, String, boolean)...boolean
   */

  // draws an individual cell
  WorldImage draw() {
    return new RectangleImage(this.size, this.size, "solid", this.c);
  }

  // returns a cell's color
  String cellColor() {
    if (this.c == Color.orange) {
      return "orange";
    }
    if (this.c == Color.magenta) {
      return "purple";
    }
    return "white";
  }

  // changes a cell's color to orange
  void changeToOrange() {
    this.c = Color.orange;
  }

  // changes a cell's color to orange
  void changeToPurple() {
    this.c = Color.magenta;
  }

  // link this cell with the cells to the left, right, top, and bottom of it
  void link(Cell other, String dir) {
    if (dir.equals("left")) {
      this.left = other;
    }
    if (dir.equals("right")) {
      this.right = other;
    }
    if (dir.equals("top")) {
      this.top = other;
    }
    if (dir.equals("bottom")) {
      this.bottom = other;
    }
  }

  // assign the make edge attribute of this cell to true
  void makeEdge() {
    this.isEdge = true;
  }

  // assign the isToporLeft attribute of this cell to be true
  void makeTopOrLeft() {
    this.isTopOrLeft = true;
  }


  // check through all connected cells to see where the path stops
  boolean checkThrough(BridgItGame bgame, int howManyThrough, String prev, boolean original) {
    if ( ((this.bottom == null) || (!this.bottom.cellColor().equals(this.cellColor()))) 
        && ((this.top == null) || (!this.top.cellColor().equals(this.cellColor()))) 
        && ((this.right == null) || (!this.right.cellColor().equals(this.cellColor()))) 
        && ((this.left == null) || (!this.left.cellColor().equals(this.cellColor()))) 
        && (howManyThrough > 0) && (this.isTopOrLeft != original)) {
      return this.isEdge;  
    }
    else {
      if ((this.left != null) 
          && (!prev.equals("right"))  
          && (!this.left.cellColor().equals("white"))) {
        if (this.left.cellColor().equals(this.cellColor())) {
          return this.left.checkThrough(bgame, howManyThrough + 1, "left", original);
        }
      }
      if ((this.right != null) 
          && (!prev.equals("left")) 
          && (!this.right.cellColor().equals("white"))) {
        if (this.right.cellColor().equals(this.cellColor())) {
          return this.right.checkThrough(bgame, howManyThrough + 1, "right", original);
        }
      }
      if ((this.top != null) 
          && (!prev.equals("bottom")) 
          && (!this.top.cellColor().equals("white"))) {
        if (this.top.cellColor().equals(this.cellColor())) {
          return this.top.checkThrough(bgame, howManyThrough + 1, "top", original);
        }
      }
      if ((this.bottom != null) 
          && (!prev.equals("top")) 
          && (!this.bottom.cellColor().equals("white"))) {
        if (this.bottom.cellColor().equals(this.cellColor())) {
          return this.bottom.checkThrough(bgame, howManyThrough + 1, "bottom", original);
        }
      } 
      else if ((howManyThrough > 0) && (this.isTopOrLeft != original)) {
        return isEdge;
      }
    }
    return false;
  }
}






// represents a BridgIt game
class BridgItGame extends World {
  ArrayList<ArrayList<Cell>> cells;
  int n;
  boolean turn;  // represents who's turn it is, purple goes first

  BridgItGame(int n) {
    this.n = n;
    this.turn = true;
    if (n < 3 || n % 2 == 0) {
      throw new RuntimeException("Board size must be above 3 and odd");
    }
    this.cells = initCells(n);
    // link cells together at start
    this.linkCells();
    // assign edges at start
    this.assignEdges();
  }

  // constructor for testing
  BridgItGame(ArrayList<ArrayList<Cell>> cells, int n) {
    this.cells = cells;
    this.n = n;
    this.turn = true;
    // link cells together at start
    this.linkCells();
    // assign edges at start
    this.assignEdges();
  }

  //constructor for testing assign edges
  BridgItGame(int n, boolean edges) {
    this.cells = initCells(n);
    this.n = n;
    this.turn = true;
    // link cells together at start
    this.linkCells();
  }


  /*
   * fields:
   *  cells...ArrayList<ArrayList<Cell>>
   *  n...int
   *  turn...boolean
   * methods:
   *  this.initCells(int n)... ArrayList<ArrayList<Cell>> 
   *  this.makeScene()...WorldScene
   *  this.onMousePressed(Posn posn)...void
   *  this.findCell(int x, int y)...cell
   *  this.linkCells()...void
   *  this.assignEdges()...void
   *  this.onKeyEvent(String k)...void
   *  this.checkWin()...String
   *  this.lastScene(String msg)...WorldScene
   */

  // initializes the placement and order of the cells on the board
  public ArrayList<ArrayList<Cell>> initCells(int n) {
    ArrayList<ArrayList<Cell>> twoD = new ArrayList<ArrayList<Cell>>();  
    for (int i = 0; i < n; i++) {
      // adds a row every loop
      ArrayList<Cell> tempRow = new ArrayList<Cell>();
      for (int k = 0; k < n; k++) {
        if (i % 2 == 0) { // adds the purple tile rows
          if (k % 2 == 0) {
            tempRow.add(new Cell(Color.white, n));
          }
          else {
            tempRow.add(new Cell(Color.magenta, n));
          }
        }
        else {  // adds the orange tile rows
          if (k % 2 == 0) {
            tempRow.add(new Cell(Color.orange, n));
          }
          else {
            tempRow.add(new Cell(Color.white, n));
          }
        }
      }
      twoD.add(tempRow);
    }
    return twoD;
  }




  // renders the game board
  public WorldScene makeScene() { 
    WorldScene scene = new WorldScene(400, 400);
    for (int i = 0; i < this.n; i++) {
      for (int j = 0; j < this.n; j++) {
        scene.placeImageXY(this.cells.get(i).get(j).draw(),
            (i * (400 / this.n)) + ((400 / this.n) / 2),
            (j * (400 / this.n)) + ((400 / this.n) / 2));
      }
    }
    return scene;
  }

  // what to do when mouse is pressed
  public void onMousePressed(Posn posn) {           
    // if anything but a white square is clicked -> nothing changes
    if (findCell(posn.x, posn.y).cellColor().equals("purple")
        || findCell(posn.x, posn.y).cellColor().equals("orange")) {
      return;
    }

    // if any of the border cells are clicked -> nothing changes
    if (posn.x <= 400 / this.n || posn.x >= (400 - 400 / this.n) ||
        posn.y <= 400 / this.n || posn.y >= (400 - 400 / this.n)) {
      return;
    }

    // if it's purple's turn
    if (turn) {
      findCell(posn.x, posn.y).changeToPurple();
      turn = false;
    }
    // if it's orange's turn
    else {
      findCell(posn.x, posn.y).changeToOrange();
      turn = true;
    }
    // end the game if it is done
    if (!this.checkWin().equals("none")) {
      // make a scene
      //this.isWon();

      // end the world
      this.endOfWorld(this.checkWin().toUpperCase() 
          + " WON");
    }
  }  


  // finds a cell at a given coordinate and returns it
  Cell findCell(int x, int y) {
    double xDoub = x;  // converts posn coords to double
    double yDoub = y;
    double cellSize = 400 / this.n;  // the size of each cell
    int arrayRow = (int) Math.floor(yDoub / cellSize);  // position in arrayList
    int arrayCol = (int) Math.floor(xDoub / cellSize);
    return this.cells.get(arrayCol).get(arrayRow);
  }

  // link the cells in the list
  void linkCells() {
    for (int i = 0; i < this.n; i = i + 1) {
      for (int j = 0; j < this.n; j = j + 1) {
        // within rows
        if (j != n - 1) {
          // link cells w cell to right of them
          this.cells.get(i).get(j).link(this.cells.get(i).get(j + 1), "right");
        }
        if (j != 0) {
          // link cells w cell to left of them
          this.cells.get(i).get(j).link(this.cells.get(i).get(j - 1), "left");
        }
        // link cells w cell on top of them
        if (i != 0) {
          this.cells.get(i).get(j).link(this.cells.get(i - 1).get(j), "top");
        }
        // link cells w cell on bottom of them
        if (i != n - 1) {
          this.cells.get(i).get(j).link(this.cells.get(i + 1).get(j), "bottom");
        }
      } 
    }
  }

  // for all cells on the edges, assign their isEdge attribute to true
  // also assign isTopOrLeft as needed
  void assignEdges() {
    for (int i = 0; i < this.n; i = i + 1) {
      for (int j = 0; j < this.n; j = j + 1) {
        if ((i == 0) || (i == (this.n - 1)) || (j == 0) || (j == (this.n - 1)) ) {
          // it is an edge
          this.cells.get(i).get(j).makeEdge();
          // if it is a top or left edge 
          if ( (i == 0) || (j == 0)) {
            this.cells.get(i).get(j).makeTopOrLeft();
          }
        }
      }
    }
  }


  //handles keystrokes
  // If the game has not ended, use the ‘r’ key to 
  // reset the game and create a new board.
  public void onKeyEvent(String k)  { 
    if (k.equals("r"))  { 
      // create new board
      this.cells = this.initCells(this.n);
      //link and assign edges
      this.linkCells();
      this.assignEdges();
      // reset turn to purple
      this.turn = true;
    }
  }


  // check if anyone has won yet
  String checkWin() {
    // check top and bottom
    // start from bottom
    for (int j = 0; j < this.n; j++) {
      if (this.cells.get(0).get(j).cellColor().equals("purple")) {           
        if (this.cells.get(0).get(j).checkThrough(this, 0, "", true)) {
          return "purple";                                               
        }
      }
      if (this.cells.get(this.n - 1).get(j).cellColor().equals("purple")) {
        if (this.cells.get(this.n - 1).get(j).checkThrough(this, 0, "", false)) {
          return "purple";
        }
      }
    }
    // check left and right
    // start from left
    for (int i = 0; i < this.n; i = i + 1) {
      if (this.cells.get(i).get(0).cellColor().equals("orange")) {
        if (this.cells.get(i).get(0).checkThrough(this, 0, "", true)) {
          return "orange";
        }
        if (this.cells.get(i).get(this.n - 1).checkThrough(this, 0, "", false)) {
          return "orange";
        }
      }
    } 
    return "none";
  }


  //displays win screen
  public WorldScene lastScene(String msg) {
    WorldScene win = this.makeScene();

    win.placeImageXY(new TextImage(msg, 50, Color.BLACK), 200, 200);

    return win;
  }
}



// examples class
class ExamplesBridgIt {

  void testGame(Tester t)  {
    BridgItGame g = new BridgItGame(9);
    g.bigBang(400, 400);
  }
}
