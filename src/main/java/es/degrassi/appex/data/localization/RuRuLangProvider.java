package es.degrassi.appex.data.localization;

import es.degrassi.appex.definition.AExpText;
import net.minecraft.data.DataGenerator;

public class RuRuLangProvider extends LocalizationProvider{
  protected RuRuLangProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  public Lang getLang() {
    return Lang.ru_ru;
  }

  @Override
  protected void generate() {
    addEnum(AExpText.class, v -> switch (v) {
      case CONVERTER_TITLE -> "Преобразователь опыта";
      case EXPERIENCE_BUTTON_INSERT -> "Вставка: %s %s";
      case EXPERIENCE_BUTTON_EXTRACT -> "Извлечение: %s %s";
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
