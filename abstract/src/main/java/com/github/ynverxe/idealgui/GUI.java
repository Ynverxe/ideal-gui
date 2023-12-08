package com.github.ynverxe.idealgui;

import com.github.ynverxe.idealgui.item.ItemHolder;
import com.github.ynverxe.idealgui.model.viewable.Viewable;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@ApiStatus.NonExtendable
@SuppressWarnings("rawtypes")
public interface GUI<Viewer, Item, Click, G extends GUI, Handle> extends Viewable<Viewer>, ItemHolder<Item, Click> {

  @NotNull Component showingTitle();

  @Contract("_ -> this")
  @NotNull G title(@NotNull Component component);

  @Contract("_ -> this")
  @NotNull G contents(@NotNull Collection<ClickableItem<Item, Click>> items);

  @Contract("_ -> this")
  @NotNull G contents(ClickableItem<Item, Click> @NotNull ... items);

  @Contract("_ -> this")
  default @NotNull G rawContents(@NotNull Collection<Item> items) {
    return contents(items.stream().map(item -> new ClickableItem<Item, Click>(item)).collect(Collectors.toList()));
  }

  @Contract("_, _ -> this")
  @NotNull G clickableItem(@NotNull ClickableItem<Item, Click> item, int index);

  @Contract("_, _ -> this")
  default @NotNull G item(@NotNull Item item, int index) {
    return clickableItem(new ClickableItem<>(item), index);
  }

  @Contract("_, _, _ -> this")
  default @NotNull G item(@NotNull Item item, int column, int row) throws UnsupportedOperationException {
    if (!(type() instanceof GUIDesignType.Squared)) {
      throw new UnsupportedOperationException("GUI doesn't have columns and rows");
    }

    GUIDesignType.Squared type = (GUIDesignType.Squared) type();
    type.checkIsInRange(column, row);

    int position = ((column - 1) * type.rowLength());
    return item(item, position - 1);
  }

  @Contract("_, _, _ -> this")
  @NotNull G clickableItemAddition(
    int startIndex, @NotNull Collection<ClickableItem<Item, Click>> toAdd, @NotNull AdditionType type);

  @Contract("_, _, _ -> this")
  default @NotNull G itemAddition(int startIndex, @NotNull Collection<Item> toAdd, @NotNull AdditionType type) {
    return clickableItemAddition(startIndex, toAdd.stream().map(item -> new ClickableItem<Item, Click>(item)).collect(Collectors.toList()), type);
  }

  @Contract("_ -> this")
  @NotNull G removeItem(int index);

  @Contract("_, _ -> this")
  @NotNull G removeItems(int from, int count);

  @Contract("_ -> this")
  @NotNull G removeItemsIf(@NotNull BiPredicate<Integer, @Nullable ClickableItem<Item, Click>> filter);

  @NotNull BiPredicate<G, Viewer> openHandler();

  @NotNull BiConsumer<G, Viewer> closeHandler();

  @NotNull BiPredicate<G, Click> clickHandler();

  @NotNull GUIDesignType type();

  @NotNull Handle handle();

  /**
   * Try to reduce the item capacity if this gui.
   *
   * @return The reduced capacity.
   */
  int reduceCapacity();

  @NotNull Pagination pagination();

  @NotNull List<ClickableItem<Item, Click>> renderedItemsView();

}