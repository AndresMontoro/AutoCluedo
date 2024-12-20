package es.ca.andresmontoro.cluedo_automatico;

import java.util.ArrayList;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.Route;

@Route("")
public class MainView extends HorizontalLayout{
  private Button tablero[][];
  private final PathFinderService pathFinderService;

  private int currentPositionRow;
  private int currentPositionColumn;

  private HorizontalLayout roomButtonsLayout = new HorizontalLayout();

  public MainView(PathFinderService pathFinderService) {
    this.pathFinderService = pathFinderService;
    
    setAlignItems(Alignment.CENTER);
    setJustifyContentMode(JustifyContentMode.AROUND);
    setPadding(true);

    add(createTable(), createMovementsController());
    
  }

  private VerticalLayout createTable() {
    VerticalLayout tableLayout = new VerticalLayout();
    tableLayout.setPadding(false);
    tableLayout.setSpacing(false);
    tablero = new Button[24][24];
  
    for(int i = 0; i < 24; i++) {
      HorizontalLayout row = new HorizontalLayout();
      row.setPadding(false);
      row.setSpacing(false);

      for(int j = 0; j < 24; j++) {
        int positionValue = pathFinderService.board[i][j];
        int rowIndex = i;
        int columnIndex = j;

        Button positionButton = Utils.createPositionButton();

        positionButton.addClickListener(e -> {
          selectCurrentPosition(rowIndex, columnIndex);
        });

        if(positionValue == PathFinderService.FORBIDDEN) {
          positionButton.getStyle().set("background-color", "red");
        } else if (positionValue > 0) {
          positionButton.getStyle().set("background-color", "green");
        }

        tablero[i][j] = positionButton;
        row.add(positionButton);
      }

      tableLayout.add(row);
    }
    return tableLayout;
  }

  private VerticalLayout createMovementsController() {
    VerticalLayout movementsController = new VerticalLayout();
    movementsController.setAlignItems(Alignment.CENTER);
    movementsController.setJustifyContentMode(JustifyContentMode.CENTER);

    movementsController.add(new H1("AUTOCLUEDO"));
    IntegerField numberOfMoves = new IntegerField("Número de movimientos");
    numberOfMoves.setMin(1);
    numberOfMoves.setMax(12);
    numberOfMoves.setValue(2);
    numberOfMoves.setStepButtonsVisible(true);
    movementsController.add(numberOfMoves);

    Button exploreButton = new Button("Buscar Caminos");
    exploreButton.addClickListener(e -> {
      explorePaths(numberOfMoves.getValue());
    });
    exploreButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    movementsController.add(exploreButton);
    movementsController.add(new H2("Habitaciones accesibles"));
    movementsController.add(roomButtonsLayout);
    
    return movementsController;
  }

  private void selectCurrentPosition(int row, int column) {
    boolean isValid = pathFinderService.isPositionValid(new Coordinate(row, column));
    if(isValid) {
      if(currentPositionRow != 0 && currentPositionColumn != 0) {
        tablero[currentPositionRow][currentPositionColumn].getStyle().set("background-color", "black");
      } else {
        tablero[currentPositionRow][currentPositionColumn].getStyle().set("background-color", "red");
      }

      currentPositionRow = row;
      currentPositionColumn = column;
      tablero[currentPositionRow][currentPositionColumn].getStyle().set("background-color", "yellow");
    } else {
      Notification.show("Posición no válida");
    }
  }

  private void explorePaths(int numberOfMoves) {
    boolean isValid = pathFinderService.isPositionValid(new Coordinate(currentPositionRow, currentPositionColumn));
    if(!isValid) {
      Notification.show("Posición no válida");
      return;
    }

    cleanBoard();

    ArrayList<Coordinate>[] positionsFlows = pathFinderService.moverFicha(
      new Coordinate(currentPositionRow, currentPositionColumn), 
      numberOfMoves);

    roomButtonsLayout.removeAll();

    if(isPositionFlowsEmpty(positionsFlows)) {
      Notification.show("No hay habitaciones accesibles");
      return;
    }
   
    for(int i = 1; i < 10; i++) {
      if(positionsFlows[i].size() > 0) {
        Button roomButton = new Button(pathFinderService.roomIdToName(i));
        int roomId = i;
        roomButton.addClickListener(e -> {
          showPath(roomId, positionsFlows);
        });
        roomButtonsLayout.add(roomButton);
      }
    } 
  }

  private void showPath(int roomId, ArrayList<Coordinate>[] positionsFlows) {
    cleanBoard();

    int cont = 1;
    for(Coordinate coordinate: positionsFlows[roomId]) {
      if(!(coordinate.row == currentPositionRow && coordinate.column == currentPositionColumn)
      && pathFinderService.board[coordinate.row][coordinate.column] == 0) {
        tablero[coordinate.row][coordinate.column].getStyle().set("background-color", "aqua");
        tablero[coordinate.row][coordinate.column].setText(String.valueOf(cont));
        cont++;
      }
    }    
  }

  private void cleanBoard() {
    for(int i = 0; i < 24; i++) {
      for(int j = 0; j < 24; j++) {
        if(pathFinderService.board[i][j] == 0 && !(i == currentPositionRow && j == currentPositionColumn)) {
          tablero[i][j].getStyle().set("background-color", "black");
          tablero[i][j].setText("");
        }
      }
    }
  }

  private boolean isPositionFlowsEmpty(ArrayList<Coordinate>[] positionsFlows) {
    for(int i = 1; i < 10; i++) {
      if(positionsFlows[i].size() > 0) {
        return false;
      }
    }
    return true;
  }
}
