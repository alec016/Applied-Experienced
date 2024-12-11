package es.degrassi.appexp.client.screen;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.guidebook.PageAnchor;
import es.degrassi.appexp.client.container.ExperienceConverterContainer;
import es.degrassi.appexp.client.widgets.ExperienceWidget;
import es.degrassi.appexp.client.widgets.Icon;
import es.degrassi.appexp.client.widgets.IconButton;
import es.degrassi.appexp.definition.AExpText;
import es.degrassi.appexp.network.client.CExperienceButtonClickedPacket;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class ExperienceConverterScreen extends AEBaseScreen<ExperienceConverterContainer> {

  public ExperienceConverterScreen(ExperienceConverterContainer menu, Inventory playerInventory, Component title, ScreenStyle style) {
    super(menu, playerInventory, title, style);
    widgets.add("experience", new ExperienceWidget(getXSize(), menu));
    for (Amount value : Amount.values()) {
      widgets.add("extract_" + value.nameL(), new ExperienceButton(true, value, (btn) -> {
        PacketDistributor.sendToServer(new CExperienceButtonClickedPacket(menu.entity.getBlockPos(), value, true));
      }));
      widgets.add("insert_" + value.nameL(), new ExperienceButton(false, value, (btn) -> {
        PacketDistributor.sendToServer(new CExperienceButtonClickedPacket(menu.entity.getBlockPos(), value, false));
      }));
    }
  }

  protected boolean shouldAddToolbar() {
    return false;
  }

  @Override
  protected @Nullable PageAnchor getHelpTopic() {
    return null;
  }

  private static class ExperienceButton extends IconButton {
    protected ExperienceButton(boolean extraction, Amount amount, OnPress onPress) {
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
      return (extraction ? AExpText.EXPERIENCE_BUTTON_EXTRACT : AExpText.EXPERIENCE_BUTTON_INSERT).formatted(getFormatted(), "level(s)");
    }
  }
}
