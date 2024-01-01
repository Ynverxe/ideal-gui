package com.github.ynverxe.idealgui.item.manager;

import com.github.ynverxe.idealgui.gui.GUI;
import com.github.ynverxe.idealgui.GUIDesignType;
import com.github.ynverxe.idealgui.gui.Pagination;
import com.github.ynverxe.idealgui.item.object.ClickableItem;
import com.github.ynverxe.idealgui.item.object.ItemProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public interface ItemContainer<Item, Click, G extends GUI> extends ItemHolder<Item, Click> {

  @Contract("_, _ -> this")
  @NotNull ItemContainer<Item, Click, G> setItem(@NotNull ItemProvider<Item, Click> item, int index);

  @Contract("_, _ -> this")
  default @NotNull ItemContainer<Item, Click, G> setItem(@Nullable Item item, int index) {
    return setItem(new ClickableItem<>(item), index);
  }

  @Contract("_, _, _ -> this")
  default @NotNull ItemContainer<Item, Click, G> setItem(@NotNull ItemProvider<Item, Click> item, int column, int row) throws UnsupportedOperationException {
    GUIDesignType type = gui().type();
    if (!(type instanceof GUIDesignType.Squared)) {
      throw new UnsupportedOperationException("GUI doesn't have columns and rows");
    }

    GUIDesignType.Squared squared = (GUIDesignType.Squared) type;
    squared.checkIsInRange(column, row);

    int position = squared.toPosition(row, column);
    return setItem(item, position);
  }

  @Contract("_, _, _ -> this")
  default @NotNull ItemContainer<Item, Click, G> setItem(@Nullable Item item, int column, int row) throws UnsupportedOperationException {
    return setItem(item == null ? null : new ClickableItem<>(item), column, row);
  }

  @Contract("_ -> this")
  @NotNull ItemContainer<Item, Click, G> removeItem(int index);

  @Contract("_, _ -> this")
  @NotNull ItemContainer<Item, Click, G> removeItems(int from, int count);

  @Contract("_ -> this")
  @NotNull ItemContainer<Item, Click, G> removeItemsIf(@NotNull BiPredicate<Integer, @NotNull ItemProvider<Item, Click>> filter);

  @Contract("_ -> this")
  @NotNull ItemContainer<Item, Click, G> contents(@NotNull Collection<ItemProvider<Item, Click>> items);

  @Contract("_ -> this")
  @NotNull ItemContainer<Item, Click, G> contents(ItemProvider<Item, Click> @NotNull ... items);

  @Contract("_ -> this")
  default @NotNull ItemContainer<Item, Click, G> rawContents(@NotNull Collection<Item> items) {
    return contents(items.stream().map(item -> new ClickableItem<Item, Click>(item)).collect(Collectors.toList()));
  }

  default @NotNull Accumulative<Item, Click, G> asAccumulative() {
    return (Accumulative<Item, Click, G>) this;
  }

  @Contract("_, _ -> this")
  default @NotNull ItemContainer<Item, Click, G> backSwitch(@NotNull Item item, int index) {
    return setItem(new ClickableItem<>(item, (a, b) -> {
      Pagination pagination = gui().pagination();

      if (pagination.hasPrevious()) pagination.previous();
      return true;
    }), index);
  }

  @Contract("_, _ -> this")
  default @NotNull ItemContainer<Item, Click, G> nextSwitch(@NotNull Item item, int index) {
    return setItem(new ClickableItem<>(item, (a, b) -> {
      Pagination pagination = gui().pagination();

      if (pagination.hasNext()) pagination.next();
      return true;
    }), index);
  }

  @NotNull G gui();

  interface Accumulative<Item, Click, G extends GUI> extends ItemContainer<Item, Click, G> {
    @Contract("_ -> this")
    @NotNull Accumulative<Item, Click, G> addItem(@NotNull ItemProvider<Item, Click> item);

    @Contract("_ -> this")
    default @NotNull Accumulative<Item, Click, G> addItem(@Nullable Item item) {
      return addItem(new ClickableItem<>(item));
    }
  }
}