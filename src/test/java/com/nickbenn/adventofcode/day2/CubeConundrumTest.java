package com.nickbenn.adventofcode.day2;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class CubeConundrumTest {

  @Test
  void sumFeasibleGames() throws IOException {
    assertEquals(8, new CubeConundrum().sumFeasibleGames(CubeConundrum.CEILINGS));
  }

  @Test
  void sumPower() throws IOException {
    assertEquals(2286, new CubeConundrum().sumPower());
  }

}