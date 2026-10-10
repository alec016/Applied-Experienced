package es.degrassi.appex.me.strategy;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.me.storage.ExternalStorageFacade;
import appeng.parts.automation.HandlerStrategy;
import es.degrassi.appex.me.key.ExperienceKey;
import es.degrassi.appex.me.key.ExperienceKeyType;
import es.degrassi.experiencelib.api.ExperienceStack;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

public class AExpHandlerStrategy {
  public static final HandlerStrategy<IExperienceHandler, ExperienceStack> EXPERIENCE = new HandlerStrategy<>(ExperienceKeyType.TYPE) {
    @Override
    public boolean isSupported(AEKey what) {
      return ExperienceKey.is(what);
    }

    @Override
    public ExternalStorageFacade getFacade(IExperienceHandler handler) {
      return AExpExternalStorageFacade.of(handler);
    }

    @Override
    public @Nullable ExperienceStack getStack(AEKey what, long amount) {
      if (!isSupported(what)) return null;
      return new ExperienceStack(amount);
    }

    @Override
    public long insert(IExperienceHandler handler, AEKey what, long amount, Actionable mode) {
      if (what instanceof ExperienceKey && amount > 0) {
        var insertAmount = amount;
        try (var tx = Transaction.open(null)) {
          var totalInserted = 0L;
          for (int i = 0; i < handler.size() && insertAmount > 0; i++) {
            try (var subtx = Transaction.open(tx)) {
              var inserted = handler.insert(i, insertAmount, subtx);
              if (inserted <= 0) continue;
              insertAmount -= inserted;
              totalInserted += inserted;
              if (!mode.isSimulate()) subtx.commit();
            }
          }
          return totalInserted;
        }
      }
      return 0;
    }
  };
}
