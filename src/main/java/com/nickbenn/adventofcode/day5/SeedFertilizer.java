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

import com.nickbenn.adventofcode.util.Chunker;
import com.nickbenn.adventofcode.view.DataSource;
import com.nickbenn.adventofcode.model.LongRange;
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

  public SeedFertilizer() throws IOException {
    this(DataSource.DEFAULT_INPUT_FILE);
  }

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

  public static void main(String[] args) throws IOException {
    SeedFertilizer seedFertilizer = new SeedFertilizer();
    System.out.println(seedFertilizer.getLowestLocation());
    System.out.println(seedFertilizer.getLowestInterpolatedLocation());
  }

  public long getLowestLocation() {
    return seeds
        .stream()
        .mapToLong((seed) -> {
          Mapping mapping = mergedMap.floorEntry(seed).getValue();
          return mapping.destinationStart() + seed - mapping.sourceStart();
        })
        .min()
        .orElse(MIN_END);
  }

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
        new Chunker<>(seeds.iterator(), 2), Spliterator.ORDERED), false)
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
