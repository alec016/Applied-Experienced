package es.degrassi.appexp.me.strategy;

import appeng.api.behaviors.StackImportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.storage.StorageHelper;
import es.degrassi.appexp.me.key.AEExperienceKey;
import es.degrassi.appexp.me.key.ExperienceKeyType;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
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
    this.cache = BlockCapabilityCache.create(ExperienceLibCapabilities.EXPERIENCE.block(), level, fromPos, fromSide);
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
    long amount = inv.insert(AEExperienceKey.KEY, rawAmount, Actionable.SIMULATE, context.getActionSource());
    long extractable = 0;
    if (amount > 0) {
      for (int i = 0; i < handler.getTanks(); i++) {
        if (amount <= 0) break;
        var extract = handler.extractExperience(i, amount, true);
        extractable += extract;
        amount -= extract;
      }
    }
    var inserted = StorageHelper.poweredInsert(
        context.getEnergySource(),
        inv,
        AEExperienceKey.KEY,
        extractable,
        context.getActionSource(),
        Actionable.MODULATE);

    if (inserted > 0) {
      for (int i = 0; i < handler.getTanks(); i++) {
        if (inserted <= 0) break;
        inserted -= handler.extractExperience(i, inserted, false);
      }
    }

    var opsUsed = Math.max(1, extractable / ExperienceKeyType.TYPE.getAmountPerOperation());
    context.reduceOperationsRemaining(opsUsed);

    return amount > 0;
  }
}
