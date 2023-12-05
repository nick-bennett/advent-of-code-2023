package com.nickbenn.adventofcode.day5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SeedFertilizerTest {

  SeedFertilizer seedFertilizer;

  @BeforeEach
  void setUp() throws IOException {
    seedFertilizer = new SeedFertilizer();
  }

  @Test
  void getLowestLocation() {
    assertEquals(35, seedFertilizer.getLowestLocation());
  }

  @Test
  void getLowestInterpolatedLocation() {
    assertEquals(46, seedFertilizer.getLowestInterpolatedLocation());
  }

}