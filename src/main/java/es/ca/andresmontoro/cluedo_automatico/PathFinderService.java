package es.ca.andresmontoro.cluedo_automatico;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class PathFinderService {
  public int[][] board;

  public static final int FORBIDDEN = -1;
  public static final int KITCHEN = 1;
  public static final int BALLROOM = 2;
  public static final int CONSERVATORY = 3;
  public static final int BILLIARD_ROOM = 4;
  public static final int LIBRARY = 5;
  public static final int OFFICE = 6;
  public static final int HALL = 7;
  public static final int LOUNGE = 8;
  public static final int DINING_ROOM = 9;

  private static int[][] directions = {
    {0, 1}, // right
    {1, 0}, // down
    {0, -1}, // left
    {-1, 0} // up
  };

  

  public PathFinderService() {
    initializeBoard();
  }

  /*
   * For now, I am filling the down area of all rooms
   */
  private void initializeBoard() {
    board = new int[24][24];
    fillArea( // FULL KITCHEN 
      FORBIDDEN,
      new Coordinate(0, 0),
      new Coordinate(5, 5)
    );
    placeKitchenDoor();

    fillArea( // BALLROOM
      FORBIDDEN,
      new Coordinate(2, 8),
      new Coordinate(6, 15));
    addBallroomSpaces();
    placeBallroomDoors();

    fillArea( // FULL CONSERVATORY
      FORBIDDEN,
      new Coordinate(0, 18),
      new Coordinate(4, 23)
    );
    placeConservatoryDoor();

    fillArea( // FULL BILLIARD ROOM
      FORBIDDEN,
      new Coordinate(7, 18),
      new Coordinate(11, 23)
    );
    placeBilliardRoomDoors();

    fillArea( // LIBRARY
      FORBIDDEN,
      new Coordinate(14, 17),
      new Coordinate(16, 23)
    );
    addLibrarySpaces();
    placeLibraryDoor();

    fillArea( // FULL OFFICE
      FORBIDDEN,
      new Coordinate(20, 17),
      new Coordinate(23, 23)
    );
    placeOfficeDoor();

    fillArea( // FULL HALL
      FORBIDDEN,
      new Coordinate(17, 9),
      new Coordinate(23, 14)
    );
    placeHallDoor();

    fillArea(   // FULL LOUNGE
      FORBIDDEN,
      new Coordinate(18, 0),
      new Coordinate(23, 6)
    );
    placeLoungeDoor();

    fillArea( // DINING ROOM
      FORBIDDEN,
      new Coordinate(9, 0),
      new Coordinate(14, 7)
    );
    addDiningRoomSpaces();
    placeDiningRoomDoors();

    fillArea( // FULL STAIRS
      FORBIDDEN,
      new Coordinate(9, 10),
      new Coordinate(15, 14)
    );

    addAdditionalForbiddenSpaces();
  }

  private void fillArea(int value, Coordinate start, Coordinate end) {
    for (int i = start.row; i <= end.row; i++) {
      for (int j = start.column; j <= end.column; j++) {
        board[i][j] = value;
      }
    }
  }

  private void placeKitchenDoor() {
    board[5][5] = KITCHEN;
  }

  private void addBallroomSpaces() {
    for (int i = 0; i <= 1; i++) {
      for (int j = 10; j <= 13; j++) {
        board[i][j] = FORBIDDEN;
      }
    }
  }

  private void placeBallroomDoors() {
    board[5][8] = BALLROOM;
    board[6][10] = BALLROOM;
    board[6][13] = BALLROOM;
    board[5][15] = BALLROOM;
  }

  private void placeConservatoryDoor() {
    board[4][18] = CONSERVATORY;
  }

  private void placeBilliardRoomDoors() {
    board[8][18] = BILLIARD_ROOM;
    board[11][21] = BILLIARD_ROOM;
  }

  private void addLibrarySpaces() {
    for(int i = 13; i <= 17; i = i+4) {
      for(int j = 18; j <= 23; j++) {
        board[i][j] = FORBIDDEN;
      }
    }
  }

  private void placeLibraryDoor() {
    board[15][17] = LIBRARY;
  }

  private void placeOfficeDoor() {
    board[20][17] = OFFICE;
  }

  private void placeHallDoor() {
    board[17][11] = HALL;
  }

  private void placeLoungeDoor() {
    board[18][6] = LOUNGE;
  }

  private void addDiningRoomSpaces() {
    for (int i = 0; i <= 4; i++) {
      board[8][i] = FORBIDDEN;
    }
  }

  private void placeDiningRoomDoors() {
    board[11][7] = DINING_ROOM;
    board[14][6] = DINING_ROOM;
  }

  private void addAdditionalForbiddenSpaces() {
    // Upper left corner
    board[0][6] = FORBIDDEN;
    board[0][7] = FORBIDDEN;
    board[0][8] = FORBIDDEN;
    board[1][6] = FORBIDDEN;

    // Upper right corner
    board[0][15] = FORBIDDEN;
    board[0][16] = FORBIDDEN;
    board[0][17] = FORBIDDEN;
    board[1][17] = FORBIDDEN;

    // Middle left
    board[6][0] = FORBIDDEN;
    board[7][0] = FORBIDDEN;
    board[7][1] = FORBIDDEN;

    // Middle right
    board[6][23] = FORBIDDEN;

    // Lower left
    board[15][0] = FORBIDDEN;
    board[17][0] = FORBIDDEN;

    // Lower right
    board[12][23] = FORBIDDEN;
    board[19][23] = FORBIDDEN;

    // Bottom left corner
    board[23][8] = FORBIDDEN;

    // Bottom right corner
    board[23][15] = FORBIDDEN;
    board[23][16] = FORBIDDEN;
  }

  public void printBoard() {
    for(int i = 0; i < 24; i++) {
      for(int j = 0; j < 24; j++) {
        if (board[i][j] == FORBIDDEN) {
          System.out.print("| X");
          continue;
        }
        System.out.print("| " + board[i][j]);
      }
      System.out.println();
    }
  }

  public ArrayList<Coordinate>[] moverFicha(Coordinate initialPosition, int numberOfMoves) {
    if(!isPositionValid(initialPosition) || numberOfMoves < 0 || numberOfMoves > 12) {
      throw new IllegalArgumentException("Invalid initial position or number of moves");
    }
    
    if(isDoor(initialPosition)) {
      numberOfMoves--;
    }

    @SuppressWarnings("unchecked")
    ArrayList<Coordinate>[] positionsFlows = (ArrayList<Coordinate>[]) new ArrayList[10];
    for (int i = 0; i < 10; i++) {
      positionsFlows[i] = new ArrayList<Coordinate>();
    }
    
    Set<Coordinate> visitedCoordinates = new HashSet<>();
    ArrayList<Coordinate> currentPath = new ArrayList<>();
    
    visitedCoordinates.add(initialPosition);
    currentPath.add(initialPosition);

    explorePaths(initialPosition, numberOfMoves, visitedCoordinates, currentPath, positionsFlows);
    return positionsFlows;
  }

  private void explorePaths(
    Coordinate currentPosition, int numberOfMoves, 
    Set<Coordinate> visitedCoordinates, ArrayList<Coordinate> currentPath,
    ArrayList<Coordinate>[] positionsFlows
  ) {
    if (isPositionValid(currentPosition)) {
      if (numberOfMoves == 0 && board[currentPosition.row][currentPosition.column] > 0) {
        int room = board[currentPosition.row][currentPosition.column];
        positionsFlows[room] = new ArrayList<Coordinate>();
        positionsFlows[room].addAll(currentPath);
      } else {
        if (numberOfMoves > 0) {
          for (int[] direction : directions) {
            Coordinate nextPosition = new Coordinate(
              currentPosition.row + direction[0],
              currentPosition.column + direction[1]
            );

            if (!visitedCoordinates.contains(nextPosition)) {
              visitedCoordinates.add(nextPosition); 
              currentPath.add(nextPosition);
              explorePaths(nextPosition, numberOfMoves - 1, visitedCoordinates, currentPath, positionsFlows);
              currentPath.remove(currentPath.size() - 1);
              visitedCoordinates.remove(nextPosition);
            }
          }
        }
      }
    }
  }

  public boolean isPositionValid(Coordinate position) {
    return (
      position.row >= 0 && position.row < 24 &&
      position.column >= 0 && position.column < 24 &&
      board[position.row][position.column] != FORBIDDEN
    );
  }

  // public void printResults() {
  //   for (int i = 0; i < 10; i++) {
  //     System.out.println("Path to " + roomIdToName(i) + ": ");
  //     for (Coordinate coordinate : positionsFlows[i]) {
  //       System.out.print("(" + coordinate.row + ", " + coordinate.column + "); ");
  //     }
  //     System.out.println();
  //   }
  // }

  public String roomIdToName(int roomId) {
    switch (roomId) {
      case 1:
        return "Cocina";
      case 2:
        return "Sala de Baile";
      case 3:
        return "Invernadero";
      case 4:
        return "Sala de Billar";
      case 5:
        return "Biblioteca";
      case 6:
        return "Despacho";
      case 7:
        return "Vestibulo";
      case 8:
        return "Salon";
      case 9:
        return "Comedor";
      default:
        return "Unknown";
    }
  }

  private boolean isDoor(Coordinate position) {
    return board[position.row][position.column] > 0;
  }
}
