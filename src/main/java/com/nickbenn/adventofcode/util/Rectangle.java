package com.nickbenn.adventofcode.util;

import java.util.Comparator;

public record Rectangle(int topRow, int leftColumn, int height, int width)
    implements Comparable<Rectangle> {

  private static final Comparator<Rectangle> NATURAL_ORDER = Comparator
      .comparing(Rectangle::topRow)
      .thenComparing(Rectangle::height)
      .thenComparing(Rectangle::leftColumn)
      .thenComparing(Rectangle::width);

  public Rectangle(Rectangle other) {
    this(other.topRow, other.leftColumn, other.height, other.width);
  }

  public Rectangle(Rectangle other, int offset) {
    this(other.topRow - offset, other.leftColumn - offset,
        other.height + 2 * offset, other.width + 2 * offset);
  }

  public Rectangle(Location location) {
    this(location.row(), location.column(), 1, 1);
  }

  @Override
  public int compareTo(@SuppressWarnings("NullableProblems") Rectangle other) {
    return NATURAL_ORDER.compare(this, other);
  }

}
