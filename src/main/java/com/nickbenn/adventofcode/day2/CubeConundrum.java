package com.nickbenn.adventofcode.day2;

import com.nickbenn.adventofcode.util.DataSource;
import com.nickbenn.adventofcode.util.Defaults;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CubeConundrum {

  private static final Pattern LINE_PATTERN = Pattern.compile("^Game\\s+(\\d+):\\s+(.*?)\\s*$");
  private static final Pattern SAMPLE_SPLITTER = Pattern.compile("[,;]\\s*");
  private static final Pattern SAMPLE_PATTERN = Pattern.compile("(\\d+)\\s*(red|green|blue)");
  static final Map<String, Integer> CEILINGS = Map.of(
      "red", 12,
      "green", 13,
      "blue", 14
  );

  private final String inputFile;

  public CubeConundrum() {
    this(Defaults.INPUT_FILE);
  }

  public CubeConundrum(String inputFile) {
    this.inputFile = inputFile;
  }

  public static void main(String[] args) throws IOException {
    System.out.println(new CubeConundrum().sumFeasibleGames(CEILINGS));
    System.out.println(new CubeConundrum().sumPower());
  }

  public int sumFeasibleGames(Map<String, Integer> ceilings) throws IOException {
    try (Stream<String> lines = getLines()) {
      return lines
          .map(Game::parse)
          .filter((game) -> game.isFeasible(ceilings))
          .mapToInt(Game::getId)
          .sum();
    }
  }

  public long sumPower() throws IOException {
    try (Stream<String> lines = getLines()) {
      return lines
          .map(Game::parse)
          .mapToLong(Game::getPower)
          .sum();
    }
  }

  private Stream<String> getLines() throws IOException {
    return new DataSource.Builder()
        .setInputFile(inputFile)
        .setContext(getClass())
        .build()
        .lines();
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
          .reduce(1L, (a, b) -> (long) a * b);
    }

    public int getId() {
      return id;
    }

  }

}
