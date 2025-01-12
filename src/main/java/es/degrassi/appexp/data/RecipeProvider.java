package es.degrassi.appexp.data;

import appeng.core.definitions.AEBlocks;
import appeng.items.storage.StorageTier;
import appeng.recipes.game.StorageCellDisassemblyRecipe;
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

import java.util.List;
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
      var component = cellComponent(cell.get().getTier());
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell::get)
          .requires(housing)
          .requires(component)
          .unlockedBy("has_cell_component_" + cell.get().getTier().namePrefix(), has(cell))
          .unlockedBy("has_experience_housing", has(housing))
          .save(output);
      output.accept(
          cell.id().withSuffix("_disassembly"),
          new StorageCellDisassemblyRecipe(
              cell.asItem(),
              List.of(component.getDefaultInstance(), housing.getDefaultInstance())),
          null);
    });

    AExpItems.getPortables().forEach(portable -> {
      var component = cellComponent(portable.get().getTier());
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, portable::get)
          .requires(AEBlocks.ME_CHEST)
          .requires(component)
          .requires(AEBlocks.ENERGY_CELL)
          .requires(housing)
          .unlockedBy("has_" + BuiltInRegistries.ITEM.getKey(housing).getPath(), has(housing))
          .unlockedBy("has_energy_cell", has(AEBlocks.ENERGY_CELL))
          .save(output);
      output.accept(
          portable.id().withSuffix("_disassembly"),
          new StorageCellDisassemblyRecipe(
              portable.asItem(),
              List.of(
                  component.getDefaultInstance(),
                  housing.getDefaultInstance(),
                  AEBlocks.ME_CHEST.stack(),
                  AEBlocks.ENERGY_CELL.stack())),
          null);
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
