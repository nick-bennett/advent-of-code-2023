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

import static com.nickbenn.adventofcode.view.Presentation.NUMERIC_SOLUTION_FORMAT;

import com.nickbenn.adventofcode.model.MatrixLocation;
import com.nickbenn.adventofcode.view.DataSource;
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

/**
 * Implements solution to parts 1 and 2 of day 3&mdash;reading an input file and interpreting its
 * contents as a schematic containing part numbers, gears, and other elements, then computing values
 * derived from the positions of those elements.
 * <p>Parts 1 and 2 of the problem differ in the computations required from the schematic:</p>
 * <ul>
 *   <li>For part 1, the {@link #sumPartNumbers()} must compute and return the sum of part numbers
 *   in the schematic; these are identifiable by adjacency to a non-digit character other than
 *   {@code '.'}.</li>
 *   <li>For part 2, the {@link #sumGearRatios()} method must find the sum of the products of the
 *   pairs of part numbers adjacent to each gear symbol in the schematic. (A gear is represented by
 *   a {@code '*'} character adjacent to exactly 2 part numbers.)</li>
 * </ul>
 *
 * @see <a href="https://adventofcode.com/2023/day/3">"Day 3: Gear Ratios"</a>.
 */
public class GearRatio {

  private static final Pattern EXTRACTOR = Pattern.compile("(\\*)|([^\\d.*])|(\\d+)");

  private final NavigableMap<MatrixLocation, String> numbers;
  private final NavigableSet<MatrixLocation> gears;
  private final NavigableSet<MatrixLocation> symbols;

  /**
   * Initializes this instance, using the value of {@link DataSource#DEFAULT_INPUT_FILE} as the name
   * (relative to the package of this class on the classpath) of the file to be read. In other
   * words, using this constructor is equivalent to using
   * {@link #GearRatio(String) GearRatio(DataSource.DEFAULT_INPUT_FILE)}.
   *
   * @throws IOException If the file referenced by {@link DataSource#DEFAULT_INPUT_FILE}} cannot be
   *                     found or read.
   */
  public GearRatio() throws IOException {
    this(DataSource.DEFAULT_INPUT_FILE);
  }

  /**
   * Initializes this instance, using the value specified in the {@code inputFile} parameter as the
   * name (relative to the package of this class on the classpath) of the file to be read, and
   * processing the schematic contained in that file to catalogue all of its part numbers, gears,
   * and other symbols.
   *
   * @param inputFile Classpath/package-relative location of file from which input is read.
   * @throws IOException If the file referenced by {@code inputFile} cannot be found or read.
   */
  public GearRatio(String inputFile) throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {
      numbers = new TreeMap<>();
      gears = new TreeSet<>();
      symbols = new TreeSet<>();
      analyzeSchematic(lines);
    }
  }

  /**
   * Creates an instance of {@link GearRatio} and invokes the relevant methods to compute the
   * required values for parts 1 and 2, printing the results.
   *
   * @param args Command-line arguments (currently ignored).
   * @throws IOException If the file referenced by {@link DataSource#DEFAULT_INPUT_FILE}} cannot be
   *                     found or read.
   */
  public static void main(String[] args) throws IOException {
    GearRatio ratio = new GearRatio();
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 1, ratio.sumPartNumbers());
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 2, ratio.sumGearRatios());
  }

  /**
   * Computes and returns the sum of part numbers in the schematic&mdash;that is, the sum of the
   * numeric values of digit strings that are adjacent to non-digit characters other than
   * {@code '.'}.
   * <p>This method does not modify the state of the instance or have any other side effects.</p>
   */
  public int sumPartNumbers() {
    return numbers
        .entrySet()
        .stream()
        .filter(this::isSymbolAdjacent)
        .map(Entry::getValue)
        .mapToInt(Integer::parseInt)
        .sum();
  }

  /**
   * Computes and returns the sum of gear ratios in the schematic&mdash;that is, the sum of the
   * products of the numeric values of the digit string pairs adjacent to each gear ({@code '*'}).
   * <p>This method does not modify the state of the instance or have any other side effects.</p>
   */
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
