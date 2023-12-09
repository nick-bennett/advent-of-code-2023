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
package com.nickbenn.adventofcode.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings("unused")
public class DataSource {

  public static final String DEFAULT_INPUT_FILE = "input.txt";
  public static final boolean DEFAULT_TRIMMED = true;
  public static final boolean DEFAULT_STRIPPED = true;
  private static final Pattern PARAGRAPH_SPLITTER = Pattern.compile("\\r?\\n\\s*?\\r?\\n");
  private static final String CANT_READ_FILE_FORMAT =
      "Unable to find file \"%1$s\" (relative to %2$s), or file cannot be opened for reading";

  private final String inputFile;
  private final boolean trimmed;
  private final boolean stripped;
  private final Class<?> context;

  private DataSource(Builder builder) throws IOException {
    inputFile = builder.inputFile;
    trimmed = builder.trimmed;
    stripped = builder.stripped;
    context = builder.context;
    //noinspection EmptyTryBlock
    try (InputStream input = getInputStream()) {
      // Do nothing.
    } catch (NullPointerException e) {
      throw new IOException(e);
    }
  }

  public static Stream<String> simpleLines(String inputFile, Class<?> context) throws IOException {
    Builder builder = new Builder(context);
    if (inputFile != null) {
      builder.setInputFile(inputFile);
    }
    return builder
        .build()
        .lines();
  }

  public static Stream<String> simpleLines(String inputFile, Object context) throws IOException {
    return simpleLines(inputFile, context.getClass());
  }

  public static Stream<String> simpleLines(String inputFile) throws IOException {
    return simpleLines(inputFile, null);
  }

  public static Stream<String> simpleLines(Class<?> context) throws IOException {
    return simpleLines(null, context);
  }

  public static Stream<String> simpleLines(Object context) throws IOException {
    return simpleLines(null, context.getClass());
  }

  public static Stream<String> simpleLines() throws IOException {
    //noinspection RedundantCast
    return simpleLines(null, (Class<?>) null);
  }

  public Stream<String> lines() throws IOException {
    Stream<String> lines = new BufferedReader(new InputStreamReader(getInputStream())).lines();
    if (trimmed) {
      lines = lines.map(String::trim);
    }
    if (stripped) {
      lines = lines.filter(Predicate.not(String::isEmpty));
    }
    return lines;
  }

  public Stream<String> blocks(Pattern splitter) throws IOException {
    Scanner scanner = new Scanner(getInputStream());
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
            new Chunker<>(iterator, chunkSize),
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

  private InputStream getInputStream() throws IOException {
    InputStream input = (context != null)
        ? context.getResourceAsStream(inputFile)
        : getClass().getClassLoader().getResourceAsStream(inputFile);
    if (input == null) {
      throw new IOException(String.format(CANT_READ_FILE_FORMAT, inputFile,
          (context != null) ? context.getPackageName() : "classpath"));
    }
    return input;
  }

  public static class Builder {

    private String inputFile = DEFAULT_INPUT_FILE;
    private Class<?> context;
    private boolean trimmed = DEFAULT_TRIMMED;
    private boolean stripped = DEFAULT_STRIPPED;

    public Builder() {
      this(null);
    }

    public Builder(Class<?> context) {
      this.context = context;
    }

    public Builder(Object context) {
      this(context.getClass());
    }

    public Builder setInputFile(String inputFile) {
      this.inputFile = inputFile;
      return this;
    }

    public Builder setContext(Class<?> context) {
      this.context = context;
      return this;
    }

    public Builder setContext(Object context) {
      this.context = context.getClass();
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

    public DataSource build() throws IOException {
      return new DataSource(this);
    }

  }

}
