package es.degrassi.appex.definition;

import appeng.api.implementations.menuobjects.IPortableTerminal;
import appeng.client.InitScreens;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.common.MEStorageMenu;
import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.api.storage.IExperienceAccessTermMenuHost;
import es.degrassi.appex.block.entity.ExperienceConverterEntity;
import es.degrassi.appex.client.container.ExperienceAccessTermMenu;
import es.degrassi.appex.client.container.ExperienceConverterContainer;
import es.degrassi.appex.client.screen.ExperienceAccessTermScreen;
import es.degrassi.appex.client.screen.ExperienceConverterScreen;
import es.degrassi.appex.weat.WeatMenu;
import es.degrassi.appex.weat.WeatScreen;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AExpMenus {
  public static final MenuType<MEStorageMenu> PORTABLE_EXPERIENCE_CELL_TYPE = MenuTypeBuilder
      .create(MEStorageMenu::new, IPortableTerminal.class)
      .build(AppliedExperienced.id("portable_experience_cell"));

  public static final MenuType<ExperienceConverterContainer> EXPERIENCE_CONVERTER = MenuTypeBuilder
      .create(ExperienceConverterContainer::new, ExperienceConverterEntity.class)
      .build(AppliedExperienced.id("experience_converter"));

  public static final MenuType<ExperienceAccessTermMenu> EXPERIENCE_ACCESS_TERM_MENU = MenuTypeBuilder
      .create(ExperienceAccessTermMenu::new, IExperienceAccessTermMenuHost.class)
      .build(AppliedExperienced.id("experienceaccessterminal"));

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
      InitScreens.<ExperienceAccessTermMenu, ExperienceAccessTermScreen<ExperienceAccessTermMenu>>register(
          event,
          EXPERIENCE_ACCESS_TERM_MENU,
          ExperienceAccessTermScreen::new,
          "/screens/terminals/experience_access_terminal.json"
      );
      InitScreens.register(event, WeatMenu.TYPE, WeatScreen::new, "/screens/wtlib/wireless_experience_access_terminal.json");
    });
  }
}
