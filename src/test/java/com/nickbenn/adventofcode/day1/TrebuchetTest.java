package com.nickbenn.adventofcode.day1;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class TrebuchetTest {

  @Test
  void sumDigits() throws IOException {
    assertEquals(142, new Trebuchet("input1.txt").sumDigits());
  }

  @Test
  void sumDigitWords() throws IOException {
    assertEquals(281, new Trebuchet("input2.txt").sumDigitWords());
  }

}