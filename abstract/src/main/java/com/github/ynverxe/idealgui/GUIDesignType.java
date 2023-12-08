package com.github.ynverxe.idealgui;

import org.jetbrains.annotations.NotNull;

public interface GUIDesignType {

  int capacity();

  /**
   * All slots are together. (Chest, Shulker, Crafting table, etc.)
   */
  final class Squared implements GUIDesignType {
    private final int columnLength, rowLength;

    Squared(int columnLength, int rowLength) {
      this.columnLength = columnLength;
      this.rowLength = rowLength;
    }

    public int rowLength() {
      return rowLength;
    }

    public int columnLength() {
      return columnLength;
    }

    public void checkIsInRange(int column, int row) {
      if (column < 1 || column > columnLength)
        throw new IndexOutOfBoundsException("Column is '" + column + "' but column count is '" + columnLength + "'");

      if (row < 1 || row > rowLength)
        throw new IndexOutOfBoundsException("Row is '" + column + "' but row count is '" + rowLength + "'");
    }

    @Override
    public int capacity() {
      return rowLength * columnLength;
    }
  }

  /**
   * Non-compact slot ordering (Furnaces, Brewing Stands, etc.)
   */
  final class Custom implements GUIDesignType {

    private final int capacity;

    Custom(int capacity) {
      this.capacity = capacity;
    }

    @Override
    public int capacity() {
      return capacity;
    }
  }

  static @NotNull Squared squared(int columns, int rows) {
    if (rows < 1 || columns < 1)
      throw new IllegalArgumentException("Invalid parameters");

    return new Squared(columns, rows);
  }

  static @NotNull Custom custom(int capacity) {
    if (capacity < 1)
      throw new IllegalArgumentException("Negative capacity");

    return new Custom(capacity);
  }
}