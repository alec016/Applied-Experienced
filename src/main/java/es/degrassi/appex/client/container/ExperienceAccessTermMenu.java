package es.degrassi.appex.client.container;

import appeng.api.storage.ILinkStatus;
import appeng.core.network.clientbound.SetLinkStatusPacket;
import appeng.menu.guisync.LinkStatusAwareMenu;
import es.degrassi.appex.api.storage.IExperienceAccessTermMenuHost;
import es.degrassi.appex.definition.AExpMenus;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import java.util.Objects;

public class ExperienceAccessTermMenu extends ExperienceContainer<IExperienceAccessTermMenuHost> implements LinkStatusAwareMenu {
  @Getter
  @Setter
  private ILinkStatus linkStatus = ILinkStatus.ofDisconnected();

  public ExperienceAccessTermMenu(int id, Inventory playerInventory, IExperienceAccessTermMenuHost host) {
    this(AExpMenus.EXPERIENCE_ACCESS_TERM_MENU, id, playerInventory, host, true);
  }

  public ExperienceAccessTermMenu(MenuType<?> menuType, int id, Inventory ip, IExperienceAccessTermMenuHost host, boolean bindInventory) {
    super(menuType, id, ip, host, bindInventory);
  }

  public long getXp() {
    return xp;
  }

  public long getCapacity() {
    return capacity;
  }

  @Override
  public void broadcastChanges() {
    super.broadcastChanges();

    updateLinkStatus();
  }

  // When using a custom implementation of ILinkStatus, override this and implement your own packet
  protected void updateLinkStatus() {
    var linkStatus = entity.getLinkStatus();
    if (!Objects.equals(this.linkStatus, linkStatus)) {
      this.linkStatus = linkStatus;
      sendPacketToClient(new SetLinkStatusPacket(linkStatus));
    }
  }
}
