/*
 *  Copyright 2023 Nicholas Bennett.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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

  public MatrixLocation move(CardinalDirection direction) {
    return new MatrixLocation(row + direction.rowIncrement(), column + direction.columnIncrement());
  }

  @Override
  public int compareTo(@SuppressWarnings("NullableProblems") MatrixLocation other) {
    return NATURAL_ORDER.compare(this, other);
  }

}
