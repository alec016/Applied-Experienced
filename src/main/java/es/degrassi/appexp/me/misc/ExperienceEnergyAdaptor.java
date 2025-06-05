package es.degrassi.appexp.me.misc;

import appeng.api.config.Actionable;
import appeng.api.config.PowerUnit;
import appeng.api.networking.security.IActionHost;
import appeng.blockentity.powersink.IExternalPowerSink;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public record ExperienceEnergyAdaptor(IExternalPowerSink sink, IActionHost host) implements IExperienceHandler {
  private static final double AE_PER_EXPERIENCE = 16;

  @Override
  public int getTanks() {
    return 1;
  }

  @Override
  public boolean canAcceptExperience(int tank, long experience) {
    return receiveExperience(tank, experience, true) > 0;
  }

  @Override
  public boolean canProvideExperience(int tank, long experience) {
    return false;
  }

  @Override
  public long getMaxExtract(int tank) {
    return 0;
  }

  @Override
  public long getMaxReceive(int tank) {
    return getExperienceCapacity();
  }

  @Override
  public long getExperience() {
    return getExperienceCapacity()
        - Math.round(sink.getExternalPowerDemand(PowerUnit.AE, getExperienceCapacity()) / AE_PER_EXPERIENCE);
  }

  @Override
  public long getExperienceCapacity() {
    var grid = host.getActionableNode();
    return grid != null ? (int) (grid.getGrid().getEnergyService().getMaxStoredPower() / AE_PER_EXPERIENCE) : 0;
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
  public long receiveExperience(int tank, long experience, boolean simulate) {
    sink.injectExternalPower(PowerUnit.AE, experience * AE_PER_EXPERIENCE, Actionable.ofSimulate(simulate));
    return Math.min(experience, getExperienceCapacity() - getExperience());
  }

  @Override
  public long extractExperience(int tank, long experience, boolean simulate) {
    return 0;
  }

  @Override
  public long extractExperienceRecipe(int tank, long experience, boolean simulate) {
    return 0;
  }

  @Override
  public long receiveExperienceRecipe(int tank, long experience, boolean simulate) {
    sink.injectExternalPower(PowerUnit.AE, experience * AE_PER_EXPERIENCE, Actionable.ofSimulate(simulate));
    return Math.min(experience, getExperienceCapacity() - getExperience());
  }

  @Override
  public CompoundTag serializeNBT(HolderLookup.Provider provider) {
    return new CompoundTag();
  }

  @Override
  public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {

  }
}
