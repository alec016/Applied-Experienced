package es.degrassi.appexp.ae2.stack;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import es.degrassi.appexp.ae2.ExperienceKey;
import es.degrassi.appexp.api.capability.AExpCapabilities;
import es.degrassi.appexp.api.capability.IExperienceHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExperienceStackExportStrategy implements StackExportStrategy {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExperienceStackExportStrategy.class);
  private final BlockCapabilityCache<IExperienceHandler, Direction> cache;

  public ExperienceStackExportStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
    this.cache = BlockCapabilityCache.create(AExpCapabilities.EXPERIENCE.block(), level, fromPos, fromSide);
  }

  @Override
  public long transfer(StackTransferContext context, AEKey what, long amount) {
    if (!(what instanceof ExperienceKey)) {
      return 0;
    }

    var handler = cache.getCapability();

    if (handler != null) {
      var insertable = handler.receiveExperience(amount, true);
      var extracted = (int) StorageHelper.poweredExtraction(
          context.getEnergySource(),
          context.getInternalStorage().getInventory(),
          ExperienceKey.KEY,
          insertable,
          context.getActionSource(),
          Actionable.MODULATE);

      if (extracted > 0) {
        handler.receiveExperience(extracted, false);
      }

      return extracted;
    }

    return 0;
  }

  @Override
  public long push(AEKey what, long amount, Actionable mode) {
    if (!(what instanceof ExperienceKey)) {
      return 0;
    }

    var handler = cache.getCapability();
    return handler != null ? handler.receiveExperience(amount, mode.isSimulate()) : 0;
  }
}
