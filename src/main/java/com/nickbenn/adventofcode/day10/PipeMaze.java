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
package com.nickbenn.adventofcode.day10;

import com.nickbenn.adventofcode.util.CardinalDirection;
import com.nickbenn.adventofcode.util.DataSource;
import com.nickbenn.adventofcode.util.MatrixLocation;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PipeMaze {

  private static final char START_SYMBOL = 'S';
  private static final char GROUND_SYMBOL = '.';
  private static final char NORTH_EAST_SYMBOL = 'L';
  private static final char NORTH_SOUTH_SYMBOL = '|';
  private static final char NORTH_WEST_SYMBOL = 'J';
  private static final char EAST_SOUTH_SYMBOL = 'F';
  private static final char EAST_WEST_SYMBOL = '-';
  private static final char SOUTH_WEST_SYMBOL = '7';

  private static final Set<CardinalDirection> NO_DIRECTIONS =
      Collections.unmodifiableSet(EnumSet.noneOf(CardinalDirection.class));
  private static final Set<CardinalDirection> NORTH_EAST_DIRECTIONS =
      Collections.unmodifiableSet(EnumSet.of(CardinalDirection.NORTH, CardinalDirection.EAST));
  private static final Set<CardinalDirection> NORTH_SOUTH_DIRECTIONS =
      Collections.unmodifiableSet(EnumSet.of(CardinalDirection.NORTH, CardinalDirection.SOUTH));
  private static final Set<CardinalDirection> NORTH_WEST_DIRECTIONS =
      Collections.unmodifiableSet(EnumSet.of(CardinalDirection.NORTH, CardinalDirection.WEST));
  private static final Set<CardinalDirection> EAST_SOUTH_DIRECTIONS =
      Collections.unmodifiableSet(EnumSet.of(CardinalDirection.SOUTH, CardinalDirection.EAST));
  private static final Set<CardinalDirection> EAST_WEST_DIRECTIONS =
      Collections.unmodifiableSet(EnumSet.of(CardinalDirection.WEST, CardinalDirection.EAST));
  private static final Set<CardinalDirection> SOUTH_WEST_DIRECTIONS =
      Collections.unmodifiableSet(EnumSet.of(CardinalDirection.WEST, CardinalDirection.SOUTH));

  private static final Map<Character, Set<CardinalDirection>> SYMBOL_DIRECTIONS = Map.of(
      START_SYMBOL, NO_DIRECTIONS,
      NORTH_EAST_SYMBOL, NORTH_EAST_DIRECTIONS,
      NORTH_SOUTH_SYMBOL, NORTH_SOUTH_DIRECTIONS,
      NORTH_WEST_SYMBOL, NORTH_WEST_DIRECTIONS,
      EAST_SOUTH_SYMBOL, EAST_SOUTH_DIRECTIONS,
      EAST_WEST_SYMBOL, EAST_WEST_DIRECTIONS,
      SOUTH_WEST_SYMBOL, SOUTH_WEST_DIRECTIONS,
      GROUND_SYMBOL, NO_DIRECTIONS
  );

  private final List<List<Set<CardinalDirection>>> maze;
  private final int loopLength;

  public PipeMaze() throws IOException {
    this(DataSource.DEFAULT_INPUT_FILE);
  }

  public PipeMaze(String inputFile) throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {
      char[][] maze = lines
          .map(String::toCharArray)
          .toArray(char[][]::new);
      List<List<Set<CardinalDirection>>> scrubbed = Arrays.stream(maze)
          .map((row) -> Stream.generate(() -> NO_DIRECTIONS)
              .limit(row.length)
              .collect(Collectors.toList())
          )
          .collect(Collectors.toList());
      MatrixLocation startLocation = findStart(maze);
      loopLength = traceLoop(maze, startLocation, scrubbed);
      this.maze = scrubbed;
    }
  }

  public static void main(String[] args) throws IOException {
    System.out.println(new PipeMaze().maxDistanceInLoop());
    System.out.println(new PipeMaze().areaEnclosedInLoop());
  }

  public int maxDistanceInLoop() {
    return loopLength / 2;
  }

  public int areaEnclosedInLoop() {
    return maze
        .stream()
        .mapToInt(this::countInside)
        .sum();
  }

  private int traceLoop(
      char[][] dirty, MatrixLocation start, List<List<Set<CardinalDirection>>> scrubbed) {
    CardinalDirection direction = startDirection(dirty, start,
        scrubbed);
    MatrixLocation next = start.move(direction);
    int steps = 1;
    do {
      scrubbed.get(next.row())
          .set(next.column(), SYMBOL_DIRECTIONS.get(dirty[next.row()][next.column()]));
      direction = nextDirection(dirty, next, direction);
      next = next.move(direction);
      steps++;
    } while (!next.equals(start));
    return steps;
  }

  private MatrixLocation findStart(char[][] maze) {
    MatrixLocation result = null;
    outer:
    for (int rowIndex = 0; rowIndex < maze.length; rowIndex++) {
      for (int colIndex = 0; colIndex < maze[rowIndex].length; colIndex++) {
        if (maze[rowIndex][colIndex] == START_SYMBOL) {
          result = new MatrixLocation(rowIndex, colIndex);
          break outer;
        }
      }
    }
    return result;
  }

  private CardinalDirection startDirection(
      char[][] dirty, MatrixLocation start, List<List<Set<CardinalDirection>>> scrubbed) {
    Set<CardinalDirection> directions = Arrays.stream(CardinalDirection.values())
        .filter((dir) -> {
          int newRow = start.row() + dir.rowIncrement();
          int newColumn = start.column() + dir.columnIncrement();
          return newRow >= 0 && newRow < dirty.length
              && newColumn >= 0 && newColumn < dirty[newRow].length
              && SYMBOL_DIRECTIONS
              .get(dirty[start.row() + dir.rowIncrement()][start.column() + dir.columnIncrement()])
              .contains(dir.opposite());
        })
        .collect(Collectors.toSet());
    scrubbed.get(start.row()).set(start.column(), directions);
    return directions
        .iterator()
        .next();
  }

  private CardinalDirection nextDirection(
      char[][] maze, MatrixLocation from, CardinalDirection arrivingFrom) {
    char plumbing = maze[from.row()][from.column()];
    return SYMBOL_DIRECTIONS
        .get(plumbing)
        .stream()
        .filter((dir) -> dir != arrivingFrom.opposite())
        .findFirst()
        .orElseThrow();
  }

  private int countInside(List<Set<CardinalDirection>> row) {
    CrossingState state = CrossingState.OUTSIDE;
    int count = 0;
    for (Set<CardinalDirection> directions : row) {
      if ((state = state.next(directions)) == CrossingState.INSIDE) {
        count++;
      }
    }
    return count;
  }

  private enum CrossingState {
    INSIDE,
    OUTSIDE,
    WALL_INSIDE_SOUTH,
    WALL_INSIDE_NORTH,
    WALL_INSIDE_EAST,
    WALL_INSIDE_WEST;

    private static final Map<CrossingState, Map<Set<CardinalDirection>, CrossingState>> TRANSITIONS =
        Map.of(
            INSIDE, Map.of(
                NORTH_EAST_DIRECTIONS, WALL_INSIDE_SOUTH,
                NORTH_SOUTH_DIRECTIONS, WALL_INSIDE_WEST,
                EAST_SOUTH_DIRECTIONS, WALL_INSIDE_NORTH,
                NO_DIRECTIONS, INSIDE
            ),
            OUTSIDE, Map.of(
                NORTH_EAST_DIRECTIONS, WALL_INSIDE_NORTH,
                NORTH_SOUTH_DIRECTIONS, WALL_INSIDE_EAST,
                EAST_SOUTH_DIRECTIONS, WALL_INSIDE_SOUTH,
                NO_DIRECTIONS, OUTSIDE
            ),
            WALL_INSIDE_NORTH, Map.of(
                NORTH_WEST_DIRECTIONS, WALL_INSIDE_WEST,
                EAST_WEST_DIRECTIONS, WALL_INSIDE_NORTH,
                SOUTH_WEST_DIRECTIONS, WALL_INSIDE_EAST
            ),
            WALL_INSIDE_EAST, Map.of(
                NORTH_EAST_DIRECTIONS, WALL_INSIDE_SOUTH,
                NORTH_SOUTH_DIRECTIONS, WALL_INSIDE_WEST,
                EAST_SOUTH_DIRECTIONS, WALL_INSIDE_NORTH,
                NO_DIRECTIONS, INSIDE
            ),
            WALL_INSIDE_SOUTH, Map.of(
                NORTH_WEST_DIRECTIONS, WALL_INSIDE_EAST,
                EAST_WEST_DIRECTIONS, WALL_INSIDE_SOUTH,
                SOUTH_WEST_DIRECTIONS, WALL_INSIDE_WEST
            ),
            WALL_INSIDE_WEST, Map.of(
                NORTH_EAST_DIRECTIONS, WALL_INSIDE_NORTH,
                NORTH_SOUTH_DIRECTIONS, WALL_INSIDE_EAST,
                EAST_SOUTH_DIRECTIONS, WALL_INSIDE_SOUTH,
                NO_DIRECTIONS, OUTSIDE
            )
        );

    public CrossingState next(Set<CardinalDirection> destination) {
      return Objects.requireNonNull(TRANSITIONS.get(this).get(destination));
    }

  }

}
