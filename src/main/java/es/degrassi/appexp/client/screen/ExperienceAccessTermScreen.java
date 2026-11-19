package es.degrassi.appexp.client.screen;

import appeng.api.storage.ILinkStatus;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import es.degrassi.appexp.client.Amount;
import es.degrassi.appexp.client.container.ExperienceAccessTermMenu;
import es.degrassi.appexp.client.widgets.ExperienceButton;
import es.degrassi.appexp.client.widgets.ExperienceWidget;
import es.degrassi.appexp.network.client.CExperienceButtonClickedInPartPacket;
import guideme.color.ConstantColor;
import guideme.document.LytRect;
import guideme.render.SimpleRenderContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class ExperienceAccessTermScreen<C extends ExperienceAccessTermMenu> extends AEBaseScreen<C> {

  private static final int GUI_PADDING_X = 8;

  private static final int GUI_HEADER_HEIGHT = 17;
  private static final int COLUMNS = 9;

  private static final int ROW_HEIGHT = 18;

  public ExperienceAccessTermScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
    super(menu, playerInventory, title, style);
    widgets.add("experience", new ExperienceWidget(getXSize() - 4, menu::getXp, menu::getCapacity, true));
    for (Amount value : Amount.values()) {
      widgets.add("extract_" + value.nameL(), new ExperienceButton(true, value, (btn) -> {
        PacketDistributor.sendToServer(new CExperienceButtonClickedInPartPacket(menu.containerId, value, true));
      }));
      widgets.add("insert_" + value.nameL(), new ExperienceButton(false, value, (btn) -> {
        PacketDistributor.sendToServer(new CExperienceButtonClickedInPartPacket(menu.containerId, value, false));
      }));
    }
  }

  @Override
  protected void init() {
    // Render inventory in correct place.
    this.imageHeight = this.imageHeight - 1;
    super.init();
  }

  @Override
  public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
    renderLinkStatus(guiGraphics, getMenu().getLinkStatus());
  }

  private void renderLinkStatus(GuiGraphics guiGraphics, ILinkStatus linkStatus) {
    // Draw an overlay indicating the grid is disconnected
    if (!linkStatus.connected()) {
      var renderContext = new SimpleRenderContext(LytRect.empty(), guiGraphics);

      var rect = new LytRect(
          GUI_PADDING_X - 1,
          GUI_HEADER_HEIGHT,
          COLUMNS * ROW_HEIGHT,
          4 * ROW_HEIGHT);

      renderContext.fillRect(rect, new ConstantColor(0x3f000000));

      // Draw the disconnect status on top of the grid
      var statusDescription = linkStatus.statusDescription();
      if (statusDescription != null) {
        renderContext.renderTextCenteredIn(statusDescription.getString(), ERROR_TEXT_STYLE, rect);
      }
    }
  }
}
