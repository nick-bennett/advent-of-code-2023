package com.nickbenn.adventofcode.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@SuppressWarnings("SpellCheckingInspection")
public record Chunker<E>(Iterator<E> source, int chunkSize)
    implements Iterator<Stream<E>> {

  @Override
  public boolean hasNext() {
    return source.hasNext();
  }

  @Override
  public Stream<E> next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    List<E> pending = new LinkedList<>();
    for (int i = 0; i < chunkSize && source.hasNext(); i++) {
      pending.add(source.next());
    }
    return pending.stream();
  }

}
