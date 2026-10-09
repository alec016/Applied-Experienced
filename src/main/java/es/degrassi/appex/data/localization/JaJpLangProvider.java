package es.degrassi.appex.data.localization;

import es.degrassi.appex.definition.AExpText;
import net.minecraft.data.DataGenerator;

public class JaJpLangProvider extends LocalizationProvider{
  protected JaJpLangProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  public Lang getLang() {
    return Lang.ja_jp;
  }

  @Override
  protected void generate() {
    addEnum(AExpText.class, v -> switch (v) {
      case CONVERTER_TITLE -> "経験値変換機";
      case EXPERIENCE_BUTTON_INSERT -> "注入: %s %s";
      case EXPERIENCE_BUTTON_EXTRACT -> "抽出: %s %s";
      //region toTranslate
      case CONVERTER_TOOLTIP -> v.getEnglishText();
      case CONVERTER_INV -> v.getEnglishText();
      case EXPERIENCE_INVERT_BUTTON_TRUE -> v.getEnglishText();
      case EXPERIENCE_INVERT_BUTTON_FALSE -> v.getEnglishText();
      case P2PAttunementExperience -> v.getEnglishText();
      //endregion
      default -> v.getEnglishText();
    });
  }
}
