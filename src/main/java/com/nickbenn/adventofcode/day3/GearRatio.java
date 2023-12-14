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
package com.nickbenn.adventofcode.day3;

import com.nickbenn.adventofcode.view.DataSource;
import com.nickbenn.adventofcode.model.MatrixLocation;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GearRatio {

  private static final Pattern EXTRACTOR = Pattern.compile("(\\*)|([^\\d.*])|(\\d+)");

  private final NavigableMap<MatrixLocation, String> numbers;
  private final NavigableSet<MatrixLocation> gears;
  private final NavigableSet<MatrixLocation> symbols;

  public GearRatio() throws IOException {
    this(DataSource.DEFAULT_INPUT_FILE);
  }

  public GearRatio(String inputFile) throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {
      numbers = new TreeMap<>();
      gears = new TreeSet<>();
      symbols = new TreeSet<>();
      analyzeSchematic(lines);
    }
  }

  public static void main(String[] args) throws IOException {
    GearRatio ratio = new GearRatio();
    System.out.println(ratio.sumPartNumbers());
    System.out.println(ratio.sumGearRatios());
  }

  public int sumPartNumbers() {
    return numbers
        .entrySet()
        .stream()
        .filter(this::isSymbolAdjacent)
        .map(Entry::getValue)
        .mapToInt(Integer::parseInt)
        .sum();
  }

  public int sumGearRatios() {
    return gears
        .stream()
        .map(this::isNumberAdjacent)
        .filter((nums) -> nums.size() == 2)
        .mapToInt((nums) -> nums
            .stream()
            .mapToInt(Integer::parseInt)
            .reduce(1, (a, b) -> a * b)
        )
        .sum();
  }

  private void analyzeSchematic(Stream<String> lines) {
    int[] lineNumber = {0};
    lines.forEach((line) -> {
      int row = lineNumber[0]++;
      Matcher matcher = EXTRACTOR.matcher(line);
      matcher
          .results()
          .forEach((result) -> processMatch(result, row));
    });
  }

  private void processMatch(MatchResult result, int row) {
    MatrixLocation location = new MatrixLocation(row, result.start());
    String numberWord = result.group(3);
    if (numberWord != null) {
      numbers.put(location, numberWord);
    } else {
      symbols.add(location);
      if (result.group(1) != null) {
        gears.add(location);
      }
    }
  }

  private boolean isSymbolAdjacent(Entry<MatrixLocation, String> entry) {
    MatrixLocation location = entry.getKey();
    int startColumn = location.column() - 1;
    int endColumn = location.column() + entry.getValue().length() + 1;
    MatrixLocation start = new MatrixLocation(location.row() - 1, startColumn);
    MatrixLocation end = new MatrixLocation(location.row() + 1, endColumn);
    return symbols
        .subSet(start, end)
        .stream()
        .anyMatch((loc) -> loc.column() >= startColumn && loc.column() < endColumn);
  }

  private List<String> isNumberAdjacent(MatrixLocation location) {
    int column = location.column();
    MatrixLocation start = new MatrixLocation(location.row() - 1, 0);
    MatrixLocation end = new MatrixLocation(location.row() + 1, column + 1);
    return numbers
        .subMap(start, true, end, true)
        .entrySet()
        .stream()
        .filter((entry) -> entry.getKey().column() <= column + 1
            && entry.getKey().column() + entry.getValue().length() >= column)
        .map(Entry::getValue)
        .collect(Collectors.toList());
  }

}
