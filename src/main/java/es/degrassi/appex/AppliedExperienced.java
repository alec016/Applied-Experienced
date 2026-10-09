package es.degrassi.appex;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.features.P2PTunnelAttunement;
import appeng.api.parts.RegisterPartCapabilitiesEvent;
import appeng.api.storage.StorageCells;
import appeng.parts.automation.ForgeExternalStorageStrategy;
import appeng.parts.automation.StackWorldBehaviors;
import appeng.parts.automation.StorageExportStrategy;
import appeng.parts.automation.StorageImportStrategy;
import com.mojang.logging.LogUtils;
import es.degrassi.appex.block.entity.ExperienceAcceptorEntity;
import es.degrassi.appex.block.entity.ExperienceConverterEntity;
import es.degrassi.appex.definition.AExpBlockEntities;
import es.degrassi.appex.definition.AExpBlocks;
import es.degrassi.appex.definition.AExpComponents;
import es.degrassi.appex.definition.AExpConfig;
import es.degrassi.appex.definition.AExpItems;
import es.degrassi.appex.definition.AExpMenus;
import es.degrassi.appex.definition.AExpParts;
import es.degrassi.appex.definition.AExpText;
import es.degrassi.appex.me.cell.ExperienceCellHandler;
import es.degrassi.appex.me.key.ExperienceKey;
import es.degrassi.appex.me.key.ExperienceKeyType;
import es.degrassi.appex.me.misc.GenericStackExperienceStorage;
import es.degrassi.appex.me.strategy.AExpHandlerStrategy;
import es.degrassi.appex.me.strategy.ExperienceContainerItemStrategy;
import es.degrassi.appex.part.ExperienceAcceptorPart;
import es.degrassi.appex.part.ExperienceP2PTunnelPart;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.slf4j.Logger;

import java.util.Locale;

@Mod(AppliedExperienced.MODID)
public class AppliedExperienced {
  public static final String MODID = "appex";
  public static final Logger LOGGER = LogUtils.getLogger();

  public AppliedExperienced(IEventBus bus, ModContainer CONTAINER) {
    CONTAINER.registerConfig(ModConfig.Type.COMMON, AExpConfig.getSpec());

    AExpParts.init();
    AExpBlocks.DR.register(bus);
    AExpItems.initialize(bus);
    AExpBlockEntities.DR.register(bus);
    AExpComponents.initialize(bus);
    AExpMenus.initialize(bus);

    bus.addListener(ExperienceKeyType::register);

    StorageCells.addCellHandler(ExperienceCellHandler.INSTANCE);
    bus.addListener(AExpItems::initCellUpgrades);

    StackWorldBehaviors.registerImportStrategy(ExperienceKeyType.TYPE,
        (level, fromPos, fromSide) ->
            new StorageImportStrategy<>(ExperienceLibCapabilities.EXPERIENCE.block(), AExpHandlerStrategy.EXPERIENCE, level, fromPos, fromSide));
    StackWorldBehaviors.registerExportStrategy(ExperienceKeyType.TYPE,
        (level, fromPos, fromSide) ->
            new StorageExportStrategy<>(ExperienceLibCapabilities.EXPERIENCE.block(), AExpHandlerStrategy.EXPERIENCE, level, fromPos, fromSide));
    StackWorldBehaviors.registerExternalStorageStrategy(ExperienceKeyType.TYPE,
        (level, fromPos, fromSide) ->
            new ForgeExternalStorageStrategy<>(ExperienceLibCapabilities.EXPERIENCE.block(), AExpHandlerStrategy.EXPERIENCE, level, fromPos, fromSide));

    ContainerItemStrategy.register(ExperienceKeyType.TYPE, ExperienceKey.class, new ExperienceContainerItemStrategy());
    GenericSlotCapacities.register(ExperienceKeyType.TYPE, ExperienceKey.MAX_EXPERIENCE);

    bus.addListener(GenericStackExperienceStorage::registerCapability);
    bus.addListener(ExperienceConverterEntity::registerCapability);
    bus.addListener(ExperienceAcceptorEntity::registerCapability);
    bus.addListener(ExperienceAcceptorPart::registerCapability);
    bus.addListener((RegisterPartCapabilitiesEvent event) -> {
      event.register(ExperienceLibCapabilities.EXPERIENCE.block(), (part, context) -> part.getExposedApi(), ExperienceP2PTunnelPart.class);
    });

    bus.addListener((FMLCommonSetupEvent event) -> {
      event.enqueueWork(this::initializeAttunement);
    });
  }

  private void initializeAttunement() {
    P2PTunnelAttunement.registerAttunementTag(AExpParts.EXPERIENCE_P2P_TUNNEL);
    P2PTunnelAttunement.registerItemAccessAttunementApi(
        AExpParts.EXPERIENCE_P2P_TUNNEL,
        ExperienceLibCapabilities.EXPERIENCE.item(),
        AExpText.P2PAttunementExperience.text()
    );
  }

  public static Identifier id(String id) {
    return Identifier.fromNamespaceAndPath(MODID, id.toLowerCase(Locale.ENGLISH));
  }
}
