package com.nickbenn.adventofcode.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DataSource {

  private static final String BAD_INPUT_FILE_FORMAT = "File not found, or is inaccessible: %s";
  private static final Pattern PARAGRAPH_SPLITTER = Pattern.compile("\\r?\\n\\s*?\\r?\\n");

  private final boolean trimmed;
  private final boolean stripped;
  private final Path path;

  private DataSource(Builder builder) throws FileNotFoundException {
    try {
      trimmed = builder.trimmed;
      stripped = builder.stripped;
      URL url = (builder.context != null)
          ? builder.context.getResource(builder.inputFile)
          : getClass().getResource(builder.inputFile);
      path = Paths.get(Objects.requireNonNull(url).toURI());
    } catch (NullPointerException e) {
      throw new FileNotFoundException(String.format(BAD_INPUT_FILE_FORMAT, builder.inputFile));
    } catch (URISyntaxException e) { // Should never happen with Class.getResource(String).toURI().
      throw new RuntimeException(e);
    }
  }

  public static Stream<String> simpleLines(String inputFile, Class<?> context) throws IOException {
    return new DataSource.Builder()
        .setInputFile(inputFile)
        .setContext(context)
        .build()
        .lines();
  }

  public Stream<String> lines() throws IOException {
    Stream<String> lines = Files.lines(path);
    if (trimmed) {
      lines = lines.map(String::trim);
    }
    if (stripped) {
      lines = lines.filter(Predicate.not(String::isEmpty));
    }
    return lines;
  }

  public Stream<String> blocks(Pattern splitter) throws IOException {
    Scanner scanner = new Scanner(path);
    scanner.useDelimiter(splitter);
    Stream<String> blocks = scanner.tokens();
    if (trimmed) {
      blocks = blocks.map(String::trim);
    }
    if (stripped) {
      blocks = blocks.filter(Predicate.not(String::isEmpty));
    }
    return blocks;
  }

  public Stream<Stream<String>> blockLines(Pattern splitter) throws IOException {
    return expand(blocks(splitter));
  }

  public Stream<String> paragraphs() throws IOException {
    return blocks(PARAGRAPH_SPLITTER);
  }

  public Stream<Stream<String>> paragraphLines() throws IOException {
    return expand(paragraphs());
  }

  public Stream<Stream<String>> chunkedLines(int chunkSize) throws IOException {
    Iterator<String> iterator = lines().iterator();
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(
            new Chunker(iterator, chunkSize),
            Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE
        ),
        false
    );
  }

  public Stream<int[]> digits() throws IOException {
    return lines()
        .map((line) -> line
            .chars()
            .map((c) -> c - '0')
            .toArray()
        );
  }

  private Stream<Stream<String>> expand(Stream<String> input) {
    return input
        .map((block) ->
            block
                .lines()
                .map(trimmed ? String::trim : Function.identity())
                .filter(stripped ? Predicate.not(String::isEmpty) : (line) -> true)
        );
  }

  public static class Builder {

    private String inputFile = Defaults.INPUT_FILE;
    private Class<?> context;
    private boolean trimmed = Defaults.TRIMMED;
    private boolean stripped = Defaults.STRIPPED;

    public Builder() {
    }

    public Builder setInputFile(String inputFile) {
      this.inputFile = inputFile;
      return this;
    }

    public Builder setContext(Class<?> context) {
      this.context = context;
      return this;
    }

    public Builder setTrimmed(boolean trimmed) {
      this.trimmed = trimmed;
      return this;
    }

    public Builder setStripped(boolean stripped) {
      this.stripped = stripped;
      return this;
    }

    public DataSource build() throws FileNotFoundException {
      return new DataSource(this);
    }

  }

  private static class Chunker implements Iterator<Stream<String>> {

    private final int chunkSize;
    private final Iterator<String> source;

    public Chunker(Iterator<String> source, int chunkSize) {
      this.source = source;
      this.chunkSize = chunkSize;
    }

    @Override
    public boolean hasNext() {
      return source.hasNext();
    }

    @Override
    public Stream<String> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      List<String> pending = new LinkedList<>();
      for (int i = 0; i < chunkSize && source.hasNext(); i++) {
        pending.add(source.next());
      }
      return pending.stream();
    }

  }

}
