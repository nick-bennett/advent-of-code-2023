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
package com.nickbenn.adventofcode.day13;

import com.nickbenn.adventofcode.util.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PointOfIncidence {

  private static final int VERTICAL_MULTIPLIER = 1;
  private static final int HORIZONTAL_MULTIPLIER = 100;

  private final List<char[][]> patterns;

  public PointOfIncidence() throws IOException {
    this(DataSource.DEFAULT_INPUT_FILE);
  }

  public PointOfIncidence(String inputFile) throws IOException {
    DataSource source = new DataSource.Builder(this)
        .setInputFile(inputFile)
        .build();
    try (Stream<Stream<String>> blocks = source.paragraphLines()) {
      patterns = blocks
          .map((block) -> block
              .map(String::toCharArray)
              .toArray(char[][]::new)
          )
          .toList();
    }
  }

  public static void main(String[] args) throws IOException {
    System.out.println(new PointOfIncidence().getReflectionSum(0));
    System.out.println(new PointOfIncidence().getReflectionSum(1));
  }

  public int getReflectionSum(int smudgesRequired) {
    return patterns
        .stream()
        .mapToInt((pattern) ->
            HORIZONTAL_MULTIPLIER * (getFirstHorizontalReflection(pattern, smudgesRequired) + 1)
                + VERTICAL_MULTIPLIER * (getFirstVerticalReflection(pattern, smudgesRequired) + 1)
        )
        .sum();
  }

  private int getFirstHorizontalReflection(char[][] pattern, int smudgesRequired) {
    return IntStream.range(0, pattern.length - 1)
        .filter((rowIndex) -> {
          int smudgeCount = 0;
          outer:
          for (int upIndex = rowIndex, downIndex = rowIndex + 1;
              upIndex >= 0 && downIndex < pattern.length;
              upIndex--, downIndex++) {
            char[] upRow = pattern[upIndex];
            char[] downRow = pattern[downIndex];
            for (int colIndex = 0; colIndex < pattern[rowIndex].length; colIndex++) {
              if (upRow[colIndex] != downRow[colIndex] && ++smudgeCount > smudgesRequired) {
                break outer;
              }
            }
          }
          return (smudgeCount == smudgesRequired);
        })
        .limit(1)
        .findFirst()
        .orElse(-1);
  }

  private int getFirstVerticalReflection(char[][] pattern, int smudgesRequired) {
    return IntStream.range(0, pattern[0].length - 1)
        .filter((colIndex) -> {
          int smudgeCount = 0;
          outer:
          for (char[] row : pattern) {
            for (int leftIndex = colIndex, rightIndex = colIndex + 1;
                leftIndex >= 0 && rightIndex < row.length;
                leftIndex--, rightIndex++) {
              if (row[leftIndex] != row[rightIndex] && ++smudgeCount > smudgesRequired) {
                break outer;
              }
            }
          }
          return (smudgeCount == smudgesRequired);
        })
        .limit(1)
        .findFirst()
        .orElse(-1);
  }

}
