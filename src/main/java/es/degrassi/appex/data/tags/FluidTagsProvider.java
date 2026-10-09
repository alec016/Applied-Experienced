package es.degrassi.appex.data.tags;

import es.degrassi.appex.AppliedExperienced;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.level.material.Fluid;

import java.util.concurrent.CompletableFuture;

public class FluidTagsProvider extends IntrinsicHolderTagsProvider<Fluid> {
  public FluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
    super(output, Registries.FLUID, lookupProvider, (e) -> e.builtInRegistryHolder().key(), AppliedExperienced.MODID);
  }

  @Override
  protected void addTags(HolderLookup.Provider registries) {
  }
}
