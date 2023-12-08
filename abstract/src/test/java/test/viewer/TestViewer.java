package test.viewer;

import com.github.ynverxe.idealgui.ClickableItem;
import test.event.TestClickEvent;
import test.item.TestItem;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public class TestViewer {

  private Component renderedTitle;
  private final List<ClickableItem<TestItem, TestClickEvent>> renderedItems = new ArrayList<>();

  public void updateTitle(Component title) {
    this.renderedTitle = title;
  }

  public void updateSlot(int slot, ClickableItem<TestItem, TestClickEvent> item) {
    renderedItems.set(slot, item);
  }

  public Component renderedTitle() {
    return renderedTitle;
  }

  public List<ClickableItem<TestItem, TestClickEvent>> renderedItems() {
    return renderedItems;
  }

  public void clear() {
    this.renderedTitle = null;
    this.renderedItems.clear();
  }

  public void open(Component title, List<ClickableItem<TestItem, TestClickEvent>> items) {
    clear();
    this.renderedTitle = title;
    this.renderedItems.addAll(items);
  }
}