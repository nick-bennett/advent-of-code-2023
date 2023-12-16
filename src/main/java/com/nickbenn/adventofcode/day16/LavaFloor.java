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
package com.nickbenn.adventofcode.day16;

import static com.nickbenn.adventofcode.view.Presentation.NUMERIC_SOLUTION_FORMAT;

import com.nickbenn.adventofcode.model.CardinalDirection;
import com.nickbenn.adventofcode.model.Direction;
import com.nickbenn.adventofcode.model.MatrixLocation;
import com.nickbenn.adventofcode.view.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LavaFloor {

  private static final char VERTICAL_SPLITTER = '|';
  private static final char HORIZONTAL_SPLITTER = '-';
  private static final char NORTH_EAST_MIRROR = '/';
  private static final char NORTH_WEST_MIRROR = '\\';
  private static final MatrixLocation INITIAL_LOCATION = new MatrixLocation(0, 0);
  private static final Direction INITIAL_DIRECTION = CardinalDirection.EAST;

  private final char[][] grid;

  public LavaFloor() throws IOException {
    this(DataSource.DEFAULT_INPUT_FILE);
  }

  public LavaFloor(String inputFile) throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {
      grid = lines
          .map(String::toCharArray)
          .toArray(char[][]::new);
    }
  }

  public static void main(String[] args) throws IOException {
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 1, new LavaFloor().countEnergizedTiles());
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 2, new LavaFloor().countMaxEnergizedTiles());
  }

  public int countEnergizedTiles() {
    return countEnergizedTiles(INITIAL_LOCATION, INITIAL_DIRECTION);
  }

  public int countMaxEnergizedTiles() {
    return Stream.concat(
        IntStream.range(0, grid.length)
        .boxed()
        .flatMap((rowIndex) -> Stream.of(
            new Photon(new MatrixLocation(rowIndex, 0), CardinalDirection.EAST),
            new Photon(new MatrixLocation(rowIndex, grid[0].length - 1), CardinalDirection.WEST)
        )),
        IntStream.range(0, grid[0].length)
            .boxed()
            .flatMap((colIndex) -> Stream.of(
                new Photon(new MatrixLocation(0, colIndex), CardinalDirection.SOUTH),
                new Photon(new MatrixLocation(grid.length - 1, colIndex), CardinalDirection.NORTH)
            ))
        )
        .mapToInt((photon) -> countEnergizedTiles(photon.location(), photon.direction()))
        .max()
        .orElseThrow();
  }

  private int countEnergizedTiles(MatrixLocation location, Direction direction) {
    Map<MatrixLocation, Set<Direction>> energized = new HashMap<>();
    Queue<Photon> photons = new LinkedList<>(List.of(new Photon(location, direction)));
    while (!photons.isEmpty()) {
      Photon photon = photons.remove();
      location = photon.location();
      direction = photon.direction();
      while (location != null
          && isInBounds(location)
          && !energized.computeIfAbsent(location, (loc) -> new HashSet<>()).contains(direction)) {
        direction = nextDirection(location, direction, energized, photons);
        location = (direction != null) ? location.move(direction) : null;
      }
    }
    return energized.size();
  }

  private boolean isInBounds(MatrixLocation location) {
    int row = location.row();
    int column = location.column();
    return row >= 0 && row < grid.length && column >= 0 && column < grid[0].length;
  }

  private Direction nextDirection(MatrixLocation location, Direction direction,
      Map<MatrixLocation, Set<Direction>> energized, Queue<Photon> photons) {
    energized.get(location).add(direction);
    switch (grid[location.row()][location.column()]) {
      case VERTICAL_SPLITTER -> {
        if (direction.isHorizontal()) {
          photons.add(new Photon(location.move(CardinalDirection.NORTH), CardinalDirection.NORTH));
          photons.add(new Photon(location.move(CardinalDirection.SOUTH), CardinalDirection.SOUTH));
          direction = null;
        }
      }
      case HORIZONTAL_SPLITTER -> {
        if (direction.isVertical()) {
          photons.add(new Photon(location.move(CardinalDirection.WEST), CardinalDirection.WEST));
          photons.add(new Photon(location.move(CardinalDirection.EAST), CardinalDirection.EAST));
          direction = null;
        }
      }
      case NORTH_EAST_MIRROR -> {
        direction = (direction.isVertical()
            ? direction.nextClockwise()
            : direction.nextCounterClockwise());
      }
      case NORTH_WEST_MIRROR -> {
        direction = (direction.isVertical()
            ? direction.nextCounterClockwise()
            : direction.nextClockwise());
      }
    }
    return direction;
  }

  private record Photon(MatrixLocation location, Direction direction) {
  }

}
