package es.degrassi.appex.data.recipes;

import es.degrassi.appex.AppliedExperienced;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class AppexRecipeProvider extends RecipeProvider {
  protected final HolderGetter<Item> items;

  public AppexRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
    super(registries, output);
    this.items = registries.lookupOrThrow(Registries.ITEM);
  }

  protected final String makeId(String path) {
    return AppliedExperienced.id(path).toString();
  }

  protected static ResourceKey<Recipe<?>> makeKey(String path) {
    return ResourceKey.create(Registries.RECIPE, AppliedExperienced.id(path));
  }

  @FunctionalInterface
  public interface RecipeProviderFactory {
    AppexRecipeProvider create(HolderLookup.Provider registries, RecipeOutput output);
  }

  public static final class Runner extends RecipeProvider.Runner {
    private static final List<RecipeProviderFactory> PROVIDERS = List.of(
      CraftingRecipes::new,
      UpgradeRecipes::new
    );

    public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
      super(packOutput, registries);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
      return new RecipeProvider(provider, recipeOutput) {
        @Override
        protected void buildRecipes() {
          for (var provider : PROVIDERS) {
            provider.create(registries, output).buildRecipes();
          }
        }
      };
    }

    @Override
    public String getName() {
      return "Appex Recipes";
    }
  }
}
