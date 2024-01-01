package com.github.ynverxe.idealgui.gui;

import com.github.ynverxe.blue.collection.list.ReadWriteDelegatedList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApiStatus.Internal
public final class GUITicker {

  private static final GUITicker INSTANCE = new GUITicker();

  private final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);
  private final Executor EXECUTOR = Executors.newCachedThreadPool();
  private final List<TickableGUI<?, ?, ?, ?, ?>> guis = new ReadWriteDelegatedList<>(false);

  private GUITicker() {
    EXECUTOR_SERVICE.scheduleAtFixedRate(this::updateGUIS, 0L, 50L, TimeUnit.MILLISECONDS);
  }

  public void updateGUIS() {
    for (TickableGUI<?, ?, ?, ?, ?> gui : guis) {
      EXECUTOR.execute(gui::tick);
    }
  }

  public void addGUI(@NotNull TickableGUI<?, ?, ?, ?, ?> gui) {
    if (guis.contains(gui)) return;

    guis.add(gui);
  }

  public void removeGUI(@NotNull TickableGUI<?, ?, ?, ?, ?> gui) {
    guis.remove(gui);
  }

  public static @NotNull GUITicker instance() {
    return INSTANCE;
  }
}