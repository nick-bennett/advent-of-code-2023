package com.nickbenn.adventofcode.util;

import java.util.Comparator;

public record IntRectangle(int topRow, int leftColumn, int height, int width)
    implements Comparable<IntRectangle> {

  private static final Comparator<IntRectangle> NATURAL_ORDER = Comparator
      .comparing(IntRectangle::topRow)
      .thenComparing(IntRectangle::height)
      .thenComparing(IntRectangle::leftColumn)
      .thenComparing(IntRectangle::width);

  public IntRectangle(IntRectangle other) {
    this(other.topRow, other.leftColumn, other.height, other.width);
  }

  public IntRectangle(IntRectangle other, int offset) {
    this(other.topRow - offset, other.leftColumn - offset,
        other.height + 2 * offset, other.width + 2 * offset);
  }

  public IntRectangle(MatrixLocation location) {
    this(location.row(), location.column(), 1, 1);
  }

  @Override
  public int compareTo(@SuppressWarnings("NullableProblems") IntRectangle other) {
    return NATURAL_ORDER.compare(this, other);
  }

}
