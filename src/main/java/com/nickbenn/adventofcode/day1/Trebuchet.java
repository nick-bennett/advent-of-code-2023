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
package com.nickbenn.adventofcode.day1;

import com.nickbenn.adventofcode.util.DataSource;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Trebuchet {

  private static final String NON_DIGITS_PATTERN = "\\D";
  private static final Pattern DIGITS_AND_WORDS_PATTERN =
      Pattern.compile("(\\d)|zero|one|two|three|four|five|six|seven|eight|nine");
  private static final Map<String, Integer> DIGIT_WORDS = Map.of(
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

  private final DataSource dataSource;

  public Trebuchet() throws IOException {
    this(DataSource.DEFAULT_INPUT_FILE);
  }

  public Trebuchet(String inputFile) throws IOException {
    dataSource = new DataSource.Builder(this)
        .setInputFile(inputFile)
        .build();
  }

  public static void main(String[] args) throws IOException {
    System.out.println(new Trebuchet().sumDigits());
    System.out.println(new Trebuchet().sumDigitWords());
  }

  public int sumDigits() throws IOException {
    try (Stream<String> lines = dataSource.lines()) {
      return lines
          .map((line) -> line.replaceAll(NON_DIGITS_PATTERN, ""))
          .map((line) -> line.charAt(0) + line.substring(line.length() - 1))
          .mapToInt(Integer::parseInt)
          .sum();
    }
  }

  public int sumDigitWords() throws IOException {
    try (Stream<String> lines = dataSource.lines()) {
      return lines
          .map(DIGITS_AND_WORDS_PATTERN::matcher)
          .mapToInt(Trebuchet::getCalibrationValue)
          .sum();
    }
  }

  private static int getCalibrationValue(Matcher matcher) {
    int sum = 0;
    int lastDigit = -1;
    int startPosition = 0;
    while (matcher.find(startPosition)) {
      String match = matcher.group();
      boolean trueDigit = (matcher.group(1) != null);
      int digit = trueDigit
          ? Character.getNumericValue(match.charAt(0))
          : DIGIT_WORDS.get(match);
      if (lastDigit < 0) {
        sum += 10 * digit;
      }
      lastDigit = digit;
      startPosition++;
    }
    return sum + lastDigit;
  }

}
