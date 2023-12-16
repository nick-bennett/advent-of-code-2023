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
package com.nickbenn.adventofcode.day17;

import com.nickbenn.adventofcode.view.DataSource;
import java.io.IOException;
import java.util.stream.Stream;

public class Day17 {

  public Day17() throws IOException {
    this(DataSource.DEFAULT_INPUT_FILE);
  }

  public Day17(String inputFile) throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {

    }
  }

  public static void main(String[] args) throws IOException {
//    System.out.printf(NUMERIC_SOLUTION_FORMAT, 1, new Day17().???());
  }

}
