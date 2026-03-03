package es.degrassi.appexp.api.integration.jei;

import appeng.api.stacks.GenericStack;
import es.degrassi.appexp.me.key.AEExperienceKey;
import es.degrassi.appexp.me.key.ExperienceKeyType;
import es.degrassi.experiencelib.api.xei.ExperienceStack;
import es.degrassi.experiencelib.api.xei.jei.IngredientTypes;
import lombok.Getter;
import mezz.jei.api.ingredients.IIngredientType;
import org.jetbrains.annotations.Nullable;
import tamaized.ae2jeiintegration.api.integrations.jei.IngredientConverter;

@Getter
public class ExperienceIngredientConverter implements IngredientConverter<ExperienceStack> {
  private final IIngredientType<ExperienceStack> ingredientType;

  public ExperienceIngredientConverter() {
    this.ingredientType = IngredientTypes.EXPERIENCE;
  }

  @Override
  public @Nullable ExperienceStack getIngredientFromStack(GenericStack stack) {
    if (stack.what().getType().equals(ExperienceKeyType.TYPE)) {
      return new ExperienceStack(Math.max(1L, stack.amount()));
    }
    return null;
  }

  @Override
  public @Nullable GenericStack getStackFromIngredient(ExperienceStack stack) {
    if (stack.getKey().equals(ExperienceStack.EMPTY.getKey())) {
      return new GenericStack(AEExperienceKey.KEY, (long) stack.getAmount());
    }
    return null;
  }
}