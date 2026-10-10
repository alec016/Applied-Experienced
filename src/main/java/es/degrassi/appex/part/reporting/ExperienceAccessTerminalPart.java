package es.degrassi.appex.part.reporting;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.parts.IPartItem;
import appeng.api.storage.ILinkStatus;
import appeng.api.storage.MEStorage;
import appeng.api.storage.SupplierStorage;
import appeng.api.util.IConfigManager;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.reporting.AbstractDisplayPart;
import es.degrassi.appex.api.storage.IExperienceAccessTermMenuHost;
import es.degrassi.appex.definition.AExpMenus;
import es.degrassi.appex.me.key.ExperienceKey;
import lombok.Getter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class ExperienceAccessTerminalPart extends AbstractDisplayPart implements IExperienceAccessTermMenuHost {
  @Getter
  private final IConfigManager configManager = IConfigManager.builder(() -> {
    getHost().markForSave();
  })
  .build();

  public ExperienceAccessTerminalPart(IPartItem<?> partItem) {
    super(partItem, true);
  }

  @Override
  public boolean onUseWithoutItem(Player player, Vec3 pos) {
    if (!super.onUseWithoutItem(player, pos) && !isClientSide()) {
      MenuOpener.open(AExpMenus.EXPERIENCE_ACCESS_TERM_MENU, player, MenuLocators.forPart(this));
    }
    return true;
  }

  @Override
  public void writeToNBT(ValueOutput data) {
    super.writeToNBT(data);
    configManager.writeToNBT(data);
  }

  @Override
  public void readFromNBT(ValueInput input) {
    super.readFromNBT(input);
    configManager.readFromNBT(input);
  }

  @Override
  public MEStorage getInventory() {
    return new SupplierStorage(() -> {
      var grid = getMainNode().getGrid();
      if (grid != null) {
        return grid.getStorageService().getInventory();
      }
      return null;
    });
  }

  @Override
  public ILinkStatus getLinkStatus() {
    return ILinkStatus.ofManagedNode(getMainNode());
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
