package es.degrassi.appex.data;

import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.data.event.TranslationLangEvent;
import es.degrassi.appex.data.localization.Lang;
import es.degrassi.appex.data.localization.LocalizationProvider;
import es.degrassi.appex.data.models.AppexModelProvider;
import es.degrassi.appex.data.models.BlockModelProvider;
import es.degrassi.appex.data.models.ItemModelProvider;
import es.degrassi.appex.data.models.PartModelProvider;
import es.degrassi.appex.data.recipes.AppexRecipeProvider;
import es.degrassi.appex.data.tags.BlockTagsProvider;
import es.degrassi.appex.data.tags.FluidTagsProvider;
import es.degrassi.appex.data.tags.ItemTagsProvider;
import es.degrassi.appex.definition.AExpBlocks;
import es.degrassi.appex.definition.AExpItems;
import es.degrassi.appex.definition.AExpParts;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

@EventBusSubscriber(modid = AppliedExperienced.MODID)
public class AppliedExperiencedDataGenerators {

  @SubscribeEvent
  public static void onGatherData(GatherDataEvent.Client event) {
    var generator = event.getGenerator();
    var registries = RegistryPatchGenerator.createLookup(event.getLookupProvider(), createDatapackEntriesBuilder())
        .thenApply(RegistrySetBuilder.PatchedRegistries::full);

    var localizations = LocalizationProvider.create(generator);
    var pack = generator.getVanillaPack(true);

    // Loot
    pack.addProvider(output -> new AppexLootTableProvider(output, registries));

    // Tags
    var blockTagsProvider = pack.addProvider(output -> new BlockTagsProvider(output, registries));
    pack.addProvider(output -> new ItemTagsProvider(output, registries, blockTagsProvider.contentsGetter()));
    pack.addProvider(output -> new FluidTagsProvider(output, registries));

    // Models
    pack.addProvider(AppexModelProvider.create(
        AppliedExperienced.MODID,
        BlockModelProvider::new,
        ItemModelProvider::new,
        PartModelProvider::new
    ));

    // Recipes
    pack.addProvider(bindRegistries(AppexRecipeProvider.Runner::new, registries));

    var translationEvent = new TranslationLangEvent();
    ModLoader.postEventWrapContainerInModOrder(translationEvent);
    // Must run last
    for (var local : localizations) {
      pack.addProvider(_ -> local.withTranslations(translationEvent.get(local.getLang())));
    }
  }

  @SubscribeEvent
  public static void addTranslations(final TranslationLangEvent event) {
    //region langs
    var es_es = Lang.es_es;
    var ja_jp = Lang.ja_jp;
    var ru_ru = Lang.ru_ru;
    var zh_cn = Lang.zh_cn;
    //endregion
    //region keys
    var xpAcceptorBlock = block(AExpBlocks.EXPERIENCE_ACCEPTOR);
    var xpAcceptorItem = item(AExpBlocks.EXPERIENCE_ACCEPTOR.item());
    var xpTankBlock = block(AExpBlocks.EXPERIENCE_CONVERTER);
    var xpTankItem = item(AExpBlocks.EXPERIENCE_CONVERTER.item());
    var xpAcceptorPart = item(AExpParts.EXPERIENCE_ACCEPTOR);
    var xpCellHousing = item(AExpItems.EXPERIENCE_CELL_HOUSING);
    var xpCell1k = item(AExpItems.EXPERIENCE_CELL_1K);
    var xpCell4k = item(AExpItems.EXPERIENCE_CELL_4K);
    var xpCell16k = item(AExpItems.EXPERIENCE_CELL_16K);
    var xpCell64k = item(AExpItems.EXPERIENCE_CELL_64K);
    var xpCell256k = item(AExpItems.EXPERIENCE_CELL_256K);
    var xpPCell1k = item(AExpItems.PORTABLE_EXPERIENCE_CELL_1K);
    var xpPCell4k = item(AExpItems.PORTABLE_EXPERIENCE_CELL_4K);
    var xpPCell16k = item(AExpItems.PORTABLE_EXPERIENCE_CELL_16K);
    var xpPCell64k = item(AExpItems.PORTABLE_EXPERIENCE_CELL_64K);
    var xpPCell256k = item(AExpItems.PORTABLE_EXPERIENCE_CELL_256K);
    var xpP2P = item(AExpParts.EXPERIENCE_P2P_TUNNEL);
    //endregion
    //region es_es
    event.add(es_es, xpAcceptorBlock, "ME Conector de Experiencia");
    event.add(es_es, xpAcceptorItem, "ME Conector de Experiencia");
    event.add(es_es, xpTankBlock, "Tanque de Experiencia");
    event.add(es_es, xpTankItem, "Tanque de Experiencia");
    event.add(es_es, xpAcceptorPart, "ME Parte de Conector de Experiencia");
    event.add(es_es, xpCellHousing, "ME Carcasa de almacenamiento de Experiencia");
    event.add(es_es, xpCell1k, "1k ME Celda de Almacenamiento de Experiencia");
    event.add(es_es, xpCell4k, "4k ME Celda de Almacenamiento de Experiencia");
    event.add(es_es, xpCell16k, "16k ME Celda de Almacenamiento de Experiencia");
    event.add(es_es, xpCell64k, "64k ME Celda de Almacenamiento de Experiencia");
    event.add(es_es, xpCell256k, "256k ME Celda de Almacenamiento de Experiencia");
    event.add(es_es, xpPCell1k, "1k Celda de Experiencia Portátil");
    event.add(es_es, xpPCell4k, "4k Celda de Experiencia Portátil");
    event.add(es_es, xpPCell16k, "16k Celda de Experiencia Portátil");
    event.add(es_es, xpPCell64k, "64k Celda de Experiencia Portátil");
    event.add(es_es, xpPCell256k, "256k Celda de Experiencia Portátil");
    event.add(es_es, xpP2P, "Tunel P2P de Experiencia");
    //region untranslated
    //endregion
    //endregion
    //region ja_jp
    event.add(ja_jp, xpAcceptorBlock, "ME経験値アクセプター");
    event.add(ja_jp, xpAcceptorItem, "ME経験値アクセプター");
    event.add(ja_jp, xpTankBlock, "経験値変換機");
    event.add(ja_jp, xpTankItem, "経験値変換機");
    event.add(ja_jp, xpCellHousing, "ME経験値セル筐体");
    event.add(ja_jp, xpCell1k, "1k ME経験値ストレージセル");
    event.add(ja_jp, xpCell4k, "4k ME経験値ストレージセル");
    event.add(ja_jp, xpCell16k, "16k ME経験値ストレージセル");
    event.add(ja_jp, xpCell64k, "64k ME経験値ストレージセル");
    event.add(ja_jp, xpCell256k, "256k ME経験値ストレージセル");
    event.add(ja_jp, xpPCell1k, "1k ポータブル経験値セル");
    event.add(ja_jp, xpPCell4k, "4k ポータブル経験値セル");
    event.add(ja_jp, xpPCell16k, "16k ポータブル経験値セル");
    event.add(ja_jp, xpPCell64k, "64k ポータブル経験値セル");
    event.add(ja_jp, xpPCell256k, "256k ポータブル経験値セル");
    event.add(ja_jp, xpP2P, "経験値P2Pトンネル");
    //region untranslated
    // TODO: fix this untranslated keys
    event.add(ja_jp, xpAcceptorPart, "Experience Acceptor Part");
    //endregion
    //endregion
    //region ru_ru
    event.add(ru_ru, xpAcceptorBlock, "МЭ приёмщик опыта");
    event.add(ru_ru, xpAcceptorItem, "МЭ приёмщик опыта");
    event.add(ru_ru, xpTankBlock, "Преобразователь опыта");
    event.add(ru_ru, xpTankItem, "Преобразователь опыта");
    event.add(ru_ru, xpCellHousing, "МЭ корпус ячейки опыта");
    event.add(ru_ru, xpCell1k, "1К МЭ ячейка хранения опыта");
    event.add(ru_ru, xpCell4k, "4К МЭ ячейка хранения опыта");
    event.add(ru_ru, xpCell16k, "16К МЭ ячейка хранения опыта");
    event.add(ru_ru, xpCell64k, "64К МЭ ячейка хранения опыта");
    event.add(ru_ru, xpCell256k, "256К МЭ ячейка хранения опыта");
    event.add(ru_ru, xpPCell1k, "1К переносная ячейка опыта");
    event.add(ru_ru, xpPCell4k, "4К переносная ячейка опыта");
    event.add(ru_ru, xpPCell16k, "16К переносная ячейка опыта");
    event.add(ru_ru, xpPCell64k, "64К переносная ячейка опыта");
    event.add(ru_ru, xpPCell256k, "256К переносная ячейка опыта");
    event.add(ru_ru, xpP2P, "P2P туннель опыта");
    //region untranslated
    // TODO: fix this untranslated keys
    event.add(ru_ru, xpAcceptorPart, "Experience Acceptor Part");
    //endregion
    //endregion
    //region zh_cn
    event.add(zh_cn, xpAcceptorBlock, "ME经验接收器");
    event.add(zh_cn, xpAcceptorItem, "ME经验接收器");
    event.add(zh_cn, xpTankBlock, "经验转换器");
    event.add(zh_cn, xpTankItem, "经验转换器");
    event.add(zh_cn, xpCellHousing, "ME经验元件外壳");
    event.add(zh_cn, xpCell1k, "1k ME经验存储元件");
    event.add(zh_cn, xpCell4k, "4k ME经验存储元件");
    event.add(zh_cn, xpCell16k, "16k ME经验存储元件");
    event.add(zh_cn, xpCell64k, "64k ME经验存储元件");
    event.add(zh_cn, xpCell256k, "256k ME经验存储元件");
    event.add(zh_cn, xpPCell1k, "1k便携经验元件1k便携经验元件");
    event.add(zh_cn, xpPCell4k, "4k便携经验元件");
    event.add(zh_cn, xpPCell16k, "16k便携经验元件");
    event.add(zh_cn, xpPCell64k, "64k便携经验元件");
    event.add(zh_cn, xpPCell256k, "256k便携经验元件");
    event.add(zh_cn, xpP2P, "经验P2P通道");
    //region untranslated
    // TODO: fix this untranslated keys
    event.add(zh_cn, xpAcceptorPart, "Experience Acceptor Part");
    //endregion
    //endregion
  }

  private static String block(BlockDefinition<?> id) {
    return "block.%s.%s".formatted(AppliedExperienced.MODID, id.id().getPath());
  }

  private static String item(ItemDefinition<?> id) {
    return "item.%s.%s".formatted(AppliedExperienced.MODID, id.id().getPath());
  }

  private static RegistrySetBuilder createDatapackEntriesBuilder() {
    return new RegistrySetBuilder();
  }

  private static <T extends DataProvider> DataProvider.Factory<T> bindRegistries(
      BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> factory,
      CompletableFuture<HolderLookup.Provider> factories) {
    return packOutput -> factory.apply(packOutput, factories);
  }
}
