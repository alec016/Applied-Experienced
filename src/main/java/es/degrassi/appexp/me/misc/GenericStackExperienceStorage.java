package es.degrassi.appexp.me.misc;

import appeng.api.AECapabilities;
import appeng.api.behaviors.GenericInternalInventory;
import appeng.api.config.Actionable;
import es.degrassi.appexp.me.key.ExperienceKey;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

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
  public boolean canAcceptExperience(long experience) {
    return insert(1, Actionable.SIMULATE) > 0;
  }

  @Override
  public boolean canProvideExperience(long experience) {
    return extract(1, Actionable.SIMULATE) > 0;
  }

  @Override
  public long getMaxExtract() {
    return Integer.MAX_VALUE;
  }

  @Override
  public long getMaxReceive() {
    return Integer.MAX_VALUE;
  }

  @Override
  public long getExperience() {
    return extract(Integer.MAX_VALUE, Actionable.SIMULATE);
  }

  @Override
  public long getExperienceCapacity() {
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
  public void setExperience(long experience) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setCapacity(long l) {
    throw new UnsupportedOperationException();
  }

  @Override
  public long receiveExperience(long experience, boolean simulate) {
    return insert(experience, Actionable.ofSimulate(simulate));
  }

  @Override
  public long extractExperience(long experience, boolean simulate) {
    return extract(experience, Actionable.ofSimulate(simulate));
  }

  @Override
  public long extractExperienceRecipe(long experience, boolean simulate) {
    return insert(experience, Actionable.ofSimulate(simulate));
  }

  @Override
  public long receiveExperienceRecipe(long experience, boolean simulate) {
    return extract(experience, Actionable.ofSimulate(simulate));
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
