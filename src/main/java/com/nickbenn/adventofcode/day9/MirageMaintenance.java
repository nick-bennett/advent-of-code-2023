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
package com.nickbenn.adventofcode.day9;

import com.nickbenn.adventofcode.util.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    System.out.println(new MirageMaintenance().sumExtrapolations(true));
    System.out.println(new MirageMaintenance().sumExtrapolations(false));
  }

  public long sumExtrapolations(boolean forward) {
    return sensorReadings
        .stream()
        .mapToLong((input) -> extrapolate(input, forward))
        .sum();
  }

  private int extrapolate(int[] input, boolean forward) {
    int[] differences = new int[input.length - 1];

    boolean allZero = true;
    for (int i = 0; i < input.length - 1; i++) {
      if ((differences[i] = input[i + 1] - input[i]) != 0) {
        allZero = false;
      }
    }
    return input[forward ? input.length - 1 : 0]
        + (allZero ? 0 : extrapolate(differences, forward) * (forward ? 1 : -1));
  }

}
