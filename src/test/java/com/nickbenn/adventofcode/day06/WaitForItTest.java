/*
 *  Copyright 2023 Nicholas Bennett.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.nickbenn.adventofcode.day06;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WaitForItTest {

  @ParameterizedTest(name = "[{index}] races={0}, expected={1}")
  @MethodSource
  void countWinningCombinations(long[][] races, int expected) {
    assertEquals(expected, new WaitForIt(races).countWinningCombinations());
  }

  static Stream<Arguments> countWinningCombinations() {
    return Stream.of(
        Arguments.of(new long[][]{{7, 9}, {15, 40}, {30, 200}}, 288),
        Arguments.of(new long[][]{{71530, 940200}}, 71503)
    );
  }

}