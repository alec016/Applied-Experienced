package es.degrassi.appex.data.tags;

import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.definition.AExpBlocks;
import es.degrassi.appex.definition.AExpItems;
import es.degrassi.appex.definition.AExpTags;
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
    tag(AExpTags.Items.CURIO)
        .addAll(AExpItems.getPortables().stream().map(ItemDefinition::get));

    tag(AExpTags.Items.XP_P2P)
        .addAll(AExpItems.getItems().stream().map(ItemDefinition::get))
        .addAll(AExpBlocks.getBlocks().stream().map(BlockDefinition::asItem));
  }
}
