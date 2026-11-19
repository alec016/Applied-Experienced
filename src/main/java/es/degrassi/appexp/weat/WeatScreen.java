package es.degrassi.appexp.weat;

import appeng.client.gui.style.ScreenStyle;
import de.mari_023.ae2wtlib.api.gui.ScrollingUpgradesPanel;
import de.mari_023.ae2wtlib.api.terminal.IUniversalTerminalCapable;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import es.degrassi.appexp.client.screen.ExperienceAccessTermScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class WeatScreen extends ExperienceAccessTermScreen<WeatMenu> implements IUniversalTerminalCapable {
  private final ScrollingUpgradesPanel upgradesPanel;
  public WeatScreen(WeatMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
    super(menu, playerInventory, title, style);

    if (this.getMenu().isWUT()) {
      this.addToLeftToolbar(this.cycleTerminalButton());
    }

    this.upgradesPanel = this.addUpgradePanel(this.widgets, this.getMenu());
  }

  public void init() {
    super.init();
    this.upgradesPanel.setMaxRows(4);
  }

  @Override
  public WTMenuHost getHost() {
    return (WTMenuHost) getMenu().getHost();
  }

  @Override
  public void storeState() {

  }

  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    return super.keyPressed(keyCode, scanCode, modifiers) || this.checkForTerminalKeys(keyCode, scanCode);
  }
}
