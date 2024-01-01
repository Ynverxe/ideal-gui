package com.github.ynverxe.idealgui.item.object;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class FramedItemProvider<Item, Click> extends ScheduledItemProvider<Item, Click, FramedItemProvider<Item, Click>> {

  private final List<ClickableItem<Item, Click>> frames = new ArrayList<>();

  private int index = 0;

  @Override
  protected @NotNull ClickableItem<Item, Click> computeItem() {
    if (frames.isEmpty()) {
      throw new IllegalArgumentException("Empty");
    }

    if (index >= this.frames.size()) {
      index = 0;
    }

    return frames.get(index++);
  }

  @Override
  protected @NotNull ClickableItem<Item, Click> initialItem() {
    if (frames.isEmpty()) {
      throw new IllegalArgumentException("Empty");
    }

    return frames.get(0);
  }

  @SafeVarargs
  public final @NotNull FramedItemProvider<Item, Click> addFrames(ClickableItem<Item, Click> @NotNull ... frames) {
    this.frames.addAll(Arrays.asList(frames));
    return this;
  }

  public @NotNull FramedItemProvider<Item, Click> addFrames(@NotNull Collection<ClickableItem<Item, Click>> frames) {
    this.frames.addAll(frames);
    return this;
  }

  @SafeVarargs
  public final @NotNull FramedItemProvider<Item, Click> frames(@NotNull ClickableItem<Item, Click>... frames) {
    this.frames.clear();
    return addFrames(frames);
  }

  public @NotNull FramedItemProvider<Item, Click> frames(@NotNull List<ClickableItem<Item, Click>> frames) {
    this.frames.clear();
    return addFrames(frames);
  }
}