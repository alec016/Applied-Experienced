package es.degrassi.appex.client.screen;

import appeng.api.config.TerminalStyle;
import appeng.api.storage.ILinkStatus;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import es.degrassi.appex.client.Amount;
import es.degrassi.appex.client.container.ExperienceAccessTermMenu;
import es.degrassi.appex.client.widgets.ExperienceButton;
import es.degrassi.appex.client.widgets.ExperienceWidget;
import es.degrassi.appex.network.client.CExperienceButtonClickedInPartPacket;
import guideme.color.ConstantColor;
import guideme.document.LytRect;
import guideme.render.SimpleRenderContext;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class ExperienceAccessTermScreen<C extends ExperienceAccessTermMenu> extends AEBaseScreen<C> {
  private static final int GUI_TOP_AND_BOTTOM_PADDING = 54;

  private static final int GUI_PADDING_X = 8;
  private static final int GUI_PADDING_Y = 6;

  private static final int GUI_HEADER_HEIGHT = 17;
  private static final int GUI_FOOTER_HEIGHT = 99;
  private static final int COLUMNS = 9;

  private static final int ROW_HEIGHT = 18;

  private int visibleRows = 0;

  public ExperienceAccessTermScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
    super(menu, playerInventory, title, style);
    widgets.add("experience", new ExperienceWidget(getImageWidth() - 4, menu::getXp, menu::getCapacity, true));
    for (Amount value : Amount.values()) {
      widgets.add("extract_" + value.nameL(), new ExperienceButton(true, value, (btn) -> {
        ClientPacketDistributor.sendToServer(new CExperienceButtonClickedInPartPacket(menu.containerId, value, true));
      }));
      widgets.add("insert_" + value.nameL(), new ExperienceButton(false, value, (btn) -> {
        ClientPacketDistributor.sendToServer(new CExperienceButtonClickedInPartPacket(menu.containerId, value, false));
      }));
    }
  }

  @Override
  protected void init() {
    this.visibleRows = Math.max(2, TerminalStyle.SMALL.getRows(
        (this.height - GUI_HEADER_HEIGHT - GUI_FOOTER_HEIGHT - GUI_TOP_AND_BOTTOM_PADDING) / ROW_HEIGHT));
    // Render inventory in correct place.
    this.imageHeight = GUI_HEADER_HEIGHT + GUI_FOOTER_HEIGHT + this.visibleRows * ROW_HEIGHT;
    super.init();
  }

  @Override
  public void drawFG(GuiGraphicsExtractor guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
    renderLinkStatus(guiGraphics, getMenu().getLinkStatus());
  }

  private void renderLinkStatus(GuiGraphicsExtractor guiGraphics, ILinkStatus linkStatus) {
    // Draw an overlay indicating the grid is disconnected
    if (!linkStatus.connected()) {
      var renderContext = new SimpleRenderContext(LytRect.empty(), guiGraphics);

      var rect = new LytRect(
          GUI_PADDING_X - 1,
          GUI_HEADER_HEIGHT,
          COLUMNS * 18,
          visibleRows * ROW_HEIGHT);

      renderContext.fillRect(rect, new ConstantColor(0x3f000000));

      // Draw the disconnect status on top of the grid
      var statusDescription = linkStatus.statusDescription();
      if (statusDescription != null) {
        renderContext.renderTextCenteredIn(statusDescription.getString(), ERROR_TEXT_STYLE, rect);
      }
    }
  }
}
