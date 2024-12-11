package es.degrassi.appexp;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.features.P2PTunnelAttunement;
import appeng.api.storage.StorageCells;
import appeng.parts.automation.StackWorldBehaviors;
import es.degrassi.appexp.block.entity.ExperienceAcceptorEntity;
import es.degrassi.appexp.block.entity.ExperienceConverterEntity;
import es.degrassi.appexp.definition.AExpBlockEntities;
import es.degrassi.appexp.definition.AExpBlocks;
import es.degrassi.appexp.definition.AExpConfig;
import es.degrassi.appexp.definition.AExpItems;
import es.degrassi.appexp.definition.AExpMenus;
import es.degrassi.appexp.me.strategy.ExperienceContainerItemStrategy;
import es.degrassi.appexp.me.key.ExperienceKey;
import es.degrassi.appexp.me.key.ExperienceKeyType;
import es.degrassi.appexp.me.misc.GenericStackExperienceStorage;
import es.degrassi.appexp.me.strategy.ExperienceExternalStorageStrategy;
import es.degrassi.appexp.me.strategy.ExperienceStackExportStrategy;
import es.degrassi.appexp.me.strategy.ExperienceStackImportStrategy;
import es.degrassi.appexp.data.AppliedExperiencedDataGenerators;
import es.degrassi.appexp.definition.AExpComponents;
import es.degrassi.appexp.me.cell.CreativeExperienceCellHandler;
import es.degrassi.appexp.me.cell.ExperienceCellHandler;
import es.degrassi.appexp.part.ExperienceAcceptorPart;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AppliedExperienced.MODID)
public class AppliedExperienced {
  public static final String MODID = "appex";
  public static final Logger LOGGER = LogManager.getLogger("Applied Experienced");

  public AppliedExperienced(final ModContainer CONTAINER, final IEventBus bus) {
    CONTAINER.registerConfig(ModConfig.Type.COMMON, AExpConfig.getSpec());

    AExpItems.initialize(bus);
    AExpBlockEntities.DR.register(bus);
    AExpBlocks.DR.register(bus);
    AExpComponents.initialize(bus);
    AExpMenus.initialize(bus);

    bus.addListener(AppliedExperiencedDataGenerators::onGatherData);

    bus.addListener(ExperienceKeyType::register);

    StorageCells.addCellHandler(ExperienceCellHandler.INSTANCE);
    StorageCells.addCellHandler(CreativeExperienceCellHandler.INSTANCE);
    bus.addListener(AExpItems::initCellUpgrades);

    StackWorldBehaviors.registerImportStrategy(ExperienceKeyType.TYPE, ExperienceStackImportStrategy::new);
    StackWorldBehaviors.registerExportStrategy(ExperienceKeyType.TYPE, ExperienceStackExportStrategy::new);
    StackWorldBehaviors.registerExternalStorageStrategy(ExperienceKeyType.TYPE, ExperienceExternalStorageStrategy::new);

    ContainerItemStrategy.register(ExperienceKeyType.TYPE, ExperienceKey.class, new ExperienceContainerItemStrategy());
    GenericSlotCapacities.register(ExperienceKeyType.TYPE, ExperienceKey.MAX_EXPERIENCE);

    bus.addListener(GenericStackExperienceStorage::registerCapability);
    bus.addListener(ExperienceConverterEntity::registerCapability);
    bus.addListener(ExperienceAcceptorEntity::registerCapability);
    bus.addListener(ExperienceAcceptorPart::registerCapability);

    bus.addListener((FMLCommonSetupEvent event) -> {
      event.enqueueWork(this::initializeAttunement);
    });
  }

  private void initializeAttunement() {
//    P2PTunnelAttunement.registerAttunementTag(AExpItems.EXPERIENCE_P2P_TUNNEL.get());
  }

  public static ResourceLocation id(String path) {
    return ResourceLocation.fromNamespaceAndPath(MODID, path);
  }
}
