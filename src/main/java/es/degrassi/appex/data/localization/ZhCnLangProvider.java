package es.degrassi.appex.data.localization;

import es.degrassi.appex.definition.AExpText;
import net.minecraft.data.DataGenerator;

public class ZhCnLangProvider extends LocalizationProvider{
  protected ZhCnLangProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  public Lang getLang() {
    return Lang.zh_cn;
  }

  @Override
  protected void generate() {
    addEnum(AExpText.class, v -> switch (v) {
      case CONVERTER_TITLE -> "经验转换器";
      case EXPERIENCE_BUTTON_INSERT -> "存入: %s %s";
      case EXPERIENCE_BUTTON_EXTRACT -> "取出: %s %s";
      case CONVERTER_TOOLTIP -> "已存储：%s XP";
      case CONVERTER_INV -> "已存储 %s";
      //region toTranslate
      case EXPERIENCE_INVERT_BUTTON_TRUE -> v.getEnglishText();
      case EXPERIENCE_INVERT_BUTTON_FALSE -> v.getEnglishText();
      case P2PAttunementExperience -> v.getEnglishText();
      //endregion
      default -> v.getEnglishText();
    });
  }
}
