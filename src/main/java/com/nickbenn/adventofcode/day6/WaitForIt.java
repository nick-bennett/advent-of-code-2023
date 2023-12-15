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
package com.nickbenn.adventofcode.day6;

import static com.nickbenn.adventofcode.view.Presentation.NUMERIC_SOLUTION_FORMAT;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements a solution to the day 6 problem, which requires finding the number of integers in the
 * domain of a quadratic expression for which the expression has a positive value. The only
 * distinction between parts 1 and 2 is the input data; in this implementation, this data is taken
 * from a {@code long[][]} constructor parameter.
 *
 * @see <a href="https://adventofcode.com/2023/day/6">"Day 6: Wait For It"</a>.
 */
public class WaitForIt {

  private static final long[][] separateRaces =
      new long[][]{{46, 358}, {68, 1054}, {98, 1807}, {66, 1080}};
  private static final long[][] joinedRaces = new long[][]{{46689866, 358105418071080L}};

  private final List<long[]> raceTimesAndDistances;

  /**
   * Initializes this instance with specified pairs of time &amp; distance values. In each nested
   * array, the first element is the time limit for a race, while the second is the stated record
   * distance for the race.
   *
   * @param raceTimesAndDistances Paired time &amp; distance values.
   */
  public WaitForIt(long[][] raceTimesAndDistances) {
    this.raceTimesAndDistances = Arrays.stream(raceTimesAndDistances).toList();
  }

  /**
   * For each of parts 1 and 2, creates an instance of {@link WaitForIt} with the relevant data,
   * invokes the {@link #countWinningCombinations()}, and prints the result.
   *
   * @param args Command-line arguments (currently ignored).
   */
  public static void main(String[] args) {
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 1,
        new WaitForIt(separateRaces).countWinningCombinations());
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 2,
        new WaitForIt(joinedRaces).countWinningCombinations());
  }

  /**
   * Counts the number of integral values of <em>x</em> in each race, for which the inequality
   * <em>x</em>(<em>T - x</em>) &gt; <em>D</em> holds (where <em>T</em> is the time limit for the
   * race, and <em>D</em> is the stated distance record), then computes and returns the product of
   * these counts.
   * <p>Implementation notes: Rather than iterating across the range 0..<em>T</em> and testing the
   * above inequality for each value, the quadratic formula is used to identify the endpoints of the
   * range of values for which the inequality holds in each race.</p>
   */
  public long countWinningCombinations() {
    return raceTimesAndDistances
        .stream()
        .mapToLong((pair) -> countWins(pair[0], pair[1]))
        .reduce(1, (a, b) -> a * b);
  }

  private long countWins(long time, long distanceRecord) {
    long count = 0;
    long discriminantSquared = time * time - 4 * distanceRecord;
    if (discriminantSquared >= 0) {
      double discriminant = Math.sqrt(discriminantSquared);
      long lowerRoot = (long) Math.floor((time - discriminant) / 2.0) + 1;
      long upperRoot = (long) Math.ceil((time + discriminant) / 2.0) - 1;
      count = upperRoot - lowerRoot + 1;
    }
    return count;
  }

}
