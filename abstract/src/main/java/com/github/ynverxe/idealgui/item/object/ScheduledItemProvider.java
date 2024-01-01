package com.github.ynverxe.idealgui.item.object;

import org.jetbrains.annotations.NotNull;

public abstract class ScheduledItemProvider<Item, Click, I extends ItemProvider<Item, Click>> extends BaseItemProvider<Item, Click, I> {

  private int updateInterval = 1; // in ticks
  private int ticksToUpdate = 1;

  @Override
  public boolean handleTick() {
    if (lastCached == null) {
      lastCached = computeItem();
      ticksToUpdate--;
      return true;
    }

    if (ticksToUpdate-- <= 0) {
      lastCached = computeItem();
      ticksToUpdate = updateInterval;
      return true;
    }

    return false;
  }

  @Override
  public @NotNull ClickableItem<Item, Click> lastCached() {
    return lastCached;
  }

  @Override
  public void restart() {
    this.ticksToUpdate = updateInterval;
    this.lastCached = initialItem();
  }

  protected abstract @NotNull ClickableItem<Item, Click> computeItem();

  protected abstract @NotNull ClickableItem<Item, Click> initialItem();

  @SuppressWarnings("unchecked")
  public @NotNull I updateInterval(int ticks) {
    this.updateInterval = ticks;
    this.ticksToUpdate = ticks;
    return (I) this;
  }
}