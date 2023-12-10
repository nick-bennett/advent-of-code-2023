package com.nickbenn.adventofcode.day10;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class PipeMazeTest {

  @Test
  void maxDistanceInLoop() throws IOException {
    assertEquals(8, new PipeMaze("input1.txt").maxDistanceInLoop());
  }

  @Test
  void areaEnclosedInLoop() throws IOException {
    assertEquals(10, new PipeMaze("input2.txt").areaEnclosedInLoop());
  }
}