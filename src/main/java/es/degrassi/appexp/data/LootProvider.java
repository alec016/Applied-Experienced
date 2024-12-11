package es.degrassi.appexp.data;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.BlockDefinition;
import es.degrassi.appexp.definition.AExpBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class LootProvider extends BlockLootSubProvider {
  public LootProvider(HolderLookup.Provider registries) {
    super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
  }

  @Override
  protected void generate() {
    for (var block : getKnownBlocks()) {
      add(
          block,
          LootTable.lootTable()
              .withPool(LootPool.lootPool()
              .setRolls(ConstantValue.exactly(1))
              .add(LootItem.lootTableItem(block))
              .when(ExplosionCondition.survivesExplosion())));
    }
  }

  @NotNull
  @Override
  protected Iterable<Block> getKnownBlocks() {
    return blocks().stream().map(BlockDefinition::block).map(Block.class::cast)::iterator;
  }

  private List<BlockDefinition<?>> blocks() {
    return AExpBlocks.getBlocks();
  }
}
