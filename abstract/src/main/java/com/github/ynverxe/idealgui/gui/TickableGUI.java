package com.github.ynverxe.idealgui.gui;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

@ApiStatus.NonExtendable
@SuppressWarnings("rawtypes")
public interface TickableGUI<Viewer, Item, Click, G extends GUI, Handle>
  extends GUI<Viewer, Item, Click, G, Handle> {

  void tick();

  @NotNull Map<String, Consumer<G>> tickHandlers();

  void addTickHandler(@NotNull String key, @NotNull Consumer<G> handler);

  @Nullable Consumer<G> removeTickHandler(@NotNull String key);

  void clearTickHandlers();

  @Contract("_ -> this")
  default @NotNull G autoTick(boolean ticking) {
    if (ticking) {
      GUITicker.instance().addGUI(this);
    } else {
      GUITicker.instance().removeGUI(this);
    }

    return (G) this;
  }
}