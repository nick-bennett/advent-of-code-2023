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
package com.nickbenn.adventofcode.day08;

import static com.nickbenn.adventofcode.view.Presentation.NUMERIC_SOLUTION_FORMAT;

import com.nickbenn.adventofcode.view.DataSource;
import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implements solutions for both parts of day 8, reading an input file with the first line
 * specifying a sequence of left and right turns, and subsequent lines specifying a directed graph,
 * with each line containing a node label and the labels of the nodes adjacent in the left and right
 * directions.
 * <p>After reading the input data, parts 1 and 2 differ significantly in the computations required.
 * Though they share the common element of counting the steps from an initial node to a terminal
 * node, the initial node in part 1 is the node with the label "AAA", and the terminal node is
 * "ZZZ"; for part 2, <em>all</em> nodes with labels ending in "A" are initial nodes, and all nodes
 * with labels ending in "Z" are <em>potential</em> terminal nodes. Further, in part 2, traversal
 * begins at <em>all</em> initial nodes and proceeds in parallel, until all of these paths reach a
 * terminal node <em>in the same step</em> (paths may pass through terminal nodes multiple times).
 *
 * @see <a href="https://adventofcode.com/2023/day/8">"Day 8: Haunted Wasteland"</a>.
 */
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

  /**
   * Initializes this instance, using the value of {@link DataSource#DEFAULT_INPUT_FILE} as the name
   * (relative to the package of this class on the classpath) of the file to be read. In other
   * words, using this constructor is equivalent to using
   * {@link #HauntedWasteland(String) HauntedWasteland(DataSource.DEFAULT_INPUT_FILE)}.
   *
   * @throws IOException If the file referenced by {@link DataSource#DEFAULT_INPUT_FILE}} cannot be
   *                     found or read.
   */
  public HauntedWasteland() throws IOException {
    this(DataSource.DEFAULT_INPUT_FILE);
  }

  /**
   * Initializes this instance, using the value specified in the {@code inputFile} parameter as the
   * name (relative to the package of this class on the classpath) of the file to be read, parsing
   * the left and right turn sequence at the start of the file, then parsing each successive line as
   * a node label, followed by a pair of labels for the adjacent nodes in the left and right
   * directions.
   *
   * @param inputFile Classpath/package-relative location of file from which input is read.
   * @throws IOException If the file referenced by {@code inputFile} cannot be found or read.
   */
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

  /**
   * For each of parts 1 and 2, creates an instance of {@link HauntedWasteland}, invokes the
   * relevant solution method, and prints the result.
   *
   * @param args Command-line arguments (currently ignored).
   * @throws IOException If the file referenced by {@link DataSource#DEFAULT_INPUT_FILE}} cannot be
   *                     found or read.
   */
  public static void main(String[] args) throws IOException {
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 1, new HauntedWasteland().countSteps());
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 2, new HauntedWasteland().countParallelSteps());
  }

  /**
   * Counts and returns the number of steps required to travel from a single initial node (labeled
   * as "AAA") to a single destination node ("ZZZ").
   */
  public long countSteps() {
    return countSteps(nodes.get(ORIGIN_NAME),
        (Node node) -> node.getName().equals(DESTINATION_NAME));
  }

  /**
   * Counts and returns the number of steps required to travel from <em>all</em> initial nodes
   * (with labels ending in as "A") to destination nodes (with labels ending in "Z"), such that if
   * one or more paths arrive at a destination node before the rest, travel continues on from that
   * destination node, until <em>all</em> paths arrive at destination nodes in the same step.
   * <p>Implementation notes:</p>
   * <ul>
   *   <li>While not stated explicitly in the instructions, this implementation assumes (inferring
   *   from the example given) that after reaching a terminal node from a given initial node the
   *   first time, it will take the same number of steps to reach the same (or another) terminal
   *   node again.</li>
   *   <li>No assumption is made regarding the coprimality (or not) of the length of the sequence of
   *   left-right turns and the number of steps required to reach a terminal node from any of the
   *   initial nodes.</li>
   * </ul>
   * <p>Given the above, the solution approach followed in this implementation is simply to compute
   * the least common multiple (LCM) of the length of the left-right turn sequence and the numbers
   * of steps required to reach terminal nodes from all of the initial nodes separately.</p>
   */
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
