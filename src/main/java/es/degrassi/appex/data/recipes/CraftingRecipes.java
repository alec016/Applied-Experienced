package es.degrassi.appex.data.recipes;

import appeng.core.ConventionTags;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.items.storage.StorageTier;
import es.degrassi.appex.definition.AExpBlocks;
import es.degrassi.appex.definition.AExpItems;
import es.degrassi.appex.definition.AExpParts;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public class CraftingRecipes extends AppexRecipeProvider {

  public CraftingRecipes(HolderLookup.Provider registries, RecipeOutput output) {
    super(registries, output);
  }

  @Override
  protected void buildRecipes() {
    var housing = AExpItems.EXPERIENCE_CELL_HOUSING;
    shaped(RecipeCategory.MISC, housing)
        .pattern("QRQ")
        .pattern("R R")
        .pattern("OOO")
        .define('Q', AEBlocks.QUARTZ_GLASS)
        .define('R', Tags.Items.DUSTS_REDSTONE)
        .define('O', Items.EXPERIENCE_BOTTLE)
        .unlockedBy("has_dusts/redstone", has(Tags.Items.DUSTS_REDSTONE));

    shaped(RecipeCategory.MISC, AExpBlocks.EXPERIENCE_CONVERTER)
        .pattern("ppp")
        .pattern("ede")
        .pattern("ccc")
        .define('p', TagKey.create(BuiltInRegistries.ITEM.key(), Identifier.parse("c:planks")))
        .define('d', AEItems.CELL_COMPONENT_64K)
        .define('c', TagKey.create(BuiltInRegistries.ITEM.key(), Identifier.parse("c:glass_panes")))
        .define('e', Items.EXPERIENCE_BOTTLE)
        .unlockedBy("has_experince_bottle", has(Items.EXPERIENCE_BOTTLE));

    shaped(RecipeCategory.MISC, AExpBlocks.EXPERIENCE_ACCEPTOR)
        .pattern("ede")
        .pattern("dad")
        .pattern("ede")
        .define('a', AEBlocks.ENERGY_ACCEPTOR)
        .define('d', AEItems.CELL_COMPONENT_1K)
        .define('e', Items.EXPERIENCE_BOTTLE)
        .unlockedBy("has_experience_bottle", has(Items.EXPERIENCE_BOTTLE));

    shaped(RecipeCategory.MISC, AExpBlocks.EXPERIENCE_ACCEPTOR)
        .pattern("ded")
        .pattern("eae")
        .pattern("ded")
        .define('a', AEBlocks.ENERGY_ACCEPTOR)
        .define('d', AEItems.CELL_COMPONENT_1K)
        .define('e', Items.EXPERIENCE_BOTTLE)
        .unlockedBy("has_experience_bottle", has(Items.EXPERIENCE_BOTTLE))
        .save(output, makeId("experience_acceptor_mirror"));

    shapeless(RecipeCategory.MISC, AExpParts.EXPERIENCE_ACCEPTOR)
        .requires(AExpBlocks.EXPERIENCE_ACCEPTOR)
        .unlockedBy("has_experience_acceptor", has(AExpBlocks.EXPERIENCE_ACCEPTOR))
        .save(output, makeId("experience_acceptor_to_part"));

    shapeless(RecipeCategory.MISC, AExpBlocks.EXPERIENCE_ACCEPTOR)
        .requires(AExpParts.EXPERIENCE_ACCEPTOR)
        .unlockedBy("has_experience_acceptor", has(AExpParts.EXPERIENCE_ACCEPTOR))
        .save(output, makeId("experience_acceptor_from_part"));

    shapeless(RecipeCategory.MISC, AExpParts.EXPERIENCE_ACCESS_TERMINAL)
        .requires(ConventionTags.ILLUMINATED_PANEL)
        .requires(AEItems.ENGINEERING_PROCESSOR)
        .requires(Items.EXPERIENCE_BOTTLE)
        .unlockedBy("has_experience_bottle", has(Items.EXPERIENCE_BOTTLE))
        .save(output, makeId("network/parts/terminals_experience_access"));

    AExpItems.getCells().forEach(cell -> {
      var component = cellComponent(cell.get().getTier());
      shapeless(RecipeCategory.MISC, cell)
          .requires(housing)
          .requires(component)
          .unlockedBy("has_cell_component_" + cell.get().getTier().namePrefix(), has(cell))
          .unlockedBy("has_experience_housing", has(housing));
    });

    AExpItems.getPortables().forEach(cell -> {
      var component = cellComponent(cell.get().getTier());
      shapeless(RecipeCategory.MISC, cell)
          .requires(AEBlocks.ME_CHEST)
          .requires(component)
          .requires(housing)
          .requires(AEBlocks.ENERGY_CELL)
          .unlockedBy("has_" + housing.id().getPath(), has(housing))
          .unlockedBy("has_energy_cell", has(AEBlocks.ENERGY_CELL))
          .save(output);
    });
  }

  private static Item cellComponent(StorageTier tier) {
    return tier.componentSupplier().get();
  }
}
