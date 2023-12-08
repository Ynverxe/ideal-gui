package com.github.ynverxe.idealgui.minestom;

import com.github.ynverxe.idealgui.AbstractGUI;
import com.github.ynverxe.idealgui.ClickableItem;
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

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class MinestomGUI
  extends AbstractGUI<Player, ItemStack, InventoryPreClickEvent, MinestomGUI, Inventory> {

  private final Inventory inventory;

  public MinestomGUI(
    @NotNull InventoryType inventoryType,
    @NotNull GUIDesignType type,
    BiPredicate<MinestomGUI, Player> openHandler,
    BiConsumer<MinestomGUI, Player> closeHandler,
    BiPredicate<MinestomGUI, InventoryPreClickEvent> clickHandler
  ) {
    super(type, openHandler, closeHandler, clickHandler);

    this.handle = new GUIInventory(inventoryType, showingTitle(), this);

    checkSize(inventoryType.getSize(), type);
    this.inventory = new Inventory(inventoryType, Component.empty());

    InventoryPreClickListener.ensureListenerIsRegistered();
  }

  public MinestomGUI(
    @NotNull InventoryType inventoryType,
    @NotNull GUIDesignType type
  ) {
    this(inventoryType, type, (players, player) -> true, (players, player) -> {}, (players, inventoryPreClickEvent) -> true);
  }

  @Override
  protected void sendTitleUpdate(@NotNull Component old, @NotNull Component title) {
    inventory.setTitle(title);
  }

  @Override
  protected void sendSlotUpdate(int slot, @Nullable ClickableItem<ItemStack, InventoryPreClickEvent> item) {
    ItemStack itemStack = item != null ? item.itemStack() : ItemStack.AIR;

    inventory.setItemStack(slot, itemStack);
  }

  @Override
  protected void tickerFail(@NotNull String key, @NotNull Throwable throwable) {
    MinecraftServer.getExceptionManager().handleException(throwable);
  }

  @Override
  protected void open(@NotNull Player player, @NotNull Component title, @NotNull List<ClickableItem<ItemStack, InventoryPreClickEvent>> itemsView) {
    player.openInventory(inventory);
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