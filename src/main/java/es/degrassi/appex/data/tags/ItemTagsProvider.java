package es.degrassi.appex.data.tags;

import es.degrassi.appex.AppliedExperienced;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider;

import java.util.concurrent.CompletableFuture;

public class ItemTagsProvider extends BlockTagCopyingItemTagProvider {
  public ItemTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries,
                          CompletableFuture<TagLookup<Block>> blockTagsProvider) {
    super(packOutput, registries, blockTagsProvider, AppliedExperienced.MODID);
  }
  @Override
  protected void addTags(HolderLookup.Provider provider) {

  }
}
