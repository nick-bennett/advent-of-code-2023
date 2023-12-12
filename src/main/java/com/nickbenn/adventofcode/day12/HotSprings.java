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
import java.util.ListIterator;
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

  private final Map<MemoizationKey, Long> memo;
  private final List<List<Character>> conditionRecords;
  private final List<List<Integer>> damageGroupRecords;
  private final List<List<Integer>> maxRemainingGroupRecords;

  public HotSprings(int repetitions) throws IOException {
    this(DataSource.DEFAULT_INPUT_FILE, repetitions);
  }

  public HotSprings(String inputFile, int repetitions) throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {
      memo = new HashMap<>();
      conditionRecords = new LinkedList<>();
      damageGroupRecords = new LinkedList<>();
      maxRemainingGroupRecords = new LinkedList<>();
      lines.forEach((line) ->
          parse(line, conditionRecords, damageGroupRecords, maxRemainingGroupRecords, repetitions));
    }
  }

  public static void main(String[] args) throws IOException {
    System.out.println(new HotSprings(1).sumArrangements());
    System.out.println(new HotSprings(5).sumArrangements());
  }

  public long sumArrangements() {
    long sum = 0;
    Iterator<List<Character>> conditionRecordIterator = conditionRecords.iterator();
    Iterator<List<Integer>> damageGroupRecordIterator = damageGroupRecords.iterator();
    Iterator<List<Integer>> maxRemainingGroupsRecordIterator = maxRemainingGroupRecords.iterator();
    while (conditionRecordIterator.hasNext()
        && damageGroupRecordIterator.hasNext()
        && maxRemainingGroupsRecordIterator.hasNext()) {
      List<Character> conditions = conditionRecordIterator.next();
      List<Integer> groups = damageGroupRecordIterator.next();
      List<Integer> maxRemainingGroups = maxRemainingGroupsRecordIterator.next();
      long count = countArrangements(conditions, groups, maxRemainingGroups, 0, OPERATIONAL,
          totalDamagePotential(conditions), totalOperationalPotential(conditions),
          totalCombinedGroupSize(groups));
      sum += count;
    }
    return sum;
  }

  private void parse(String line, List<List<Character>> conditionRecords,
      List<List<Integer>> damageGroupRecords, List<List<Integer>> remainingMaxGroupRecords,
      int repetitions) {
    String[] splitRecord = LINE_SPLITTER.split(line);
    List<Character> conditions = Stream.generate(() -> splitRecord[0])
        .limit(repetitions)
        .collect(Collectors.joining(UNFOLDED_CONDITIONS_DELIMITER))
        .chars()
        .mapToObj((value) -> (char) value)
        .toList();
    remainingMaxGroupRecords.add(maxRemainingGroups(conditions));
    conditionRecords.add(conditions);
    damageGroupRecords.add(
        GROUP_SPLITTER
            .splitAsStream(
                Stream.generate(() -> splitRecord[1])
                    .limit(repetitions)
                    .collect(Collectors.joining(UNFOLDED_GROUPS_DELIMITER))
            )
            .map(Integer::valueOf)
            .toList()
    );
  }

  private long countArrangements(List<Character> conditions, List<Integer> groups,
      List<Integer> maxRemainingGroups, int currentGroupSize, char previous,
      int remainingDamagePotential, int remainingOperationalPotential,
      int remainingCombinedGroupSize) {
    long count;
    MemoizationKey key = new MemoizationKey(conditions, groups, currentGroupSize);
    Long lookupCount = memo.get(key);
    if (lookupCount == null) {
      long remainingGroups = groups.size();
      if (conditions.isEmpty()) {
        count = (groups.isEmpty() ||
            remainingGroups == 1 && currentGroupSize == groups.get(0)) ? 1 : 0;
      } else if (maxRemainingGroups.get(0) < remainingGroups - ((currentGroupSize > 0) ? 1 : 0)
          || remainingOperationalPotential < remainingGroups - 1
          || remainingDamagePotential < remainingCombinedGroupSize
          || conditions.size() < remainingCombinedGroupSize + remainingGroups - 1) {
        count = 0;
      } else {
        List<Character> nextConditions = conditions.subList(1, conditions.size());
        List<Integer> nextMaxRemainingGroups =
            maxRemainingGroups.subList(1, maxRemainingGroups.size());
        count = switch (conditions.get(0)) {
          case OPERATIONAL -> countForOperational(nextConditions, groups, nextMaxRemainingGroups,
              currentGroupSize, previous, remainingDamagePotential, remainingOperationalPotential,
              remainingCombinedGroupSize, false);
          case DAMAGED -> countForDamaged(nextConditions, groups, nextMaxRemainingGroups,
              currentGroupSize, remainingDamagePotential, remainingOperationalPotential,
              remainingCombinedGroupSize, false);
          case UNKNOWN -> countForOperational(nextConditions, groups, nextMaxRemainingGroups,
              currentGroupSize, previous, remainingDamagePotential, remainingOperationalPotential,
              remainingCombinedGroupSize, true)
              + countForDamaged(nextConditions, groups, nextMaxRemainingGroups, currentGroupSize,
              remainingDamagePotential, remainingOperationalPotential, remainingCombinedGroupSize,
              true);
          default -> throw new IllegalArgumentException();
        };
      }
      memo.put(key, count);
    } else {
      count = lookupCount;
    }
    return count;
  }

  private List<Integer> maxRemainingGroups(List<Character> conditions) {
    List<Integer> result = new LinkedList<>();
    int count = 0;
    int consecutiveUnknown = 1;
    for (
        ListIterator<Character> conditionIterator = conditions.listIterator(conditions.size());
        conditionIterator.hasPrevious(); ) {
      char current = conditionIterator.previous();
      switch (current) {
        case OPERATIONAL -> {
          consecutiveUnknown = 1;
        }
        case DAMAGED -> {
          count += (consecutiveUnknown % 2);
          consecutiveUnknown = 0;
        }
        case UNKNOWN -> {
          count += (consecutiveUnknown % 2);
          consecutiveUnknown++;
        }
      }
      result.add(0, count);
    }
    return result;
  }

  private int totalDamagePotential(List<Character> conditions) {
    return (int) conditions
        .stream()
        .filter((condition) -> condition != OPERATIONAL)
        .count();
  }

  private int totalOperationalPotential(List<Character> conditions) {
    return (int) conditions
        .stream()
        .filter((condition) -> condition != DAMAGED)
        .count();
  }

  private int totalCombinedGroupSize(List<Integer> groups) {
    return groups
        .stream()
        .mapToInt(Integer::intValue)
        .sum();
  }

  private long countForOperational(List<Character> conditions, List<Integer> groups,
      List<Integer> maxRemainingGroups, int currentGroupSize, char previous,
      int remainingDamagePotential, int remainingOperationalPotential,
      int remainingCombinedGroupSize, boolean unknown) {
    int adjustedDamagePotential = remainingDamagePotential - (unknown ? 1 : 0);
    int adjustedOperationalPotential = remainingOperationalPotential - 1;
    return (previous != DAMAGED)
        ? countArrangements(conditions, groups, maxRemainingGroups, 0, OPERATIONAL,
            adjustedDamagePotential, adjustedOperationalPotential, remainingCombinedGroupSize)
        : (currentGroupSize != groups.get(0))
            ? 0
            : countArrangements(conditions, groups.subList(1, groups.size()),
                maxRemainingGroups, 0, OPERATIONAL, adjustedDamagePotential,
                adjustedOperationalPotential, remainingCombinedGroupSize);
  }

  private long countForDamaged(List<Character> nextConditions, List<Integer> groups,
      List<Integer> maxRemainingGroups, int currentGroupSize, int remainingDamagePotential,
      int remainingOperationalPotential, int remainingCombinedGroupSize, boolean unknown) {
    int adjustedDamagePotential = remainingDamagePotential - 1;
    int adjustedOperationalPotential = remainingOperationalPotential - (unknown ? 1 : 0);
    return (groups.isEmpty() || currentGroupSize >= groups.get(0))
        ? 0
        : countArrangements(nextConditions, groups, maxRemainingGroups, currentGroupSize + 1,
            DAMAGED, adjustedDamagePotential, adjustedOperationalPotential,
            remainingCombinedGroupSize - 1);
  }

  private record MemoizationKey(
      List<Character> conditions, List<Integer> groups, int currentGroupSize) {
  }

}
