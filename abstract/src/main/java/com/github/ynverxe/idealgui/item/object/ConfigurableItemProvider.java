package com.github.ynverxe.idealgui.item.object;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public final class ConfigurableItemProvider<Item, Click> extends ScheduledItemProvider<Item, Click, ConfigurableItemProvider<Item, Click>> {

  private final @NotNull ClickableItem<Item, Click> base;
  private final List<Function<ClickableItem<Item, Click>, ClickableItem<Item, Click>>> mappers = new ArrayList<>();
  private int index;

  public ConfigurableItemProvider(@NotNull ClickableItem<Item, Click> base) {
    this.base = base;
  }

  @Override
  protected @NotNull ClickableItem<Item, Click> computeItem() {
    if (index >= this.mappers.size()) {
      index = 0;
    }

    Function<ClickableItem<Item, Click>, ClickableItem<Item, Click>> mapper = mappers.get(index++);
    return mapper.apply(base);
  }

  @Override
  protected @NotNull ClickableItem<Item, Click> initialItem() {
    Function<ClickableItem<Item, Click>, ClickableItem<Item, Click>> mapper = mappers.get(0);
    return mapper.apply(base);
  }

  @SafeVarargs
  public final @NotNull ConfigurableItemProvider<Item, Click> addMappers(
    Function<ClickableItem<Item, Click>, ClickableItem<Item, Click>> @NotNull ... mappers) {
    this.mappers.addAll(Arrays.asList(mappers));
    return this;
  }

  public @NotNull ConfigurableItemProvider<Item, Click> addMappers(
    @NotNull Collection<Function<ClickableItem<Item, Click>, ClickableItem<Item, Click>>> mappers) {
    this.mappers.addAll(mappers);
    return this;
  }

  @SafeVarargs
  public final @NotNull ConfigurableItemProvider<Item, Click> mappers(
    Function<ClickableItem<Item, Click>, ClickableItem<Item, Click>> @NotNull ... mappers) {
    this.mappers.clear();
    return addMappers(mappers);
  }

  public @NotNull ConfigurableItemProvider<Item, Click> mappers(
    @NotNull Collection<Function<ClickableItem<Item, Click>, ClickableItem<Item, Click>>> mappers) {
    this.mappers.clear();
    return addMappers(mappers);
  }

  @SafeVarargs
  public final @NotNull ConfigurableItemProvider<Item, Click> mappingItem(
    Function<Item, Item> @NotNull ... mappers) {
    return mappingItem(Arrays.asList(mappers));
  }

  public @NotNull ConfigurableItemProvider<Item, Click> mappingItem(
    @NotNull List<Function<Item, Item>> mappers) {
    for (Function<Item, Item> mapper : mappers) {
      this.mappers.add(clickableItem -> {
        Item newItem = mapper.apply(clickableItem.itemStack());
        return new ClickableItem<>(newItem, clickableItem.clickHandler());
      });
    }
    return this;
  }
}