package com.github.ynverxe.idealgui;

import com.github.ynverxe.blue.collection.list.ReadWriteDelegatedList;
import com.github.ynverxe.idealgui.item.manager.ItemHolder;
import com.github.ynverxe.idealgui.item.object.ItemProvider;
import org.jetbrains.annotations.NotNull;

import java.util.*;

class RenderCache<Item, Click> implements ItemHolder<Item, Click> {

  private final AbstractGUI<?, Item, Click, ?, ?> gui;
  private final List<@NotNull ItemProvider<Item, Click>> renderedItems;
  private final int capacity;

  public RenderCache(AbstractGUI<?, Item, Click, ?, ?> gui) {
    this.gui = gui;
    GUIDesignType type = gui.type();
    this.capacity = type.capacity();

    ItemProvider<Item, Click>[] array = new ItemProvider[capacity];
    Arrays.fill(array, ItemProvider.nullProvider());
    this.renderedItems = new ReadWriteDelegatedList<>(false, Arrays.asList(array));
  }

  void renderItem(@NotNull ItemProvider<Item, Click> item, int slot, boolean checkStaticSlot) {
    if (slot >= capacity) {
      throw new IndexOutOfBoundsException("Slot index '" + slot + "' but capacity is '" + capacity + "'");
    }

    if (checkStaticSlot && !gui.staticItems().isNull(slot)) {
      return;
    }

    ItemProvider<Item, Click> previous = renderedItems.set(slot, item);
    previous.restart();

    gui.sendSlotUpdate(slot, item.lastCached());
  }

  @Override
  public @NotNull ItemProvider<Item, Click> getItem(int index) {
    return renderedItems.get(index);
  }

  @Override
  public @NotNull List<ItemProvider<Item, Click>> collectItems(int fromIndex, int count) {
    Objects.checkIndex(fromIndex, capacity);
    Objects.checkIndex(fromIndex + count, capacity);

    List<ItemProvider<Item, Click>> collected = new ArrayList<>(count);
    for (int i = (fromIndex + count) - 1; i >= fromIndex; i--) {
      collected.add(getItem(i));
    }

    return collected;
  }

  @Override
  public int length() {
    return capacity;
  }

  @Override
  public int nonNullCount() {
    return Math.toIntExact(renderedItems.stream().filter(provider -> !provider.isNull()).count());
  }

  @NotNull
  @Override
  public Iterator<@NotNull ItemProvider<Item, Click>> iterator() {
    return Collections.unmodifiableList(renderedItems).iterator();
  }
}