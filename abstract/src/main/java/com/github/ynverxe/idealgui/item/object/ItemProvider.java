package com.github.ynverxe.idealgui.item.object;

import org.jetbrains.annotations.NotNull;

public interface ItemProvider<Item, Click> {

  /**
   * @return true if there's a new item to retrieve.
   */
  boolean handleTick();

  @NotNull ClickableItem<Item, Click> lastCached();

  void restart();

  default boolean isNull() {
    return this == nullProvider();
  }

  @SuppressWarnings("unchecked")
  static <Item, Click> @NotNull ItemProvider<Item, Click> nullProvider() {
    return NullItemProvider.INSTANCE;
  }
}