package es.degrassi.appexp.data;

import appeng.api.features.P2PTunnelAttunement;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.appexp.definition.AExpBlocks;
import es.degrassi.appexp.definition.AExpItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ItemTagsProvider extends net.minecraft.data.tags.ItemTagsProvider {

  public ItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                          CompletableFuture<TagLookup<Block>> blockTagsProvider, ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, blockTagsProvider, AppliedExperienced.MODID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
//    var tanks = tag(P2PTunnelAttunement.getAttunementTag(AExpItems.EXPERIENCE_P2P_TUNNEL::get));
//    tanks.add(Items.EXPERIENCE_BOTTLE);
//    tanks.addAll(AExpItems.getItems()
//        .stream()
//        .map(ItemDefinition::holder)
//        .map(Holder::unwrapKey)
//        .filter(Optional::isPresent)
//        .map(Optional::get)
//        .toList()
//    );
//    tanks.addAll(AExpBlocks.getBlocks()
//        .stream()
//        .map(BlockDefinition::item)
//        .map(ItemDefinition::holder)
//        .map(Holder::unwrapKey)
//        .filter(Optional::isPresent)
//        .map(Optional::get)
//        .toList()
//    );
  }
}
