package com.github.ynverxe.idealgui.minestom.listener;

import com.github.ynverxe.idealgui.minestom.MinestomGUI;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.InventoryClickEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.trait.InventoryEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

public class InventoryPreClickListener implements Consumer<InventoryPreClickEvent> {

  private static final String NODE_KEY = "gui-click-listener-node";
  private static final EventNode<InventoryEvent> NODE =
    EventNode.event(NODE_KEY, EventFilter.INVENTORY, event -> event instanceof InventoryClickEvent);

  @Override
  public void accept(InventoryPreClickEvent event) {
    if (event.getInventory() instanceof MinestomGUI.GUIInventory guiInventory) {
      MinestomGUI gui = guiInventory.gui();
      gui.clickHandler().test(gui, event);
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