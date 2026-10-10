package es.degrassi.appex.weat;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import es.degrassi.appex.api.storage.IExperienceAccessTermMenuHost;
import es.degrassi.appex.me.key.ExperienceKey;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class WeatMenuHost extends WTMenuHost implements IExperienceAccessTermMenuHost {
  public WeatMenuHost(ItemWT item, Player player, ItemMenuHostLocator locator, BiConsumer<Player, ISubMenu> returnToMainMenu) {
    super(item, player, locator, returnToMainMenu);
  }

  @Override
  public @Nullable IGridNode getGridNode() {
    return getActionableNode();
  }

  @Override
  public long getExperience() {
    if (!getLinkStatus().connected()) return 0;
    return getInventory()
        .getAvailableStacks()
        .get(ExperienceKey.KEY);
  }

  @Override
  public long getExperienceCapacity() {
    if (!getLinkStatus().connected()) return 0;
    return getExperience() + getInventory()
        .insert(ExperienceKey.KEY, Long.MAX_VALUE, Actionable.SIMULATE, IActionSource.ofMachine(this));
  }
}
