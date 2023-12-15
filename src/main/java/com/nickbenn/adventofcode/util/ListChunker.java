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
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Chunks ordered sequences of elements from an underlying {@link Iterator Iterator&lt;E&gt;},
 * implementing an {@code Iterator<List<E>>} over the streamed lists (chunks).
 *
 * @param source    {@link Iterator Iterator&lt;E&gt;} over individual elements.
 * @param chunkSize Number of elements to include in the {@link List List&lt;E&gt;} returned from
 *                  each invocation of {@link #next()}.
 * @param <E>       Element type.
 */
@SuppressWarnings("SpellCheckingInspection")
public record ListChunker<E>(Iterator<E> source, int chunkSize)
    implements Iterator<List<E>> {

  @Override
  public boolean hasNext() {
    return source.hasNext();
  }

  @Override
  public List<E> next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    List<E> pending = new LinkedList<>();
    for (int i = 0; i < chunkSize && source.hasNext(); i++) {
      pending.add(source.next());
    }
    return pending;
  }

}
