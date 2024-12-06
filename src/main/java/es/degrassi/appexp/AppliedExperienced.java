package es.degrassi.appexp;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.features.P2PTunnelAttunement;
import appeng.api.storage.StorageCells;
import appeng.parts.automation.StackWorldBehaviors;
import es.degrassi.appexp.ae2.ExperienceContainerItemStrategy;
import es.degrassi.appexp.ae2.ExperienceKey;
import es.degrassi.appexp.ae2.ExperienceKeyType;
import es.degrassi.appexp.ae2.GenericStackExperienceStorage;
import es.degrassi.appexp.ae2.stack.ExperienceExternalStorageStrategy;
import es.degrassi.appexp.ae2.stack.ExperienceStackExportStrategy;
import es.degrassi.appexp.ae2.stack.ExperienceStackImportStrategy;
import es.degrassi.appexp.api.capability.AExpCapabilities;
import es.degrassi.appexp.api.capability.BasicExperienceTank;
import es.degrassi.appexp.api.capability.IExperienceHandler;
import es.degrassi.appexp.data.AppliedExperiencedDataGenerators;
import es.degrassi.appexp.item.AEComponents;
import es.degrassi.appexp.item.CreativeExperienceCellHandler;
import es.degrassi.appexp.item.ExperienceCellHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AppliedExperienced.MODID)
public class AppliedExperienced {
  public static final String MODID = "appex";
  public static final Logger LOGGER = LogManager.getLogger("Applied Experienced");

  public AppliedExperienced(final IEventBus bus) {
    AEItems.initialize(bus);
    AEComponents.initialize(bus);
    AEMenus.initialize(bus);

    bus.addListener(AppliedExperiencedDataGenerators::onGatherData);

    bus.addListener(ExperienceKeyType::register);

    StorageCells.addCellHandler(ExperienceCellHandler.INSTANCE);
    StorageCells.addCellHandler(CreativeExperienceCellHandler.INSTANCE);
    bus.addListener(AEItems::initCellUpgrades);

    StackWorldBehaviors.registerImportStrategy(ExperienceKeyType.TYPE, ExperienceStackImportStrategy::new);
    StackWorldBehaviors.registerExportStrategy(ExperienceKeyType.TYPE, ExperienceStackExportStrategy::new);
    StackWorldBehaviors.registerExternalStorageStrategy(ExperienceKeyType.TYPE, ExperienceExternalStorageStrategy::new);

    ContainerItemStrategy.register(ExperienceKeyType.TYPE, ExperienceKey.class, new ExperienceContainerItemStrategy());
    GenericSlotCapacities.register(ExperienceKeyType.TYPE, ExperienceKey.MAX_EXPERIENCE);

    bus.addListener(GenericStackExperienceStorage::registerCapability);

    bus.addListener(this::addCapabilities);

    bus.addListener((FMLCommonSetupEvent event) -> {
      event.enqueueWork(this::initializeAttunement);
    });
  }

  private void addCapabilities(final RegisterCapabilitiesEvent event) {
    event.registerItem(AExpCapabilities.EXPERIENCE.item(), (x, y) -> {
      if (x.getItem() instanceof ExperienceBottleItem) {
        IExperienceHandler handler = new BasicExperienceTank(7, null);
        handler.setExperience(7);
        return handler;
      } else if (x.getItem() instanceof BottleItem) {
        return new BasicExperienceTank(7, null);
      }
      return null;
    }, Items.EXPERIENCE_BOTTLE, Items.GLASS_BOTTLE);
  }

  private void initializeAttunement() {
    P2PTunnelAttunement.registerAttunementTag(AEItems.EXPERIENCE_P2P_TUNNEL.get());
  }

  public static ResourceLocation id(String path) {
    return ResourceLocation.fromNamespaceAndPath(MODID, path);
  }
}
