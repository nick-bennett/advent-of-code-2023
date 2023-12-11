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
package com.nickbenn.adventofcode.day11;

import com.nickbenn.adventofcode.util.DataSource;
import com.nickbenn.adventofcode.util.MatrixLocation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CosmicExpansion {

  private static final char GALAXY_SYMBOL = '#';

  private final List<MatrixLocation> galaxies;

  public CosmicExpansion(int expansionCoefficient) throws IOException {
    this(DataSource.DEFAULT_INPUT_FILE, expansionCoefficient);
  }

  public CosmicExpansion(String inputFile, int expansionCoefficient) throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {
      char[][] galaxyMap = lines
          .map(String::toCharArray)
          .toArray(char[][]::new);
      galaxies = extractLocations(galaxyMap, expansionCoefficient);
    }
  }

  public static void main(String[] args) throws IOException {
    System.out.println(new CosmicExpansion(2).getShortestPathsSum());
    System.out.println(new CosmicExpansion(1_000_000).getShortestPathsSum());
  }

  public long getShortestPathsSum() {
    long sum = 0;
    for (int fromIndex = 0; fromIndex < galaxies.size() - 1; fromIndex++) {
      MatrixLocation from = galaxies.get(fromIndex);
      for (int toIndex = fromIndex + 1; toIndex < galaxies.size(); toIndex++) {
        MatrixLocation to = galaxies.get(toIndex);
        sum += from.manhattanDistance(to);
      }
    }
    return sum;
  }

  private List<MatrixLocation> extractLocations(char[][] galaxyMap, int expansionCoefficient) {
    List<MatrixLocation> galaxies = new ArrayList<>();
    int[] rowOffsets = new int[galaxyMap.length];
    int[] columnOffsets = new int[galaxyMap[0].length];
    int[] columnGalaxyCounts = new int[columnOffsets.length];
    int rowOffset = 0;
    for (int rowIndex = 0; rowIndex < galaxyMap.length; rowIndex++) {
      int rowGalaxyCount = 0;
      for (int colIndex = 0; colIndex < galaxyMap[rowIndex].length; colIndex++) {
        if (galaxyMap[rowIndex][colIndex] == GALAXY_SYMBOL) {
          rowGalaxyCount++;
          columnGalaxyCounts[colIndex]++;
          galaxies.add(new MatrixLocation(rowIndex, colIndex));
        }
      }
      rowOffset += ((rowGalaxyCount == 0) ? (expansionCoefficient - 1) : 0);
      rowOffsets[rowIndex] = rowOffset;
    }
    for (int colIndex = 0, colOffset = 0; colIndex < columnGalaxyCounts.length; colIndex++) {
      colOffset += ((columnGalaxyCounts[colIndex] == 0) ? (expansionCoefficient - 1) : 0);
      columnOffsets[colIndex] = colOffset;
    }
    return galaxies
        .stream()
        .map((location) -> new MatrixLocation(location.row() + rowOffsets[location.row()], location.column() + columnOffsets[location.column()]))
        .toList();
  }

}
