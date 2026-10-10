package es.degrassi.appex.me.strategy;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.parts.automation.ForgeExternalStorageStrategy;
import appeng.parts.automation.StorageExportStrategy;
import appeng.parts.automation.StorageImportStrategy;
import es.degrassi.experiencelib.api.ExperienceStack;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class StorageIOStrategies {
  public static StorageImportStrategy<IExperienceHandler, ExperienceStack> createImport(ServerLevel level, BlockPos fromPos, Direction fromSide) {
    return new StorageImportStrategy<>(
        ExperienceLibCapabilities.EXPERIENCE.block(),
        AExpHandlerStrategy.EXPERIENCE,
        level,
        fromPos,
        fromSide
    );
  }

  public static StorageExportStrategy<IExperienceHandler, ExperienceStack> createExport(
      ServerLevel level,
      BlockPos fromPos,
      Direction fromSide
  ) {
    return new StorageExportStrategy<>(
        ExperienceLibCapabilities.EXPERIENCE.block(),
        AExpHandlerStrategy.EXPERIENCE,
        level,
        fromPos,
        fromSide
    );
  }

  public static ForgeExternalStorageStrategy<IExperienceHandler, ExperienceStack> createExternalStorage(
      ServerLevel level,
      BlockPos fromPos,
      Direction fromSide
  ) {
    return new ForgeExternalStorageStrategy<>(
        ExperienceLibCapabilities.EXPERIENCE.block(),
        AExpHandlerStrategy.EXPERIENCE,
        level,
        fromPos,
        fromSide
    );
  }
}
