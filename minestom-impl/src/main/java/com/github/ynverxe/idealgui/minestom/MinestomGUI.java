package com.github.ynverxe.idealgui.minestom;

import com.github.ynverxe.idealgui.AbstractGUI;
import com.github.ynverxe.idealgui.item.object.ClickableItem;
import com.github.ynverxe.idealgui.GUIDesignType;
import com.github.ynverxe.idealgui.minestom.listener.InventoryPreClickListener;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class MinestomGUI
  extends AbstractGUI<Player, ItemStack, InventoryPreClickEvent, MinestomGUI, Inventory> {

  private static final Map<InventoryType, GUIDesignType> SQUARED_TYPES = new HashMap<>();

  static {
    SQUARED_TYPES.put(InventoryType.CHEST_1_ROW, GUIDesignType.squared(9, 1));
    SQUARED_TYPES.put(InventoryType.CHEST_2_ROW, GUIDesignType.squared(9, 2));
    SQUARED_TYPES.put(InventoryType.CHEST_3_ROW, GUIDesignType.squared(9, 3));
    SQUARED_TYPES.put(InventoryType.CHEST_4_ROW, GUIDesignType.squared(9, 4));
    SQUARED_TYPES.put(InventoryType.CHEST_5_ROW, GUIDesignType.squared(9, 5));
    SQUARED_TYPES.put(InventoryType.CHEST_6_ROW, GUIDesignType.squared(9, 6));
    SQUARED_TYPES.put(InventoryType.WINDOW_3X3, GUIDesignType.squared(3, 3));
  }

  public MinestomGUI(
    @NotNull InventoryType inventoryType,
    BiPredicate<MinestomGUI, Player> openHandler,
    BiConsumer<MinestomGUI, Player> closeHandler,
    BiPredicate<ItemStack, InventoryPreClickEvent> clickHandler
  ) {
    super(SQUARED_TYPES.getOrDefault(inventoryType, GUIDesignType.custom(inventoryType.getSize())), openHandler, closeHandler, clickHandler);

    this.handle = new GUIInventory(inventoryType, showingTitle(), this);

    if (inventoryType.getSize() != type().capacity())
      throw new IllegalArgumentException();

    InventoryPreClickListener.ensureListenerIsRegistered();
  }

  public MinestomGUI(@NotNull InventoryType inventoryType) {
    this(inventoryType, (players, player) -> false, (players, player) -> {}, (players, inventoryPreClickEvent) -> false);
  }

  @Override
  protected void sendTitleUpdate(@NotNull Component title) {
    handle.setTitle(title);
  }

  @Override
  public void sendSlotUpdate(int slot, @Nullable ClickableItem<ItemStack, InventoryPreClickEvent> item) {
    ItemStack itemStack = item != null ? item.itemStack() : ItemStack.AIR;

    if (itemStack == null) itemStack = ItemStack.AIR;

    handle.setItemStack(slot, itemStack);
  }

  @Override
  protected void tickerFail(@NotNull String key, @NotNull Throwable throwable) {
    MinecraftServer.getExceptionManager().handleException(throwable);
  }

  @Override
  protected void open(@NotNull Player player) {
    player.openInventory(handle);
  }

  @Override
  protected void close(@NotNull Player player) {
    player.closeInventory();
  }

  public static class GUIInventory extends Inventory {
    private final MinestomGUI gui;

    private GUIInventory(@NotNull InventoryType inventoryType, @NotNull Component title, MinestomGUI gui) {
      super(inventoryType, title);
      this.gui = gui;
    }

    public MinestomGUI gui() {
      return gui;
    }
  }
}