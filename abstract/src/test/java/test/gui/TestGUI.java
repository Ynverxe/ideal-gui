package test.gui;

import com.github.ynverxe.idealgui.AbstractGUI;
import com.github.ynverxe.idealgui.ClickableItem;
import com.github.ynverxe.idealgui.GUIDesignType;
import org.jetbrains.annotations.Nullable;
import test.event.TestClickEvent;
import test.item.TestItem;
import test.viewer.TestViewer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class TestGUI extends AbstractGUI<TestViewer, TestItem, TestClickEvent, TestGUI, Object> {

  public TestGUI(@NotNull GUIDesignType type, BiPredicate<TestGUI, TestViewer> openHandler, BiConsumer<TestGUI, TestViewer> closeHandler, BiPredicate<TestGUI, TestClickEvent> clickHandler) {
    super(type, openHandler, closeHandler, clickHandler);
    this.handle = new Object();
  }

  public TestGUI(@NotNull GUIDesignType type) {
    this(type, (testViewers, testOpenEvent) -> true, (testViewers, testCloseEvent) -> {}, (testViewers, testClickEvent) -> true);
  }

  @Override
  protected void sendTitleUpdate(@NotNull Component old, @NotNull Component title) {
    viewers().forEach(testViewer -> testViewer.updateTitle(title));
  }

  @Override
  protected void sendSlotUpdate(int slot, @Nullable ClickableItem<TestItem, TestClickEvent> item) {
    viewers().forEach(testViewer -> testViewer.updateSlot(slot, item));
  }

  @Override
  protected void tickerFail(@NotNull String key, @NotNull Throwable throwable) {
    throwable.fillInStackTrace();
  }

  @Override
  protected void open(@NotNull TestViewer testViewer, @NotNull Component title, @NotNull List<ClickableItem<TestItem, TestClickEvent>> itemsView) {
    testViewer.open(title, itemsView);
  }

  @Override
  protected void close(@NotNull TestViewer testViewer) {
    testViewer.clear();
  }
}