package com.github.ynverxe.idealgui.model.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public interface PropertyHolder<T> {

  @NotNull <V> Optional<V> property(@NotNull String key);

  default @NotNull <V> Optional<V> property(@NotNull String key, @NotNull Class<V> expected) {
    return property(key).map(object -> expected.isInstance(object) ? expected.cast(object) : null);
  }

  default @NotNull <V> Optional<V> property(@NotNull PropertyPointer<V> propertyPointer) {
    return property(propertyPointer.key(), propertyPointer.expected());
  }

  @NotNull <V> T setProperty(@NotNull String key, @Nullable V value);

  @NotNull Map<String, Object> propertiesView();

}