package com.nickbenn.adventofcode.day6;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class WaitForItTest {

  @Test
  void countWinningCombinations_separated() {
    WaitForIt waitForIt = new WaitForIt(new long[][]{{7, 9}, {15, 40}, {30, 200}});
    assertEquals(288, waitForIt.countWinningCombinations());
  }

  @Test
  void countWinningCombinations_joined() {
    WaitForIt waitForIt = new WaitForIt(new long[][]{{71530, 940200}});
    assertEquals(71503, waitForIt.countWinningCombinations());
  }

}