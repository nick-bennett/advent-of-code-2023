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
package com.nickbenn.adventofcode.day09;

import static com.nickbenn.adventofcode.view.Presentation.NUMERIC_SOLUTION_FORMAT;

import com.nickbenn.adventofcode.view.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @see <a href="https://adventofcode.com/2023/day/9">"Day 9: Mirage Maintenance"</a>.
 */
public class MirageMaintenance {

  private static final Pattern SPLITTER = Pattern.compile("\\s+");

  private final List<int[]> sensorReadings;

  public MirageMaintenance() throws IOException {
    this(DataSource.DEFAULT_INPUT_FILE);
  }

  public MirageMaintenance(String inputFile) throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {
      sensorReadings = lines
          .map((line) -> SPLITTER
              .splitAsStream(line)
              .filter(Predicate.not(String::isEmpty))
              .mapToInt(Integer::parseInt)
              .toArray()
          )
          .collect(Collectors.toList());
    }
  }

  public static void main(String[] args) throws IOException {
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 1, new MirageMaintenance().sumExtrapolations(true));
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 2, new MirageMaintenance().sumExtrapolations(false));
  }

  public long sumExtrapolations(boolean forward) {
    return sensorReadings
        .stream()
        .mapToLong((input) -> extrapolate(input, forward))
        .sum();
  }

  private int extrapolate(int[] input, boolean forward) {
    int[] differences = new int[input.length - 1];
    boolean constantDifferences = true;
    for (int i = 0; i < input.length - 1; i++) {
      differences[i] = input[i + 1] - input[i];
      if (i > 0 && differences[i] != differences[i - 1]) {
        constantDifferences = false;
      }
    }
    int recursiveResult = constantDifferences ? differences[0] : extrapolate(differences, forward);
    return forward ? (input[input.length - 1] + recursiveResult) : (input[0] - recursiveResult);
  }

}
