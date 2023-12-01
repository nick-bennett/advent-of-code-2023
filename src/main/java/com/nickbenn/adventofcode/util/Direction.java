package com.nickbenn.adventofcode.util;

public enum Direction {

  NORTH(-1, 0),
  EAST(0, 1),
  SOUTH(1, 0),
  WEST(0, -1);

  private final int rowIncrement;
  private final int columnIncrement;

  Direction(int rowIncrement, int columnIncrement) {
    this.rowIncrement = rowIncrement;
    this.columnIncrement = columnIncrement;
  }

  public int rowIncrement() {
    return rowIncrement;
  }

  public int columnIncrement() {
    return columnIncrement;
  }

}
