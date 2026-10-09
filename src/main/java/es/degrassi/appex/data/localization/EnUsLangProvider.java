package es.degrassi.appex.data.localization;

import es.degrassi.appex.definition.AExpBlocks;
import es.degrassi.appex.definition.AExpItems;
import es.degrassi.appex.definition.AExpTags;
import es.degrassi.appex.definition.AExpText;
import net.minecraft.data.DataGenerator;

public class EnUsLangProvider extends LocalizationProvider {
  protected EnUsLangProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  public Lang getLang() {
    return Lang.en_us;
  }

  @Override
  protected void generate() {
    for (var block : AExpBlocks.getBlocks()) {
      add("block.%s.%s".formatted(block.id().getNamespace(), block.id().getPath()), block.getEnglishName());
      add("item.%s.%s".formatted(block.id().getNamespace(), block.id().getPath()), block.getEnglishName());
    }
    for (var block : AExpItems.getItems()) {
      add("item.%s.%s".formatted(block.id().getNamespace(), block.id().getPath()), block.getEnglishName());
    }

    AExpTags.addLocations(this::add);

    addEnum(AExpText.class);
  }
}
