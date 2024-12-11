package es.degrassi.appexp.client.container;

import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import es.degrassi.appexp.block.entity.ExperienceConverterEntity;
import es.degrassi.appexp.definition.AExpMenus;
import net.minecraft.world.entity.player.Inventory;

public class ExperienceConverterContainer extends AEBaseMenu {
  public final ExperienceConverterEntity entity;
  @GuiSync(2)
  public long xp = 0;
  @GuiSync(3)
  public long capacity = 0;

  public ExperienceConverterContainer(int id, Inventory playerInventory, ExperienceConverterEntity host) {
    super(AExpMenus.EXPERIENCE_CONVERTER, id, playerInventory.player.getInventory(), host);
    this.entity = host;
  }

  @Override
  public void broadcastChanges() {
    if (isServerSide()) {
      this.xp = entity.getExperience();
      this.capacity = entity.getExperienceCapacity();
    }

    super.broadcastChanges();
  }
}
