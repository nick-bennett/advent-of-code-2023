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
package com.nickbenn.adventofcode.day07;

import static com.nickbenn.adventofcode.view.Presentation.NUMERIC_SOLUTION_FORMAT;

import com.nickbenn.adventofcode.view.DataSource;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implements solutions for both parts of day 7, reading an input file where each line specifies a
 * card hands and bid (wager), for subsequent ranking and summing by the {@link #getWinnings()}
 * method.
 * <p>After reading the input data, parts 1 and 2 both require the same type of
 * calculation&mdash;namely, rank the hands in reverse (the hand with the lowest value has a rank
 * of 1), multiply each rank by the bid for that hand, and sum the result. However, the two parts
 * differ in the treatment of a Jack (represented by {@code 'J'} in the input file):</p>
 * <ul>
 *   <li>In part 1, Jacks are treated as usual, ranking between 10s and Queens.</li>
 *   <li>In part 2, Jacks are Jokers&mdash;that is, they are wild. Wild Jacks in a hand are always
 *   added to the largest group (other than Jacks) of cards in the hand.</li>
 * </ul>
 *
 * @see <a href="https://adventofcode.com/2023/day/7">"Day 7: Camel Cards"</a>.
 */
public class CamelCards {

  private static final Pattern HAND_BID_EXTRACTOR = Pattern.compile("(\\S+)\\s+(\\d+)");

  private final Map<Hand, Integer> hands;

  /**
   * Initializes this instance, using the value of {@link DataSource#DEFAULT_INPUT_FILE} as the name
   * (relative to the package of this class on the classpath) of the file to be read, with
   * {@code jackIsWild} specifying whether the Jack card (identified as {@code 'J'} in the input) is
   * wild. In other words, using this constructor is equivalent to using
   * {@link #CamelCards(String, boolean) CamelCards(DataSource.DEFAULT_INPUT_FILE, jackIsWild)}.
   *
   * @param jackIsWild Specifies whether the Jack card ({@code 'J'}) is wild.
   * @throws IOException If the file referenced by {@link DataSource#DEFAULT_INPUT_FILE}} cannot be
   *                     found or read.
   */
  public CamelCards(boolean jackIsWild) throws IOException {
    this(DataSource.DEFAULT_INPUT_FILE, jackIsWild);
  }

  /**
   * Initializes this instance, using {@code inputFile} as the name (relative to the package of this
   * class on the classpath) of the file to be read, with {@code jackIsWild} specifying whether the
   * Jack card (identified as {@code 'J'} in the input) is wild. Each line of {@code inputFile} is
   * parsed, extracting the five cards of the hand and the bid (wager) for that hand.
   *
   * @param jackIsWild Specifies whether the Jack card ({@code 'J'}) is wild.
   * @throws IOException If the file referenced by {@link DataSource#DEFAULT_INPUT_FILE}} cannot be
   *                     found or read.
   */
  public CamelCards(String inputFile, boolean jackIsWild) throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {
      hands = lines
          .map(HAND_BID_EXTRACTOR::matcher)
          .filter(Matcher::matches)
          .collect(Collectors.toMap((matcher) -> new Hand(matcher.group(1), jackIsWild),
              (matcher) -> Integer.parseInt(matcher.group(2))));
    }
  }

  /**
   * For each of parts 1 and 2, creates an instance of {@link CamelCards}, invokes the
   * {@link #getWinnings()} solution method, and prints the result.
   *
   * @param args Command-line arguments (currently ignored).
   * @throws IOException If the file referenced by {@link DataSource#DEFAULT_INPUT_FILE}} cannot be
   *                     found or read.
   */
  public static void main(String[] args) throws IOException {
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 1, new CamelCards(false).getWinnings());
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 2, new CamelCards(true).getWinnings());
  }

  /**
   * Sorts hands by value, assigns each a reversed rank (1 for the lowest-valued hand, and
   * ascending), multiplies the rank by the bid for that hand, computes the sum of these products
   * over all hands, and returns the result.
   *
   * @return Sum of (reversed rank X bid) over all hands.
   */
  public int getWinnings() {
    int[] count = {0};
    return hands
        .entrySet()
        .stream()
        .sorted(Entry.comparingByKey())
        .mapToInt((entry) -> ++count[0] * entry.getValue())
        .sum();
  }

  private static class Hand implements Comparable<Hand> {

    private static final char JACK = 'J';
    private static final Map<Character, Integer> FACE_CARD_VALUES = Map.ofEntries(
        Map.entry('T', 10),
        Map.entry('J', 11),
        Map.entry('Q', 12),
        Map.entry('K', 13),
        Map.entry('A', 14)
    );

    private final String cards;
    private final IntUnaryOperator cardEvaluator;
    private final List<Integer> counts;

    public Hand(String cards, boolean jackIsWild) {
      this.cards = cards;
      cardEvaluator = (card) -> cardValue((char) card, jackIsWild);
      Map<Integer, Long> frequencies = computeFrequencies(cards);
      counts = compileGroupCounts(frequencies, jackIsWild);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(Hand other) {
      int comparison = compareGroups(other);
      if (comparison == 0) {
        comparison = compareCards(other);
      }
      return comparison;
    }

    private int compareGroups(Hand other) {
      Iterator<Integer> thisIterator = counts.iterator();
      Iterator<Integer> otherIterator = other.counts.iterator();
      int comparison = 0;
      while (thisIterator.hasNext() && otherIterator.hasNext() && comparison == 0) {
        comparison = thisIterator.next() - otherIterator.next();
      }
      return comparison;
    }

    private int compareCards(Hand other) {
      char[] thisCards = cards.toCharArray();
      char[] otherCards = other.cards.toCharArray();
      int comparison = 0;
      for (int i = 0; i < thisCards.length && comparison == 0; i++) {
        comparison =
            cardEvaluator.applyAsInt(thisCards[i]) - cardEvaluator.applyAsInt(otherCards[i]);
      }
      return comparison;
    }

    private static Map<Integer, Long> computeFrequencies(String labels) {
      return labels
          .chars()
          .boxed()
          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private static List<Integer> compileGroupCounts(
        Map<Integer, Long> frequencies, boolean jackIsWild) {
      int wildCount = jackIsWild ? frequencies.getOrDefault((int) JACK, 0L).intValue() : 0;
      List<Integer> counts = frequencies
          .entrySet()
          .stream()
          .filter((entry) -> !jackIsWild || entry.getKey() != JACK)
          .sorted(Entry.<Integer, Long>comparingByValue().reversed())
          .map((entry) -> entry.getValue().intValue())
          .collect(Collectors.toList());
      if (wildCount > 0) {
        if (counts.isEmpty()) {
          counts.add(wildCount);
        } else {
          counts.set(0, counts.get(0) + wildCount);
        }
      }
      return counts;
    }

    private int cardValue(char card, boolean jackIsWild) {
      return (!jackIsWild || card != JACK)
          ? (Character.isDigit(card)
              ? Character.getNumericValue(card)
              : FACE_CARD_VALUES.get(card))
          : 0;
    }

  }

}
