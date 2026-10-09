package es.degrassi.appex.client.screen;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import es.degrassi.appex.client.container.ExperienceConverterContainer;
import es.degrassi.appex.client.widgets.ExperienceWidget;
import es.degrassi.appex.client.widgets.Icon;
import es.degrassi.appex.client.widgets.IconButton;
import es.degrassi.appex.definition.AExpText;
import es.degrassi.appex.network.client.CExperienceButtonClickedPacket;
import es.degrassi.appex.network.client.CInvertExperienceButtonClickedPacket;
import lombok.Getter;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.Locale;

public class ExperienceConverterScreen extends AEBaseScreen<ExperienceConverterContainer> {

  public ExperienceConverterScreen(ExperienceConverterContainer menu, Inventory playerInventory, Component title, ScreenStyle style) {
    super(menu, playerInventory, title, style);
    var invertButton = new InvertButton(btn -> ClientPacketDistributor.sendToServer(
        new CInvertExperienceButtonClickedPacket(menu.getEntity().getBlockPos())
    ));
    widgets.add("experience", new ExperienceWidget(getImageWidth() - invertButton.getWidth() - 4, menu));
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

  private static class ExperienceButton extends IconButton {
    protected ExperienceButton(boolean extraction, Amount amount, Button.OnPress onPress) {
      super(onPress);
      setIcon(amount.getIcon(extraction));
      setMessage(amount.message(extraction));
    }
  }

  @Getter
  public enum Amount {
    ONE("1", 1),
    TEN("10", 10),
    HUNDRED("100", 100),
    ALL("All");

    private final String formatted;
    private int amount = 0;

    Amount(String formatted, int amount) {
      this(formatted);
      this.amount = amount;
    }

    Amount(String formatted) {
      this.formatted = formatted;
    }

    public String nameL() {
      return name().toLowerCase(Locale.ROOT);
    }

    public boolean isAll() {
      return this == ALL;
    }

    Icon getIcon(boolean extraction) {
      return switch (this) {
        case ONE -> extraction ? Icon.XP_EXTRACT_1 : Icon.XP_INSERT_1;
        case TEN -> extraction ? Icon.XP_EXTRACT_10 : Icon.XP_INSERT_10;
        case HUNDRED -> extraction ? Icon.XP_EXTRACT_100 : Icon.XP_INSERT_100;
        case ALL -> extraction ? Icon.XP_EXTRACT_ALL : Icon.XP_INSERT_ALL;
      };
    }

    public int getAmount(boolean extraction) {
      return extraction ? amount : -amount;
    }

    public Component message(boolean extraction) {
      return (extraction ? AExpText.EXPERIENCE_BUTTON_EXTRACT : AExpText.EXPERIENCE_BUTTON_INSERT).text(getFormatted(), "level(s)");
    }
  }
}
