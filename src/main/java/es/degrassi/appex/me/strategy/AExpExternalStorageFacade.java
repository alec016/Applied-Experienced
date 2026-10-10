package es.degrassi.appex.me.strategy;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.me.storage.ExternalStorageFacade;
import com.google.common.primitives.Ints;
import es.degrassi.appex.me.key.ExperienceKey;
import es.degrassi.appex.me.key.ExperienceKeyType;
import es.degrassi.experiencelib.api.ExperienceResource;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.Set;

public class AExpExternalStorageFacade {
  public static ExternalStorageFacade of(IExperienceHandler handler) {
    return new ExperienceHandlerFacade(handler);
  }

  private static class ExperienceHandlerFacade extends ExternalStorageFacade {
    protected final IExperienceHandler handler;
     public ExperienceHandlerFacade(IExperienceHandler handler) {
       this.handler = handler;
     }

     protected @org.jspecify.annotations.Nullable ExperienceResource toResource(AEKey key, long amount) {
       return (key instanceof ExperienceKey && amount > 0) ? ExperienceResource.of(amount) : null;
     }

     @Override
     public int getSlots() {
       return handler.size();
     }

     @Override
     public @org.jspecify.annotations.Nullable GenericStack getStackInSlot(int slot) {
       var amount = handler.getAmountAsLong(slot);
       return amount == 0 ? null : new GenericStack(ExperienceKey.KEY, amount);
     }

     @Override
     public AEKeyType getKeyType() {
       return ExperienceKeyType.TYPE;
     }

     @Override
     protected int insertExternal(AEKey what, int amount, Actionable mode) {
       var resource = toResource(what, amount);
       if (resource == null) {
         return 0;
       }

       try (var tx = Transaction.openRoot()) {
         var total = 0;
         for (int i = 0; i < handler.size() && amount > 0; i++) {
           try (var subtx = Transaction.open(tx)) {
             var inserted = Ints.saturatedCast(handler.insert(i, amount, subtx));
             if (!mode.isSimulate()) subtx.commit();
             total += inserted;
             amount -= inserted;
           }
         }
         if (!mode.isSimulate()) tx.commit();
         return total;
       }
     }

     @Override
     protected int extractExternal(AEKey what, int amount, Actionable mode) {
       var resource = toResource(what, amount);
       if (resource == null) {
         return 0;
       }

       try (var tx = Transaction.openRoot()) {
         var total = 0;
         for (int i = 0; i < handler.size() && amount > 0; i++) {
           try (var subtx = Transaction.open(tx)) {
             var extracted = Ints.saturatedCast(handler.extract(i, amount, subtx));
             if (!mode.isSimulate()) subtx.commit();
             total += extracted;
             amount -= extracted;
           }
         }
         if (!mode.isSimulate()) tx.commit();
         return total;
       }
     }

     @Override
     public void getAvailableStacks(KeyCounter out) {
       for (int i = 0; i < handler.size(); i++) {
         // Skip resources that cannot be extracted if that filter was enabled
         var stack = handler.getAmountAsLong(i);
         if (stack == 0) {
           continue;
         }

         long amount = handler.getAmountAsLong(i);

         if (extractableOnly) {
           // Try to determine whether the resource is extractable

           try (var tx = Transaction.openRoot()) {
             var extracted = handler.extract(i, 1, tx);
             // Try again in case the handler only allows extracting the resource in its entirety (i.e.
             // cauldrons)
             if (extracted == 0) {
               extracted = handler.extract(i, 1, tx);
             }
             if (extracted == 0) {
               continue; // Skip unextractable slots
             }
           }
         }

         out.add(ExperienceKey.KEY, amount);
       }
     }

     @Override
     public boolean containsAnyFuzzy(Set<AEKey> keys) {
       for (int i = 0; i < handler.size(); i++) {
         var what = handler.getAmountAsLong(i) > 0 ? ExperienceKey.KEY : null;
         if (what != null) {
           if (keys.contains(what.dropSecondary())) {
             return true;
           }
         }
       }
       return false;
     }
   }
}
