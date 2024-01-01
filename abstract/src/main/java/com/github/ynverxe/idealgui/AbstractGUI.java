package com.github.ynverxe.idealgui;

import com.github.ynverxe.idealgui.gui.Pagination;
import com.github.ynverxe.idealgui.gui.render.ComposedGUIRenderer;
import com.github.ynverxe.idealgui.gui.TickableGUI;
import com.github.ynverxe.idealgui.gui.render.ItemRelocator;
import com.github.ynverxe.idealgui.item.object.ClickableItem;
import com.github.ynverxe.idealgui.item.manager.ItemContainer;
import com.github.ynverxe.idealgui.item.manager.ItemHolder;
import com.github.ynverxe.idealgui.item.object.ItemProvider;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

@ApiStatus.NonExtendable
public abstract class AbstractGUI<Viewer, Item, Click, G extends AbstractGUI<?, ?, ?, ?, ?>, Handle>
  implements TickableGUI<Viewer, Item, Click, G, Handle> {

  protected Handle handle;
  private final @NotNull GUIDesignType type;

  // slots
  private final @NotNull ComposedGUIRenderer<Item, Click, G> guiRenderer = new ComposedGUIRenderer<>(castThis());

  // title
  private volatile @NotNull Component title = Component.empty();

  // items
  private final ItemContainer.Accumulative<Item, Click, G> dynamicItemContainer;
  private final ItemContainer<Item, Click, G> staticItems;
  final RenderCache<Item, Click> renderCache;
  private boolean relocateItems;

  // event handlers
  private final BiPredicate<G, Viewer> openHandler;
  private final BiConsumer<G, Viewer> closeHandler;
  private final BiPredicate<Item, Click> clickHandler;

  // tickers
  private final Map<String, Consumer<G>> tickers = new ConcurrentHashMap<>();

  // viewers
  private final Set<Viewer> viewers = ConcurrentHashMap.newKeySet();

  // pages
  private final Pagination pagination;

  private final Map<String, Object> properties = new ConcurrentHashMap<>();

  protected AbstractGUI(@NotNull GUIDesignType type, BiPredicate<G, Viewer> openHandler, BiConsumer<G, Viewer> closeHandler, BiPredicate<Item, Click> clickHandler) {
    this.type = type;
    this.renderCache = new RenderCache<>(this);
    this.openHandler = openHandler;
    this.closeHandler = closeHandler;
    this.clickHandler = clickHandler;
    this.pagination = new PaginationImpl(this);
    this.dynamicItemContainer = new BaseItemContainer.AccumulativeImpl<>(castThis());
    this.staticItems = new BaseItemContainer.PreSizedItemContainer<>(castThis());
  }

  @Override
  public @NotNull Component showingTitle() {
    return title;
  }

  @Override
  public @NotNull G title(@NotNull Component component) {
    this.title = component;
    sendTitleUpdate(component);
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
  public @NotNull BiPredicate<Item, Click> clickHandler() {
    return clickHandler;
  }

  @Override
  public void tick() {
    tickProviders();

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
  public @NotNull Pagination pagination() {
    return pagination;
  }

  @Override
  public @NotNull ItemHolder<Item, Click> renderedItemsView() {
    return renderCache;
  }

  @Override
  public @NotNull ItemContainer<Item, Click, G> staticItems() {
    return staticItems;
  }

  @Override
  public ItemContainer.@NotNull Accumulative<Item, Click, G> items() {
    return dynamicItemContainer;
  }

  @Override
  public @NotNull G relocateItems(boolean relocate) {
    if (this.relocateItems == relocate) return castThis();
    this.relocateItems = relocate;
    renderContents();
    return castThis();
  }

  @Override
  public @NotNull ComposedGUIRenderer<Item, Click, G> renderer() {
    return guiRenderer;
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
    boolean cancel = openHandler.test(castThis(), viewer);

    if (cancel) return false;

    if (!openHandler.test(castThis(), viewer)) {
      return false;
    }

    boolean added = viewers.add(viewer);

    if (added) {
      displayGUI(viewer);
    }

    return added;
  }

  @Override
  public <V> @NotNull G setProperty(@NotNull String key, @Nullable V value) {
    properties.put(key, value);
    return castThis();
  }

  @Override
  public @NotNull Map<String, Object> propertiesView() {
    return Collections.unmodifiableMap(properties);
  }

  @Override
  public @NotNull <V> Optional<V> property(@NotNull String key) {
    return Optional.ofNullable((V) properties.get(key));
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

  protected abstract void sendTitleUpdate(@NotNull Component title);

  protected abstract void sendSlotUpdate(int slot, @Nullable ClickableItem<Item, Click> item);

  protected abstract void tickerFail(@NotNull String key, @NotNull Throwable throwable);

  protected abstract void open(@NotNull Viewer viewer);

  protected abstract void close(@NotNull Viewer viewer);

  private void displayGUI(@NotNull Viewer viewer) {
    open(viewer);
  }

  /**
   * Collect all dynamic items of the current Pagination index and
   * the static items and render it.
   * Null static items aren't consumed.
   */
  void renderContents() {
    ItemProvider<Item, Click>[] pageItems = collectPageItems();

    if (relocateItems) {
      ItemRelocator.relocateItems(pageItems, this);
    }

    boolean checkSlotSkipping = guiRenderer.rendererList().isEmpty();
    guiRenderer.render(pageItems,this);

    int slot = 0;
    for (@Nullable ItemProvider<Item, Click> item : pageItems) {
      if (item != null) {
        renderCache.renderItem(item, slot, checkSlotSkipping);
      }

      slot++;
    }

    int staticSlot = 0;
    for (ItemProvider<Item, Click> staticItem : staticItems) {
      if (!staticItem.isNull()) {
        renderCache.renderItem(staticItem, staticSlot, false);
      }

      staticSlot++;
    }
  }

  private @Nullable ItemProvider<Item, Click>[] collectPageItems() {
    @Nullable ItemProvider<Item, Click>[] items = new ItemProvider[type.capacity()];

    int page = pagination.index();
    int itemsPerPage = itemsPerPage();
    int minRenderedIndex = page * itemsPerPage;
    int maxRenderedIndex = (minRenderedIndex + itemsPerPage);

    for (int slot = 0; slot < items.length; slot++) {
      ItemProvider<Item, Click> item = ItemProvider.nullProvider();

      int index = minRenderedIndex + slot;
      if (index <= maxRenderedIndex && index < items().length()) {
        item = items().getItem(index);
      }

      items[slot] = item;
    }

    return items;
  }

  /**
   * Called when a GUIItemContainer is modified.
   *
   * @param container The container that is calling this method
   * @param changed The changed indexes
   */
  void handleItemContainerChange(ItemContainer<?, ?, ?> container, Map<Integer, MutationType> changed) {
    int page = pagination.index();
    int itemsPerPage = itemsPerPage();
    int minRenderedIndex = page * itemsPerPage;
    int maxRenderedIndex = (minRenderedIndex + itemsPerPage);

    for (Map.Entry<Integer, MutationType> entry : changed.entrySet()) {
      int index = entry.getKey();
      MutationType type = entry.getValue();

      if (type != MutationType.REPLACE && index <= maxRenderedIndex) {
        renderContents();
        return;
      }
    }

    for (Integer index : changed.keySet()) {
      ItemProvider<Item, Click> item = (ItemProvider<Item, Click>) container.getItem(index);

      boolean staticItem = container == staticItems;
      if (staticItem || index >= minRenderedIndex && index <= maxRenderedIndex) {
        int slot = staticItem ? index : getIndexSlot(index, itemsPerPage);
        renderCache.renderItem(item, slot, !staticItem);
      }
    }
  }

  private void tickProviders() {
    int slot = 0;
    for (@NotNull ItemProvider<Item, Click> provider : renderCache) {
      if (provider.handleTick()) {
        sendSlotUpdate(slot, provider.lastCached());
      }

      slot++;
    }
  }

  private static int getIndexSlot(int index, int itemsPerPage) {
    return (int) Math.floor((double) index % itemsPerPage);
  }
}