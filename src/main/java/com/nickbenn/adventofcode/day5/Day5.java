package com.nickbenn.adventofcode.day5;

import com.nickbenn.adventofcode.util.DataSource;
import com.nickbenn.adventofcode.util.Defaults;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day5 {

  public Day5() throws IOException {
    this(Defaults.INPUT_FILE);
  }

  public Day5(String inputFile) throws IOException {
    try (Stream<String> lines = new DataSource.Builder()
        .setInputFile(inputFile)
        .setContext(getClass())
        .build()
        .lines()) {
    }
  }

}
