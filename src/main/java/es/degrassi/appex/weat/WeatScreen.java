package es.degrassi.appex.weat;

import appeng.client.gui.style.ScreenStyle;
import de.mari_023.ae2wtlib.api.gui.ScrollingUpgradesPanel;
import de.mari_023.ae2wtlib.api.terminal.IUniversalTerminalCapable;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import es.degrassi.appex.client.screen.ExperienceAccessTermScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class WeatScreen extends ExperienceAccessTermScreen<WeatMenu> implements IUniversalTerminalCapable {
  private final ScrollingUpgradesPanel upgradesPanel;
  public WeatScreen(WeatMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
    super(menu, playerInventory, title, style);

    if (this.getMenu().isWUT()) {
      this.addTerminalSelectionPanel(this.widgets);
    }

    this.upgradesPanel = this.addUpgradePanel(this.widgets, this.getMenu());
  }

  public void init() {
    super.init();
    this.upgradesPanel.setMaxRows(Math.max(2, this.getVisibleRows()));
  }

  @Override
  public WTMenuHost getHost() {
    return (WTMenuHost) getMenu().getHost();
  }

  public boolean keyPressed(KeyEvent event) {
    boolean value = super.keyPressed(event);
    return !value ? this.checkForTerminalKeys(event) : true;
  }
}
