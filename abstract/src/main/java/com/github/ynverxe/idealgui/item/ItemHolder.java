package com.github.ynverxe.idealgui.item;

import com.github.ynverxe.idealgui.ClickableItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ItemHolder<I, C> {
  @Nullable ClickableItem<I, C> getItem(int index);

  @NotNull Map<Integer, @Nullable ClickableItem<I, C>> itemsSnapshot();

  @NotNull List<ClickableItem<I, C>> collectItems(int fromIndex, int count);

  int itemCount();

  default @NotNull Optional<ClickableItem<I, C>> optionalItem(int index) {
    return Optional.ofNullable(getItem(index));
  }

  default void consumeItem(int index, @NotNull Consumer<ClickableItem<I, C>> consumer) {
    optionalItem(index).ifPresent(consumer);
  }

  default <T> @UnknownNullability T mapItem(int index, @NotNull Function<ClickableItem<I, C>, T> mapper) {
    return optionalItem(index).map(mapper).orElse(null);
  }
}