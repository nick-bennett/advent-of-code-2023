package com.nickbenn.adventofcode.day3;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GearRatioTest {

  GearRatio gearRatio;

  @BeforeEach
  void setUp() throws IOException {
    gearRatio = new GearRatio();
  }

  @Test
  void sumPartNumbers() {
    assertEquals(4361, gearRatio.sumPartNumbers());
  }

  @Test
  void sumGearRatios() {
    assertEquals(467835, gearRatio.sumGearRatios());
  }

}