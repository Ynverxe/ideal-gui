package com.github.ynverxe.idealgui.item.object;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("rawtypes")
class NullItemProvider implements ItemProvider {
  static final NullItemProvider INSTANCE = new NullItemProvider();

  private NullItemProvider() {}

  @Override
  public boolean handleTick() {
    return false;
  }

  @Override
  public @NotNull ClickableItem lastCached() {
    return ClickableItem.empty();
  }

  @Override
  public void restart() {}
}