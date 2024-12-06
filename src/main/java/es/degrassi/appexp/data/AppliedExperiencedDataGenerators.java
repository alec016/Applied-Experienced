package es.degrassi.appexp.data;

import net.neoforged.neoforge.data.event.GatherDataEvent;

public class AppliedExperiencedDataGenerators {
  public static void onGatherData(GatherDataEvent event) {
    var generator = event.getGenerator();
    var packOutput = generator.getPackOutput();
    var lookupProvider = event.getLookupProvider();
    var existingFileHelper = event.getExistingFileHelper();

    var blockTagsProvider = new BlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
    generator.addProvider(true, blockTagsProvider);
    generator.addProvider(true, new ItemTagsProvider(packOutput, lookupProvider,
        blockTagsProvider.contentsGetter(), existingFileHelper));

    generator.addProvider(true, new ItemModelProvider(packOutput, existingFileHelper));
    generator.addProvider(true, new RecipeProvider(packOutput, lookupProvider));
  }
}
