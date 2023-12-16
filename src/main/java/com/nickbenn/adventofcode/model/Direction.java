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

/**
 * Declares basic operations relevant to directions of travel. The main use cases for
 * implementations of this class involve travel between grid/lattice locations&mdash;that is, from
 * one {@link MatrixLocation} to another. However, while these operations (like those of
 * {@code MatrixLocation} are integer-oriented, there is nothing that prevents their use without an
 * underling two-dimensional array. For example, this class and {@code MatrixLocation} can be used
 * in a {@link java.util.Set} or {@link java.util.Map} to help manage the logic of movement on a
 * large&mdash;but sparse&mdash;lattice.
 */
public interface Direction {

  /**
   * Returns the change in the row index (in a two-dimensional matrix or lattice) that will result
   * from a single step in this direction. Note that row indices increase when moving down and
   * decrease with upward movement, as they conventionally do in two-dimensional array
   * visualizations and computer graphics.
   */
  int rowOffset();

  /**
   * Returns the change in the column index (in a two-dimensional matrix or lattice) that will
   * result from a single step in this direction. (Column indices increase when moving
   * right and decrease with when moving left.)
   */
  int columnOffset();

  /**
   * Returns a {@link Direction} instance in the opposite direction of this one.
   */
  Direction opposite();

  /**
   * Returns a {@link Direction} instance representing the next direction (as defined in the
   * implementing class) from this one, in the clockwise direction. For example, in an
   * implementation for cardinal directions, the next direction clockwise from east is south.
   */
  Direction nextClockwise();

  /**
   * Returns a {@link Direction} instance representing the next direction (as defined in the
   * implementing class) from this one, in the counterclockwise direction. For example, in an
   * implementation for cardinal directions, the next direction counterclockwise from east is north.
   */
  Direction nextCounterClockwise();

  /**
   * Returns a flag indicating whether this direction is vertical&mdash;that is, without any
   * horizontal component. It will always be the case that
   * {@code isVertical() == (columnOffset() == 0)}.
   */
  default boolean isVertical() {
    return columnOffset() == 0;
  }

  /**
   * Returns a flag indicating whether this direction is horizontal&mdash;that is, without any
   * vertical component. It will always be the case that
   * {@code isHorizontal() == (rowOffset() == 0)}.
   */
  default boolean isHorizontal() {
    return rowOffset() == 0;
  }

  /**
   * Returns a flag indicating whether this direction is slanted or diagonal&mdash;that is, it is
   * neither horizontal nor vertical. It will always be the case that
   * {@code isOblique() == (rowOffset() != 0 && columnOffset() != 0)}.
   */
  default boolean isOblique() {
    return rowOffset() != 0 && columnOffset() != 0;
  }

}
