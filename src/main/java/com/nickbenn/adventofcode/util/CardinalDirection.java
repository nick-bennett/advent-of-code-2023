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

public enum CardinalDirection {

  NORTH(-1, 0),
  EAST(0, 1),
  SOUTH(1, 0),
  WEST(0, -1);

  private final int rowIncrement;
  private final int columnIncrement;

  CardinalDirection(int rowIncrement, int columnIncrement) {
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
