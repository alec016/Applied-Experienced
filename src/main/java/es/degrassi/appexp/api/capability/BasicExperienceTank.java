package es.degrassi.appexp.api.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

public class BasicExperienceTank implements IExperienceHandler, INBTSerializable<Tag> {
  private long experience;
  private final long capacity;
  private final @Nullable IContentsListener listener;

  public BasicExperienceTank(long capacity, @Nullable IContentsListener listener) {
    this.capacity = capacity;
    this.experience = 0L;
    this.listener = listener;
  }

  @Override
  public boolean canAcceptExperience(long experience) {
    return this.receiveExperience(experience, true) > 0;
  }

  @Override
  public boolean canProvideExperience(long experience) {
    return this.extractExperience(experience, true) > 0;
  }

  @Override
  public boolean canReceive() {
    return getMaxReceive() > 0;
  }

  @Override
  public boolean canExtract() {
    return getMaxExtract() > 0;
  }

  @Override
  public long getMaxExtract() {
    return capacity;
  }

  @Override
  public long getMaxReceive() {
    return capacity;
  }

  @Override
  public long getExperience() {
    return experience;
  }

  @Override
  public long getExperienceCapacity() {
    return capacity;
  }

  @Override
  public void setExperience(long experience) {
    this.experience = experience;
  }

  @Override
  public long receiveExperience(long toReceive, boolean simulate) {
    if (this.canReceive() && toReceive > 0) {
      long experienceReceived = Mth.clamp(this.capacity - this.experience, 0, Math.min(this.getMaxReceive(), toReceive));
      if (!simulate) {
        this.experience += experienceReceived;
        if (listener != null) listener.onContentsChanged();
      }

      return experienceReceived;
    } else {
      return 0;
    }
  }

  @Override
  public long extractExperience(long toExtract, boolean simulate) {
    if (this.canExtract() && toExtract > 0) {
      long experienceExtracted = Math.min(this.experience, Math.min(this.getMaxExtract(), toExtract));
      if (!simulate) {
        this.experience -= experienceExtracted;
        if (listener != null) listener.onContentsChanged();
      }

      return experienceExtracted;
    } else {
      return 0;
    }
  }

  @Override
  public Tag serializeNBT(HolderLookup.Provider provider) {
    return LongTag.valueOf(getExperience());
  }

  @Override
  public void deserializeNBT(HolderLookup.Provider provider, Tag nbt) {
    if (nbt instanceof LongTag longTag) {
      this.experience = longTag.getAsLong();
    } else {
      throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
    }
  }
}
