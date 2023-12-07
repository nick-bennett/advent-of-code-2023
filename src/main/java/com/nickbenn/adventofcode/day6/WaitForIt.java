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
    WaitForIt waitForIt;
    waitForIt = new WaitForIt(separateRaces);
    System.out.println(waitForIt.countWinningCombinations());
    waitForIt = new WaitForIt(joinedRaces);
    System.out.println(waitForIt.countWinningCombinations());
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
      long lowerRoot = (long) Math.floor((time - discriminant) / 2d) + 1;
      long upperRoot = (long) Math.ceil((time + discriminant) / 2d) - 1;
      count = upperRoot - lowerRoot + 1;
    } else {
      count = 0;
    }
    return count;
  }

}
