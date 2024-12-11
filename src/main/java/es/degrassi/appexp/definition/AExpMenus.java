package es.degrassi.appexp.definition;

import appeng.api.implementations.menuobjects.IPortableTerminal;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.init.client.InitScreens;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.common.MEStorageMenu;
import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.appexp.block.entity.ExperienceConverterEntity;
import es.degrassi.appexp.client.container.ExperienceConverterContainer;
import es.degrassi.appexp.client.screen.ExperienceConverterScreen;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class AExpMenus {
  private AExpMenus() {
  }

  public static final MenuType<MEStorageMenu> PORTABLE_EXPERIENCE_CELL_TYPE = MenuTypeBuilder
      .create(MEStorageMenu::new, IPortableTerminal.class)
      .build(AppliedExperienced.id("portable_experience_cell"));

  public static final MenuType<ExperienceConverterContainer> EXPERIENCE_CONVERTER = MenuTypeBuilder
      .create(ExperienceConverterContainer::new, ExperienceConverterEntity.class)
      .build(AppliedExperienced.id("experience_converter"));

  @SuppressWarnings("RedundantTypeArguments")
  public static void initialize(IEventBus bus) {
    bus.addListener((RegisterMenuScreensEvent event) -> {
      InitScreens.<MEStorageMenu, MEStorageScreen<MEStorageMenu>>register(
          event,
          PORTABLE_EXPERIENCE_CELL_TYPE,
          MEStorageScreen::new,
          "/screens/terminals/portable_experience_cell.json"
      );
      InitScreens.<ExperienceConverterContainer, ExperienceConverterScreen>register(
          event,
          EXPERIENCE_CONVERTER,
          ExperienceConverterScreen::new,
          "/screens/experience_converter.json"
      );
    });
  }
}
