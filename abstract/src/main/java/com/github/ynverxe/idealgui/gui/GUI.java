package com.github.ynverxe.idealgui.gui;

import com.github.ynverxe.idealgui.gui.render.ComposedGUIRenderer;
import com.github.ynverxe.idealgui.GUIDesignType;
import com.github.ynverxe.idealgui.item.manager.ItemContainer;
import com.github.ynverxe.idealgui.item.manager.ItemHolder;
import com.github.ynverxe.idealgui.model.property.PropertyHolder;
import com.github.ynverxe.idealgui.model.viewable.Viewable;
import com.github.ynverxe.idealgui.util.slot.SlotIterator;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

@ApiStatus.NonExtendable
@SuppressWarnings("rawtypes")
public interface GUI<Viewer, Item, Click, G extends GUI, Handle> extends Viewable<Viewer>, PropertyHolder<G> {

  @NotNull Component showingTitle();

  @Contract("_ -> this")
  @NotNull G title(@NotNull Component component);

  @NotNull BiPredicate<G, Viewer> openHandler();

  @NotNull BiConsumer<G, Viewer> closeHandler();

  @NotNull BiPredicate<Item, Click> clickHandler();

  @NotNull GUIDesignType type();

  @NotNull Handle handle();

  @NotNull Pagination pagination();

  @NotNull ItemHolder<Item, Click> renderedItemsView();

  @NotNull ItemContainer<Item, Click, G> staticItems();

  @NotNull ItemContainer.Accumulative<Item, Click, G> items();

  @Contract("_ -> this")
  @NotNull G relocateItems(boolean relocate);

  @NotNull ComposedGUIRenderer<Item, Click, G> renderer();

  default int itemsPerPage() {
    return type().capacity() - staticItems().nonNullCount();
  }

  @Contract("_, _ -> this")
  default @NotNull G iterateRow(int row, @NotNull BiConsumer<G, Integer> consumer) {
    if (!(type() instanceof GUIDesignType.Squared)) {
      throw new IllegalStateException("GUI type is not squared");
    }

    GUIDesignType.Squared type = (GUIDesignType.Squared) type();

    G gui = (G) this;

    SlotIterator.ofRow(row, type).forEach(slot -> consumer.accept(gui, slot));

    return gui;
  }

  @Contract("_, _ -> this")
  default @NotNull G iterateColumn(int column, @NotNull BiConsumer<G, Integer> consumer) {
    if (!(type() instanceof GUIDesignType.Squared)) {
      throw new IllegalStateException("GUI type is not squared");
    }

    GUIDesignType.Squared type = (GUIDesignType.Squared) type();

    G gui = (G) this;

    SlotIterator.ofColumn(column, type).forEach(slot -> consumer.accept(gui, slot));

    return gui;
  }

  @Contract("_ -> this")
  default @NotNull G iterateBorders(@NotNull BiConsumer<G, Integer> consumer) {
    if (!(type() instanceof GUIDesignType.Squared)) {
      throw new IllegalStateException("GUI type is not squared");
    }

    GUIDesignType.Squared type = (GUIDesignType.Squared) type();

    G gui = (G) this;

    SlotIterator.ofBorders(type).forEach(slot -> consumer.accept(gui, slot));

    return gui;
  }
}