package es.degrassi.appex.client.integration.xei.jei;

import appeng.api.ids.AEComponents;
import appeng.client.api.integrations.jei.IngredientConverters;
import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.definition.AExpBlocks;
import es.degrassi.appex.definition.AExpItems;
import es.degrassi.experiencelib.common.ExperienceLibRegistration;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
  private static final Identifier ID = AppliedExperienced.id("core");
  public JEIPlugin() {
    IngredientConverters.register(new ExperienceIngredientConverter());
  }

  @Override
  public Identifier getPluginUid() {
    return ID;
  }
  
  @Override
  public void registerItemSubtypes(ISubtypeRegistration subtypeRegistry) {
    subtypeRegistry.registerSubtypeInterpreter(AExpItems.PORTABLE_EXPERIENCE_CELL_1K.asItem(),
        subTypeInterpreterForIngredientList(AEComponents.STORED_ENERGY));
    subtypeRegistry.registerSubtypeInterpreter(AExpItems.PORTABLE_EXPERIENCE_CELL_4K.asItem(),
        subTypeInterpreterForIngredientList(AEComponents.STORED_ENERGY));
    subtypeRegistry.registerSubtypeInterpreter(AExpItems.PORTABLE_EXPERIENCE_CELL_16K.asItem(),
        subTypeInterpreterForIngredientList(AEComponents.STORED_ENERGY));
    subtypeRegistry.registerSubtypeInterpreter(AExpItems.PORTABLE_EXPERIENCE_CELL_64K.asItem(),
        subTypeInterpreterForIngredientList(AEComponents.STORED_ENERGY));
    subtypeRegistry.registerSubtypeInterpreter(AExpItems.PORTABLE_EXPERIENCE_CELL_256K.asItem(),
        subTypeInterpreterForIngredientList(AEComponents.STORED_ENERGY));
    subtypeRegistry.registerSubtypeInterpreter(AExpBlocks.EXPERIENCE_CONVERTER.asItem(),
        subTypeInterpreterForIngredientList(ExperienceLibRegistration.EXPERIENCE_COMPONENT.get()));
  }

  private static ISubtypeInterpreter<ItemStack> subTypeInterpreterForIngredientList(DataComponentType<?>... types) {
    return (ingredient, context) -> {
      if (context == UidContext.Recipe) {
        return null;
      }
      if (types.length == 1) {
        return ingredient.get(types[0]);
      } else {
        var result = new ArrayList<@Nullable Object>(types.length);
        for (var type : types) {
          result.add(ingredient.get(type));
        }
        return result;
      }
    };
  }
}
