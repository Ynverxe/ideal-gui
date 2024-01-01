package com.github.ynverxe.idealgui.minestomdemo;

import com.github.ynverxe.idealgui.GUIDesignType;
import com.github.ynverxe.idealgui.item.object.ClickableItem;
import com.github.ynverxe.idealgui.item.manager.ItemContainer;
import com.github.ynverxe.idealgui.minestom.MinestomGUI;
import com.github.ynverxe.idealgui.minestom.item.MinestomItemHelper;
import com.github.ynverxe.idealgui.util.slot.SlotIterator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.function.Consumer;

/**
 * A DemoServer for practical tests, personal modifications will not be committed.
 */
public final class MinestomDemoServer {

  public static void main(String[] args) {
    new MinestomDemoServer().start();
  }

  private static final Random RANDOM = new Random();
  private static final List<Material> GLASS_PANES = new ArrayList<>();
  private static final MinestomItemHelper ITEM_HELPER = MinestomItemHelper.INSTANCE;

  static {
    for (Material value : Material.values()) {
      if (value != Material.GLASS_PANE && value.name().contains("pane")) {
        GLASS_PANES.add(value);
      }
    }
  }

  public void start() {
    MinecraftServer.init().start(new InetSocketAddress(25565));

    GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();

    MinestomGUI gui = createGUI();

    Instance instance = MinecraftServer.getInstanceManager().createInstanceContainer();
    eventHandler.addListener(PlayerLoginEvent.class, event -> {
      Player player = event.getPlayer();
      event.setSpawningInstance(instance);
      player.setAllowFlying(true);
      player.setFlying(true);

      MinecraftServer.getSchedulerManager().buildTask(() -> gui.addViewer(player))
        .delay(TaskSchedule.seconds(2))
        .schedule();
    });
  }

  private static MinestomGUI createGUI() {
    MinestomGUI gui = new MinestomGUI(InventoryType.CHEST_3_ROW)
      .autoTick(true)
      .relocateItems(true);

    gui.iterateBorders((theGui, slot) -> gui.staticItems().setItem(ClickableItem.empty(), slot))
      .items().rawContents(randomItems())
      .gui()
      .staticItems()
      .backSwitch(ItemStack.of(Material.ARROW), 21)
      .nextSwitch(ItemStack.of(Material.ARROW), 23);

    applyBorderDecoration(gui);

    return gui;
  }

  private static void applyBorderDecoration(MinestomGUI gui) {
    List<Map.Entry<Integer, ItemStack>> items = getBorderDecoration(gui);

    gui.addTickHandler("border_decoration_handler", new Consumer<>() {
      int i = 0;
      int updateInterval = 5;
      int current = updateInterval;
      Map.Entry<Integer, ItemStack> first;
      Map.Entry<Integer, ItemStack> second;

      @Override
      public void accept(MinestomGUI theGui) {
        if (current-- > 0) {
          return;
        }

        current = updateInterval;

        ItemContainer<ItemStack, InventoryPreClickEvent, ?> container = gui.staticItems();

        if (first != null && container.equals(first.getKey(), first.getValue())) {
          container.setItem(ClickableItem.empty(), first.getKey());
        }

        if (second != null && container.equals(second.getKey(), second.getValue())) {
          container.setItem(ClickableItem.empty(), second.getKey());
        }

        first = items.get(i++);
        second = items.get(i++);

        int firstIndex = first.getKey();
        int secondIndex = second.getKey();

        if (container.isAbsolutelyEmpty(firstIndex)) {
          container.setItem(ITEM_HELPER.cancelClick(first.getValue()), firstIndex);
        }

        if (container.isAbsolutelyEmpty(secondIndex)) {
          container.setItem(ITEM_HELPER.cancelClick(second.getValue()), secondIndex);
        }

        if (i >= items.size()) {
          i = 0;
        }
      }
    });
  }

  private static List<Map.Entry<Integer, ItemStack>> getBorderDecoration(MinestomGUI gui) {
    List<Integer> borders = new ArrayList<>();
    SlotIterator.ofBorders((GUIDesignType.Squared) gui.type())
      .forEach(borders::add);

    List<Map.Entry<Integer, ItemStack>> items = new ArrayList<>();

    int xPointer1 = 4, xPointer2 = 4;

    for (int row = 0; row < 3; row++) {
      if (row == 1) {
        xPointer1 = 0;
        xPointer2 = 8;
        appendDecorationItem((GUIDesignType.Squared) gui.type(), row, xPointer1, xPointer2, items);
        continue;
      }

      int pointer1Vec = row == 2 ? 1 : -1;
      int pointer2Vec = row == 2 ? -1 : 1;

      while (xPointer1 >= 0 && xPointer1 <= 8 && xPointer2 >= 0 && xPointer2 <= 8) {
        boolean last = xPointer1 == xPointer2 && row == 2;

        appendDecorationItem((GUIDesignType.Squared) gui.type(), row, xPointer1, xPointer2, items);

        xPointer1 += pointer1Vec;
        xPointer2 += pointer2Vec;

        if (last) break;
      }
    }

    return items;
  }

  private static void appendDecorationItem(GUIDesignType.Squared squared, int row, int pointer1, int pointer2, List<Map.Entry<Integer, ItemStack>> items) {
    int paneIndex = RANDOM.nextInt(GLASS_PANES.size() - 1);
    ItemStack pane = ItemStack.of(GLASS_PANES.get(paneIndex));

    items.add(new AbstractMap.SimpleImmutableEntry<>(squared.toPosition(row + 1, pointer1 + 1), pane));
    items.add(new AbstractMap.SimpleImmutableEntry<>(squared.toPosition(row + 1, pointer2 + 1), pane));
  }

  private static List<ItemStack> randomItems() {
    List<ItemStack> items = new ArrayList<>();
    List<Material> materials = new ArrayList<>(Material.values());
    for (int i = 0; i < materials.size(); i++) {
      items.add(ItemStack.of(materials.get(RANDOM.nextInt(materials.size() - 1))));
    }
    return items;
  }
}