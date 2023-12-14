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

import com.nickbenn.adventofcode.view.DataSource;
import com.nickbenn.adventofcode.view.Presentation;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Implements solutions for parts 1 and 2 of day 1&mdash;that is, reading and analyzes a sequence
 * of lines of text, extracting a 2-digit integer comprising the first and last digits that appear
 * in the line from each, and then summing those numbers.
 * <p>Parts 1 and 2 of the problem differ in what qualifies as a digit:</p>
 * <ul>
 *   <li>In part 1, the only digits recognized are the characters 0..9 from the base-10
 *   Indo-Arabic numerals. The method using this rule is {@link #sumDigits()}.</li>
 *   <li>In part 2, the digit characters 0..9 are recognized, along with the words spelling them
 *   out: "zero", "one", "two", "three", etc. The method employing this relaxed definition is
 *   {@link #sumDigitWords()}.</li>
 * </ul>
 * <p>In both cases, the first and last digits aren't necessarily distinct. For example, in a line
 * comprising simply "9", 9 is both the first digit and the last digit; the resulting 2-digit number
 * for that line would be 99. Similarly, digit words (in part 2) may overlap. For example in the
 * line "oneight", the first digit is 1, represented by the word "one", and the last is 8,
 * represented by "eight".</p>
 *
 * @see <a href="https://adventofcode.com/2023/day/1">"Day 1: Trebuchet?!"</a>.
 */
public class Trebuchet {

  private static final String NON_DIGITS_PATTERN = "\\D";
  private static final Pattern FIRST_DIGIT_OR_WORD_EXTRACTOR =
      Pattern.compile("^.*?((\\d)|zero|one|two|three|four|five|six|seven|eight|nine).*$");
  private static final Pattern LAST_DIGIT_OR_WORD_EXTRACTOR =
      Pattern.compile("^.*((\\d)|zero|one|two|three|four|five|six|seven|eight|nine).*$");
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

  private final String inputFile;

  /**
   * Initializes this instance, using the value of {@link DataSource#DEFAULT_INPUT_FILE} as the name
   * (relative to the package of this class on the classpath) of the file to be read. In other
   * words, using this constructor is equivalent to using
   * {@link #Trebuchet(String) Trebuchet(DataSource.DEFAULT_INPUT_FILE)}.
   */
  public Trebuchet() {
    this(DataSource.DEFAULT_INPUT_FILE);
  }

  /**
   * Initializes this instance, using the value specified in the {@code inputFile} parameter as the
   * name (relative to the package of this class on the classpath) of the file to be read.
   *
   * @param inputFile Classpath/package-relative location of file from which input is read.
   */
  public Trebuchet(String inputFile) {
    this.inputFile = inputFile;
  }

  /**
   * Creates an instance of {@link Trebuchet} for each of the two parts of the problem, implicitly
   * specifying that input should be read from the file specified in
   * {@link DataSource#DEFAULT_INPUT_FILE}, and invoking the relevant method to compute the required
   * value, which is then printed to the console.
   *
   * @param args Command-line arguments (currently ignored).
   * @throws IOException If the input file specified (implicitly or explicitly) in the constructor
   *                     invocation cannot be found or read.
   */
  public static void main(String[] args) throws IOException {
    System.out.printf(Presentation.NUMERIC_SOLUTION_FORMAT, 1, new Trebuchet().sumDigits());
    System.out.printf(Presentation.NUMERIC_SOLUTION_FORMAT, 2, new Trebuchet().sumDigitWords());
  }

  /**
   * Reads each line of the input file (as specified in the constructor invocation, or
   * {@link DataSource#DEFAULT_INPUT_FILE} if the {@link #Trebuchet()} constructor is used),
   * extracts the first digit found in the line (reading left-to-right) and the last digit found (or
   * the first found, reading right-to-left), combines them to form a two-digit integer, and returns
   * the sum of these.
   *
   * @return The sum of two-digit numbers extracted from the input.
   * @throws IOException If the input file specified (implicitly or explicitly) in the constructor
   *                     invocation cannot be found or read.
   */
  public int sumDigits() throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {
      return lines
          .map((line) -> line.replaceAll(NON_DIGITS_PATTERN, ""))
          .map((line) -> line.charAt(0) + line.substring(line.length() - 1))
          .mapToInt(Integer::parseInt)
          .sum();
    }
  }

  /**
   * Reads each line of the input file (as specified in the constructor invocation, or
   * {@link DataSource#DEFAULT_INPUT_FILE} if the {@link #Trebuchet()} constructor is used),
   * extracts the first digit character or digit word found in the line (reading left-to-right) and
   * the last digit character or digit word found (or the first found, reading right-to-left),
   * combines them to form a two-digit integer, and returns the sum of these.
   *
   * @return The sum of two-digit numbers extracted from the input.
   * @throws IOException If the input file specified (implicitly or explicitly) in the constructor
   *                     invocation cannot be found or read.
   */
  public int sumDigitWords() throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {
      return lines
          .mapToInt(this::getCalibrationValue)
          .sum();
    }
  }

  private int getCalibrationValue(String line) {
    Matcher firstMatcher = FIRST_DIGIT_OR_WORD_EXTRACTOR.matcher(line);
    Matcher lastMatcher = LAST_DIGIT_OR_WORD_EXTRACTOR.matcher(line);
    if (!firstMatcher.matches() || !lastMatcher.matches()) {
      throw new IllegalArgumentException();
    }
    return 10 * value(firstMatcher) + value(lastMatcher);
  }

  private int value(Matcher matcher) {
    String digitCapture = matcher.group(2);
    return (digitCapture != null)
        ? Character.getNumericValue(digitCapture.charAt(0))
        : DIGIT_WORDS.get(matcher.group(1));
  }

}
