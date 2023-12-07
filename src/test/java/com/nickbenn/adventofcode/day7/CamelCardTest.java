package com.nickbenn.adventofcode.day7;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CamelCardTest {

  @Test
  void getWinnings_noWild() throws IOException {
    CamelCard camelCard = new CamelCard(false);
    assertEquals(6440, camelCard.getWinnings());
  }

  @Test
  void getWinnings_jacksWild() throws IOException {
    CamelCard camelCard = new CamelCard(true);
    assertEquals(5905, camelCard.getWinnings());
  }

}