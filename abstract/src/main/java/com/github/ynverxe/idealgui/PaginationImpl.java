package com.github.ynverxe.idealgui;

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
    int itemCount = gui.itemCount();

    if (itemCount == 0) return 1;

    int itemsPerPage = gui.type().capacity();
    return (int) Math.round((double) itemCount / itemsPerPage);
  }

  @Override
  public int itemsPerPage() {
    return gui.type().capacity();
  }

  @Override
  public @NotNull synchronized Pagination set(int index) throws IndexOutOfBoundsException {
    if (index >= length()) {
      throw new IndexOutOfBoundsException("Index is '" + index + "' but length is '" + length() + "'");
    }

    this.index = index;
    gui.rendererHandler.renderPage(index);
    return this;
  }
}