package es.degrassi.appex.me.strategy;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.me.storage.ExternalStorageFacade;
import appeng.parts.automation.HandlerStrategy;
import com.google.common.primitives.Ints;
import es.degrassi.appex.me.key.ExperienceKey;
import es.degrassi.appex.me.key.ExperienceKeyType;
import es.degrassi.experiencelib.api.ExperienceResource;
import es.degrassi.experiencelib.api.ExperienceStack;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

public class AExpHandlerStrategy {
  public static final HandlerStrategy<IExperienceHandler, ExperienceStack> EXPERIENCE = new HandlerStrategy<>(ExperienceKeyType.TYPE) {
    @Override
    public ExternalStorageFacade getFacade(IExperienceHandler handler) {
      return AExpExternalStorageFacade.of(handler);
    }

    @Override
    public @Nullable ExperienceStack getStack(AEKey what, long amount) {
      if (!(what instanceof ExperienceKey)) return null;
      return new ExperienceStack(amount);
    }

    @Override
    public long insert(IExperienceHandler handler, AEKey what, long amount, Actionable mode) {
      if (what instanceof ExperienceKey && amount > 0) {
        var insertAmount = Ints.saturatedCast(amount);
        try (var tx = Transaction.open(null)) {
          var inserted = handler.insert(ExperienceResource.of(insertAmount), insertAmount, tx);
          if (!mode.isSimulate()) {
            tx.commit();
          }
          return inserted;
        }
      }
      return 0;
    }
  };
}
