package com.nickbenn.adventofcode.util;

import java.util.function.Predicate;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

public interface Direction {

  int rowOffset();

  int columnOffset();

  Direction opposite();

  Direction nextClockwise();

  Direction nextCounterClockwise();

}
