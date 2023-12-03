package com.nickbenn.adventofcode.util;

import java.util.Comparator;

public record Location(int row, int column) implements Comparable<Location> {

  private static final Comparator<Location> NATURAL_ORDER = Comparator
      .comparing(Location::row)
      .thenComparing(Location::column);

  public Location(Location location) {
    this(location.row, location.column);
  }

  @Override
  public int compareTo(@SuppressWarnings("NullableProblems") Location other) {
    return NATURAL_ORDER.compare(this, other);
  }

}
