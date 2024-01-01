package com.github.ynverxe.idealgui.item.manager;

import com.github.ynverxe.idealgui.item.object.ClickableItem;
import com.github.ynverxe.idealgui.item.object.ConfigurableItemProvider;
import com.github.ynverxe.idealgui.item.object.FramedItemProvider;
import com.github.ynverxe.idealgui.item.object.ItemProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemHelper<Item, Click> {

  public @NotNull ConfigurableItemProvider<Item, Click> configurable(@NotNull ClickableItem<Item, Click> base) {
    return new ConfigurableItemProvider<>(base);
  }

  public @NotNull ConfigurableItemProvider<Item, Click> configurable(@NotNull Item baseItem) {
    return configurable(new ClickableItem<>(baseItem));
  }

  public @NotNull ConfigurableItemProvider<Item, Click> configurableAir() {
    return configurable(air());
  }

  public @NotNull FramedItemProvider<Item, Click> framed() {
    return new FramedItemProvider<>();
  }

  @SafeVarargs
  public final @NotNull FramedItemProvider<Item, Click> framed(ClickableItem<Item, Click> @NotNull ... frames) {
    FramedItemProvider<Item, Click> framed = new FramedItemProvider<>();
    return framed.frames(frames);
  }

  @SafeVarargs
  public final @NotNull FramedItemProvider<Item, Click> framed(Item @NotNull ... frames) {
    FramedItemProvider<Item, Click> framed = new FramedItemProvider<>();
    List<ClickableItem<Item, Click>> items = new ArrayList<>();
    for (Item frame : frames) {
      items.add(byItem(frame));
    }
    return framed.frames(items);
  }

  public @NotNull ClickableItem<Item, Click> byItem(@Nullable Item item) {
    return new ClickableItem<>(item);
  }

  public @NotNull ClickableItem<Item, Click> cancelClick(@Nullable Item item) {
    return new ClickableItem<>(item, (a, b) -> true);
  }

  protected abstract Item air();

}