package com.github.ynverxe.idealgui;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

@SuppressWarnings("rawtypes, unchecked")
public enum AdditionType {

  /**
   * Replaces the elements of the array with the elements of the collection based on the given index.
   */
  REPLACE {
    @Override
    public <I, C> ClickableItem<I, C>[] performAddition(int start, ClickableItem<I, C> @NotNull [] array, @NotNull Collection<ClickableItem<I, C>> items) {
      int additionRange = start + items.size();

      if (additionRange > array.length) {
        array = Arrays.copyOf(array, additionRange);
      }

      Iterator<ClickableItem<I, C>> iterator = items.iterator();

      for (int i = start; i < array.length; i++) {
        ClickableItem replacement = iterator.hasNext() ? iterator.next() : null;

        array[i] = replacement;
      }

      return array;
    }
  },
  /**
   * Adds the elements of the collection to the provided array from the given index.
   * If the added elements can overlap any element in the array, they are advanced.
   */
  APPEND {
    @Override
    public <I, C> ClickableItem<I, C>[] performAddition(int start, ClickableItem<I, C> @NotNull [] array, @NotNull Collection<ClickableItem<I, C>> items) {
      boolean tailAppend = start == array.length;
      int tail = array.length;

      array = Arrays.copyOf(array, array.length + items.size());

      Iterator<ClickableItem<I, C>> iterator = items.iterator();
      if (!tailAppend) {
        int pushLength = items.size();

        for (int i = tail - 1; i >= start; i--) {
          ClickableItem<I, C> toPush = array[i];
          array[i + pushLength] = toPush;
        }
      }

      for (int i = start; i < tail; i++) {
        ClickableItem<I, C> item = iterator.hasNext() ? iterator.next() : null;

        array[i] = item;
      }

      return array;
    }
  };

  public abstract <I, C> ClickableItem<I, C>[] performAddition(
    int start, ClickableItem<I, C> @NotNull [] array, @NotNull Collection<ClickableItem<I, C>> items);

}