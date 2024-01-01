package com.github.ynverxe.idealgui.gui.render;

import com.github.ynverxe.idealgui.gui.GUI;
import com.github.ynverxe.idealgui.item.object.ItemProvider;
import org.jetbrains.annotations.NotNull;

public final class ItemRelocator {
  private ItemRelocator() {}

  public static void relocateItems(ItemProvider<?, ?>[] dynamicItems, @NotNull GUI<?, ?, ?, ?, ?> gui) {
    int slot = 0;
    int capacity = dynamicItems.length;
    Object[] copy = new ItemProvider[capacity];

    for (Object item : dynamicItems) {
      while(slot < capacity) {
        if (!gui.staticItems().isNull(slot)) {
          slot++;
        } else {
          copy[slot] = item;
          slot++;
          break;
        }
      }
    }

    System.arraycopy(copy, 0, dynamicItems, 0, copy.length);
  }
}