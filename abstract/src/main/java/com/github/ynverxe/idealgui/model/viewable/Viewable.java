package com.github.ynverxe.idealgui.model.viewable;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface Viewable<V> extends Iterable<V> {

  @NotNull Set<V> viewers();

  boolean removeViewer(@NotNull V viewer);

  boolean addViewer(@NotNull V viewer);

}