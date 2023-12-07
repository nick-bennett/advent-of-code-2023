package com.nickbenn.adventofcode.util;

import java.util.Comparator;

public record MatrixLocation(int row, int column) implements Comparable<MatrixLocation> {

  private static final Comparator<MatrixLocation> NATURAL_ORDER = Comparator
      .comparing(MatrixLocation::row)
      .thenComparing(MatrixLocation::column);

  @SuppressWarnings("unused")
  public MatrixLocation(MatrixLocation location) {
    this(location.row, location.column);
  }

  @Override
  public int compareTo(@SuppressWarnings("NullableProblems") MatrixLocation other) {
    return NATURAL_ORDER.compare(this, other);
  }

}
