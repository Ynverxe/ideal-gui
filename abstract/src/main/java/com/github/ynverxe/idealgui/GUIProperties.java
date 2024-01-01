package com.github.ynverxe.idealgui;

import com.github.ynverxe.idealgui.model.property.PropertyPointer;
import org.jetbrains.annotations.NotNull;

public final class GUIProperties<T> implements PropertyPointer<T> {

  public static final PropertyPointer<Boolean> RELOCATE_SLOTS = new GUIProperties<>("relocate_slots", Boolean.class);

  private final String key;
  private final Class<T> expectedType;

  private GUIProperties(String key, Class<T> expectedType) {
    this.key = key;
    this.expectedType = expectedType;
  }

  @Override
  public @NotNull String key() {
    return key;
  }

  @Override
  public @NotNull Class<T> expected() {
    return expectedType;
  }

}