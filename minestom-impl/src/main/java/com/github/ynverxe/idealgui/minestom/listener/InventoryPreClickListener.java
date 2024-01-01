package com.github.ynverxe.idealgui.minestom.listener;

import com.github.ynverxe.idealgui.item.object.ClickableItem;
import com.github.ynverxe.idealgui.item.object.ItemProvider;
import com.github.ynverxe.idealgui.minestom.MinestomGUI;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class InventoryPreClickListener implements Consumer<InventoryPreClickEvent> {

  private static final String NODE_KEY = "gui-click-listener-node";
  private static final EventNode<InventoryEvent> NODE =
    EventNode.event(NODE_KEY, EventFilter.INVENTORY, event -> event instanceof InventoryPreClickEvent);

  static {
    NODE.addListener(InventoryPreClickEvent.class, new InventoryPreClickListener());
  }

  @Override
  public void accept(InventoryPreClickEvent event) {
    if (event.getInventory() instanceof MinestomGUI.GUIInventory guiInventory) {
      MinestomGUI gui = guiInventory.gui();

      int clickedSlot = event.getSlot();

      ItemProvider<ItemStack, InventoryPreClickEvent> provider = gui.renderedItemsView()
        .getItem(clickedSlot);

      ClickableItem<ItemStack, InventoryPreClickEvent> item = null;
      if (provider != null) {
        item = provider.lastCached();
      }

      BiPredicate<ItemStack, InventoryPreClickEvent> clickHandler = item != null && item.clickHandler() != null ?
        item.clickHandler() : gui.clickHandler();

      boolean cancel = clickHandler.test(event.getClickedItem(), event);
      event.setCancelled(cancel);
    }
  }

  @ApiStatus.Internal
  public static void ensureListenerIsRegistered() {
    GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();

    if (!eventHandler.findChildren(NODE_KEY).contains(NODE)) {
      eventHandler.addChild(NODE);
    }
  }
}