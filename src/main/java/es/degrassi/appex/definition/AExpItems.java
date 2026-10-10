package es.degrassi.appex.definition;

import appeng.api.upgrades.Upgrades;
import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import appeng.core.localization.GuiText;
import appeng.items.AEBaseItem;
import appeng.items.materials.MaterialItem;
import appeng.items.storage.StorageTier;
import de.mari_023.ae2wtlib.api.gui.Icon;
import de.mari_023.ae2wtlib.api.registration.AddTerminalEvent;
import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.api.item.ICreativeTabItem;
import es.degrassi.appex.item.ExperiencePortableCellItem;
import es.degrassi.appex.item.ExperienceStorageCell;
import es.degrassi.appex.weat.ItemWeat;
import es.degrassi.appex.weat.WeatMenu;
import es.degrassi.appex.weat.WeatMenuHost;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AExpItems {

  private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
      AppliedExperienced.MODID);
  private static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();
  public static final DeferredRegister.Items DR = DeferredRegister.createItems(AppliedExperienced.MODID);

  public static List<ItemDefinition<?>> getItems() {
    return Collections.unmodifiableList(ITEMS);
  }

  public static void initialize(IEventBus bus) {
    TABS.register(bus);
    DR.register(bus);
    bus.addListener(RegisterEvent.class, event -> {
      if (event.getRegistryKey().equals(Registries.ITEM)) {
        AddTerminalEvent.register(ev -> {
          ev.builder(
              "experience_access",
              WeatMenuHost::new,
              WeatMenu.TYPE,
              AExpItems.EXPERIENCE_TERMINAL.asItem(),
              new Icon(0, 0, 16, 16, new Icon.Texture(AppliedExperienced.id("textures/guis/wireless_icon.png"), 16, 16))
          ).addTerminal();
        });
      }
    });
  }

  private static Item basic(Item.Properties p) {
    return new MaterialItem(p);
  }

  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = TABS.register("main",
      () -> CreativeModeTab
          .builder()
          .title(AExpText.CREATIVE_TAB.text())
          .icon(() -> new ItemStack(AExpItems.EXPERIENCE_CELL_256K.get()))
          .displayItems(AExpItems::display)
          .build()
  );

  private static void display(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
    var defs = new ArrayList<ItemDefinition<?>>();
    defs.addAll(getItems());
    defs.addAll(AExpBlocks.getBlocks().stream().map(BlockDefinition::item).toList());

    for (var def : defs) {
      var item = def.asItem();

      switch (item) {
        case AEBaseBlockItem baseItem when baseItem.getBlock() instanceof AEBaseBlock baseBlock -> baseBlock.addToMainCreativeTab(params, output);
        case AEBaseItem baseItem -> baseItem.addToMainCreativeTab(params, output);
        case ICreativeTabItem tabItem -> tabItem.addToMainCreativeTab(params, output);
        default -> output.accept(def);
      }
    }
  }

  public static final ItemDefinition<Item> EXPERIENCE_CELL_HOUSING = item(
      "ME Experience Cell Housing",
      "experience_cell_housing",
      AExpItems::basic
  );

  public static final ItemDefinition<ItemWeat> EXPERIENCE_TERMINAL = item(
      "Wireless Experience Access Terminal",
      "wireless_experience_access_terminal",
      ItemWeat::new
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

  private static ItemDefinition<ExperienceStorageCell> cell(StorageTier tier) {
    return item(
        tier.namePrefix() + " ME Experience Storage Cell",
        "experience_storage_cell_" + tier.namePrefix(),
        p -> new ExperienceStorageCell(p.stacksTo(1), tier)
    );
  }

  private static ItemDefinition<ExperiencePortableCellItem> portable(StorageTier tier, int defaultColor) {
    return item(
        tier.namePrefix() + " Portable Experience Cell",
        "portable_experience_cell_" + tier.namePrefix(),
        p -> new ExperiencePortableCellItem(tier, p.stacksTo(1), defaultColor)
    );
  }

  static <T extends Item> ItemDefinition<T> item(
      String englishName,
      String id,
      Function<Item.Properties, T> factory
  ) {
    var definition = new ItemDefinition<>(englishName, DR.registerItem(id, factory));
    ITEMS.add(definition);
    return definition;
  }

  static <T extends Item> ItemDefinition<T> item(
      String englishName,
      Identifier id,
      Function<Item.Properties, T> factory
  ) {
    return item(englishName, id.getPath(), factory);
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
        Upgrades.add(AEItems.VOID_CARD, cell::get, 1, GuiText.StorageCells.getTranslationKey());
      });
      getPortables().forEach(cell -> {
        Upgrades.add(AEItems.ENERGY_CARD, cell, 2, GuiText.PortableCells.getTranslationKey());
        Upgrades.add(AEItems.VOID_CARD, cell, 1, GuiText.PortableCells.getTranslationKey());
      });
    });
  }
}
