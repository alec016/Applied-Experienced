package es.degrassi.appexp;

import appeng.api.implementations.menuobjects.IPortableTerminal;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.init.client.InitScreens;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.common.MEStorageMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class AEMenus {

  public static final MenuType<MEStorageMenu> PORTABLE_EXPERIENCE_CELL_TYPE = MenuTypeBuilder
      .create(MEStorageMenu::new, IPortableTerminal.class)
      .build(AppliedExperienced.id("portable_experience_cell"));

  @SuppressWarnings("RedundantTypeArguments")
  public static void initialize(IEventBus bus) {
    bus.addListener((RegisterMenuScreensEvent event) -> {
      InitScreens.<MEStorageMenu, MEStorageScreen<MEStorageMenu>>register(event, PORTABLE_EXPERIENCE_CELL_TYPE,
          MEStorageScreen::new, "/screens/terminals/portable_experience_cell.json");
    });
  }
}
