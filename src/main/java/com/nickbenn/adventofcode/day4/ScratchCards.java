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
package com.nickbenn.adventofcode.day4;

import static com.nickbenn.adventofcode.view.Presentation.NUMERIC_SOLUTION_FORMAT;

import com.nickbenn.adventofcode.day3.GearRatio;
import com.nickbenn.adventofcode.view.DataSource;
import com.nickbenn.adventofcode.view.Presentation;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Implements solution to parts 1 and 2 of day 4&mdash;reading an input file and parsing each line
 * as a scratchcard containing a pair of sets: a lottery draw, followed by picked numbers; these
 * paired sets are then collected into a list. The list is used to compute the answers required for
 * parts 1 and 2:
 * <ul>
 *   <li>For part 1, the {@link #getTotalValue()} method computes the value of each scratchcard from
 *   the number of matches between the draw and the pick, and sums those values over all of the
 *   scratchcards read from the input.</li>
 *   <li>For part 2, the {@link #getTotalCards()} implements a different payoff scheme: each match
 *   on a given scratchcard earns a copy of 1 of the succeeding cards (e.g. 3 matches = 1 copy of
 *   each of the 3 cards immediately following the given card. These new cards are then checked for
 *   matches just as the originals are. The total number of cards resulting from this process is
 *   returned in this case.</li>
 * </ul>
 *
 * @see <a href="https://adventofcode.com/2023/day/4">"Day 4: Scratchcards"</a>.
 */
public class ScratchCards {

  private final List<DailyNumbers> numbers;

  /**
   * Initializes this instance, using the value of {@link DataSource#DEFAULT_INPUT_FILE} as the name
   * (relative to the package of this class on the classpath) of the file to be read. In other
   * words, using this constructor is equivalent to using
   * {@link #ScratchCards(String) ScratchCards(DataSource.DEFAULT_INPUT_FILE)}.
   *
   * @throws IOException If the file referenced by {@link DataSource#DEFAULT_INPUT_FILE}} cannot be
   *                     found or read.
   */
  public ScratchCards() throws IOException {
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
  public ScratchCards(String inputFile) throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {
      numbers = lines
          .map(DailyNumbers::parse)
          .collect(Collectors.toList());
    }
  }

  /**
   * For each of parts 1 and 2, creates an instance of {@link ScratchCards}, invokes the relevant
   * solution method, and prints the result.
   *
   * @param args Command-line arguments (currently ignored).
   * @throws IOException If the file referenced by {@link DataSource#DEFAULT_INPUT_FILE}} cannot be
   *                     found or read.
   */
  public static void main(String[] args) throws IOException {
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 1, new ScratchCards().getTotalValue());
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 2, new ScratchCards().getTotalCards());
  }

  /**
   * Iterates over the scratchcards read from the input file, and for a non-zero number of
   * matches <em>n</em>, computes the value 2<sup>(<em>n</em> - 1)</sup>, and sums the values. (A
   * scratchcard with no matches has a value of 0.)
   *
   * @return Total value of all scratchcards.
   */
  public int getTotalValue() {
    return numbers
        .stream()
        .mapToInt(DailyNumbers::getMatches)
        .map((matches) -> (matches > 0) ? (1 << (matches - 1)): 0)
        .sum();
  }

  /**
   * Iterates over the scratchcards read from the input file, starting with a count of 1 for each,
   * increasing the counts of successive cards based on the number of matches found in previous
   * cards, then summing the resulting total number of cards.
   *
   * @return Total number of scratchcards, including those won by matching.
   */
  public int getTotalCards() {
    int[] counter = {0};
    int[] counts = new int[numbers.size()];
    Arrays.fill(counts, 1);
    numbers
        .forEach((card) -> {
          int cardIndex = counter[0]++;
          int multiplier = counts[cardIndex];
          int matches = card.getMatches();
          for (int i = cardIndex + 1; i <= cardIndex + matches; i++) {
            counts[i] += multiplier;
          }
        });
    return IntStream
        .of(counts)
        .sum();
  }

  private static class DailyNumbers {

    private static final Pattern GROUP_EXTRACTOR = Pattern.compile("[^:]+:([^|]*)\\|(.*)");
    private static final Pattern GROUP_SPLITTER = Pattern.compile("\\s+");

    private final Set<Integer> winners;
    private final Set<Integer> picks;
    private final int matches;

    private DailyNumbers(String winnersInput, String picksInput) {
      winners = split(winnersInput);
      picks = split(picksInput);
      matches = (int) picks
          .stream()
          .filter(winners::contains)
          .count();
    }

    public static DailyNumbers parse(String input) {
      Matcher matcher = GROUP_EXTRACTOR.matcher(input);
      if (!matcher.matches()) {
        throw new IllegalArgumentException();
      }
      return new DailyNumbers(matcher.group(1).strip(), matcher.group(2).strip());
    }

    public Set<Integer> getWinners() {
      return winners;
    }

    public Set<Integer> getPicks() {
      return picks;
    }

    public int getMatches() {
      return matches;
    }

    private Set<Integer> split(String input) {
      return GROUP_SPLITTER
          .splitAsStream(input.strip())
          .map(Integer::valueOf)
          .collect(Collectors.toUnmodifiableSet());
    }

  }

}
