package es.degrassi.appexp.data;

import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.appexp.definition.AExpBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BlockTagsProvider extends net.neoforged.neoforge.common.data.BlockTagsProvider {

  public BlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                           ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, AppliedExperienced.MODID, existingFileHelper);
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
