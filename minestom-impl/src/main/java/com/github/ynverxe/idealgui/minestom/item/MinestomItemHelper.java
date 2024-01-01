package com.github.ynverxe.idealgui.minestom.item;

import com.github.ynverxe.idealgui.item.manager.ItemHelper;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.ItemStack;

public class MinestomItemHelper extends ItemHelper<ItemStack, InventoryPreClickEvent> {

  public static final MinestomItemHelper INSTANCE = new MinestomItemHelper();

  @Override
  protected ItemStack air() {
    return ItemStack.AIR;
  }
}