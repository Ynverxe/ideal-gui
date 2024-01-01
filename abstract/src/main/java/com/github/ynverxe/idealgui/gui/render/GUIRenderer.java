package com.github.ynverxe.idealgui.gui.render;

import com.github.ynverxe.idealgui.gui.GUI;
import com.github.ynverxe.idealgui.item.object.ItemProvider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * An interface used to customize Item position during GUI rendering.
 */
public interface GUIRenderer<Item, Click> {

  void render(ItemProvider<Item, Click> @NotNull [] dynamicItems, @NotNull GUI<?, ?, ?, ?, ?> gui);

  default void handleEmptySlots(
    @NotNull Consumer<Integer> consumer, ItemProvider<Item, Click> @NotNull [] dynamicItems, @NotNull GUI<?, ?, ?, ?, ?> gui) {
    for (int i = 0; i < dynamicItems.length; i++) {
      ItemProvider<Item, Click> item = dynamicItems[i];

      if (item == null && !gui.staticItems().containsIndex(i)) {
        consumer.accept(i);
      }
    }
  }
}