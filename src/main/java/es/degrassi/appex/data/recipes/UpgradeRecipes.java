package es.degrassi.appex.data.recipes;

import appeng.core.definitions.ItemDefinition;
import appeng.recipes.game.StorageCellDisassemblyRecipe;
import appeng.recipes.game.StorageCellUpgradeRecipe;
import es.degrassi.appex.definition.AExpItems;
import es.degrassi.appex.item.ExperiencePortableCellItem;
import es.degrassi.appex.item.ExperienceStorageCell;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

public class UpgradeRecipes extends AppexRecipeProvider {
  public UpgradeRecipes(HolderLookup.Provider registries, RecipeOutput output) {
    super(registries, output);
  }

  record CellUpgradeTier(String suffix, ItemDefinition<?> cell, ItemLike component) {
  }

  @Override
  public void buildRecipes() {
    storageCellUpgradeRecipes(output);
  }

  private void storageCellUpgradeRecipes(RecipeOutput output) {
    storageCellUpgradeRecipes(
        output,
        List.of(
            new CellUpgradeTier("1k", AExpItems.EXPERIENCE_CELL_1K, componentFromCell(AExpItems.EXPERIENCE_CELL_1K)),
            new CellUpgradeTier("4k", AExpItems.EXPERIENCE_CELL_4K, componentFromCell(AExpItems.EXPERIENCE_CELL_4K)),
            new CellUpgradeTier("16k", AExpItems.EXPERIENCE_CELL_16K, componentFromCell(AExpItems.EXPERIENCE_CELL_16K)),
            new CellUpgradeTier("64k", AExpItems.EXPERIENCE_CELL_64K, componentFromCell(AExpItems.EXPERIENCE_CELL_64K)),
            new CellUpgradeTier("256k", AExpItems.EXPERIENCE_CELL_256K, componentFromCell(AExpItems.EXPERIENCE_CELL_256K))
        ),
        List.of(AExpItems.EXPERIENCE_CELL_HOUSING)
    );
    storageCellUpgradeRecipes(
        output,
        List.of(
            new CellUpgradeTier("1k", AExpItems.PORTABLE_EXPERIENCE_CELL_1K, componentFromPortableCell(AExpItems.PORTABLE_EXPERIENCE_CELL_1K)),
            new CellUpgradeTier("4k", AExpItems.PORTABLE_EXPERIENCE_CELL_4K, componentFromPortableCell(AExpItems.PORTABLE_EXPERIENCE_CELL_4K)),
            new CellUpgradeTier("16k", AExpItems.PORTABLE_EXPERIENCE_CELL_16K, componentFromPortableCell(AExpItems.PORTABLE_EXPERIENCE_CELL_16K)),
            new CellUpgradeTier("64k", AExpItems.PORTABLE_EXPERIENCE_CELL_64K, componentFromPortableCell(AExpItems.PORTABLE_EXPERIENCE_CELL_64K)),
            new CellUpgradeTier("256k", AExpItems.PORTABLE_EXPERIENCE_CELL_256K, componentFromPortableCell(AExpItems.PORTABLE_EXPERIENCE_CELL_256K))
        ),
        List.of(AExpItems.EXPERIENCE_CELL_HOUSING)
    );
  }

  private Item componentFromCell(ItemDefinition<ExperienceStorageCell> cell) {
    return cell.asItem().getTier().componentSupplier().get();
  }

  private Item componentFromPortableCell(ItemDefinition<ExperiencePortableCellItem> cell) {
    return cell.asItem().getTier().componentSupplier().get();
  }

  private void storageCellUpgradeRecipes(RecipeOutput output, List<CellUpgradeTier> tiers,
                                         List<ItemLike> additionalDisassemblyItems) {
    for (int i = 0; i < tiers.size(); i++) {
      var fromTier = tiers.get(i);
      var inputCell = fromTier.cell().asItem();
      var inputId = fromTier.cell().id();
      var resultComponent = fromTier.component().asItem();

      cellDisassembly(output, additionalDisassemblyItems, fromTier);

      // Allow a direct upgrade to any higher tier
      for (int j = i + 1; j < tiers.size(); j++) {
        var toTier = tiers.get(j);
        var resultCell = toTier.cell().asItem();
        var inputComponent = toTier.component().asItem();

        var recipeId = inputId.withPath(path -> "upgrade/" + path + "_to_" + toTier.suffix);

        output.accept(
            key(recipeId),
            new StorageCellUpgradeRecipe(
                inputCell, inputComponent,
                resultCell, resultComponent),
            null);
      }
    }
  }

  private void cellDisassembly(RecipeOutput output, List<ItemLike> additionalReturn, CellUpgradeTier tier) {
    List<ItemStackTemplate> results = new ArrayList<>();
    for (var itemLike : additionalReturn) {
      results.add(new ItemStackTemplate(itemLike.asItem()));
    }
    results.add(new ItemStackTemplate(tier.component.asItem()));

    output.accept(
        key(tier.cell.id().withPrefix("cell_upgrade/")),
        new StorageCellDisassemblyRecipe(
            tier.cell.asItem(),
            results),
        null);
  }

  private static ResourceKey<Recipe<?>> key(Identifier id) {
    return ResourceKey.create(Registries.RECIPE, id);
  }
}
