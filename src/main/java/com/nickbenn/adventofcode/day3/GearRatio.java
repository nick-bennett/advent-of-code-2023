package com.nickbenn.adventofcode.day3;

import com.nickbenn.adventofcode.util.DataSource;
import com.nickbenn.adventofcode.util.Defaults;
import com.nickbenn.adventofcode.util.Location;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GearRatio {

  private static final Pattern EXTRACTOR = Pattern.compile("(\\*)|([^\\d.*])|(\\d+)");

  private final NavigableMap<Location, String> numbers;
  private final NavigableSet<Location> gears;
  private final NavigableSet<Location> symbols;

  public GearRatio() throws IOException {
    this(Defaults.INPUT_FILE);
  }

  public GearRatio(String inputFile) throws IOException {
    try (Stream<String> lines = new DataSource.Builder()
        .setInputFile(inputFile)
        .setContext(getClass())
        .build()
        .lines()) {
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
        .filter((entry) -> {
          Location location = entry.getKey();
          int startColumn = location.column() - 1;
          int endColumn = location.column() + entry.getValue().length() + 1;
          Location start = new Location(location.row() - 1, startColumn);
          Location end = new Location(location.row() + 1, endColumn);
          return symbols
              .tailSet(start)
              .headSet(end)
              .stream()
              .anyMatch((loc) -> loc.column() >= startColumn && loc.column() < endColumn);
        })
        .map(Entry::getValue)
        .mapToInt(Integer::parseInt)
        .sum();
  }

  public int sumGearRatios() {
    return gears
        .stream()
        .map((location) -> {
          int column = location.column();;
          Location start = new Location(location.row() - 1, 0);
          Location end = new Location(location.row() + 1, column + 1);
          return numbers
              .tailMap(start, true)
              .headMap(end, true)
              .entrySet()
              .stream()
              .filter((entry) -> entry.getKey().column() <= column + 1
                  && entry.getKey().column() + entry.getValue().length() >= column)
              .map(Entry::getValue)
              .collect(Collectors.toList());
        })
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
          .forEach((result) -> {
            Location location = new Location(row, result.start());
            String numberWord = result.group(3);
            if (numberWord != null) {
              numbers.put(location, numberWord);
            } else {
              symbols.add(location);
              if (result.group(1) != null) {
                gears.add(location);
              }
            }
          });
    });
  }

}
