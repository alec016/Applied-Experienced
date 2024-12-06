package es.degrassi.appexp;

import net.minecraft.network.chat.Component;

public enum AEText {
  EXPERIENCE("experience"),
  CREATIVE_TAB("creative_tab"),
  LEVELS("levels");

  public final String key;

  AEText(String key) {
    this.key = "text.%s.%s".formatted(AppliedExperienced.MODID, key);
  }

  public Component formatted(Object...params) {
    return Component.translatable(key, params);
  }
}
