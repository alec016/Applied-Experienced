package es.degrassi.appexp;

import appeng.api.parts.PartModels;
import appeng.api.upgrades.Upgrades;
import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.ItemDefinition;
import appeng.core.localization.GuiText;
import appeng.items.AEBaseItem;
import appeng.items.materials.MaterialItem;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import appeng.items.storage.StorageTier;
import es.degrassi.appexp.ae2.ExperienceP2PTunnelPart;
import es.degrassi.appexp.item.CreativeExperienceCellItem;
import es.degrassi.appexp.item.ExperiencePortableCellItem;
import es.degrassi.appexp.item.ExperienceStorageCell;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class AEItems {
  private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
      AppliedExperienced.MODID);
  private static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();
  private static final DeferredRegister.Items DR = DeferredRegister.createItems(AppliedExperienced.MODID);

  public static List<ItemDefinition<?>> getItems() {
    return Collections.unmodifiableList(ITEMS);
  }

  public static void initialize(IEventBus bus) {
    TABS.register(bus);
    DR.register(bus);
  }

  private static Item basic(Item.Properties p) {
    return new MaterialItem(p);
  }

  private static Item.Properties properties() {
    return new Item.Properties();
  }

  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = TABS.register("main", () -> CreativeModeTab
      .builder()
      .title(AEText.CREATIVE_TAB.formatted())
      .icon(() -> new ItemStack(AEItems.EXPERIENCE_CELL_64K.get()))
      .displayItems((params, output) -> {
        for (var entry : DR.getEntries()) {
          var item = entry.get();

          if (item instanceof AEBaseBlockItem baseItem && baseItem.getBlock() instanceof AEBaseBlock baseBlock) {
            baseBlock.addToMainCreativeTab(params, output);
          } else if (item instanceof AEBaseItem baseItem) {
            baseItem.addToMainCreativeTab(params, output);
          } else {
            output.accept(item);
          }
        }
      })
      .build()
  );

  public static final ItemDefinition<Item> EXPERIENCE_CELL_HOUSING = item(
      "ME Experience Cell Housing",
      "experience_cell_housing",
      AEItems::basic
  );

  public static final ItemDefinition<CreativeExperienceCellItem> EXPERIENCE_CELL_CREATIVE = item(
      "Creative ME Experience Storage Cell",
      "creative_experience_cell",
      (p) -> new CreativeExperienceCellItem(p.stacksTo(1))
  );
  public static final ItemDefinition<ExperienceStorageCell> EXPERIENCE_CELL_1K = cell(StorageTier.SIZE_1K);
  public static final ItemDefinition<ExperienceStorageCell> EXPERIENCE_CELL_4K = cell(StorageTier.SIZE_4K);
  public static final ItemDefinition<ExperienceStorageCell> EXPERIENCE_CELL_16K = cell(StorageTier.SIZE_16K);
  public static final ItemDefinition<ExperienceStorageCell> EXPERIENCE_CELL_64K = cell(StorageTier.SIZE_64K);
  public static final ItemDefinition<ExperienceStorageCell> EXPERIENCE_CELL_256K = cell(StorageTier.SIZE_256K);

  public static final ItemDefinition<ExperiencePortableCellItem> PORTABLE_EXPERIENCE_CELL_1K =
      portable(StorageTier.SIZE_1K, 0x80caff);
  public static final ItemDefinition<ExperiencePortableCellItem> PORTABLE_EXPERIENCE_CELL_4K =
      portable(StorageTier.SIZE_4K, 0x80caff);
  public static final ItemDefinition<ExperiencePortableCellItem> PORTABLE_EXPERIENCE_CELL_16K =
      portable(StorageTier.SIZE_16K, 0x80caff);
  public static final ItemDefinition<ExperiencePortableCellItem> PORTABLE_EXPERIENCE_CELL_64K =
      portable(StorageTier.SIZE_64K, 0x80caff);
  public static final ItemDefinition<ExperiencePortableCellItem> PORTABLE_EXPERIENCE_CELL_256K =
      portable(StorageTier.SIZE_256K, 0x80caff);

  public static final ItemDefinition<PartItem<ExperienceP2PTunnelPart>> EXPERIENCE_P2P_TUNNEL = Util.make(() -> {
    PartModels.registerModels(PartModelsHelper.createModels(ExperienceP2PTunnelPart.class));
    return item(
        "ME Experience P2P Tunnel",
        "experience_p2p_tunnel",
        (p) -> new PartItem<>(p, ExperienceP2PTunnelPart.class, ExperienceP2PTunnelPart::new)
    );
  });

  private static ItemDefinition<ExperienceStorageCell> cell(StorageTier tier) {
    return item(
        tier.namePrefix() + " ME Experience Storage Cell",
        "experience_storage_cell_" + tier.namePrefix(),
        p -> new ExperienceStorageCell(p.stacksTo(1), tier, EXPERIENCE_CELL_HOUSING.get())
    );
  }

  private static ItemDefinition<ExperiencePortableCellItem> portable(StorageTier tier, int defaultColor) {
    return item(
        tier.namePrefix() + " Portable Experience Cell",
        "portable_experience_cell_" + tier.namePrefix(),
        p -> new ExperiencePortableCellItem(tier, p.stacksTo(1), defaultColor)
    );
  }

  private static <T extends Item> ItemDefinition<T> item(
      String englishName,
      String id,
      Function<Item.Properties, T> factory
  ) {
    var definition = new ItemDefinition<>(englishName, DR.registerItem(id, factory));
    ITEMS.add(definition);
    return definition;
  }

  public static List<ItemDefinition<ExperienceStorageCell>> getCells() {
    return List.of(EXPERIENCE_CELL_1K, EXPERIENCE_CELL_4K, EXPERIENCE_CELL_16K, EXPERIENCE_CELL_64K, EXPERIENCE_CELL_256K);
  }

  public static List<ItemDefinition<ExperiencePortableCellItem>> getPortables() {
    return List.of(
        PORTABLE_EXPERIENCE_CELL_1K,
        PORTABLE_EXPERIENCE_CELL_4K,
        PORTABLE_EXPERIENCE_CELL_16K,
        PORTABLE_EXPERIENCE_CELL_64K,
        PORTABLE_EXPERIENCE_CELL_256K);
  }

  public static void initCellUpgrades(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      getCells().forEach(cell -> {
        Upgrades.add(appeng.core.definitions.AEItems.VOID_CARD, cell::get, 1, GuiText.StorageCells.getTranslationKey());
      });
      getPortables().forEach(cell -> {
        Upgrades.add(appeng.core.definitions.AEItems.ENERGY_CARD, cell, 2, GuiText.PortableCells.getTranslationKey());
        Upgrades.add(appeng.core.definitions.AEItems.VOID_CARD, cell, 1, GuiText.PortableCells.getTranslationKey());
      });
    });
  }
}
