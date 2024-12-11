package es.degrassi.appexp.client;

import appeng.api.client.AEKeyRendering;
import appeng.api.client.StorageCellModels;
import appeng.items.storage.BasicStorageCell;
import appeng.items.tools.powered.PortableCellItem;
import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.appexp.client.renderer.entity.ExperienceConverterEntityRenderer;
import es.degrassi.appexp.definition.AExpBlockEntities;
import es.degrassi.appexp.definition.AExpItems;
import es.degrassi.appexp.me.key.ExperienceKey;
import es.degrassi.appexp.me.key.ExperienceKeyType;
import net.minecraft.util.FastColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import static es.degrassi.appexp.AppliedExperienced.id;

@Mod(value = AppliedExperienced.MODID, dist = Dist.CLIENT)
public class AppliedExperiencedClient {
  public AppliedExperiencedClient(final IEventBus bus) {
    AEKeyRendering.register(ExperienceKeyType.TYPE, ExperienceKey.class, new AExpStackRenderer());
    bus.addListener(this::registerItemColors);
    bus.addListener(this::initializeModels);
    bus.addListener(this::initBlockEntityRenderer);
  }

  private void registerItemColors(RegisterColorHandlersEvent.Item event) {
    for (var cell : AExpItems.getCells()) {
      event.register(
          (stack, tintIndex) -> FastColor.ARGB32.opaque(BasicStorageCell.getColor(stack, tintIndex)), cell);
    }

    for (var portable : AExpItems.getPortables()) {
      event.register(
          (stack, tintIndex) -> FastColor.ARGB32.opaque(PortableCellItem.getColor(stack, tintIndex)),
          portable);
    }
  }

  private void initializeModels(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      var prefix = "block/drive/cells/";
      AExpItems.getCells().forEach(cell ->
          StorageCellModels.registerModel(cell.get(), id(prefix + cell.id().getPath()))
      );
      AExpItems.getPortables().forEach(cell ->
          StorageCellModels.registerModel(cell.get(), id(prefix + cell.id().getPath()))
      );
      StorageCellModels.registerModel(
          AExpItems.EXPERIENCE_CELL_CREATIVE, id(prefix + "creative_experience_cell"));
    });
  }

  private void initBlockEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(AExpBlockEntities.EXPERIENCE_CONVERTER.get(), ExperienceConverterEntityRenderer::new);
  }
}
