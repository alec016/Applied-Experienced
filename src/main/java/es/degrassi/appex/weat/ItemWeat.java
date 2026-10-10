package es.degrassi.appex.weat;

import appeng.menu.locator.ItemMenuHostLocator;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class ItemWeat extends ItemWT {
  public ItemWeat(Properties p) {
    super(p);
  }

  @Override
  public MenuType<?> getMenuType(ItemMenuHostLocator itemMenuHostLocator, Player player) {
    return WeatMenu.TYPE;
  }
}
