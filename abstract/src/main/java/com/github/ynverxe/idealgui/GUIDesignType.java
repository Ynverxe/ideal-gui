package com.github.ynverxe.idealgui;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface GUIDesignType {

  int capacity();

  /**
   * All slots are together. (Chest, Shulker, Crafting table, etc.)
   */
  final class Squared implements GUIDesignType {
    private final int columns, rows;

    Squared(int columns, int rows) {
      this.columns = columns;
      this.rows = rows;
    }

    public int rows() {
      return rows;
    }

    public int columns() {
      return columns;
    }

    public void checkIsInRange(int column, int row) {
      if (column < 1 || column > columns)
        throw new IndexOutOfBoundsException("Column is '" + column + "' but column count is '" + columns + "'");

      if (row < 1 || row > rows)
        throw new IndexOutOfBoundsException("Row is '" + row + "' but row count is '" + rows + "'");
    }

    @Override
    public int capacity() {
      return rows * columns;
    }

    @Override
    public boolean equals(Object object) {
      if (this == object) return true;
      if (object == null || getClass() != object.getClass()) return false;
      Squared squared = (Squared) object;
      return columns == squared.columns && rows == squared.rows;
    }

    @Override
    public int hashCode() {
      return Objects.hash(columns, rows);
    }

    public int toPosition(int row, int column) {
      return (((row - 1) * columns) + column) - 1;
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

    @Override
    public boolean equals(Object object) {
      if (this == object) return true;
      if (object == null || getClass() != object.getClass()) return false;
      Custom custom = (Custom) object;
      return capacity == custom.capacity;
    }

    @Override
    public int hashCode() {
      return Objects.hash(capacity);
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