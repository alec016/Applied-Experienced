package es.degrassi.appexp.me.strategy;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import es.degrassi.appexp.me.key.ExperienceKey;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
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
    this.cache = BlockCapabilityCache.create(ExperienceLibCapabilities.EXPERIENCE.block(), level, fromPos, fromSide);
  }

  @Override
  public long transfer(StackTransferContext context, AEKey what, long amount) {
    if (!(what instanceof ExperienceKey)) {
      return 0;
    }

    var handler = cache.getCapability();

    if (handler == null) return 0;

    var inv = context.getInternalStorage().getInventory();
    long rawAmount = Math.min(amount, handler.getExperienceCapacity() - handler.getExperience());

    long insertable = 0;
    for (int i = 0; i < handler.getTanks(); i++) {
      if (rawAmount <= 0) break;
      var insert = handler.receiveExperience(i, rawAmount, true);
      insertable += insert;
      rawAmount -= insert;
    }
    var extracted = StorageHelper.poweredExtraction(
        context.getEnergySource(),
        inv,
        ExperienceKey.KEY,
        insertable,
        context.getActionSource(),
        Actionable.MODULATE);

    if (extracted > 0) {
      for (int i = 0; i < handler.getTanks(); i++) {
        if (extracted <= 0) break;
        extracted -= handler.receiveExperience(i, extracted, false);
      }

    }

    return extracted;
  }

  @Override
  public long push(AEKey what, long amount, Actionable mode) {
    if (!(what instanceof ExperienceKey)) {
      return 0;
    }

    var handler = cache.getCapability();
    if (handler != null) {
      long inserted = 0;
      for (int i = 0; i < handler.getTanks(); i++) {
        if (amount <= 0) break;
        long ins = handler.receiveExperience(i, amount, mode.isSimulate());
        inserted += ins;
        amount -= ins;
      }

      return inserted;
    }
    return 0;
  }
}
