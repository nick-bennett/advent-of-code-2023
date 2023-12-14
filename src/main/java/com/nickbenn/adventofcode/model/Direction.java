package com.nickbenn.adventofcode.model;

public interface Direction {

  int rowOffset();

  int columnOffset();

  Direction opposite();

  Direction nextClockwise();

  Direction nextCounterClockwise();

}
