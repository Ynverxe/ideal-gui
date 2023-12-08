package com.github.ynverxe.idealgui.minestomdemo;

import com.github.ynverxe.idealgui.GUIDesignType;
import com.github.ynverxe.idealgui.Pagination;
import com.github.ynverxe.idealgui.minestom.MinestomGUI;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.net.InetSocketAddress;

public final class MinestomDemoServer {

  public static void main(String[] args) {
    start();
  }

  public static void start() {
    MinecraftServer.init().start(new InetSocketAddress(25565));

    GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();

    MinestomGUI gui = new MinestomGUI(InventoryType.CHEST_3_ROW, GUIDesignType.squared(9, 3));

    for (int i = 0; i < gui.type().capacity() * 6; i++) {
      gui.item(ItemStack.of(Material.DIAMOND_SWORD).withDisplayName(Component.text(i)), i);
    }

    Instance instance = MinecraftServer.getInstanceManager().createInstanceContainer();
    eventHandler.addListener(PlayerLoginEvent.class, event -> {
      Player player = event.getPlayer();
      player.setInstance(instance);
      player.setAllowFlying(true);
      player.setFlying(true);

      gui.addViewer(player);
    });

    MinecraftServer.getSchedulerManager().buildTask(() -> {
      Pagination pagination = gui.pagination();
      if (!pagination.hasNext()) {
        pagination.set(0);
      } else {
        pagination.next();
      }

      gui.title(Component.text("Page #" + pagination.index() + 1));
    });
  }
}