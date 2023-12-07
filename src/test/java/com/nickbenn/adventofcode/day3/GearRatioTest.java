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