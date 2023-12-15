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

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Convenience class that wraps an instance of {@link ListChunker ListChunker&lt;E&gt;}, returning
 * a {@link Stream Stream&lt;E&gt;} (instead of a {@link java.util.List List&lt;E&gt;}) on each
 * invocation of {@link #next()}.
 *
 * @param chunkSource {@link ListChunker ListChunker&lt;E&gt;} over ordered chunks.
 * @param <E>         Element type.
 */
@SuppressWarnings("SpellCheckingInspection")
public record StreamChunker<E>(ListChunker<E> chunkSource) implements Iterator<Stream<E>> {

  /**
   * Initializes this instance with the specified {@link Iterator Iterator&lt;E&gt;} and
   * {@code chunkSize}. Use of this constructor is equivalent to
   * {@link #StreamChunker(ListChunker) StreamChunker(new ListChunker(source, chunkSize))}.
   *
   * @param source Underlying {@link Iterator Iterator&lt;E&gt;} over individual elements.
   * @param chunkSize Maximum number of elements to include in each chunk.
   */
  public StreamChunker(Iterator<E> source, int chunkSize) {
    this(new ListChunker<E>(source, chunkSize));
  }

  @Override
  public boolean hasNext() {
    return chunkSource.hasNext();
  }

  @Override
  public Stream<E> next() {
    return chunkSource
        .next()
        .stream();
  }

}
