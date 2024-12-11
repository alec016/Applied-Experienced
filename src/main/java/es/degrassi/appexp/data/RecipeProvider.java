package es.degrassi.appexp.data;

import appeng.core.definitions.AEBlocks;
import appeng.items.storage.StorageTier;
import es.degrassi.appexp.definition.AExpBlocks;
import es.degrassi.appexp.definition.AExpItems;
import es.degrassi.appexp.AppliedExperienced;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {

  public RecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
    super(output, lookupProvider);
  }

  @Override
  protected void buildRecipes(RecipeOutput output) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AExpItems.EXPERIENCE_CELL_HOUSING::get)
        .pattern("QRQ")
        .pattern("R R")
        .pattern("OOO")
        .define('Q', AEBlocks.QUARTZ_GLASS)
        .define('R', Tags.Items.DUSTS_REDSTONE)
        .define('O', Items.EXPERIENCE_BOTTLE)
        .unlockedBy("has_dusts/redstone", has(Tags.Items.DUSTS_REDSTONE))
        .save(output, AppliedExperienced.id("experience_cell_housing"));

    var housing = AExpItems.EXPERIENCE_CELL_HOUSING.get();

    AExpItems.getCells().forEach(cell -> {
      var tierName = cell.get().getTier().toString().toLowerCase(Locale.ROOT);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell::get)
          .requires(housing)
          .requires(cellComponent(cell.get().getTier()))
          .unlockedBy("has_cell_component" + tierName, has(cell))
          .unlockedBy("has_experience_housing", has(housing))
          .save(output);
    });

    AExpItems.getPortables().forEach(portable -> {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, portable::get)
          .requires(AEBlocks.ME_CHEST)
          .requires(cellComponent(portable.get().getTier()))
          .requires(AEBlocks.ENERGY_CELL)
          .requires(housing)
          .unlockedBy("has_" + BuiltInRegistries.ITEM.getKey(housing).getPath(), has(housing))
          .unlockedBy("has_energy_cell", has(AEBlocks.ENERGY_CELL))
          .save(output);
    });

    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AExpItems.EXPERIENCE_ACCEPTOR_PART)
        .requires(AExpBlocks.EXPERIENCE_ACCEPTOR)
        .unlockedBy("has_experience_acceptor", has(AExpBlocks.EXPERIENCE_ACCEPTOR))
        .save(output, AppliedExperienced.id("cable_experience_acceptor"));
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AExpBlocks.EXPERIENCE_ACCEPTOR)
        .requires(AExpItems.EXPERIENCE_ACCEPTOR_PART)
        .unlockedBy("has_experience_acceptor", has(AExpBlocks.EXPERIENCE_ACCEPTOR))
        .save(output, AppliedExperienced.id("experience_acceptor_from_part"));
  }

  private static Item cellComponent(StorageTier tier) {
    return tier.componentSupplier().get();
  }
}
