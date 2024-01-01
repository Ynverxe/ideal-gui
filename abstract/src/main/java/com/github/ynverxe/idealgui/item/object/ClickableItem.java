package com.github.ynverxe.idealgui.item.object;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

public final class ClickableItem<I, C> implements ItemProvider<I, C> {

  private final @Nullable I itemStack;
  private final @Nullable BiPredicate<I, C> clickHandler;

  public ClickableItem(@Nullable I itemStack) {
    this(itemStack, null);
  }

  public ClickableItem(@Nullable I itemStack, @Nullable BiPredicate<I, C> clickHandler) {
    this.itemStack = itemStack;
    this.clickHandler = clickHandler;
  }

  public @Nullable I itemStack() {
    return itemStack;
  }

  public @Nullable BiPredicate<I, C> clickHandler() {
    return clickHandler;
  }

  public boolean isEmpty() {
    return itemStack == null && clickHandler == null;
  }

  @Override
  public boolean handleTick() {
    return false;
  }

  @Override
  public @NotNull ClickableItem<I, C> lastCached() {
    return this;
  }

  @Override
  public void restart() {}

  @Override
  public String toString() {
    return "ClickableItem{" +
      "itemStack=" + itemStack +
      '}';
  }

  @Contract("_ -> new")
  public @NotNull ClickableItem<I, C> withClickHandler(@NotNull BiPredicate<I, C> clickHandler) {
    return new ClickableItem<>(itemStack, clickHandler);
  }

  @Contract("_ -> new")
  public @NotNull ClickableItem<I, C> composeClickHandler(@NotNull BiPredicate<I, C> clickHandler) {
    return new ClickableItem<>(itemStack, this.clickHandler != null ? this.clickHandler.and(clickHandler) : clickHandler);
  }

  public static <Item, Click> ClickableItem<Item, Click> empty(@Nullable BiPredicate<Item, Click> clickHandler) {
    return new ClickableItem<>(null, clickHandler);
  }

  public static <Item, Click> ClickableItem<Item, Click> empty() {
    return empty(null);
  }
}