package es.degrassi.appexp.data;

import appeng.api.features.P2PTunnelAttunement;
import es.degrassi.appexp.AEItems;
import es.degrassi.appexp.AppliedExperienced;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ItemTagsProvider extends net.minecraft.data.tags.ItemTagsProvider {

  public ItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                          CompletableFuture<TagLookup<Block>> blockTagsProvider,
                          @Nullable ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, blockTagsProvider, AppliedExperienced.MODID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
        var tanks = tag(P2PTunnelAttunement.getAttunementTag(AEItems.EXPERIENCE_P2P_TUNNEL::get));
        tanks.add(Items.EXPERIENCE_BOTTLE);
  }
}
