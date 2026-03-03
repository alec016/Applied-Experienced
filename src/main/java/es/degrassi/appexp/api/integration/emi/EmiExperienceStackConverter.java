package es.degrassi.appexp.api.integration.emi;

import appeng.api.integrations.emi.EmiStackConverter;
import appeng.api.stacks.GenericStack;
import dev.emi.emi.api.stack.EmiStack;
import es.degrassi.appexp.me.key.AEExperienceKey;
import es.degrassi.experiencelib.api.xei.ExperienceKey;
import es.degrassi.experiencelib.api.xei.IExperienceLibAccess;
import es.degrassi.experiencelib.api.xei.emi.ExperienceEmiStack;
import org.jetbrains.annotations.Nullable;

import static es.degrassi.appexp.me.key.AEExperienceKey.KEY;

public final class EmiExperienceStackConverter implements EmiStackConverter {
  @Override
  public Class<?> getKeyType() {
    return ExperienceKey.class;
  }

  @Override
  public @Nullable EmiStack toEmiStack(GenericStack stack) {
    if (stack.what() instanceof AEExperienceKey key) {
      return IExperienceLibAccess.INSTANCE.emiHelper().createEmiStack(key.getStack());
    }
    return null;
  }

  @Override
  public @Nullable GenericStack toGenericStack(EmiStack stack) {
    var key = stack.getKeyOfType(ExperienceKey.class);
    if (key != null) {
      return new GenericStack(KEY, stack.getAmount());
    }
    return null;
  }
}
