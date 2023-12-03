package com.nickbenn.adventofcode.day3;

import com.nickbenn.adventofcode.util.DataSource;
import com.nickbenn.adventofcode.util.Defaults;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GearRatio {

  private static final char EMPTY_SPACE = '.';
  private static final char GEAR_SYMBOL = '*';

  private final Map<Integer, Map<Integer, String>> partNumbers;
  private final Set<Location> gears;

  public GearRatio() throws IOException {
    this(Defaults.INPUT_FILE);
  }

  public GearRatio(String inputFile) throws IOException {
    try (Stream<String> lines = new DataSource.Builder()
        .setInputFile(inputFile)
        .setContext(getClass())
        .build()
        .lines()) {
      char[][] schematic = lines
          .map(String::toCharArray)
          .toArray(char[][]::new);
      partNumbers = new TreeMap<>();
      gears = new TreeSet<>();
      analyzeSchematic(schematic);
    }
  }

  public static void main(String[] args) throws IOException {
    GearRatio ratio = new GearRatio();
    System.out.println(new GearRatio().sumPartNumbers());
    System.out.println(new GearRatio().sumGearRatios());
  }

  public int sumPartNumbers() {
    return partNumbers
        .values()
        .stream()
        .flatMap((map) -> map.values().stream())
        .mapToInt(Integer::parseInt)
        .sum();
  }

  public long sumGearRatios() {
    return gears
        .stream()
        .map((gear) -> {
          int gearRow = gear.row();
          int gearColumn = gear.column();
          return partNumbers
              .entrySet()
              .stream()
              .skip(Math.max(gearRow - 1, 0))
              .takeWhile((entry) -> entry.getKey() <= gearRow + 1)
              .flatMap((entry) -> entry.getValue().entrySet().stream())
              .filter((entry) -> {
                int col = entry.getKey();
                String partNumberWord = entry.getValue();
                int partNumberLength = partNumberWord.length();
                return col <= gearColumn + 1 && col + partNumberLength > gearColumn - 1;
              })
              .map(Entry::getValue)
              .collect(Collectors.toList());
        })
        .filter((numbers) -> numbers.size() == 2)
        .mapToLong((numbers) -> numbers
            .stream()
            .mapToLong(Long::parseLong)
            .reduce(1L, (a, b)-> a * b)
        )
        .sum();
  }

  private void analyzeSchematic(char[][] schematic) {
    StringBuilder builder = new StringBuilder();
    for (int rowIndex = 0; rowIndex < schematic.length; rowIndex++) {
      Map<Integer, String> rowPartNumbers = new TreeMap<>();
      partNumbers.put(rowIndex, rowPartNumbers);
      boolean inNumber = false;
      boolean symbolAdjacent = false;
      int partNumberStart = -1;
      builder.setLength(0);
      for (int colIndex = 0; colIndex < schematic[rowIndex].length; colIndex++) {
        char c = schematic[rowIndex][colIndex];
        if (Character.isDigit(c)) {
          builder.append(c);
          symbolAdjacent |= isSymbolAdjacent(schematic, rowIndex, colIndex, inNumber);
          if (!inNumber) {
            partNumberStart = colIndex;
            inNumber = true;
          }
        } else {
          if (inNumber) {
            if (symbolAdjacent) {
              rowPartNumbers.put(partNumberStart, builder.toString());
            }
            inNumber = false;
            symbolAdjacent = false;
            builder.setLength(0);
          }
          if (c == GEAR_SYMBOL) {
            gears.add(new Location(rowIndex, colIndex));
          }
        }
      }
      if (inNumber && symbolAdjacent) {
        rowPartNumbers.put(partNumberStart, builder.toString());
      }
    }
  }

  private boolean isSymbolAdjacent(char[][] schematic, int row, int column, boolean rightOnly) {
    boolean result = false;
    int startRow = row - ((row > 0) ? 1 : 0);
    int endRow = row + ((row < schematic.length - 1) ? 1 : 0);
    int startCol = rightOnly
        ? column + 1
        : column - ((column > 0) ? 1 : 0);
    int endCol = column + ((column < schematic[row].length - 1) ? 1 : 0);
    outer:
    for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
      for (int colIindex = startCol; colIindex <= endCol; colIindex++) {
        char c = schematic[rowIndex][colIindex];
        if (c != EMPTY_SPACE && !Character.isDigit(c)) {
          result = true;
          break outer;
        }
      }
    }
    return result;
  }

  private record Location(int row, int column) implements Comparable<Location> {

    private static final Comparator<Location> COMPARATOR = Comparator
        .comparing(Location::row)
        .thenComparing(Location::column);

    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") Location other) {
      return COMPARATOR.compare(this, other);
    }

  }

}
