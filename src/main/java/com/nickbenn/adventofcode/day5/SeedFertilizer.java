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
package com.nickbenn.adventofcode.day5;

import static com.nickbenn.adventofcode.view.Presentation.NUMERIC_SOLUTION_FORMAT;

import com.nickbenn.adventofcode.model.LongRange;
import com.nickbenn.adventofcode.util.StreamChunker;
import com.nickbenn.adventofcode.view.DataSource;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Implements solutions for both parts of day 5, reading a line specifying seed numbers, then a
 * sequence of mapping layers, each with one or more segments defining an input range and a
 * corresponding output range. (An output range may be translated from the input range, but not
 * scaled; that is, and input range and its corresponding output range have the same length.)
 * <p>After reading the input data, parts 1 and 2 both require the same type of
 * calculation&mdash;namely, find the lowest location value (the output from the final mapping
 * layer) for any of the seeds specified in the first line of input. However, the two parts require
 * different assumptions about the specification of seed numbers:</p>
 * <ul>
 *   <li>In part 1, every value on the first line of input is a seed number.</li>
 *   <li>In part 2, every <em>pair</em> of values on the first line of input specifies a
 *   starting seed number and a range length. For example, the pair of values <em>a b</em>
 *   specifies the integral range of seed numbers <em>a</em>..(<em>b</em> - 1).</li>
 * </ul>
 *
 * @see <a href="https://adventofcode.com/2023/day/5">"Day 5: If You Give A Seed A Fertilizer"</a>.
 */
public class SeedFertilizer {

  private static final Pattern BLOCK_EXTRACTOR =
      Pattern.compile("(?:(seeds)|[^:]*?):\\s+(.*)$", Pattern.DOTALL);
  private static final Pattern LINE_SPLITTER = Pattern.compile("\\n");
  private static final Pattern SEED_SPLITTER = Pattern.compile("\\s+");
  private static final long MIN_START = 0;
  private static final long MIN_END = Long.MAX_VALUE;

  private final List<Long> seeds = new LinkedList<>();
  private final List<LongRange> seedRanges = new LinkedList<>();
  private final NavigableMap<Long, Mapping> mergedMap;

  /**
   * Initializes this instance, using the value of {@link DataSource#DEFAULT_INPUT_FILE} as the name
   * (relative to the package of this class on the classpath) of the file to be read. In other
   * words, using this constructor is equivalent to using
   * {@link #SeedFertilizer(String) SeedFertilizer(DataSource.DEFAULT_INPUT_FILE)}.
   *
   * @throws IOException If the file referenced by {@link DataSource#DEFAULT_INPUT_FILE}} cannot be
   *                     found or read.
   */
  public SeedFertilizer() throws IOException {
    this(DataSource.DEFAULT_INPUT_FILE);
  }

  /**
   * Initializes this instance, using the value specified in the {@code inputFile} parameter as the
   * name (relative to the package of this class on the classpath) of the file to be read, parsing
   * the seed numbers at the start of the file, then parsing each successive block of lines as a
   * series of corresponding input and output ranges, together forming a mapping layer.
   * <p>Implementation notes:</p>
   * <ul>
   *   <li>Each mapping layer is normalized, filling in the gaps in the input layer, resulting in
   *   a continuous set of input segments, covering the range <code>0..(Long.MAX_VALUE - 1)</code>.
   *   </li>
   *   <li>The individual layers are not maintained in the instance state; instead, after
   *   normalization, each layer is merged into a reduced result obtained to that point. The merging
   *   process replaces each segment in the merged layer produced to that point with one or more
   *   segments formed from the intersections of the output range of the given segment with the
   *   input ranges of the layer being merged.</li>
   *   <li>The layer merging described above starts with a seed layer consisting of a single
   *   segment, with identical input and output ranges of <code>0..(Long.MAX_VALUE - 1)</code>.</li>
   * </ul>
   *
   * @param inputFile Classpath/package-relative location of file from which input is read.
   * @throws IOException If the file referenced by {@code inputFile} cannot be found or read.
   */
  public SeedFertilizer(String inputFile) throws IOException {
    try (Stream<String> blocks = new DataSource.Builder(this)
        .setInputFile(inputFile)
        .build()
        .paragraphs()) {
      mergedMap = blocks
          .map(BLOCK_EXTRACTOR::matcher)
          .filter(Matcher::matches)
          .map((matcher) -> {
            String data = matcher.group(2);
            if (matcher.group(1) != null) {
              seeds.addAll(getSeeds(data));
              seedRanges.addAll(getRanges(seeds));
              return null;
            }
            return data;
          })
          .filter(Predicate.not(Objects::isNull))
          .map(this::getMap)
          .map(this::normalize)
          .reduce(new TreeMap<>(Map.of(MIN_START, new Mapping(MIN_START, MIN_START, MIN_END))),
              this::merge);
    }
  }

  /**
   * For each of parts 1 and 2, creates an instance of {@link SeedFertilizer}, invokes the relevant
   * solution method, and prints the result.
   *
   * @param args Command-line arguments (currently ignored).
   * @throws IOException If the file referenced by {@link DataSource#DEFAULT_INPUT_FILE}} cannot be
   *                     found or read.
   */
  public static void main(String[] args) throws IOException {
    SeedFertilizer seedFertilizer = new SeedFertilizer();
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 1, seedFertilizer.getLowestLocation());
    System.out.printf(NUMERIC_SOLUTION_FORMAT, 2, seedFertilizer.getLowestInterpolatedLocation());
  }

  /**
   * Computes and returns the lowest final location number resulting for any of the seed numbers
   * explicitly listed at the start of the input.
   * <p>This method does not modify the state of the instance or have any other side effects.</p>
   */
  public long getLowestLocation() {
    return seeds
        .stream()
        .mapToLong((seed) -> {
          Mapping mapping = mergedMap.floorEntry(seed).getValue();
          return seed + mapping.destinationStart() - mapping.sourceStart();
        })
        .min()
        .orElse(MIN_END);
  }

  /**
   * Computes and returns the lowest final location number resulting for any seed number falling in
   * the seed number ranges listed at the start of the input.
   * <p>Implementation notes: This process does not iterate over all of the values falling in the
   * seed number ranges; instead, it proceeds in a similar fashion to that described for the merging
   * performed by {@link #SeedFertilizer(String)}: Each range of seed numbers is replaced by one or
   * more segments, each with a corresponding output range (based on the merge mapping layers).
   * Since the lowest location value <em>must</em> occur at the start of one of these segments, it
   * is thus a simple matter to compute the final location value for the seed number at the start of
   * each segment, and take the lowest of these values.</p>
   * <p>This method does not modify the state of the instance or have any other side effects.</p>
   */
  public long getLowestInterpolatedLocation() {
    return seedRanges
        .stream()
        .flatMap((range) -> {
          long start = range.start();
          long length = range.length();
          Mapping base = new Mapping(start, start, length);
          return segments(base, mergedMap);
        })
        .mapToLong(Mapping::destinationStart)
        .min()
        .orElse(MIN_END);
  }

  private List<Long> getSeeds(String data) {
    return SEED_SPLITTER
        .splitAsStream(data)
        .map(String::strip)
        .filter(Predicate.not(String::isEmpty))
        .map(Long::valueOf)
        .collect(Collectors.toList());
  }

  private List<LongRange> getRanges(List<Long> seeds) {
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
            new StreamChunker<>(seeds.iterator(), 2),
            Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE), false)
        .map((Stream<Long> stream) -> stream.mapToLong(Long::longValue).toArray())
        .map((pair) -> new LongRange(pair[0], pair[1]))
        .collect(Collectors.toList());
  }

  private NavigableMap<Long, Mapping> getMap(String data) {
    NavigableMap<Long, Mapping> map = new TreeMap<>();
    LINE_SPLITTER
        .splitAsStream(data)
        .map(String::strip)
        .map(Mapping::parse)
        .forEach((mapping) -> map.put(mapping.sourceStart(), mapping));
    return map;
  }

  private NavigableMap<Long, Mapping> normalize(NavigableMap<Long, Mapping> map) {
    long sourceStart = MIN_START;
    do {
      Entry<Long, Mapping> entry = map.ceilingEntry(sourceStart);
      if (entry != null) {
        long entryStart = entry.getKey();
        long entryLength = entry.getValue().length();
        if (entryStart > sourceStart) {
          map.put(sourceStart, new Mapping(sourceStart, sourceStart, entryStart - sourceStart));
        }
        sourceStart = entryStart + entryLength;
      } else if (sourceStart < MIN_END) {
        map.put(sourceStart, new Mapping(sourceStart, sourceStart, MIN_END - sourceStart));
        break;
      } else {
        break;
      }
    } while (true);
    return map;
  }

  private NavigableMap<Long, Mapping> merge(NavigableMap<Long, Mapping> input,
      NavigableMap<Long, Mapping> output) {
    NavigableMap<Long, Mapping> merged = new TreeMap<>();
    input
        .values()
        .stream()
        .flatMap((mapping) -> segments(mapping, output))
        .forEach((mapping) -> merged.put(mapping.sourceStart(), mapping));
    return merged;
  }

  private Stream<Mapping> segments(Mapping base, NavigableMap<Long, Mapping> map) {
    long destinationStart = base.destinationStart();
    long destinationEnd = base.destinationEnd();
    long delta = base.delta();
    return map
        .headMap(destinationEnd, false)
        .entrySet()
        .stream()
        .dropWhile((entry) -> entry.getKey() + entry.getValue().length() <= destinationStart)
        .map((entry) -> {
          Mapping layer = entry.getValue();
          long layerDelta = layer.delta();
          long newDestinationStart = Math.max(destinationStart, layer.sourceStart()) + layerDelta;
          long newDestinationEnd = Math.min(destinationEnd, layer.sourceEnd()) + layerDelta;
          long newLength = newDestinationEnd - newDestinationStart;
          long newSourceStart = newDestinationStart - delta - layerDelta;
          return new Mapping(newDestinationStart, newSourceStart, newLength);
        });
  }

  private record Mapping(long destinationStart, long sourceStart, long length) {

    private static final Pattern MAPPING_EXTRACTOR =
        Pattern.compile("\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)\\s*");

    public long delta() {
      return destinationStart - sourceStart;
    }

    public long sourceEnd() {
      return sourceStart + length;
    }

    public long destinationEnd() {
      return destinationStart + length;
    }

    public static Mapping parse(String input) {
      Matcher matcher = MAPPING_EXTRACTOR.matcher(input);
      if (!matcher.matches()) {
        throw new IllegalArgumentException();
      }
      return new Mapping(Long.parseLong(matcher.group(1)), Long.parseLong(matcher.group(2)),
          Long.parseLong(matcher.group(3)));
    }

  }

}
