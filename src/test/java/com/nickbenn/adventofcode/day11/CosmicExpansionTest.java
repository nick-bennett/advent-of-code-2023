package com.nickbenn.adventofcode.day11;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CosmicExpansionTest {

  @ParameterizedTest
  @CsvSource(
      value = {
          "expansion, expected",
          "2, 374",
          "10, 1030",
          "100, 8410"
      },
      useHeadersInDisplayName = true
  )
  void getShortestPathsSum(int expansionCofficient, long expected) throws IOException {
    CosmicExpansion expansion = new CosmicExpansion(expansionCofficient);
    assertEquals(expected, expansion.getShortestPathsSum());
  }
}