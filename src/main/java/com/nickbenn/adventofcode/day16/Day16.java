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
package com.nickbenn.adventofcode.day16;

import static com.nickbenn.adventofcode.view.Presentation.NUMERIC_SOLUTION_FORMAT;

import com.nickbenn.adventofcode.view.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day16 {

  private final String inputFile;

  public Day16() throws IOException{
    this(DataSource.DEFAULT_INPUT_FILE);
  }

  public Day16(String inputFile) throws IOException {
    this.inputFile = inputFile;
  }

  public static void main(String[] args) throws IOException {
//    System.out.printf(NUMERIC_SOLUTION_FORMAT, 1, new Day16().???);
  }

}
