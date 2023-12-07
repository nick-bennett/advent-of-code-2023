package com.nickbenn.adventofcode.day7;

import com.nickbenn.adventofcode.util.DataSource;
import com.nickbenn.adventofcode.util.Defaults;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CamelCard {

  private static final Pattern HAND_BID_EXTRACTOR = Pattern.compile("(\\S+)\\s+(\\d+)");
  private static final Map<Character, Integer> NON_WILD_VALUES = Map.ofEntries(
      Map.entry('2', 0),
      Map.entry('3', 1),
      Map.entry('4', 2),
      Map.entry('5', 3),
      Map.entry('6', 4),
      Map.entry('7', 5),
      Map.entry('8', 6),
      Map.entry('9', 7),
      Map.entry('T', 8),
      Map.entry('J', 9),
      Map.entry('Q', 10),
      Map.entry('K', 11),
      Map.entry('A', 12)
  );
  private static final Map<Character, Integer> WILD_VALUES = Map.ofEntries(
      Map.entry('J', 0),
      Map.entry('2', 1),
      Map.entry('3', 2),
      Map.entry('4', 3),
      Map.entry('5', 4),
      Map.entry('6', 5),
      Map.entry('7', 6),
      Map.entry('8', 7),
      Map.entry('9', 8),
      Map.entry('T', 9),
      Map.entry('Q', 10),
      Map.entry('K', 11),
      Map.entry('A', 12)
  );

  private final Map<Hand, Integer> hands;

  public CamelCard(boolean jackIsWild) throws IOException {
    this(Defaults.INPUT_FILE, jackIsWild);
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
        .peek(System.out::println)
        .mapToInt((entry) -> count[0]++ * entry.getValue())
        .sum();
  }

  private static class Hand implements Comparable<Hand> {

    private final String labels;
    private final boolean jackIsWild;
    private final List<Integer> counts;

    public Hand(String labels, boolean jackIsWild) {
      this.labels = labels;
      this.jackIsWild = jackIsWild;
      Map<Integer, Long> frequencies = labels
          .chars()
          .boxed()
          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
      int wildCount = jackIsWild ? frequencies.getOrDefault((int) 'J', 0L).intValue() : 0;
      System.out.println(wildCount);
      counts = frequencies
          .entrySet()
          .stream()
          .filter((entry) -> !jackIsWild || entry.getKey() != 'J')
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
    }

    public String getLabels() {
      return labels;
    }

    @Override
    public int compareTo(Hand other) {
      Iterator<Integer> thisIterator = counts.iterator();
      Iterator<Integer> otherIterator = other.counts.iterator();
      int comparison = 0;
      while (thisIterator.hasNext() && otherIterator.hasNext()) {
        comparison = thisIterator.next() - otherIterator.next();
        if (comparison != 0) {
          break;
        }
      }
      if (comparison == 0) {
        if (thisIterator.hasNext()) {
          comparison = 1;
        } else if (otherIterator.hasNext()) {
          comparison = -1;
        }
      }
      if (comparison == 0) {
        Map<Character, Integer> valueMap = jackIsWild ? WILD_VALUES : NON_WILD_VALUES;
        char[] thisLabels = labels.toCharArray();
        char[] otherLabels = other.labels.toCharArray();
        for (int i = 0; i < thisLabels.length; i++) {
          comparison = Integer.compare(valueMap.get(thisLabels[i]), valueMap.get(otherLabels[i]));
          if (comparison != 0) {
            break;
          }
        }
      }
      return comparison;
    }

    @Override
    public String toString() {
      return String.format("%1$s[%2$s : %3$s]", getClass().getSimpleName(), labels, counts);
    }
  }

}
