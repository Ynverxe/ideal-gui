package com.github.ynverxe.idealgui.model.property;

import org.jetbrains.annotations.NotNull;

public interface PropertyPointer<T> {

  @NotNull String key();

  @NotNull Class<T> expected();

}