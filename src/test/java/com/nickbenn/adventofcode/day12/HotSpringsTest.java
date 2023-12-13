package com.nickbenn.adventofcode.day12;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

class HotSpringsTest {

  @ParameterizedTest
  @CsvFileSource(resources = "tests.csv", useHeadersInDisplayName = true)
  void sumArrangements(int repetitions, long expected) throws IOException {
    assertEquals(expected, new HotSprings(repetitions).sumArrangements());
  }

}