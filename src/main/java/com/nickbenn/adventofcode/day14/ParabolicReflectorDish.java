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
package com.nickbenn.adventofcode.day14;

import static com.nickbenn.adventofcode.view.Presentation.NUMERIC_SOLUTION_FORMAT;

import com.nickbenn.adventofcode.model.CardinalDirection;
import com.nickbenn.adventofcode.model.MatrixLocation;
import com.nickbenn.adventofcode.view.DataSource;
import com.nickbenn.adventofcode.view.Presentation;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ParabolicReflectorDish {

  private static final char ROUND_ROCK = 'O';
  private static final char EMPTY_SPACE = '.';
  private static final List<CardinalDirection> CYCLE = List.of(CardinalDirection.NORTH,
      CardinalDirection.WEST, CardinalDirection.SOUTH, CardinalDirection.EAST);
  private static final int NUM_CYCLES = 1_000_000_000;
  private static final Map<CardinalDirection, Function<char[][], IntStream>>
      ROW_INDEX_STREAMS = Map.of(
      CardinalDirection.NORTH, (platform) ->
          IntStream.range(0, platform.length),
      CardinalDirection.EAST, (platform) ->
          IntStream.range(0, platform.length),
      CardinalDirection.SOUTH, (platform) ->
          IntStream.range(0, platform.length)
              .map((raw) -> platform.length - 1 - raw),
      CardinalDirection.WEST, (platform) ->
          IntStream.range(0, platform.length)
  );
  private static final Map<CardinalDirection, Function<char[][], IntStream>>
      COLUMN_INDEX_STREAMS = Map.of(
      CardinalDirection.NORTH, (platform) ->
          IntStream.range(0, platform[0].length),
      CardinalDirection.EAST, (platform) ->
          IntStream.range(0, platform[0].length)
              .map((raw) -> platform[0].length - 1 - raw),
      CardinalDirection.SOUTH, (platform) ->
          IntStream.range(0, platform[0].length),
      CardinalDirection.WEST, (platform) ->
          IntStream.range(0, platform[0].length)
  );
  private static final Map<CardinalDirection,
      BiFunction<char[][], MatrixLocation, Stream<MatrixLocation>>> SLIDE_INDEX_STREAMS = Map.of(
      CardinalDirection.NORTH, (platform, location) ->
          IntStream.range(0, location.row())
              .map((raw) -> location.row() - 1 - raw)
              .mapToObj((rowIndex) -> new MatrixLocation(rowIndex, location.column())),
      CardinalDirection.EAST, (platform, location) ->
          IntStream.range(location.column() + 1, platform[0].length)
              .mapToObj((colIndex) -> new MatrixLocation(location.row(), colIndex)),
      CardinalDirection.SOUTH, (platform, location) ->
          IntStream.range(location.row() + 1, platform.length)
              .mapToObj((rowIndex) -> new MatrixLocation(rowIndex, location.column())),
      CardinalDirection.WEST, (platform, location) ->
          IntStream.range(0, location.column())
              .map((raw) -> location.column() - 1 - raw)
              .mapToObj((colIndex) -> new MatrixLocation(location.row(), colIndex))
  );

  private final char[][] platform;

  public ParabolicReflectorDish() throws IOException {
    this(DataSource.DEFAULT_INPUT_FILE);
  }

  public ParabolicReflectorDish(String inputFile) throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {
      platform = lines
          .map(String::toCharArray)
          .toArray(char[][]::new);
    }
  }

  public static void main(String[] args) throws IOException {
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 1, new ParabolicReflectorDish().getSingleTiltLoad());
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 2, new ParabolicReflectorDish().getExtendedLoad());
  }

  public int getSingleTiltLoad() {
    tilt(CardinalDirection.NORTH);
    return getLoad();
  }

  public int getExtendedLoad() {
    Map<String, Integer> history = new HashMap<>();
    return IntStream.rangeClosed(1, NUM_CYCLES)
        .peek((cycleIndex)-> CYCLE.forEach(this::tilt))
        .map((currentCycle) -> {
          String key = getPlatformKey();
          Integer previousCycle = history.putIfAbsent(key, currentCycle);
          return (previousCycle == null)
              ? currentCycle
              : NUM_CYCLES - (NUM_CYCLES - currentCycle) % (currentCycle - previousCycle);
        })
        .dropWhile((currentCycle) -> currentCycle < NUM_CYCLES)
        .map((currentCycle) -> getLoad())
        .findFirst()
        .orElseThrow();
  }

  private void tilt(CardinalDirection direction) {
    ROW_INDEX_STREAMS
        .get(direction)
        .apply(platform)
        .forEach((rowIndex) -> {
          COLUMN_INDEX_STREAMS
              .get(direction)
              .apply(platform)
              .filter((colIndex) -> platform[rowIndex][colIndex] == ROUND_ROCK)
              .forEach((colIndex) -> slide(new MatrixLocation(rowIndex, colIndex), direction));
        });
  }

  private void slide(MatrixLocation start, CardinalDirection direction) {
    SLIDE_INDEX_STREAMS
        .get(direction)
        .apply(platform, start)
        .takeWhile((location) -> platform[location.row()][location.column()] == EMPTY_SPACE)
        .reduce((a, b) -> b)
        .ifPresent((end) -> {
          platform[end.row()][end.column()] = ROUND_ROCK;
          platform[start.row()][start.column()] = EMPTY_SPACE;
        });
  }

  private int getLoad() {
    return IntStream.range(0, platform.length)
        .flatMap((rowIndex) -> IntStream.range(0, platform[0].length)
            .filter((colIndex) -> platform[rowIndex][colIndex] == ROUND_ROCK)
            .map((colIndex) -> platform.length - rowIndex))
        .sum();
  }

  private String getPlatformKey() {
    return Arrays.stream(platform)
        .map(String::valueOf)
        .collect(Collectors.joining());
  }

}
