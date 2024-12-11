package es.degrassi.appexp.definition;

import es.degrassi.appexp.AppliedExperienced;
import net.minecraft.network.chat.Component;

public enum AExpText {
  EXPERIENCE("experience"),
  CREATIVE_TAB("creative_tab"),
  EXPERIENCE_BUTTON_INSERT("experience_button.insert"),
  EXPERIENCE_BUTTON_EXTRACT("experience_button.extract");

  public final String key;

  AExpText(String key) {
    this.key = "text.%s.%s".formatted(AppliedExperienced.MODID, key);
  }

  public Component formatted(Object...params) {
    return Component.translatable(key, params);
  }
}
