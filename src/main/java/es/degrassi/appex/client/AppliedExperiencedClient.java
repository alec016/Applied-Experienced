package es.degrassi.appex.client;

import appeng.api.client.StorageCellModels;
import appeng.client.api.AEKeyRendering;
import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.client.renderer.entity.ExperienceConverterEntityRenderer;
import es.degrassi.appex.client.renderer.item.ExperienceConverterItemModel;
import es.degrassi.appex.definition.AExpBlockEntities;
import es.degrassi.appex.definition.AExpItems;
import es.degrassi.appex.me.key.ExperienceKey;
import es.degrassi.appex.me.key.ExperienceKeyType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterItemModelsEvent;

import static es.degrassi.appex.AppliedExperienced.id;


@Mod(value = AppliedExperienced.MODID, dist = Dist.CLIENT)
public class AppliedExperiencedClient {
  public AppliedExperiencedClient(final IEventBus bus) {
    AEKeyRendering.register(ExperienceKeyType.TYPE, ExperienceKey.class, new AExpStackRenderer());
    bus.addListener(this::initializeModels);
    bus.addListener(this::initBlockEntityRenderer);
    bus.addListener(this::registerClientExtensions);
  }

  private void initializeModels(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      var prefix = "block/drive_%s_experience_cell";

      for (var i = 0; i < AExpItems.getCells().size(); i++) {
        var cell = AExpItems.getCells().get(i);
        StorageCellModels.registerModel(cell.get(), id(prefix.formatted(cell.get().getTier().namePrefix())));
        StorageCellModels.registerModel(AExpItems.getPortables().get(i).get(), id(prefix.formatted(cell.get().getTier().namePrefix())));
      }
    });
  }

  private void initBlockEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(AExpBlockEntities.EXPERIENCE_CONVERTER.get(), ExperienceConverterEntityRenderer::new);
  }

  private void registerClientExtensions(final RegisterItemModelsEvent event) {
    event.register(ExperienceConverterItemModel.Unbaked.ID, ExperienceConverterItemModel.Unbaked.MAP_CODEC);
  }
}
