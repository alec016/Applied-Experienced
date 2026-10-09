package es.degrassi.appex.definition;

import es.degrassi.appex.AppliedExperienced;
import es.degrassi.experiencelib.api.datagen.LocalizationEnum;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum AExpText implements LocalizationEnum {
  CONVERTER_TOOLTIP("item.converter.tooltip", "Stored: %s XP"),
  CONVERTER_INV("item.converter.inv", "Stored %s"),
  CONVERTER_TITLE("gui.title.experience_converter", "Experience Tank"),
  EXPERIENCE_WIDGET("gui.element.experience", "%s / %s"),
  CREATIVE_TAB("creative_tab", "Applied Experienced"),
  EXPERIENCE_BUTTON_INSERT("gui.element.experience_button.insert", "Insert: %s %s"),
  EXPERIENCE_BUTTON_EXTRACT("gui.element.experience_button.extract", "Extract: %s %s"),
  EXPERIENCE_INVERT_BUTTON_TRUE("gui.element.experience_invert_button.inverted", "Click to extract xp from Experience Bottles"),
  EXPERIENCE_INVERT_BUTTON_FALSE("gui.element.experience_invert_button.to_invert", "Click to insert xp to Glass Bottles"),
  P2PAttunementExperience("p2p_attunement.experrience", "Portable Experience Storage (i.e. XP Bottle, Glass Bottle, XP Tanks)");

  public final String key;
  @Getter
  private final String englishText;

  AExpText(String key, String englishText) {
    this.key = "text.%s.%s".formatted(AppliedExperienced.MODID, key);
    this.englishText = englishText;
  }

  @Override
  public String getTranslationKey() {
    return key;
  }
}
