package es.degrassi.appex.data.tags;

import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.definition.AExpBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class BlockTagsProvider extends IntrinsicHolderTagsProvider<Block> {
  public BlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
    super(output, Registries.BLOCK, lookupProvider, block -> block.builtInRegistryHolder().key(), AppliedExperienced.MODID);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    tag(BlockTags.NEEDS_IRON_TOOL)
        .add(
            AExpBlocks.EXPERIENCE_CONVERTER.block()
        );

    tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .add(
            AExpBlocks.EXPERIENCE_CONVERTER.block()
        );
  }
}
