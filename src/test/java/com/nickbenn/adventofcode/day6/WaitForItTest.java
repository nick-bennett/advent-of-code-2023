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
package com.nickbenn.adventofcode.day6;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class WaitForItTest {

  @Test
  void countWinningCombinations_separated() {
    WaitForIt waitForIt = new WaitForIt(new long[][]{{7, 9}, {15, 40}, {30, 200}});
    assertEquals(288, waitForIt.countWinningCombinations());
  }

  @Test
  void countWinningCombinations_joined() {
    WaitForIt waitForIt = new WaitForIt(new long[][]{{71530, 940200}});
    assertEquals(71503, waitForIt.countWinningCombinations());
  }

}