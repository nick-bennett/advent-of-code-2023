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
import java.util.Map.Entry;
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

  private static final Map<Character, Set<CardinalDirection>> MAZE_DIRECTIONS = Map.of(
      START_SYMBOL, NO_DIRECTIONS,
      NORTH_EAST_SYMBOL, NORTH_EAST_DIRECTIONS,
      NORTH_SOUTH_SYMBOL, NORTH_SOUTH_DIRECTIONS,
      NORTH_WEST_SYMBOL, NORTH_WEST_DIRECTIONS,
      EAST_SOUTH_SYMBOL, EAST_SOUTH_DIRECTIONS,
      EAST_WEST_SYMBOL, EAST_WEST_DIRECTIONS,
      SOUTH_WEST_SYMBOL, SOUTH_WEST_DIRECTIONS,
      GROUND_SYMBOL, NO_DIRECTIONS
  );
  private static final Map<Set<CardinalDirection>, Character> DIRECTION_CHARS = MAZE_DIRECTIONS
      .entrySet()
      .stream()
      .filter((entry) -> !entry.getKey().equals(START_SYMBOL))
      .collect(Collectors.toUnmodifiableMap(Entry::getValue, Entry::getKey));

  private final List<List<Set<CardinalDirection>>> maze;
  private final MatrixLocation startLocation;
  private final int loopLength;

  public PipeMaze() throws IOException {
    this(DataSource.DEFAULT_INPUT_FILE);
  }

  public PipeMaze(String inputFile) throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {
      char[][] maze = lines
          .map(String::toCharArray)
          .toArray(char[][]::new);
      String ground = String.valueOf(GROUND_SYMBOL);
      List<List<Set<CardinalDirection>>> scrubbed = Arrays.stream(maze)
          .map((row) -> Stream.generate(() -> NO_DIRECTIONS)
              .limit(row.length)
              .collect(Collectors.toList())
          )
          .collect(Collectors.toList());
      startLocation = findStart(maze);
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
        .mapToInt((row) -> {
          CrossingState state = CrossingState.OUTSIDE;
          int count = 0;
          for (Set<CardinalDirection> directions : row) {
            if ((state = state.next(directions)) == CrossingState.INSIDE) {
              count++;
            }
          }
          return count;
        })
        .sum();
  }

  private int traceLoop(
      char[][] dirty, MatrixLocation start, List<List<Set<CardinalDirection>>> scrubbed) {
    Set<CardinalDirection> directions = Arrays.stream(CardinalDirection.values())
        .filter((dir) -> {
          int newRow = start.row() + dir.rowIncrement();
          int newColumn = start.column() + dir.columnIncrement();
          return newRow >= 0
              && newRow < dirty.length
              && newColumn >= 0
              && newColumn < dirty[newRow].length
              && MAZE_DIRECTIONS
              .get(dirty[start.row() + dir.rowIncrement()][start.column() + dir.columnIncrement()])
              .contains(dir.opposite());
        })
        .collect(Collectors.toSet());
    CardinalDirection direction = directions
        .iterator()
        .next();
    MatrixLocation next = start.move(direction);
    int steps = 1;
    do {
      scrubbed.get(next.row())
          .set(next.column(), MAZE_DIRECTIONS.get(dirty[next.row()][next.column()]));
      direction = nextDirection(dirty, next, direction);
      next = next.move(direction);
      steps++;
    } while (!next.equals(start));
    scrubbed.get(start.row()).set(start.column(), directions);
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

  private CardinalDirection nextDirection(
      char[][] maze, MatrixLocation from, CardinalDirection arrivingFrom) {
    char plumbing = maze[from.row()][from.column()];
    return MAZE_DIRECTIONS
        .get(plumbing)
        .stream()
        .filter((dir) -> dir != arrivingFrom.opposite())
        .findFirst()
        .orElseThrow();
  }

  private enum CrossingState {
    INSIDE {
      @Override
      public CrossingState next(Set<CardinalDirection> directions) {
        CrossingState next;
        if (directions.equals(NORTH_EAST_DIRECTIONS)) {
          next = WALL_INSIDE_SOUTH;
        } else if (directions.equals(NORTH_SOUTH_DIRECTIONS)) {
          next = WALL_INSIDE_WEST;
        } else if (directions.equals(EAST_SOUTH_DIRECTIONS)) {
          next = WALL_INSIDE_NORTH;
        } else if (directions.equals(NO_DIRECTIONS)) {
          next = this;
        } else {
          throw new IllegalArgumentException();
        }
        return next;
      }
    },
    OUTSIDE {
      @Override
      public CrossingState next(Set<CardinalDirection> directions) {
        CrossingState next;
        if (directions.equals(NORTH_EAST_DIRECTIONS)) {
          next = WALL_INSIDE_NORTH;
        } else if (directions.equals(NORTH_SOUTH_DIRECTIONS)) {
          next = WALL_INSIDE_EAST;
        } else if (directions.equals(EAST_SOUTH_DIRECTIONS)) {
          next = WALL_INSIDE_SOUTH;
        } else if (directions.equals(NO_DIRECTIONS)) {
          next = this;
        } else {
          throw new IllegalArgumentException();
        }
        return next;
      }
    },
    WALL_INSIDE_SOUTH {
      @Override
      public CrossingState next(Set<CardinalDirection> directions) {
        CrossingState next;
        if (directions.equals(NORTH_WEST_DIRECTIONS)) {
          next = WALL_INSIDE_EAST;
        } else if (directions.equals(EAST_WEST_DIRECTIONS)) {
          next = this;
        } else if (directions.equals(SOUTH_WEST_DIRECTIONS)) {
          next = WALL_INSIDE_WEST;
        } else {
          throw new IllegalArgumentException();
        }
        return next;
      }
    },
    WALL_INSIDE_NORTH {
      @Override
      public CrossingState next(Set<CardinalDirection> directions) {
        CrossingState next;
        if (directions.equals(NORTH_WEST_DIRECTIONS)) {
          next = WALL_INSIDE_WEST;
        } else if (directions.equals(EAST_WEST_DIRECTIONS)) {
          next = this;
        } else if (directions.equals(SOUTH_WEST_DIRECTIONS)) {
          next = WALL_INSIDE_EAST;
        } else {
          throw new IllegalArgumentException();
        }
        return next;
      }
    },
    WALL_INSIDE_EAST {
      @Override
      public CrossingState next(Set<CardinalDirection> directions) {
        CrossingState next;
        if (directions.equals(NORTH_EAST_DIRECTIONS)) {
          next = WALL_INSIDE_SOUTH;
        } else if (directions.equals(NORTH_SOUTH_DIRECTIONS)) {
          next = WALL_INSIDE_WEST;
        } else if (directions.equals(EAST_SOUTH_DIRECTIONS)) {
          next = WALL_INSIDE_NORTH;
        } else if (directions.equals(NO_DIRECTIONS)) {
          next = INSIDE;
        } else {
          throw new IllegalArgumentException();
        }
        return next;
      }
    },
    WALL_INSIDE_WEST {
      @Override
      public CrossingState next(Set<CardinalDirection> directions) {
        CrossingState next;
        if (directions.equals(NORTH_EAST_DIRECTIONS)) {
          next = WALL_INSIDE_NORTH;
        } else if (directions.equals(NORTH_SOUTH_DIRECTIONS)) {
          next = WALL_INSIDE_EAST;
        } else if (directions.equals(EAST_SOUTH_DIRECTIONS)) {
          next = WALL_INSIDE_SOUTH;
        } else if (directions.equals(NO_DIRECTIONS)) {
          next = OUTSIDE;
        } else {
          throw new IllegalArgumentException();
        }
        return next;
      }
    };

    public abstract CrossingState next(Set<CardinalDirection> destination);

  }
}
