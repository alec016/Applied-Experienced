package es.degrassi.appex.data;

import es.degrassi.appex.data.loot.BlockDropProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class AppexLootTableProvider extends LootTableProvider {
  private static final List<SubProviderEntry> SUB_PROVIDERS = List.of(
      new SubProviderEntry(BlockDropProvider::new, LootContextParamSets.BLOCK)
  );

  public AppexLootTableProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
    super(packOutput, Set.of(), SUB_PROVIDERS, provider);
  }

  @Override
  protected void validate(WritableRegistry<LootTable> tables, ValidationContextSource validationContext,
                          ProblemReporter.Collector problems) {
    // Do not validate against all registered loot tables
  }
}
