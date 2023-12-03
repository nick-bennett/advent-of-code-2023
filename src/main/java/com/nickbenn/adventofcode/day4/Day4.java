package com.nickbenn.adventofcode.day4;

import com.nickbenn.adventofcode.util.Defaults;
import java.io.IOException;
import java.util.regex.Pattern;

public class Day4 {

  private static final Pattern EXTRACTOR = Pattern.compile("(\\*)|([^\\d.*])|(\\d+)");

  public Day4() throws IOException {
    this(Defaults.INPUT_FILE);
  }

  public Day4(String inputFile) throws IOException {
  }

  public static void main(String[] args) throws IOException {
    Day4 day4 = new Day4();
  }

}
