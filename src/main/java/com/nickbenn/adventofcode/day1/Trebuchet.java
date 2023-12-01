package com.nickbenn.adventofcode.day1;

import com.nickbenn.adventofcode.util.DataSource;
import com.nickbenn.adventofcode.util.Defaults;
import java.io.IOException;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Trebuchet {

  private static final String NON_DIGITS_PATTERN = "\\D";
  private static final Pattern DIGITS_AND_WORDS_PATTERN =
      Pattern.compile("(\\d)|zero|one|two|three|four|five|six|seven|eight|nine");
  private static final Map<String, Integer> WORDS_DIGIT = Map.of(
      "zero", 0,
      "one", 1,
      "two", 2,
      "three", 3,
      "four", 4,
      "five", 5,
      "six", 6,
      "seven", 7,
      "eight", 8,
      "nine", 9
  );

  private final String inputFile;

  public Trebuchet() {
    this(Defaults.INPUT_FILE);
  }

  public Trebuchet(String inputFile) {
    this.inputFile = inputFile;
  }

  public static void main(String[] args) throws IOException {
    System.out.println(new Trebuchet().sumDigits());
    System.out.println(new Trebuchet().sumDigitWords());
  }

  public int sumDigits() throws IOException {
    try (Stream<String> lines = getLines()) {
      return lines
          .map((line) -> line.replaceAll(NON_DIGITS_PATTERN, ""))
          .map((line) -> line.charAt(0) + line.substring(line.length() - 1))
          .mapToInt(Integer::parseInt)
          .sum();
    }
  }

  public int sumDigitWords() throws IOException {
    try (Stream<String> lines = getLines()) {
      return lines
          .map(DIGITS_AND_WORDS_PATTERN::matcher)
          .mapToInt((matcher) -> {
              int sum = 0;
              int lastDigit = -1;
              int startPosition = 0;
              while (matcher.find(startPosition)) {
                String match = matcher.group();
                boolean trueDigit = (matcher.group(1) != null);
                int digit = trueDigit
                    ? Character.getNumericValue(match.charAt(0))
                    : WORDS_DIGIT.get(match);
                if (lastDigit < 0) {
                  sum += 10 * digit;
                }
                lastDigit = digit;
                startPosition = trueDigit ? matcher.end() : startPosition + 1;
              }
              return sum + lastDigit;
          })
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

}
