package es.degrassi.appex.client;


import es.degrassi.appex.client.widgets.Icon;
import es.degrassi.appex.definition.AExpText;
import lombok.Getter;
import net.minecraft.network.chat.Component;

import java.util.Locale;

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

  public Icon getIcon(boolean extraction) {
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
