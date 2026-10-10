package es.degrassi.appex.client.integration.xei.jei;

import appeng.api.stacks.GenericStack;
import appeng.client.api.integrations.jei.IngredientConverter;
import es.degrassi.appex.me.key.ExperienceKey;
import es.degrassi.experiencelib.api.ExperienceStack;
import es.degrassi.experiencelib.api.xei.jei.IngredientTypes;
import mezz.jei.api.ingredients.IIngredientType;
import org.jetbrains.annotations.Nullable;

public class ExperienceIngredientConverter implements IngredientConverter<ExperienceStack> {
  @Override
  public IIngredientType<ExperienceStack> getIngredientType() {
    return IngredientTypes.EXPERIENCE;
  }

  @Override
  public @Nullable ExperienceStack getIngredientFromStack(GenericStack stack) {
    if (stack.what() instanceof ExperienceKey) {
      return new ExperienceStack(Math.max(1, stack.amount()));
    }
    return null;
  }

  @Override
  public @Nullable GenericStack getStackFromIngredient(ExperienceStack ingredient) {
    return new GenericStack(ExperienceKey.KEY, (long) ingredient.getAmount());
  }
}
