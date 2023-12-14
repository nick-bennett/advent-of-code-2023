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
package com.nickbenn.adventofcode.day2;

import com.nickbenn.adventofcode.view.DataSource;
import com.nickbenn.adventofcode.view.Presentation;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Implements solutions to the Day 2 problem, involving random drawing of colored cubes from a bag.
 * <p>Parts 1 and 2 of the problem differ in the information that must be inferred from the input
 * data (which consists of the number of blocks of each color selected in 1 or more draws in each
 * game):</p>
 * <ul>
 *   <li>For day 1, the {@link #sumFeasibleGames(Map)} must return the sum of game IDs for all games
 *   in the input file that could occur with specified (in {@link #CEILINGS}) number of red, green,
 *   and blue cubes.</li>
 *   <li>For day 2, the {@link #sumPower()} method must find the smallest number of cubes of each
 *   color which would make each of the games feasible, compute the product of these numbers for
 *   each game, and return the sum of these products.</li>
 * </ul>
 *
 * @see <a href="https://adventofcode.com/2023/day/2">Day 2: Cube Conundrum</a>
 */
public class CubeConundrum {

  private static final Pattern LINE_PATTERN = Pattern.compile("^Game\\s+(\\d+):\\s+(.*?)\\s*$");
  private static final Pattern SAMPLE_SPLITTER = Pattern.compile("[,;]\\s*");
  private static final Pattern SAMPLE_PATTERN = Pattern.compile("(\\d+)\\s*(red|green|blue)");

  /**
   * Maximum numbers of red, green, and blue cubes allowed for feasible games in part 1.
   */
  public static final Map<String, Integer> CEILINGS = Map.of(
      "red", 12,
      "green", 13,
      "blue", 14
  );

  private final String inputFile;

  /**
   * Initializes this instance, using the value of {@link DataSource#DEFAULT_INPUT_FILE} as the name
   * (relative to the package of this class on the classpath) of the file to be read. In other
   * words, using this constructor is equivalent to using
   * {@link #CubeConundrum(String) CubeConundrum(DataSource.DEFAULT_INPUT_FILE)}.
   */
  public CubeConundrum() {
    this(DataSource.DEFAULT_INPUT_FILE);
  }

  /**
   * Initializes this instance, using the value specified in the {@code inputFile} parameter as the
   * name (relative to the package of this class on the classpath) of the file to be read.
   *
   * @param inputFile Classpath/package-relative location of file from which input is read.
   */
  public CubeConundrum(String inputFile) {
    this.inputFile = inputFile;
  }

  /**
   * Creates an instance of {@link CubeConundrum} for each of the two parts of the problem,
   * implicitly specifying that input should be read from the file given by
   * {@link DataSource#DEFAULT_INPUT_FILE}, and invoking the relevant method to compute the required
   * value, which is then printed to the console.
   *
   * @param args Command-line arguments (currently ignored).
   * @throws IOException If the input file specified (implicitly or explicitly) in the constructor
   *                     invocation cannot be found or read.
   */
  public static void main(String[] args) throws IOException {
    System.out.printf(
        Presentation.NUMERIC_SOLUTION_FORMAT, 1, new CubeConundrum().sumFeasibleGames(CEILINGS));
    System.out.printf(Presentation.NUMERIC_SOLUTION_FORMAT, 2, new CubeConundrum().sumPower());
  }

  /**
   * Computes the sum of IDs of all games that would be feasible with the cube color limits
   * specified in {@code ceilings}.
   *
   * @param ceilings {@link Map Map&lt;String,Integer&gt;} giving the maximum number of cubes of
   *                 each color.
   * @return Sum of IDs for all games which would be feasible with the specified cube color limits.
   * @throws IOException If the input file specified (implicitly or explicitly) in the constructor
   *                     invocation cannot be found or read.
   */
  public int sumFeasibleGames(Map<String, Integer> ceilings) throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {
      return lines
          .map(Game::parse)
          .filter((game) -> game.isFeasible(ceilings))
          .mapToInt(Game::getId)
          .sum();
    }
  }

  /**
   * Computes the sum of the power of all games, where the power of a game is defined as the product
   * of the minimum numbers of red, green, and blue cubes required to produce the color draws read
   * from the input file.
   *
   * @return Sum of the powers (product of the minimum number required of each cube color in a game)
   * across all games.
   * @throws IOException If the input file specified (implicitly or explicitly) in the constructor
   *                     invocation cannot be found or read.
   */
  public long sumPower() throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {
      return lines
          .map(Game::parse)
          .mapToLong(Game::getPower)
          .sum();
    }
  }

  private static class Game {

    private final int id;
    private final Map<String, Integer> maxDraws;

    private Game(int id) {
      this.id = id;
      maxDraws = new HashMap<>();
    }

    public static Game parse(String line) {
      Matcher lineMatcher = LINE_PATTERN.matcher(line);
      if (!lineMatcher.matches()) {
        throw new IllegalArgumentException();
      }
      int id = Integer.parseInt(lineMatcher.group(1));
      Game game = new Game(id);
      SAMPLE_SPLITTER
          .splitAsStream(lineMatcher.group(2))
          .forEach((sample) -> {
            Matcher samplePatcher = SAMPLE_PATTERN.matcher(sample);
            if (!samplePatcher.matches()) {
              throw new IllegalArgumentException();
            }
            int count = Integer.parseInt(samplePatcher.group(1));
            game.update(samplePatcher.group(2), count);
          });
      return game;
    }

    public void update(String color, int count) {
      maxDraws.put(color, Math.max(count, maxDraws.getOrDefault(color, 0)));
    }

    public boolean isFeasible(Map<String, Integer> ceilings) {
      return ceilings
          .entrySet()
          .stream()
          .noneMatch((entry) -> entry.getValue() < maxDraws.getOrDefault(entry.getKey(), 0));
    }

    public long getPower() {
      return maxDraws
          .values()
          .stream()
          .mapToLong(Integer::longValue)
          .reduce(1L, (a, b) -> a * b);
    }

    public int getId() {
      return id;
    }

  }

}
