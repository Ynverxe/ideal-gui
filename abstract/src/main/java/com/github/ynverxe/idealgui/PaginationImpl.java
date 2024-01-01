package com.github.ynverxe.idealgui;

import com.github.ynverxe.idealgui.gui.Pagination;
import org.jetbrains.annotations.NotNull;

public class PaginationImpl implements Pagination {

  private int index;
  private final AbstractGUI<?, ?, ?, ?, ?> gui;

  public PaginationImpl(AbstractGUI<?, ?, ?, ?, ?> gui) {
    this.gui = gui;
  }

  @Override
  public int index() {
    return index;
  }

  @Override
  public int length() {
    int itemCount = gui.items().length();

    int itemsPerPage = gui.itemsPerPage();

    return (int) Math.ceil((double) itemCount / itemsPerPage);
  }

  @Override
  public @NotNull synchronized Pagination set(int index) throws IndexOutOfBoundsException {
    if (index >= length()) {
      throw new IndexOutOfBoundsException("Index is '" + index + "' but length is '" + length() + "'");
    }

    this.index = index;
    gui.renderContents();
    return this;
  }
}