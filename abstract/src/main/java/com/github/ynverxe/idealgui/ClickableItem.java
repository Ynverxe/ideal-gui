package com.github.ynverxe.idealgui;

import org.jetbrains.annotations.NotNull;
import java.util.function.BiPredicate;

public final class ClickableItem<I, C> {

  private final @NotNull I itemStack;
  private final @NotNull BiPredicate<I, C> clickHandler;

  public ClickableItem(@NotNull I itemStack) {
    this(itemStack, (item, event) -> true);
  }

  public ClickableItem(@NotNull I itemStack, @NotNull BiPredicate<I, C> clickHandler) {
    this.itemStack = itemStack;
    this.clickHandler = clickHandler;
  }

  public @NotNull I itemStack() {
    return itemStack;
  }

  public @NotNull BiPredicate<I, C> clickHandler() {
    return clickHandler;
  }

  public boolean handleClick(@NotNull I item, @NotNull C event) {
    return clickHandler.test(item, event);
  }
}