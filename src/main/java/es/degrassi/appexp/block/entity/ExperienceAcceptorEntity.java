package es.degrassi.appexp.block.entity;

import appeng.api.AECapabilities;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.PowerUnit;
import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import appeng.blockentity.powersink.IExternalPowerSink;
import es.degrassi.appexp.definition.AExpBlockEntities;
import es.degrassi.appexp.me.misc.ExperienceEnergyAdaptor;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class ExperienceAcceptorEntity extends AENetworkedBlockEntity implements IExternalPowerSink {
  private final ExperienceEnergyAdaptor adaptor = new ExperienceEnergyAdaptor(this, this);

  public ExperienceAcceptorEntity(BlockPos pos, BlockState state) {
    super(AExpBlockEntities.EXPERIENCE_ACCEPTOR.get(), pos, state);
    getMainNode().setIdlePowerUsage(0);
  }

  public static void registerCapability(final RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(
        AECapabilities.IN_WORLD_GRID_NODE_HOST, AExpBlockEntities.EXPERIENCE_ACCEPTOR.get(),
        (be, ctx) -> be
    );
    event.registerBlockEntity(
        ExperienceLibCapabilities.EXPERIENCE.block(),
        AExpBlockEntities.EXPERIENCE_ACCEPTOR.get(),
        (be, ctx) -> be.adaptor
    );
  }

  @Override
  public AECableType getCableConnectionType(Direction dir) {
    return AECableType.COVERED;
  }

  protected double getFunnelPowerDemand(double maxRequired) {
    var grid = getMainNode().getGrid();
    return grid != null ? grid.getEnergyService().getEnergyDemand(maxRequired) : 0;
  }

  protected double funnelPowerIntoStorage(double power, Actionable mode) {
    var grid = getMainNode().getGrid();
    return grid != null ? grid.getEnergyService().injectPower(power, mode) : 0;
  }

  @Override
  public double injectExternalPower(PowerUnit externalUnit, double amount, Actionable mode) {
    return PowerUnit.AE.convertTo(externalUnit, funnelPowerIntoStorage(externalUnit.convertTo(PowerUnit.AE, amount), mode));
  }

  @Override
  public double getExternalPowerDemand(PowerUnit externalUnit, double maxPowerRequired) {
    var demand = getFunnelPowerDemand(externalUnit.convertTo(PowerUnit.AE, maxPowerRequired));
    return PowerUnit.AE.convertTo(externalUnit, Math.max(0.0, demand));
  }

  @Override
  public double injectAEPower(double amt, Actionable mode) {
    return 0;
  }

  @Override
  public double getAEMaxPower() {
    return 0;
  }

  @Override
  public double getAECurrentPower() {
    return 0;
  }

  @Override
  public boolean isAEPublicPowerStorage() {
    return false;
  }

  @Override
  public AccessRestriction getPowerFlow() {
    return AccessRestriction.READ_WRITE;
  }

  @Override
  public double extractAEPower(double amt, Actionable mode, PowerMultiplier usePowerMultiplier) {
    return 0;
  }
}
