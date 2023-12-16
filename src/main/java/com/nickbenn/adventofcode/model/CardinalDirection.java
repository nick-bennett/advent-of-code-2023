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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

public enum CardinalDirection implements Direction {

  NORTH(-1, 0),
  EAST(0, 1),
  SOUTH(1, 0),
  WEST(0, -1);

  private final int rowOffset;
  private final int columnOffset;

  CardinalDirection(int rowOffset, int columnOffset) {
    this.rowOffset = rowOffset;
    this.columnOffset = columnOffset;
  }

  @Override
  public int rowOffset() {
    return rowOffset;
  }

  @Override
  public int columnOffset() {
    return columnOffset;
  }

  @Override
  public CardinalDirection opposite() {
    CardinalDirection[] values = values();
    int length = values.length;
    return values[(ordinal() + length / 2) % length];
  }

  @Override
  public CardinalDirection nextClockwise() {
    CardinalDirection[] values = values();
    return values[(ordinal() + 1) % values.length];
  }

  @Override
  public CardinalDirection nextCounterClockwise() {
    CardinalDirection[] values = values();
    return values[(ordinal() - 1 + values.length) % values.length];
  }

  public static CardinalDirection choose(RandomGenerator rng, Predicate<CardinalDirection> filter) {
    CardinalDirection[] values = values();
    return values[rng.nextInt(values.length)];
  }

  public static Stream<Direction> stream() {
    return Stream.of(values());
  }

  public static Stream<Direction> randomStream(Random rng) {
    List<Direction> shuffled = new ArrayList<>();
    Collections.addAll(shuffled, values());
    Collections.shuffle(shuffled, rng);
    return shuffled.stream();
  }

}
