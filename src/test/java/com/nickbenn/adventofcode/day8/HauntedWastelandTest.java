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
package com.nickbenn.adventofcode.day8;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HauntedWastelandTest {

  @ParameterizedTest
  @CsvSource(value = {
      "input file, expected steps",
      "countSteps-1.txt, 2",
      "countSteps-2.txt, 6"
  }, useHeadersInDisplayName = true)
  void countSteps(String file, long expected) throws IOException {
    HauntedWasteland wasteland = new HauntedWasteland(file);
    assertEquals(expected, wasteland.countSteps());
  }

  @Test
  void countParallelSteps() throws IOException {
    HauntedWasteland wasteland = new HauntedWasteland("countParallelSteps.txt");
    assertEquals(6, wasteland.countParallelSteps());
  }

}