package test;

import com.github.ynverxe.idealgui.AdditionType;
import com.github.ynverxe.idealgui.ClickableItem;
import com.github.ynverxe.idealgui.GUIDesignType;
import test.event.TestClickEvent;
import test.gui.TestGUI;
import test.item.TestItem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GUIOperationsTest {

  private final TestGUI gui = new TestGUI(GUIDesignType.squared(3, 9));

  @Test
  public void testItemAddition() {
    List<ClickableItem<TestItem, TestClickEvent>> contents = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      contents.add(new ClickableItem<>(new TestItem()));
    }

    gui.contents(contents);

    assertTrue(contents.containsAll(gui.itemsSnapshot().values()));

    List<ClickableItem<TestItem, TestClickEvent>> newItems = new ArrayList<>();

    for (int i = 0; i < 5; i++) {
      newItems.add(new ClickableItem<>(new TestItem()));
    }

    List<ClickableItem<TestItem, TestClickEvent>> expectedOrder = new ArrayList<>();
    expectedOrder.addAll(contents.subList(0, 5));
    expectedOrder.addAll(newItems);
    expectedOrder.addAll(contents.subList(5, 10));

    assertEquals(15, expectedOrder.size());

    gui.clickableItemAddition(5, newItems, AdditionType.APPEND);
    assertTrue(expectedOrder.containsAll(gui.itemsSnapshot().values()));

    gui.clickableItemAddition(10, newItems, AdditionType.REPLACE);
    assertTrue(newItems.containsAll(gui.collectItems(9, 5)));
  }
}