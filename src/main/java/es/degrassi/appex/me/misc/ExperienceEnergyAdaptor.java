package es.degrassi.appex.me.misc;

import appeng.api.config.Actionable;
import appeng.api.config.PowerUnit;
import appeng.api.networking.security.IActionHost;
import appeng.blockentity.powersink.IExternalPowerSink;
import es.degrassi.experiencelib.api.ExperienceResource;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public record ExperienceEnergyAdaptor(IExternalPowerSink sink, IActionHost host) implements IExperienceHandler {
  private static final double AE_PER_EXPERIENCE = 32;
  @Override
  public long getMaxExtract(int tank) {
    return 0;
  }

  @Override
  public long getMaxInsert(int tank) {
    return Long.MAX_VALUE;
  }

  @Override
  public void setExperience(int tank, long amount) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setCapacity(int tank, long amount) {
    throw new UnsupportedOperationException();
  }

  @Override
  public long insert(int tank, long amount, TransactionContext context) {
    var sim = sink.injectExternalPower(PowerUnit.AE, amount * AE_PER_EXPERIENCE, Actionable.SIMULATE);
    var real = sink.injectExternalPower(PowerUnit.AE, sim, Actionable.MODULATE);
    return (long) real;
  }

  @Override
  public long extract(int tank, long amount, TransactionContext context) {
    return 0;
  }

  @Override
  public long insertRecipe(int tank, long amount, TransactionContext context) {
    var sim = sink.injectExternalPower(PowerUnit.AE, amount * AE_PER_EXPERIENCE, Actionable.SIMULATE);
    var real = sink.injectExternalPower(PowerUnit.AE, sim, Actionable.MODULATE);
    return (long) real;
  }

  @Override
  public long extractRecipe(int tank, long amount, TransactionContext context) {
    return 0;
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public long getAmountAsLong(int index) {
    return getCapacityAsLong(index, ExperienceResource.EMPTY)
        - Math.round(sink.getExternalPowerDemand(PowerUnit.AE, getCapacityAsLong(index, ExperienceResource.EMPTY)) / AE_PER_EXPERIENCE);
  }

  @Override
  public long getCapacityAsLong(int index, ExperienceResource resource) {
    var grid = host.getActionableNode();
    return grid != null ? (int) (grid.getGrid().getEnergyService().getMaxStoredPower() / AE_PER_EXPERIENCE) : 0;
  }
}
