package com.github.ynverxe.idealgui;

import com.github.ynverxe.blue.collection.list.ReadWriteDelegatedList;
import com.github.ynverxe.idealgui.item.manager.ItemContainer;
import com.github.ynverxe.idealgui.item.object.ItemProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static com.github.ynverxe.idealgui.item.object.ItemProvider.nullProvider;

@SuppressWarnings("unchecked, rawtypes")
abstract class BaseItemContainer<Item, Click, G extends AbstractGUI>
  implements ItemContainer<Item, Click, G> {

  protected final G gui;
  protected final List<@NotNull ItemProvider<Item, Click>> items;

  BaseItemContainer(@NotNull G gui, @NotNull List<ItemProvider<Item, Click>> items) {
    this.gui = gui;
    this.items = new ReadWriteDelegatedList<>(false, items);
  }

  @Override
  public @NotNull ItemProvider<Item, Click> getItem(int index) {
    return items.get(index);
  }

  @Override
  public @NotNull List<ItemProvider<Item, Click>> collectItems(int fromIndex, int count) {
    checkIndex(fromIndex);
    checkIndex(fromIndex + count);

    List<ItemProvider<Item, Click>> collected = new ArrayList<>();

    for (int i = 0; i < count; i++) {
      collected.add(this.items.get(i));
    }

    return collected;
  }

  @Override
  public int length() {
    return items.size();
  }

  @Override
  public int nonNullCount() {
    return Math.toIntExact(items.stream().filter(provider -> provider != nullProvider()).count());
  }

  @SafeVarargs
  @Override
  public final @NotNull ItemContainer<Item, Click, G> contents(ItemProvider<Item, Click> @NotNull ... contents) {
    setContents(Arrays.copyOf(contents, this.items.size()));
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull ItemContainer<Item, Click, G> contents(@NotNull Collection<ItemProvider<Item, Click>> clickableItems) {
    setContents(clickableItems.toArray(new ItemProvider[0]));
    return this;
  }

  @Override
  public @NotNull ItemContainer<Item, Click, G> setItem(@NotNull ItemProvider<Item, Click> item, int index) {
    this.items.set(index, item);
    gui.handleItemContainerChange(this, Collections.singletonMap(index, MutationType.REPLACE));
    return this;
  }

  @Override
  public @NotNull ItemContainer<Item, Click, G> removeItem(int index) {
    doRemove(index);
    gui.handleItemContainerChange(this, Collections.singletonMap(index, MutationType.REMOVE));
    return this;
  }

  @Override
  public @NotNull ItemContainer<Item, Click, G> removeItemsIf(@NotNull BiPredicate<Integer, @Nullable ItemProvider<Item, Click>> filter) {
    List<Integer> removed = new ArrayList<>();
    for (int i = 0; i < this.items.size(); i++) {
      if (filter.test(i, getItem(i))) {
        doRemove(i);
        removed.add(i);
      }
    }

    gui.handleItemContainerChange(this, removed.stream()
      .map(i -> new AbstractMap.SimpleEntry<>(i, MutationType.REMOVE))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

    return this;
  }

  @Override
  public @NotNull ItemContainer<Item, Click, G> removeItems(int from, int count) {
    checkIndex(from);
    checkIndex(count);
    for (int i = (from + count) - 1; i >= from; i--) {
      doRemove(i);
    }
    return this;
  }

  @Override
  public @NotNull G gui() {
    return gui;
  }

  protected abstract void setContents(ItemProvider<Item, Click>... items);

  protected abstract void doRemove(int index);

  @NotNull
  @Override
  public Iterator<@Nullable ItemProvider<Item, Click>> iterator() {
    return new ItemIterator();
  }

  private void checkIndex(int index) {
    int size = items.size();
    if (index >= size) {
      throw new IndexOutOfBoundsException("Index is '" + index + "' but size is '" + size + "'");
    }
  }

  private class ItemIterator implements Iterator<@Nullable ItemProvider<Item, Click>> {
    private int index = -1;

    @Override
    public boolean hasNext() {
      return index + 1 < length();
    }

    @Override
    public @Nullable ItemProvider<Item, Click> next() {
      return getItem(++index);
    }

    @Override
    public void remove() {
      removeItem(index);
    }
  }

  static class AccumulativeImpl<Item, Click, G extends AbstractGUI> extends BaseItemContainer<Item, Click, G>
    implements Accumulative<Item, Click, G> {

    AccumulativeImpl(@NotNull G gui) {
      super(gui, new ArrayList<>());
    }

    @Override
    public @NotNull Accumulative<Item, Click, G> addItem(@NotNull ItemProvider<Item, Click> item) {
      int index = items.size();
      items.add(item);

      gui.handleItemContainerChange(this, Collections.singletonMap(index, MutationType.ADD));
      return this;
    }

    @Override
    protected void setContents(ItemProvider<Item, Click>... items) {
      this.items.clear();
      for (ItemProvider<Item, Click> item : items) {
        addItem(item);
      }
      this.gui.renderContents();
    }

    @Override
    protected void doRemove(int index) {
      this.items.remove(index);
    }
  }

  static class PreSizedItemContainer<Item, Click, G extends AbstractGUI> extends BaseItemContainer<Item, Click, G> {

    PreSizedItemContainer(@NotNull G gui) {
      super(gui, Arrays.asList(new ItemProvider[gui.type().capacity()]));

      items.replaceAll(ignored -> nullProvider());
    }

    @Override
    protected void setContents(ItemProvider<Item, Click>... items) {
      if (items.length >= this.items.size()) {
        throw new IllegalArgumentException("Contents length exceeds the capacity");
      }

      for (int i = 0; i < this.items.size(); i++) {
        ItemProvider<Item, Click> item = nullProvider();
        if (i < items.length) {
          item = items[i];
        }
        this.items.set(i, item);
      }

      gui.renderContents();
    }

    @Override
    protected void doRemove(int index) {
      this.items.set(index, nullProvider());
    }
  }
}