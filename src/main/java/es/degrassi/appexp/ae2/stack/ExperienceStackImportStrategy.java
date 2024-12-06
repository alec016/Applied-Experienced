package es.degrassi.appexp.ae2.stack;

import appeng.api.behaviors.StackImportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import es.degrassi.appexp.ae2.ExperienceKey;
import es.degrassi.appexp.ae2.ExperienceKeyType;
import es.degrassi.appexp.api.capability.AExpCapabilities;
import es.degrassi.appexp.api.capability.IExperienceHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExperienceStackImportStrategy implements StackImportStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExperienceStackImportStrategy.class);
  private final BlockCapabilityCache<IExperienceHandler, Direction> cache;

  public ExperienceStackImportStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
    this.cache = BlockCapabilityCache.create(AExpCapabilities.EXPERIENCE.block(), level, fromPos, fromSide);
  }
  @Override
  public boolean transfer(StackTransferContext context) {
    if (!context.isKeyTypeEnabled(ExperienceKeyType.TYPE)) {
      return false;
    }

    var handler = cache.getCapability();

    if (handler == null) {
      return false;
    }

    long remainingTransferAmount = (long) context.getOperationsRemaining() * ExperienceKeyType.TYPE.getAmountPerOperation();
    long rawAmount = Math.min(remainingTransferAmount, handler.getExperience());

    var inv = context.getInternalStorage().getInventory();

    // Check how much source we can actually insert
    var amount = inv.insert(ExperienceKey.KEY, rawAmount, Actionable.SIMULATE, context.getActionSource());

    if (amount > 0) {
      handler.extractExperience((int) amount, false);
    }

    var inserted = inv.insert(ExperienceKey.KEY, amount, Actionable.MODULATE, context.getActionSource());

    if (inserted < amount) {
      var leftover = amount - inserted;
      var backFill = (int) Math.min(leftover, handler.getExperienceCapacity() - handler.getExperience());

      if (backFill > 0) {
        handler.receiveExperience(backFill, false);
      }

      if (leftover > backFill) {
        LOGGER.error("Storage import issue, voided {} source.", leftover - backFill);
      }
    }

    var opsUsed = Math.max(1, inserted / ExperienceKeyType.TYPE.getAmountPerOperation());
    context.reduceOperationsRemaining(opsUsed);

    return amount > 0;
  }
}
