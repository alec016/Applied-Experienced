package es.degrassi.appex.me.misc;

import appeng.api.AECapabilities;
import appeng.api.behaviors.GenericInternalInventory;
import appeng.api.config.Actionable;
import es.degrassi.appex.me.key.ExperienceKey;
import es.degrassi.experiencelib.api.ExperienceResource;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public record GenericStackExperienceStorage(GenericInternalInventory inv) implements IExperienceHandler {
  public static void registerCapability(RegisterCapabilitiesEvent event) {
    for (var block : BuiltInRegistries.BLOCK) {
      if (event.isBlockRegistered(AECapabilities.GENERIC_INTERNAL_INV, block)) {
        event.registerBlock(
            ExperienceLibCapabilities.EXPERIENCE.block(),
            (level, pos, state, be, context) -> {
              var genericInv = level.getCapability(AECapabilities.GENERIC_INTERNAL_INV, pos, state, be, context);
              return genericInv != null ? new GenericStackExperienceStorage(genericInv) : null;
            },
            block
        );
      }
    }
  }

  @Override
  public int size() {
    return 1;
  }
  @Override
  public long getMaxExtract(int tank) {
    return Integer.MAX_VALUE;
  }

  @Override
  public long getMaxInsert(int tank) {
    return Integer.MAX_VALUE;
  }

  @Override
  public long getAmountAsLong(int index) {
    return extract(Long.MAX_VALUE, Actionable.SIMULATE);
  }

  @Override
  public long getCapacityAsLong(int tank, ExperienceResource resource) {
    var slots = 0;

    for (var i = 0; i < inv.size(); i++) {
      var key = inv.getKey(i);

      if (key == null || key == ExperienceKey.KEY) {
        slots += 1;
      }
    }

    return slots * inv.getMaxAmount(ExperienceKey.KEY);
  }

  @Override
  public void setExperience(int tank, long experience) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setCapacity(int tank, long l) {
    throw new UnsupportedOperationException();
  }

  @Override
  public long insert(int tank, long experience, TransactionContext context) {
    try (Transaction sub = Transaction.open(context)) {
      var simulated = insert(experience, Actionable.SIMULATE);
      if (simulated < experience) return 0;
      insert(simulated, Actionable.MODULATE);
      sub.commit();
      return simulated;
    }
  }

  @Override
  public long extract(int tank, long experience, TransactionContext context) {
    try (Transaction sub = Transaction.open(context)) {
      var simulated = extract(experience, Actionable.SIMULATE);
      if (simulated < experience) return 0;
      extract(simulated, Actionable.MODULATE);
      sub.commit();
      return simulated;
    }
  }

  @Override
  public long extractRecipe(int tank, long experience, TransactionContext context) {
    try (Transaction sub = Transaction.open(context)) {
      var simulated = extract(experience, Actionable.SIMULATE);
      if (simulated < experience) return 0;
      extract(simulated, Actionable.MODULATE);
      sub.commit();
      return simulated;
    }
  }

  @Override
  public long insertRecipe(int tank, long experience, TransactionContext context) {
    try (Transaction sub = Transaction.open(context)) {
      var simulated = insert(experience, Actionable.SIMULATE);
      if (simulated < experience) return 0;
      insert(simulated, Actionable.MODULATE);
      sub.commit();
      return simulated;
    }
  }

  private long insert(long amount, Actionable mode) {
    var inserted = 0L;

    for (var i = 0; i < inv.size() && inserted < amount; ++i) {
      inserted += inv.insert(i, ExperienceKey.KEY, amount - inserted, mode);
    }

    return inserted;
  }

  private long extract(long amount, Actionable mode) {
    var extracted = 0L;

    for (var i = 0; i < inv.size() && extracted < amount; ++i) {
      extracted += inv.extract(i, ExperienceKey.KEY, amount - extracted, mode);
    }

    return extracted;
  }
}
