package com.github.ynverxe.idealgui.item.manager;

import com.github.ynverxe.idealgui.item.object.ClickableItem;
import com.github.ynverxe.idealgui.item.object.ItemProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ItemHolder<I, C> extends Iterable<ItemProvider<I, C>> {
  @NotNull ItemProvider<I, C> getItem(int index);

  default boolean containsIndex(int index) {
    return index < length();
  }

  default boolean isNull(int index) {
    try {
      ItemProvider<I, C> item = getItem(index);

      return item.isNull();
    } catch (IndexOutOfBoundsException e) {
      return false;
    }
  }

  default boolean isAbsolutelyEmpty(int index) {
    try {
      ItemProvider<I, C> item = getItem(index);

      return item.isNull() || item.lastCached().isEmpty();
    } catch (IndexOutOfBoundsException e) {
      return false;
    }
  }

  default boolean equals(int index, @NotNull ClickableItem<I, C> item) {
    return optionalItem(index).map(found -> found.equals(item)).orElse(false);
  }

  default boolean equals(int index, @NotNull I item) {
    return optionalItem(index).map(found -> {
      ClickableItem<I, C> lastCached = found.lastCached();

      return Objects.equals(item, lastCached.itemStack());
    }).orElse(false);
  }

  @NotNull List<ItemProvider<I, C>> collectItems(int fromIndex, int count);

  int length();

  int nonNullCount();

  default @NotNull Optional<ItemProvider<I, C>> optionalItem(int index) {
    if (!containsIndex(index)) return Optional.empty();

    ItemProvider<I, C> provider = getItem(index);

    return provider.isNull() ? Optional.empty() : Optional.of(provider);
  }

  default void consumeItem(int index, @NotNull Consumer<ItemProvider<I, C>> consumer) {
    optionalItem(index).ifPresent(consumer);
  }

  default <T> @UnknownNullability T mapItem(int index, @NotNull Function<ItemProvider<I, C>, T> mapper) {
    return optionalItem(index).map(mapper).orElse(null);
  }
}