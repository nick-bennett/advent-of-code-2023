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
import java.util.Collection;
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
  private static final Photon INITIAL_PHOTON =
      new Photon(new MatrixLocation(0, 0), CardinalDirection.EAST);
  private static final Map<? extends Direction, Map<Character, Collection<? extends Direction>>>
      INTERACTION_RULES = Map.of(
      CardinalDirection.NORTH, Map.of(
          HORIZONTAL_SPLITTER, List.of(CardinalDirection.EAST, CardinalDirection.WEST),
          NORTH_EAST_MIRROR, List.of(CardinalDirection.EAST),
          NORTH_WEST_MIRROR, List.of(CardinalDirection.WEST)
      ),
      CardinalDirection.EAST, Map.of(
          VERTICAL_SPLITTER, List.of(CardinalDirection.NORTH, CardinalDirection.SOUTH),
          NORTH_EAST_MIRROR, List.of(CardinalDirection.NORTH),
          NORTH_WEST_MIRROR, List.of(CardinalDirection.SOUTH)
      ),
      CardinalDirection.SOUTH, Map.of(
          HORIZONTAL_SPLITTER, List.of(CardinalDirection.EAST, CardinalDirection.WEST),
          NORTH_EAST_MIRROR, List.of(CardinalDirection.WEST),
          NORTH_WEST_MIRROR, List.of(CardinalDirection.EAST)
      ),
      CardinalDirection.WEST, Map.of(
          VERTICAL_SPLITTER, List.of(CardinalDirection.NORTH, CardinalDirection.SOUTH),
          NORTH_EAST_MIRROR, List.of(CardinalDirection.SOUTH),
          NORTH_WEST_MIRROR, List.of(CardinalDirection.NORTH)
      )
  );

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
    return countEnergizedTiles(INITIAL_PHOTON);
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
        .mapToInt(this::countEnergizedTiles)
        .max()
        .orElseThrow();
  }

  private int countEnergizedTiles(Photon initialPhoton) {
    Map<MatrixLocation, Set<Direction>> energized = new HashMap<>();
    Queue<Photon> photons = new LinkedList<>(List.of(initialPhoton));
    for (Photon photon = photons.poll(); photon != null; photon = photons.poll()) {
      MatrixLocation location = photon.location();
      Direction direction = photon.direction();
      if (isInBounds(location)
          && !energized.computeIfAbsent(location, (loc) -> new HashSet<>()).contains(direction)) {
        energized.get(location).add(direction);
        photons.addAll(next(location, direction));
      }
    }
    return energized.size();
  }

  private boolean isInBounds(MatrixLocation location) {
    int row = location.row();
    int column = location.column();
    return row >= 0 && row < grid.length && column >= 0 && column < grid[0].length;
  }

  private List<Photon> next(MatrixLocation location, Direction direction) {
    return INTERACTION_RULES
        .get(direction)
        .getOrDefault(grid[location.row()][location.column()], List.of(direction))
        .stream()
        .map((dir) -> new Photon(location.move(dir), dir))
        .toList();
  }

  private record Photon(MatrixLocation location, Direction direction) {

  }

}
