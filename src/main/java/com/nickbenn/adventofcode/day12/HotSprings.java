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
package com.nickbenn.adventofcode.day12;

import com.nickbenn.adventofcode.util.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HotSprings {

  private static final Pattern LINE_SPLITTER = Pattern.compile("\\s+");
  private static final Pattern GROUP_SPLITTER = Pattern.compile("\\s*,\\s*");
  private static final char OPERATIONAL = '.';
  private static final char DAMAGED = '#';
  private static final char UNKNOWN = '?';
  private static final String UNFOLDED_CONDITIONS_DELIMITER = "?";
  private static final String UNFOLDED_GROUPS_DELIMITER = ",";

  private final Map<MemoKey, Long> memo;
  private final List<String> conditionRecords;
  private final List<List<Integer>> damageGroupRecords;

  public HotSprings(int repetitions) throws IOException {
    this(DataSource.DEFAULT_INPUT_FILE, repetitions);
  }

  public HotSprings(String inputFile, int repetitions) throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {
      memo = new HashMap<>();
      conditionRecords = new LinkedList<>();
      damageGroupRecords = new LinkedList<>();
      lines.forEach((line) -> parse(line, conditionRecords, damageGroupRecords, repetitions));
    }
  }

  public static void main(String[] args) throws IOException {
    System.out.println(new HotSprings(1).sumArrangements());
    System.out.println(new HotSprings(5).sumArrangements());
  }

  public long sumArrangements() {
    long sum = 0;
    Iterator<String> conditionRecordIterator = conditionRecords.iterator();
    Iterator<List<Integer>> damageGroupRecordIterator = damageGroupRecords.iterator();
    while (conditionRecordIterator.hasNext()) {
      String conditions = conditionRecordIterator.next();
      List<Integer> groups = damageGroupRecordIterator.next();
      long count = getCount(conditions, groups, 0, OPERATIONAL);
      sum += count;
    }
    return sum;
  }

  private void parse(String line, List<String> conditionRecords,
      List<List<Integer>> damageGroupRecords, int repetitions) {
    String[] splitRecord = LINE_SPLITTER.split(line);
    String unfoldedConditions = Stream.generate(() -> splitRecord[0])
        .limit(repetitions)
        .collect(Collectors.joining(UNFOLDED_CONDITIONS_DELIMITER));
    conditionRecords.add(unfoldedConditions);
    List<Integer> unfoldedGroupSizes = GROUP_SPLITTER
        .splitAsStream(
            Stream.generate(() -> splitRecord[1])
                .limit(repetitions)
                .collect(Collectors.joining(UNFOLDED_GROUPS_DELIMITER))
        )
        .map(Integer::valueOf)
        .toList();
    damageGroupRecords.add(unfoldedGroupSizes);
  }

  private long getCount(
      String conditions, List<Integer> groups, int currentGroupSize, char previous) {
    long count;
    MemoKey key = new MemoKey(conditions, groups, currentGroupSize);
    count = memo.getOrDefault(key, Long.MIN_VALUE);
    if (count < 0) {
      count = count(conditions, groups, currentGroupSize, previous);
      memo.put(key, count);
    }
    return count;
  }

  private long count(String conditions, List<Integer> groups, int currentGroupSize, char previous) {
    long count;
    if (conditions.isEmpty()) {
      count = (groups.isEmpty() || groups.size() == 1 && currentGroupSize == groups.get(0)) ? 1 : 0;
    } else {
      char current = conditions.charAt(0);
      conditions = conditions.substring(1);
      count = switch (current) {
        case OPERATIONAL -> countForOperational(conditions, groups, currentGroupSize, previous);
        case DAMAGED -> countForDamaged(conditions, groups, currentGroupSize);
        case UNKNOWN -> countForOperational(conditions, groups, currentGroupSize, previous)
            + countForDamaged(conditions, groups, currentGroupSize);
        default -> throw new IllegalArgumentException();
      };
    }
    return count;
  }

  private long countForOperational(
      String conditions, List<Integer> groups, int currentGroupSize, char previous) {
    return (previous != DAMAGED)
        ? getCount(conditions, groups, 0, OPERATIONAL)
        : (currentGroupSize != groups.get(0))
            ? 0
            : getCount(conditions, groups.subList(1, groups.size()), 0, OPERATIONAL);
  }

  private long countForDamaged(String conditions, List<Integer> groups, int currentGroupSize) {
    return (groups.isEmpty() || currentGroupSize >= groups.get(0))
        ? 0
        : getCount(conditions, groups, currentGroupSize + 1, DAMAGED);
  }

  private record MemoKey(String conditions, List<Integer> groups, int currentGroupSize) {
  }

}
