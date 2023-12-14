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

import com.nickbenn.adventofcode.view.DataSource;
import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HauntedWasteland {

  private static final String ORIGIN_NAME = "AAA";
  private static final String DESTINATION_NAME = "ZZZ";
  private static final String ORIGIN_INDICATOR = "A";
  private static final String DESTINATION_INDICATOR = "Z";
  private static final Pattern NODE_EXTRACTOR =
      Pattern.compile("^(\\S+)\\s+=\\s*\\((\\S+)\\s*,\\s*(\\S+)\\)\\s*$");

  private final String moves;
  private final Map<String, Node> nodes;
  private final Map<Character, UnaryOperator<Node>> navigator = Map.of(
      'L', Node::getLeft,
      'R', Node::getRight
  );

  public HauntedWasteland() throws IOException {
    this(DataSource.DEFAULT_INPUT_FILE);
  }

  public HauntedWasteland(String inputFile) throws IOException {
    try (Stream<String> lines = DataSource.simpleLines(inputFile, this)) {
      StringBuilder builder = new StringBuilder();
      nodes = lines
          .map(NODE_EXTRACTOR::matcher)
          .peek((matcher) -> builder.append(matcher.matches() ? "" : matcher.replaceAll("")))
          .filter((Matcher::matches))
          .collect(Collectors.toUnmodifiableMap(
              (matcher) -> matcher.group(1),
              (matcher) -> new Node(matcher.group(1), matcher.group(2), matcher.group(3))));
      moves = builder.toString();
      nodes
          .values()
          .forEach((node) -> node.resolve(nodes));
    }
  }

  public static void main(String[] args) throws IOException {
    System.out.println(new HauntedWasteland().countSteps());
    System.out.println(new HauntedWasteland().countParallelSteps());
  }

  public long countSteps() {
    return countSteps(nodes.get(ORIGIN_NAME),
        (Node node) -> node.getName().equals(DESTINATION_NAME));
  }

  public long countParallelSteps() {
    return nodes
        .entrySet()
        .stream()
        .filter((entry) -> entry.getKey().endsWith(ORIGIN_INDICATOR))
        .mapToLong((entry) ->
            countSteps(entry.getValue(), (node) -> node.getName().endsWith(DESTINATION_INDICATOR)))
        .reduce(moves.length(), this::lcm);
  }

  private long countSteps(Node from, Predicate<Node> destinationCheck) {
    char[] moves = this.moves.toCharArray();
    int moveIndex = 0;
    long count = 0;
    for (Node node = from; !destinationCheck.test(node); ) {
      node = navigator.get(moves[moveIndex]).apply(node);
      moveIndex = (moveIndex + 1) % moves.length;
      count++;
    }
    return count;
  }

  private long lcm(long a, long b) {
    return a * b / gcd(a, b);
  }

  private long gcd(long a, long b) {
    return (b == 0) ? Math.abs(a) : gcd(b, a % b);
  }

  private static class Node {

    private final String name;
    private final String leftName;
    private final String rightName;

    private Node left;
    private Node right;

    public Node(String name, String leftName, String rightName) {
      this.name = name;
      this.leftName = leftName;
      this.rightName = rightName;
    }

    public void resolve(Map<String, Node> nodeNames) {
      left = nodeNames.get(leftName);
      right = nodeNames.get(rightName);
    }

    public String getName() {
      return name;
    }

    public Node getLeft() {
      return left;
    }

    public Node getRight() {
      return right;
    }

  }

}
