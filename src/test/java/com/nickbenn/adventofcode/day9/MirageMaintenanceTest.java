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
package com.nickbenn.adventofcode.day9;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

class MirageMaintenanceTest {

  MirageMaintenance maintenance;

  @BeforeEach
  void setUp() throws IOException {
    maintenance = new MirageMaintenance();
  }

  @ParameterizedTest
  @CsvFileSource(resources = "tests.csv", useHeadersInDisplayName = true)
  void sumExtrapolations(boolean forward, long expected) {
    assertEquals(expected, maintenance.sumExtrapolations(forward));
  }

}