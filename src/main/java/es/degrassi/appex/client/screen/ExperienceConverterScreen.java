package es.degrassi.appex.client.screen;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import es.degrassi.appex.client.Amount;
import es.degrassi.appex.client.container.ExperienceConverterContainer;
import es.degrassi.appex.client.widgets.ExperienceButton;
import es.degrassi.appex.client.widgets.ExperienceWidget;
import es.degrassi.appex.client.widgets.Icon;
import es.degrassi.appex.client.widgets.IconButton;
import es.degrassi.appex.definition.AExpText;
import es.degrassi.appex.network.client.CExperienceButtonClickedPacket;
import es.degrassi.appex.network.client.CInvertExperienceButtonClickedPacket;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class ExperienceConverterScreen extends AEBaseScreen<ExperienceConverterContainer> {

  public ExperienceConverterScreen(ExperienceConverterContainer menu, Inventory playerInventory, Component title, ScreenStyle style) {
    super(menu, playerInventory, title, style);
    var invertButton = new InvertButton(btn -> ClientPacketDistributor.sendToServer(
        new CInvertExperienceButtonClickedPacket(menu.getEntity().getBlockPos())
    ));
    widgets.add("experience", new ExperienceWidget(getImageWidth() - invertButton.getWidth() - 4, menu::getXp, menu::getCapacity, false));
    for (Amount value : Amount.values()) {
      widgets.add("extract_" + value.nameL(), new ExperienceButton(true, value, (btn) -> {
        ClientPacketDistributor.sendToServer(new CExperienceButtonClickedPacket(menu.getEntity().getBlockPos(), value, true));
      }));
      widgets.add("insert_" + value.nameL(), new ExperienceButton(false, value, (btn) -> {
        ClientPacketDistributor.sendToServer(new CExperienceButtonClickedPacket(menu.getEntity().getBlockPos(), value, false));
      }));
    }
    widgets.add("invert_xp_action", invertButton);
  }

  private class InvertButton extends IconButton {
    protected InvertButton(Button.OnPress onPress) {
      super(onPress);
    }

    private boolean inverted() {
      return ExperienceConverterScreen.this.getMenu().inverted;
    }

    @Override
    public Icon getIcon() {
      return inverted() ? Icon.ARROW_RIGHT : Icon.ARROW_LEFT;
    }

    @Override
    public Component getMessage() {
      return (inverted() ? AExpText.EXPERIENCE_INVERT_BUTTON_TRUE : AExpText.EXPERIENCE_INVERT_BUTTON_FALSE).text();
    }
  }
}
