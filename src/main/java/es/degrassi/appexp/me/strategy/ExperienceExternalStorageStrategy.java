package es.degrassi.appexp.me.strategy;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.core.localization.GuiText;
import es.degrassi.appexp.me.key.ExperienceKey;
import es.degrassi.appexp.me.key.ExperienceKeyType;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import org.jetbrains.annotations.Nullable;

public class ExperienceExternalStorageStrategy implements ExternalStorageStrategy {
  private final BlockCapabilityCache<IExperienceHandler, Direction> cache;
  public ExperienceExternalStorageStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
    this.cache = BlockCapabilityCache.create(ExperienceLibCapabilities.EXPERIENCE.block(), level, fromPos, fromSide);
  }
  @Override
  public @Nullable MEStorage createWrapper(boolean extractableOnly, Runnable injectOrExtractCallback) {
    var handler = cache.getCapability();
    return handler != null ? new Adaptor(handler, injectOrExtractCallback) : null;
  }

  private record Adaptor(IExperienceHandler handler, Runnable injectOrExtractCallback) implements MEStorage {
    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
      if (!(what instanceof ExperienceKey)) {
        return 0;
      }

      var inserted = handler.receiveExperience(amount, mode.isSimulate());

      if (inserted > 0 && mode == Actionable.MODULATE) {
        injectOrExtractCallback.run();
      }

      return inserted;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
      if (!(what instanceof ExperienceKey)) {
        return 0;
      }

      var extracted = handler.extractExperience(amount, mode.isSimulate());

      if (extracted > 0 && mode == Actionable.MODULATE) {
        injectOrExtractCallback.run();
      }

      return extracted;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
      var currentExperience = handler.getExperience();

      if (currentExperience != 0) {
        out.add(ExperienceKey.KEY, currentExperience);
      }
    }

    @Override
    public Component getDescription() {
      return GuiText.ExternalStorage.text(ExperienceKeyType.TYPE.getDescription());
    }
  }
}
