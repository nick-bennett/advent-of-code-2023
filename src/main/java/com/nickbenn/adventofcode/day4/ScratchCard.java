package com.nickbenn.adventofcode.day4;

import com.nickbenn.adventofcode.util.DataSource;
import com.nickbenn.adventofcode.util.Defaults;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ScratchCard {

  private final List<DailyNumbers> numbers;

  public ScratchCard() throws IOException {
    this(Defaults.INPUT_FILE);
  }

  public ScratchCard(String inputFile) throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {
      numbers = lines
          .map(DailyNumbers::parse)
          .collect(Collectors.toList());
    }
  }

  public static void main(String[] args) throws IOException {
    ScratchCard scratchCard = new ScratchCard();
    System.out.println(scratchCard.getTotalValue());
    System.out.println(scratchCard.getTotalCards());
  }

  public int getTotalValue() {
    return numbers
        .stream()
        .mapToInt((card) -> {
          int matches = card.getMatches();
          return (matches > 0) ? (1 << (matches - 1)): 0;
        })
        .sum();
  }

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
