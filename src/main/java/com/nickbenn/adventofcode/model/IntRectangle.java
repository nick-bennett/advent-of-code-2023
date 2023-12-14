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
package com.nickbenn.adventofcode.model;

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
