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

  /**
   * @return The title that is actually being displayed.
   */
  @NotNull Component showingTitle();

  /**
   * Sets the title and send it to all viewers.
   * @param component The title component
   * @return this gui.
   */
  @Contract("_ -> this")
  @NotNull G title(@NotNull Component component);

  /**
   * @return The open handler of this GUI.
   */
  @NotNull BiPredicate<G, Viewer> openHandler();

  /**
   * @return The close handler of this GUI.
   */
  @NotNull BiConsumer<G, Viewer> closeHandler();

  /**
   * @return The click handler of this GUI.
   */
  @NotNull BiPredicate<Item, Click> clickHandler();

  /**
   * @return The GUIDesignType of this GUI. This object defines the capacity
   * and the slot format of the GUI.
   */
  @NotNull GUIDesignType type();

  /**
   * @return The platform handle of this GUI.
   */
  @NotNull Handle handle();

  /**
   * @return The {@link Pagination} of this GUI.
   */
  @NotNull Pagination pagination();

  /**
   * @return A view of the rendered items.
   */
  @NotNull ItemHolder<Item, Click> renderedItemsView();

  /**
   * @return An {@link ItemContainer} holding the static items of this gui.
   */
  @NotNull ItemContainer<Item, Click, G> staticItems();

  /**
   * @return An {@link ItemContainer.Accumulative} holding the normal items of this gui.
   */
  @NotNull ItemContainer.Accumulative<Item, Click, G> items();

  /**
   * Relocate the normal items in order of the static items.
   * @see com.github.ynverxe.idealgui.gui.render.ItemRelocator
   *
   * @param relocate the option
   * @return this gui.
   */
  @Contract("_ -> this")
  @NotNull G relocateItems(boolean relocate);

  /**
   * @return A {@link ComposedGUIRenderer} to customize item rendering.
   */
  @NotNull ComposedGUIRenderer<Item, Click, G> renderer();

  /**
   * @return The normal items count that can be rendered. This value is equivalent to
   * subtract the static {@link ItemContainer#nonNullCount()} to the {@link GUIDesignType#capacity()}.
   */
  default int itemsPerPage() {
    return type().capacity() - staticItems().nonNullCount();
  }

  /**
   * Iterates through a row.
   *
   * @param row The row
   * @param consumer The slot consumer
   * @return This gui.
   */
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

  /**
   * Iterates through a column.
   *
   * @param column The row
   * @param consumer The slot consumer
   * @return This gui.
   */
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

  /**
   * Iterates through the borders of this gui.
   *
   * @param consumer The slot consumer
   * @return This gui.
   */
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