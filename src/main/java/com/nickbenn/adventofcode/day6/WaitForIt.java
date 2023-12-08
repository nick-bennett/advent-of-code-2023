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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WaitForIt {

  private static final long[][] separateRaces =
      new long[][]{{46, 358}, {68, 1054}, {98, 1807}, {66, 1080}};
  private static final long[][] joinedRaces = new long[][]{{46689866, 358105418071080L}};

  private final List<long[]> parameterPairs;

  public WaitForIt(long[][] parameterPairs) {
    this.parameterPairs = Arrays.stream(parameterPairs).collect(Collectors.toList());
  }

  public static void main(String[] args) {
    System.out.println(new WaitForIt(separateRaces).countWinningCombinations());
    System.out.println(new WaitForIt(joinedRaces).countWinningCombinations());
  }

  public long countWinningCombinations() {
    return parameterPairs
        .stream()
        .mapToLong((pair) -> countWins(pair[0], pair[1]))
        .reduce(1, (a, b) -> a * b);
  }

  private long countWins(long time, long distanceRecord) {
    long count;
    long discriminantSquared = time * time - 4 * distanceRecord;
    if (discriminantSquared >= 0) {
      double discriminant = Math.sqrt(discriminantSquared);
      long lowerRoot = (long) Math.floor((time - discriminant) / 2.0) + 1;
      long upperRoot = (long) Math.ceil((time + discriminant) / 2.0) - 1;
      count = upperRoot - lowerRoot + 1;
    } else {
      count = 0;
    }
    return count;
  }

}
