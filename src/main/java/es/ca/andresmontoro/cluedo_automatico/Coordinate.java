package es.ca.andresmontoro.cluedo_automatico;

public class Coordinate {
  public int row;
  public int column;

  public Coordinate(int row, int column) {
    this.row = row;
    this.column = column;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    Coordinate that = (Coordinate) obj;
    return row == that.row && column == that.column;
  }

  @Override
  public int hashCode() {
    return 31 * row + column; // Genera un código hash usando las coordenadas
  }
}

