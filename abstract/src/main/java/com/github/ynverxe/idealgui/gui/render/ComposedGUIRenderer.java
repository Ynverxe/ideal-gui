package com.github.ynverxe.idealgui.gui.render;

import com.github.ynverxe.idealgui.gui.GUI;
import com.github.ynverxe.idealgui.item.object.ItemProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ComposedGUIRenderer<Item, Click, G> implements GUIRenderer<Item, Click> {

  private final G gui;
  private final List<GUIRenderer<Item, Click>> rendererList = new ArrayList<>();

  public ComposedGUIRenderer(G gui) {
    this.gui = gui;
  }

  @Override
  public void render(ItemProvider<Item, Click> @NotNull [] dynamicItems, @NotNull GUI<?, ?, ?, ?, ?> gui) {
    for (GUIRenderer<Item, Click> renderer : rendererList) {
      renderer.render(dynamicItems, gui);
    }
  }

  @Contract("-> this")
  public @NotNull ComposedGUIRenderer<Item, Click, G> clearRenderers() {
    rendererList.clear();
    return this;
  }

  @Contract("_ -> this")
  public @NotNull ComposedGUIRenderer<Item, Click, G> addRenderer(@NotNull GUIRenderer<Item, Click> renderer) {
    rendererList.add(renderer);
    return this;
  }

  @Contract("_ -> this")
  public @NotNull ComposedGUIRenderer<Item, Click, G> removeRenderer(@NotNull GUIRenderer<Item, Click> renderer) {
    rendererList.remove(renderer);
    return this;
  }

  public @NotNull G gui() {
    return gui;
  }

  public @NotNull List<GUIRenderer<Item, Click>> rendererList() {
    return rendererList;
  }

}