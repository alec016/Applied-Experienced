package es.degrassi.appexp;

import appeng.api.client.AEKeyRendering;
import appeng.api.client.StorageCellModels;
import appeng.items.storage.BasicStorageCell;
import appeng.items.tools.powered.PortableCellItem;
import es.degrassi.appexp.ae2.AEExperienceStackRenderer;
import es.degrassi.appexp.ae2.ExperienceKey;
import es.degrassi.appexp.ae2.ExperienceKeyType;
import net.minecraft.util.FastColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import static es.degrassi.appexp.AppliedExperienced.id;

@Mod(value = AppliedExperienced.MODID, dist = Dist.CLIENT)
public class AppliedExperiencedClient {
  public AppliedExperiencedClient(final IEventBus bus) {
    AEKeyRendering.register(ExperienceKeyType.TYPE, ExperienceKey.class, new AEExperienceStackRenderer());
    bus.addListener(this::registerItemColors);
    bus.addListener(this::initializeModels);
  }

  private void registerItemColors(RegisterColorHandlersEvent.Item event) {
    for (var cell : AEItems.getCells()) {
      event.register(
          (stack, tintIndex) -> FastColor.ARGB32.opaque(BasicStorageCell.getColor(stack, tintIndex)), cell);
    }

    for (var portable : AEItems.getPortables()) {
      event.register(
          (stack, tintIndex) -> FastColor.ARGB32.opaque(PortableCellItem.getColor(stack, tintIndex)),
          portable);
    }
  }

  private void initializeModels(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      var prefix = "block/drive/cells/";
      AEItems.getCells().forEach(cell ->
          StorageCellModels.registerModel(cell.get(), id(prefix + cell.id().getPath()))
      );
      AEItems.getPortables().forEach(cell ->
          StorageCellModels.registerModel(cell.get(), id(prefix + cell.id().getPath()))
      );
      StorageCellModels.registerModel(
          AEItems.EXPERIENCE_CELL_CREATIVE, id(prefix + "creative_experience_cell"));
    });
  }
}
