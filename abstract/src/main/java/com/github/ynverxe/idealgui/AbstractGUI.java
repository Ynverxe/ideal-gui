package com.github.ynverxe.idealgui;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.*;

@ApiStatus.NonExtendable
@SuppressWarnings("rawtypes")
public abstract class AbstractGUI<Viewer, Item, Click, G extends GUI, Handle>
  implements TickableGUI<Viewer, Item, Click, G, Handle> {

  protected Handle handle;
  private final @NotNull GUIDesignType type;

  // Concurrency for inventory updates
  private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  private final Lock readLock = readWriteLock.readLock();
  private final Lock writeLock = readWriteLock.writeLock();

  // title
  private @NotNull Component title = Component.empty();

  // items
  private ClickableItem<Item, Click>[] items;
  final ItemRenderHandler rendererHandler;

  // event handlers
  private final BiPredicate<G, Viewer> openHandler;
  private final BiConsumer<G, Viewer> closeHandler;
  private final BiPredicate<G, Click> clickHandler;

  // tickers
  private final Map<String, Consumer<G>> tickers = new ConcurrentHashMap<>();

  // viewers
  private final Set<Viewer> viewers = ConcurrentHashMap.newKeySet();

  // pages
  private final Pagination pagination;

  @SuppressWarnings("unchecked")
  protected AbstractGUI(@NotNull GUIDesignType type, BiPredicate<G, Viewer> openHandler, BiConsumer<G, Viewer> closeHandler, BiPredicate<G, Click> clickHandler) {
    this.type = type;
    this.items = new ClickableItem[type.capacity()];
    this.rendererHandler = new ItemRenderHandler(type);
    this.openHandler = openHandler;
    this.closeHandler = closeHandler;
    this.clickHandler = clickHandler;
    this.pagination = new PaginationImpl(this);
  }

  @Override
  public @NotNull Component showingTitle() {
    return read(() -> title);
  }

  @Override
  public @NotNull G title(@NotNull Component component) {
    write(() -> {
      Component old = this.title;
      this.title = component;

      sendTitleUpdate(old, component);
    });

    return castThis();
  }

  @SafeVarargs
  @Override
  public final @NotNull G contents(ClickableItem<Item, Click> @NotNull ... items) {
    write(() -> {
      int currentPageIndex = pagination.index();

      this.items = Arrays.copyOf(items, items.length);

      int newPage = Math.min(currentPageIndex, pagination.length() - 1);
      rendererHandler.renderPage(newPage);
    });

    return castThis();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull G contents(@NotNull Collection<ClickableItem<Item, Click>> clickableItems) {
    write(() -> {
      int currentPageIndex = pagination.index();

      this.items = clickableItems.toArray(new ClickableItem[0]);

      int newPage = Math.min(currentPageIndex, pagination.length() - 1);
      rendererHandler.renderPage(newPage);
    });

    return castThis();
  }

  @Override
  public @NotNull G clickableItem(@NotNull ClickableItem<Item, Click> item, int index) {
    write(() -> {
      if (items.length <= index) {
        this.items = Arrays.copyOfRange(items, 0, index + 1);
      }

      items[index] = item;

      rendererHandler.render(index, item);
    });

    return castThis();
  }

  @Override
  public @NotNull G clickableItemAddition(
    int startIndex, @NotNull Collection<ClickableItem<Item, Click>> toAdd, @NotNull AdditionType type) {
    write(() -> this.items = type.performAddition(startIndex, this.items, toAdd));
    return castThis();
  }

  @Override
  public @NotNull G removeItem(int index) {
    write(() -> performItemRemove(index));
    return castThis();
  }

  @Override
  public @NotNull G removeItemsIf(@NotNull BiPredicate<Integer, @Nullable ClickableItem<Item, Click>> filter) {
    write(() -> {
      for (int i = 0; i < this.items.length; i++) {
        if (filter.test(i, items[i])) {
          performItemRemove(i);
        }
      }
    });

    return castThis();
  }

  @Override
  public @NotNull G removeItems(int from, int count) {
    write(() -> {
      for (int i = 0; i < count; i++) {
        performItemRemove(i);
      }
    });

    return castThis();
  }

  @Override
  public @NotNull BiPredicate<G, Viewer> openHandler() {
    return openHandler;
  }

  @Override
  public @NotNull BiConsumer<G, Viewer> closeHandler() {
    return closeHandler;
  }

  @Override
  public @NotNull BiPredicate<G, Click> clickHandler() {
    return clickHandler;
  }

  @Override
  public void tick() {
    tickers.forEach((key, ticker) -> {
      try {
        ticker.accept(castThis());
      } catch (Throwable throwable) {
        tickerFail(key, new RuntimeException("Ticker '" + key + "' caused an exception", throwable));
      }
    });
  }

  @Override
  public @NotNull Map<String, Consumer<G>> tickHandlers() {
    return Collections.unmodifiableMap(tickers);
  }

  @Override
  public void addTickHandler(@NotNull String key, @NotNull Consumer<G> handler) {
    this.tickers.put(key, handler);
  }

  @Override
  public @Nullable Consumer<G> removeTickHandler(@NotNull String key) {
    return tickers.remove(key);
  }

  @Override
  public void clearTickHandlers() {
    tickers.clear();
  }

  @Override
  public @NotNull GUIDesignType type() {
    return type;
  }

  @Override
  public @NotNull Handle handle() {
    return handle;
  }

  @Override
  public int reduceCapacity() {
    synchronized (this) {
      int toReduce = 0;

      for (int i = items.length - 1; i >= 0; i--) {
        if (items[i] != null) break;

        toReduce++;
      }

      if (toReduce != 0) {
        this.items = Arrays.copyOfRange(items, 0, items.length - toReduce);
      }

      return toReduce;
    }
  }

  @Override
  public @NotNull Pagination pagination() {
    return pagination;
  }

  @Override
  public @Nullable ClickableItem<Item, Click> getItem(int index) {
    checkIndex(index);

    return read(() -> items[index]);
  }

  @Override
  public @NotNull Map<Integer, @Nullable ClickableItem<Item, Click>> itemsSnapshot() {
    return read(() -> {
      LinkedHashMap<Integer, ClickableItem<Item, Click>> items = new LinkedHashMap<>();

      for (int i = 0; i < this.items.length; i++) {
        items.put(i, this.items[i]);
      }

      return items;
    });
  }

  @Override
  public @NotNull List<ClickableItem<Item, Click>> collectItems(int fromIndex, int count) {
    return read(() -> {
      checkIndex(fromIndex);

      List<ClickableItem<Item, Click>> collected = new ArrayList<>();

      for (int i = 0; i < count; i++) {
        collected.add(this.items[fromIndex + i]);
      }

      return collected;
    });
  }

  @Override
  public int itemCount() {
    return read(() -> items.length);
  }

  @Override
  public @NotNull List<ClickableItem<Item, Click>> renderedItemsView() {
    return Arrays.asList(rendererHandler.rendered);
  }

  @Override
  public @NotNull Set<Viewer> viewers() {
    return Collections.unmodifiableSet(viewers);
  }

  @Override
  public boolean removeViewer(@NotNull Viewer viewer) {
    boolean removed = viewers.remove(viewer);

    if (removed) {
      closeHandler.accept(castThis(), viewer);
      close(viewer);
    }

    return removed;
  }

  @Override
  public boolean addViewer(@NotNull Viewer viewer) {
    return read(() -> {
      boolean cancel = openHandler.test(castThis(), viewer);

      if (cancel) return false;

      boolean added = viewers.add(viewer);

      if (added) {
        open(viewer, title, renderedItemsView());
      }

      return added;
    });
  }

  @NotNull
  @Override
  public Iterator<Viewer> iterator() {
    return viewers().iterator();
  }

  @SuppressWarnings("unchecked")
  private G castThis() {
    return (G) this;
  }

  protected void write(@NotNull Runnable runnable) {
    try {
      writeLock.lock();

      runnable.run();
    } finally {
      writeLock.unlock();
    }
  }

  protected <E> E read(@NotNull Supplier<E> supplier) {
    try {
      readLock.lock();

      return supplier.get();
    } finally {
      readLock.unlock();
    }
  }

  protected abstract void sendTitleUpdate(@NotNull Component old, @NotNull Component title);

  protected abstract void sendSlotUpdate(int slot, @Nullable ClickableItem<Item, Click> item);

  protected abstract void tickerFail(@NotNull String key, @NotNull Throwable throwable);

  protected abstract void open(@NotNull Viewer viewer, @NotNull Component title, @NotNull List<ClickableItem<Item, Click>> itemsView);

  protected abstract void close(@NotNull Viewer viewer);

  protected static void checkSize(int size, @NotNull GUIDesignType type) {
    if (size != type.capacity()) {
      throw new IllegalArgumentException("Incompatible inventory type with gui design type");
    }
  }

  private void checkIndex(int index) {
    if (index >= items.length)
      throw new IndexOutOfBoundsException("Index is '" + index + "' but length is '" + items.length + "'");
  }

  private void performItemAdd(int index, ClickableItem<Item, Click> item) {
    this.items[index] = item;

    int page = getIndexPage(index);

    if (page == pagination.index()) {
      rendererHandler.renderByIndex(index, item);
    }
  }

  private void performItemRemove(int index) {
    this.items[index] = null;

    int page = getIndexPage(index);

    if (page == pagination.index()) {
      rendererHandler.renderByIndex(index, null);
    }  }

  private int getIndexPage(int index) {
    return Math.min(0, (int) Math.floor((double) index % type.capacity()) - 1);
  }

  class ItemRenderHandler {
    private final ClickableItem<Item, Click>[] rendered;

    @SuppressWarnings("unchecked")
    public ItemRenderHandler(GUIDesignType type) {
      this.rendered = new ClickableItem[type.capacity()];
    }

    public void render(int slot, ClickableItem<Item, Click> item) {
      rendered[slot] = item;
      sendSlotUpdate(slot, item);
    }

    public void renderByIndex(int index, ClickableItem<Item, Click> item) {
      int slot = (int) Math.floor((double) index % rendered.length);

      rendered[slot] = item;
      sendSlotUpdate(slot, item);
    }

    public void renderPage(int page) {
      int start = page * rendered.length;
      for (int slot = 0; slot < rendered.length; slot++) {
        int index = start + slot;
        ClickableItem<Item, Click> item = index < items.length ? items[slot] : null;
        rendered[slot] = item;
        sendSlotUpdate(slot, item);
      }
    }
  }
}