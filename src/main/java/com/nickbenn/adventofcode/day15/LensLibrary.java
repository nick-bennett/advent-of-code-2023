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
package com.nickbenn.adventofcode.day15;

import static com.nickbenn.adventofcode.view.Presentation.NUMERIC_SOLUTION_FORMAT;

import com.nickbenn.adventofcode.view.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LensLibrary {

  private static final int MULTIPLIER = 17;
  private static final int MASK = 255;
  private static final Pattern SPLITTER = Pattern.compile(",");
  private static final Pattern EXTRACTOR = Pattern.compile("^\\s*([^-=]+)(?:(-)|(=))(\\d*)\\s*$");

  private final DataSource source;

  public LensLibrary() throws IOException{
    this(DataSource.DEFAULT_INPUT_FILE);
  }

  public LensLibrary(String inputFile) throws IOException {
    source = new DataSource.Builder(this)
        .setInputFile(inputFile)
        .build();
  }

  public static void main(String[] args) throws IOException {
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 1, new LensLibrary().getHashSum());
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 2, new LensLibrary().getFocusingPower());
  }

  public int getHashSum() throws IOException {
    try (Stream<String> lines = source.blocks(SPLITTER)) {
      return lines
          .mapToInt(this::hash)
          .sum();
    }
  }

  public int getFocusingPower() throws IOException {
    return buildBoxes()
        .entrySet()
        .stream()
        .mapToInt((boxEntry) -> {
          int boxNumber = boxEntry.getKey();
          int[] position = {0};
          return (boxNumber + 1) * boxEntry
              .getValue()
              .values()
              .stream()
              .mapToInt((focalLength) -> ++position[0] * focalLength)
              .sum();
        })
        .sum();
  }

  private Map<Integer, Map<String, Integer>> buildBoxes() throws IOException {
    Map<Integer, Map<String, Integer>> boxes = new HashMap<>();
    try (Stream<String> lines = source.blocks(SPLITTER)) {
      lines
          .map(EXTRACTOR::matcher)
          .filter(Matcher::matches)
          .forEachOrdered((matcher) -> {
            String label = matcher.group(1);
            Map<String, Integer> contents =
                boxes.computeIfAbsent(hash(label), (box) -> new LinkedHashMap<>());
            if (matcher.group(2) != null) {
              contents.remove(label);
            } else {
              contents.put(label, Integer.parseInt(matcher.group(4)));
            }
          });
    }
    return boxes;
  }

  private int hash(String input) {
    return input
        .chars()
        .reduce(0, (sum, next) -> (sum + next) * MULTIPLIER & MASK);
  }

}
