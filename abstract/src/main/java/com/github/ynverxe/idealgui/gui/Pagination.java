package com.github.ynverxe.idealgui.gui;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Pagination {

  int index();

  int length();

  @Contract("_ -> this")
  @NotNull Pagination set(int index) throws IndexOutOfBoundsException;

  default boolean hasNext() {
    return index() + 1 < length();
  }

  default boolean hasPrevious() {
    return index() > 0;
  }

  @Contract("-> this")
  default @NotNull Pagination next() throws IllegalStateException {
    if (!hasNext()) {
      throw new IllegalStateException("Index reached the final page");
    }

    return set(index() + 1);
  }

  @Contract("_ -> this")
  default @NotNull Pagination advance(int count) throws IndexOutOfBoundsException {
    int finalIndex = index() + count;

    if (finalIndex > length()) {
      throw new IndexOutOfBoundsException("Final index is '" + finalIndex + "' but length is '" + length() + "'");
    }

    return set(finalIndex);
  }

  @Contract("_ -> this")
  default @NotNull Pagination back(int count) throws IndexOutOfBoundsException {
    int finalIndex = index() - count;

    if (finalIndex < 0) {
      throw new IndexOutOfBoundsException("Final index must be positive '" + finalIndex + "'");
    }

    return set(finalIndex);
  }

  @Contract("-> this")
  default @NotNull Pagination previous() throws IllegalStateException {
    if (!hasPrevious()) {
      throw new IllegalStateException("Index is at the start of pagination");
    }

    return set(index() - 1);
  }
}