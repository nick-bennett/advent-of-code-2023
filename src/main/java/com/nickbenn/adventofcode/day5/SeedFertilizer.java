package com.nickbenn.adventofcode.day5;

import com.nickbenn.adventofcode.util.DataSource;
import com.nickbenn.adventofcode.util.Defaults;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class SeedFertilizer {

  private static final Pattern BLOCK_EXTRACTOR = Pattern.compile("(?:(seeds)"
      + "|(?:(seed-to-soil)"
      + "|(soil-to-fertilizer)"
      + "|(fertilizer-to-water)"
      + "|(water-to-light)"
      + "|(light-to-temperature)"
      + "|(temperature-to-humidity)"
      + "|(humidity-to-location))"
      + "\\s+map):\\s+(.*)$", Pattern.DOTALL);
  private static final Pattern LINE_SPLITTER = Pattern.compile("\\n");
  private static final Pattern SEED_SPLITTER = Pattern.compile("\\s+");
  private static final Pattern SEED_PAIR_EXTRACTOR = Pattern.compile("(\\d+)\\s+(\\d+)");
  private static final int PATTERN_OFFSET = 2;
  private static final long MIN_SOURCE_START = 0;
  private static final long MAX_SOURCE_START = Long.MAX_VALUE;

  private final List<Long> seeds = new LinkedList<>();
  private final List<SourceRange> sourceRanges = new LinkedList<>();
  private final List<? extends NavigableMap<Long, Mapping>> maps = IntStream.range(0, 7)
      .mapToObj((ignored) -> new TreeMap<Long, Mapping>())
      .toList();
  private final List<RangedMap> rangedMaps = Stream.generate(() -> (RangedMap) null)
      .limit(7)
      .collect(Collectors.toList());

  public SeedFertilizer() throws IOException {
    this(Defaults.INPUT_FILE);
  }

  public SeedFertilizer(String inputFile) throws IOException {
    try (Stream<String> blocks = new DataSource.Builder()
        .setInputFile(inputFile)
        .setContext(getClass())
        .build()
        .paragraphs()) {
      blocks.forEach((block) -> {
        Matcher matcher = BLOCK_EXTRACTOR.matcher(block);
        if (!matcher.matches()) {
          throw new IllegalArgumentException();
        }
        int firstMatch = firstMatchingGroup(matcher);
        String data = matcher.group(9);
        if (firstMatch == 1) {
          SEED_SPLITTER
              .splitAsStream(data)
              .map(String::strip)
              .filter(Predicate.not(String::isEmpty))
              .map(Long::valueOf)
              .forEach(seeds::add);
          SEED_PAIR_EXTRACTOR
              .matcher(data)
              .results()
              .map((result) -> {
                long start = Long.parseLong(result.group(1));
                long length = Long.parseLong(result.group(2));
                return new SourceRange(start, length);
              })
              .forEach(sourceRanges::add);
        } else {
          int position = firstMatch - PATTERN_OFFSET;
          LINE_SPLITTER
              .splitAsStream(matcher.group(9))
              .map(String::strip)
              .map(Mapping::parse)
              .forEach((mapping) ->
                  maps.get(position).put(mapping.sourceStart(), mapping));
          normalize(maps.get(position));
        }
      });
    }
  }

  public static void main(String[] args) throws IOException {
    SeedFertilizer seedFertilizer = new SeedFertilizer();
    System.out.println(seedFertilizer.getLowestLocation());
//    System.out.println(seedFertilizer.getLowestInterpolatedLocation());
  }

  public long getLowestLocation() {
    return seeds
        .stream()
        .map((seed) -> {
          Long prev = seed;
          for (NavigableMap<Long, Mapping> map : maps) {
            prev = translate(prev, map);
          }
          return prev;
        })
        .mapToLong(Long::longValue)
        .min()
        .orElse(Long.MAX_VALUE);
  }

  public long getLowestInterpolatedLocation() {
    return sourceRanges
        .stream()
        .peek(System.out::println)
        .flatMap((range) -> {
          long start = range.start();
          long end = start + range.length;
          return LongStream.range(start, end)
              .boxed();
        })
        .map((seed) -> {
          Long prev = seed;
          for (RangedMap map : rangedMaps) {
            prev = map.translate(prev);
          }
          return prev;
        })
        .mapToLong(Long::longValue)
        .min()
        .orElse(Long.MAX_VALUE);
  }

  private int firstMatchingGroup(Matcher matcher) {
    int groupNumber = -1;
    if (matcher.group() != null) {
      groupNumber = 0;
      for (int i = 1; i <= matcher.groupCount(); i++) {
        if (matcher.group(i) != null) {
          groupNumber = i;
          break;
        }
      }
    }
    return groupNumber;
  }

  private static Long translate(Long source, NavigableMap<Long, Mapping> map) {
    return map
        .headMap(source, true)
        .entrySet()
        .stream()
        .dropWhile((entry) -> entry.getKey() + entry.getValue().length() <= source)
        .map(Entry::getValue)
        .findFirst()
        .map((mapping) -> source - mapping.sourceStart() + mapping.destinationStart())
        .orElse(source);
  }

  private static SourceRange getActiveRange(NavigableMap<Long, Mapping> map) {
    Long min = map.firstKey();
    Mapping lastMapping = map.lastEntry().getValue();
    Long max = lastMapping.sourceStart() + lastMapping.length();
    return new SourceRange(min, max - min);
  }

  private static void normalize(NavigableMap<Long, Mapping> map) {
    long sourceStart = MIN_SOURCE_START;
    do {
      Entry<Long, Mapping> entry = map.ceilingEntry(sourceStart);
      if (entry != null) {
        long entryStart = entry.getKey();
        long entryLength = entry.getValue().length();
        if (entryStart > sourceStart) {
          map.put(sourceStart, new Mapping(sourceStart, sourceStart, entryStart - sourceStart));
        }
        sourceStart = entryStart + entryLength;
      } else if (sourceStart < MAX_SOURCE_START) {
        map.put(sourceStart,
            new Mapping(sourceStart, sourceStart, MAX_SOURCE_START - sourceStart));
        break;
      } else {
        break;
      }
    } while (true);
  }

  private static NavigableMap<Long, Mapping> merge(NavigableMap<Long, Mapping> input,
      NavigableMap<Long, Mapping> output) {
    NavigableMap<Long, Mapping> merged = new TreeMap<>();
    Entry<Long, Mapping> inputEntry = input.firstEntry();
    long inputSourceStart = inputEntry.getKey();
    Mapping inputMapping = inputEntry.getValue();
    long inputDestinationStart = inputMapping.destinationStart();
    long inputLength = inputMapping.length();

    return merged;
  }

  private record Mapping(long destinationStart, long sourceStart, long length) {

    private static final Pattern MAPPING_EXTRACTOR =
        Pattern.compile("\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)\\s*");

    public static Mapping parse(String input) {
      Matcher matcher = MAPPING_EXTRACTOR.matcher(input);
      if (!matcher.matches()) {
        throw new IllegalArgumentException();
      }
      return new Mapping(Long.parseLong(matcher.group(1)), Long.parseLong(matcher.group(2)),
          Long.parseLong(matcher.group(3)));
    }

  }

  private record SourceRange(long start, long length) {

    private static final Pattern SEED_PAIR_EXTRACTOR = Pattern.compile("(\\d+)\\s+(\\d+)");

    public static SourceRange parse(String input) {
      Matcher matcher = SEED_PAIR_EXTRACTOR.matcher(input);
      if (!matcher.matches()) {
        throw new IllegalArgumentException();
      }
      return new SourceRange(Long.parseLong(matcher.group(1)), Long.parseLong(matcher.group(2)));
    }

  }

  private record RangedMap(SourceRange range, NavigableMap<Long, Mapping> map) {

    public RangedMap(NavigableMap<Long, Mapping> map) {
      this(getActiveRange(map), map);
    }

    public Long translate(Long source) {
      return (source >= range.start() && source < range.start() + range.length())
          ? map
              .headMap(source, true)
              .entrySet()
              .stream()
              .dropWhile((entry) -> entry.getKey() + entry.getValue().length() <= source)
              .map(Entry::getValue)
              .findFirst()
              .map((mapping) -> source - mapping.sourceStart() + mapping.destinationStart())
              .orElse(source)
          : source;
    }

  }

}
