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
package com.nickbenn.adventofcode.day7;

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

public class CamelCard {

  private static final Pattern HAND_BID_EXTRACTOR = Pattern.compile("(\\S+)\\s+(\\d+)");

  private final Map<Hand, Integer> hands;

  public CamelCard(boolean jackIsWild) throws IOException {
    this(DataSource.DEFAULT_INPUT_FILE, jackIsWild);
  }

  public CamelCard(String inputFile, boolean jackIsWild) throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {
      hands = lines
          .map(HAND_BID_EXTRACTOR::matcher)
          .filter(Matcher::matches)
          .collect(Collectors.toMap((matcher) -> new Hand(matcher.group(1), jackIsWild),
              (matcher) -> Integer.parseInt(matcher.group(2))));
    }
  }

  public static void main(String[] args) throws IOException {
    System.out.println(new CamelCard(false).getWinnings());
    System.out.println(new CamelCard(true).getWinnings());
  }

  public int getWinnings() {
    int[] count = {1};
    return hands
        .entrySet()
        .stream()
        .sorted(Entry.comparingByKey())
        .mapToInt((entry) -> count[0]++ * entry.getValue())
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
