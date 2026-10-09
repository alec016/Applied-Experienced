package es.degrassi.appex.me.strategy;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.me.storage.ExternalStorageFacade;
import es.degrassi.appex.me.key.ExperienceKey;
import es.degrassi.appex.me.key.ExperienceKeyType;
import es.degrassi.experiencelib.api.ExperienceResource;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nullable;
import java.util.Set;

public class AExpExternalStorageFacade {
  public static ExternalStorageFacade of(ResourceHandler<ExperienceResource> handler) {
    return new ExperienceHandlerFacade(handler);
  }

  private static abstract class ResourceHandlerFacade<R extends Resource, K extends AEKey> extends ExternalStorageFacade {
    protected final ResourceHandler<R> handler;

    public ResourceHandlerFacade(ResourceHandler<R> handler) {
      this.handler = handler;
    }

    @Override
    public int getSlots() {
      return handler.size();
    }

    @Nullable
    @Override
    public GenericStack getStackInSlot(int slot) {
      K key = toKey(handler.getResource(slot));
      return key == null ? null : new GenericStack(key, handler.getAmountAsLong(slot));
    }

    @Override
    public int insertExternal(AEKey what, int amount, Actionable mode) {
      var resource = toResource(what, amount);
      if (resource == null) {
        return 0;
      }

      try (var tx = Transaction.openRoot()) {
        var inserted = handler.insert(resource, amount, tx);
        if (!mode.isSimulate()) {
          tx.commit();
        }
        return inserted;
      }
    }

    @Override
    public int extractExternal(AEKey what, int amount, Actionable mode) {
      var resource = toResource(what, amount);
      if (resource == null) {
        return 0;
      }

      try (var tx = Transaction.openRoot()) {
        var extracted = handler.extract(resource, amount, tx);
        if (!mode.isSimulate()) {
          tx.commit();
        }
        return extracted;
      }
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
      for (int i = 0; i < handler.size(); i++) {
        // Skip resources that cannot be extracted if that filter was enabled
        var stack = handler.getResource(i);
        if (stack.isEmpty()) {
          continue;
        }

        long amount = handler.getAmountAsLong(i);

        if (extractableOnly) {
          // Try to determine whether the resource is extractable

          try (var tx = Transaction.openRoot()) {
            var extracted = handler.extract(i, stack, 1, tx);
            // Try again in case the handler only allows extracting the resource in its entirety (i.e.
            // cauldrons)
            if (extracted == 0) {
              extracted = handler.extract(i, stack, 1, tx);
            }
            if (extracted == 0) {
              continue; // Skip unextractable slots
            }
          }
        }

        out.add(toKey(stack), amount);
      }
    }

    @Override
    public boolean containsAnyFuzzy(Set<AEKey> keys) {
      for (int i = 0; i < handler.size(); i++) {
        var what = toKey(handler.getResource(i));
        if (what != null) {
          if (keys.contains(what.dropSecondary())) {
            return true;
          }
        }
      }
      return false;
    }

    @Nullable
    protected abstract K toKey(R resource);

    @Nullable
    protected abstract R toResource(AEKey key, long amount);
  }

   private static class ExperienceHandlerFacade extends ResourceHandlerFacade<ExperienceResource, ExperienceKey> {
     public ExperienceHandlerFacade(ResourceHandler<ExperienceResource> handler) {
       super(handler);
     }

     @Override
     protected @org.jspecify.annotations.Nullable ExperienceKey toKey(ExperienceResource resource) {
       return resource == null || resource.isEmpty() ? null : ExperienceKey.KEY;
     }

     @Override
     protected @org.jspecify.annotations.Nullable ExperienceResource toResource(AEKey key, long amount) {
       return (key instanceof ExperienceKey) ? ExperienceResource.of(amount) : null;
     }

     @Override
     public AEKeyType getKeyType() {
       return ExperienceKeyType.TYPE;
     }
   }
}
