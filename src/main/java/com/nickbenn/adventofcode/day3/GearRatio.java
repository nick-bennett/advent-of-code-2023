package com.nickbenn.adventofcode.day3;

import com.nickbenn.adventofcode.util.DataSource;
import com.nickbenn.adventofcode.util.Defaults;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GearRatio {

  private static final Pattern EXTRACTOR = Pattern.compile("(\\*)|([^\\d.*])|(\\d+)");

  private final List<Map<Integer, String>> numbers;
  private final List<BitSet> gears;
  private final List<BitSet> symbols;

  public GearRatio() throws IOException {
    this(Defaults.INPUT_FILE);
  }

  public GearRatio(String inputFile) throws IOException {
    try (Stream<String> lines = new DataSource.Builder()
        .setInputFile(inputFile)
        .setContext(getClass())
        .build()
        .lines()) {
      numbers = new ArrayList<>();
      gears = new ArrayList<>();
      symbols = new ArrayList<>();
      analyzeSchematic(lines);
    }
  }

  public static void main(String[] args) throws IOException {
    GearRatio ratio = new GearRatio();
    System.out.println(ratio.sumPartNumbers());
    System.out.println(ratio.sumGearRatios());
  }

  public int sumPartNumbers() {
    int sum = 0;
    for (int rowIndex = 0; rowIndex < this.numbers.size(); rowIndex++) {
      Map<Integer, String> numbers = this.numbers.get(rowIndex);
      List<BitSet> symbols = this.symbols.subList(
          Math.max(rowIndex - 1, 0), Math.min(rowIndex + 2, this.symbols.size()));
      sum += numbers
          .entrySet()
          .stream()
          .filter((entry) -> {
            int start = entry.getKey();
            int end = start + entry.getValue().length();
            return symbols
                .stream()
                .flatMapToInt(BitSet::stream)
                .anyMatch((pos) -> pos >= start - 1 && pos < end + 1);
          })
          .map(Entry::getValue)
          .mapToInt(Integer::parseInt)
          .sum();
    }
    return sum;
  }

  public int sumGearRatios() {
    int sum = 0;
    for (int rowIndex = 0; rowIndex < this.gears.size(); rowIndex++) {
      BitSet gears = this.gears.get(rowIndex);
      List<Map<Integer, String>> numbers = this.numbers.subList(
          Math.max(rowIndex - 1, 0), Math.min(rowIndex + 2, this.numbers.size()));
      sum += gears
          .stream()
          .boxed()
          .map((pos) -> numbers
              .stream()
              .map(Map::entrySet)
              .flatMap(Set::stream)
              .filter((entry) -> {
                int start = entry.getKey();
                int end = start + entry.getValue().length();
                return pos >= start - 1 && pos < end + 1;
              })
              .map(Entry::getValue)
              .collect(Collectors.toList())
          )
          .filter((nums) -> nums.size() == 2)
          .mapToInt((nums) -> nums
              .stream()
              .mapToInt(Integer::parseInt)
              .reduce(1, (a, b) -> a * b)
          )
          .sum();
    }
    return sum;
  }

  private void analyzeSchematic(Stream<String> lines) {
    lines.forEach((line) -> {
      Map<Integer, String> numbers = new TreeMap<>();
      BitSet symbols = new BitSet();
      BitSet gears = new BitSet();
      this.numbers.add(numbers);
      this.symbols.add(symbols);
      this.gears.add(gears);
      Matcher matcher = EXTRACTOR.matcher(line);
      matcher
          .results()
          .forEach((result) -> {
            int start = result.start();
            String numberWord = result.group(3);
            if (numberWord != null) {
              numbers.put(start, numberWord);
            } else {
              symbols.set(start);
              if (result.group(1) != null) {
                gears.set(start);
              }
            }
          });
    });
  }

}
