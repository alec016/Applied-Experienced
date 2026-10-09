package es.degrassi.appex.data.localization;

import es.degrassi.appex.definition.AExpText;
import net.minecraft.data.DataGenerator;

public class EsEsLangProvider extends LocalizationProvider{
  protected EsEsLangProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  public Lang getLang() {
    return Lang.es_es;
  }

  @Override
  protected void generate() {
    addEnum(AExpText.class, v -> switch (v) {
      case CONVERTER_TOOLTIP -> "Almacenado: %s XP";
      case CONVERTER_INV -> "Almacenado %s";
      case CONVERTER_TITLE -> "Tanque de Experiencia";
      case EXPERIENCE_BUTTON_INSERT -> "Insertar: %s %s";
      case EXPERIENCE_BUTTON_EXTRACT -> "Extraer: %s %s";
      case EXPERIENCE_INVERT_BUTTON_TRUE -> "Click para extraer xp desde las Botellas de Experiencia";
      case EXPERIENCE_INVERT_BUTTON_FALSE -> "Click para insertar xp a Botellas de Cristal";
      case P2PAttunementExperience -> "Almacenamiento de Experiencia Portable (i.e. Botellas de Experiencia, Botellas de Cristal, Tanques de Experiencia)";
      default -> v.getEnglishText();
    });
  }
}
