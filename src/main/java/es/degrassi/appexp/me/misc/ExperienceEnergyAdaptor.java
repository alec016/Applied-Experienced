package es.degrassi.appexp.me.misc;

import appeng.api.config.Actionable;
import appeng.api.config.PowerUnit;
import appeng.api.networking.security.IActionHost;
import appeng.blockentity.powersink.IExternalPowerSink;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;

public record ExperienceEnergyAdaptor(IExternalPowerSink sink, IActionHost host) implements IExperienceHandler {
  private static final double AE_PER_EXPERIENCE = 16;

  @Override
  public boolean canAcceptExperience(long experience) {
    return receiveExperience(experience, true) > 0;
  }

  @Override
  public boolean canProvideExperience(long experience) {
    return false;
  }

  @Override
  public long getMaxExtract() {
    return 0;
  }

  @Override
  public long getMaxReceive() {
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
  public void setExperience(long experience) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setCapacity(long l) {
    throw new UnsupportedOperationException();
  }

  @Override
  public long receiveExperience(long experience, boolean simulate) {
    sink.injectExternalPower(PowerUnit.AE, experience * AE_PER_EXPERIENCE, Actionable.ofSimulate(simulate));
    return Math.min(experience, getExperienceCapacity() - getExperience());
  }

  @Override
  public long extractExperience(long experience, boolean simulate) {
    return 0;
  }

  @Override
  public long extractExperienceRecipe(long experience, boolean simulate) {
    return 0;
  }

  @Override
  public long receiveExperienceRecipe(long experience, boolean simulate) {
    sink.injectExternalPower(PowerUnit.AE, experience * AE_PER_EXPERIENCE, Actionable.ofSimulate(simulate));
    return Math.min(experience, getExperienceCapacity() - getExperience());
  }
}
