package com.github.ynverxe.idealgui.item.object;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class BaseItemProvider<Item, Click, I extends ItemProvider<Item, Click>> implements ItemProvider<Item, Click> {

  protected ClickableItem<Item, Click> lastCached;
  private boolean cancelClick;

  @Override
  public @NotNull ClickableItem<Item, Click> lastCached() {
    return lastCached.composeClickHandler((item, click) -> cancelClick);
  }

  @Contract("_ -> this")
  public @NotNull I cancelClick(boolean cancelClick) {
    this.cancelClick = cancelClick;
    return (I) this;
  }
}