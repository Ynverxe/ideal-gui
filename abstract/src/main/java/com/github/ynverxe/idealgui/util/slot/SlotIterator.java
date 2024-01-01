package com.github.ynverxe.idealgui.util.slot;

import com.github.ynverxe.idealgui.GUIDesignType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public interface SlotIterator {

  void forEach(@NotNull Consumer<Integer> consumer);

  default @NotNull SlotIterator and(@NotNull SlotIterator other) {
    Composed composed = this instanceof Composed ? (Composed) this : new Composed();
    composed.iterators.add(other);
    return composed;
  }

  class Simple implements SlotIterator {
    private final int min, max, append;

    Simple(int min, int max, int append) {
      this.min = min;
      this.max = max;
      this.append = append;
    }

    public int min() {
      return min;
    }

    public int max() {
      return max;
    }

    public int append() {
      return append;
    }

    @Override
    public void forEach(@NotNull Consumer<Integer> consumer) {
      for (int i = min; i <= max; i+=append) {
        consumer.accept(i);
      }
    }
  }

  class Composed implements SlotIterator {
    private final List<SlotIterator> iterators;

    Composed() {
      this.iterators = new ArrayList<>();
    }

    @Override
    public void forEach(@NotNull Consumer<Integer> consumer) {
      iterators.forEach(iterator -> iterator.forEach(consumer));
    }
  }

  static @NotNull SlotIterator ofBorders(@NotNull GUIDesignType.Squared squared) {
    return compose(ofColumn(1, squared), ofColumn(squared.columns(), squared), ofRow(1, squared), ofRow(squared.rows(), squared));
  }

  static @NotNull SlotIterator compose(@NotNull SlotIterator... all) {
    Composed composed = new Composed();
    composed.iterators.addAll(Arrays.asList(all));
    return composed;
  }

  static SlotIterator ofColumn(int column, @NotNull GUIDesignType.Squared squared) {
    int maxSlot = column + (squared.columns() * (squared.rows() - 1)) - 1;

    if (maxSlot >= squared.capacity()) {
      throw new IllegalArgumentException("Max >= capacity");
    }

    return new Simple(column - 1, maxSlot, squared.columns());
  }

  static SlotIterator ofRow(int row, @NotNull GUIDesignType.Squared squared) {
    int min = (row - 1) * squared.columns();
    int max = (min + squared.columns()) - 1;

    if (max >= squared.capacity()) {
      throw new IllegalArgumentException("Max >= capacity");
    }

    return new Simple(min, max, 1);
  }
}