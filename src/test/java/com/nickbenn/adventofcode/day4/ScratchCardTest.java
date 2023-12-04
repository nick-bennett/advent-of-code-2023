package com.nickbenn.adventofcode.day4;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScratchCardTest {

  ScratchCard card;

  @BeforeEach
  void setUp() throws IOException {
    card = new ScratchCard();
  }

  @Test
  void getTotalValue() {
    assertEquals(13, card.getTotalValue());
  }

  @Test
  void getTotalCards() {
    assertEquals(30, card.getTotalCards());
  }
}