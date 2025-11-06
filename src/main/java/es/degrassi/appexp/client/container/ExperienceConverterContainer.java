package es.degrassi.appexp.client.container;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.slot.AppEngSlot;
import es.degrassi.appexp.block.entity.ExperienceConverterEntity;
import es.degrassi.appexp.definition.AExpMenus;
import net.minecraft.world.entity.player.Inventory;

public class ExperienceConverterContainer extends AEBaseMenu {
  public final ExperienceConverterEntity entity;
  @GuiSync(2)
  public long xp = 0;
  @GuiSync(3)
  public long capacity = 0;

  private static final SlotSemantic IN_SLOT = SlotSemantics.register("APEX_IN_SLOT", false);
  private static final SlotSemantic OUT_SLOT = SlotSemantics.register("APEX_OUT_SLOT", false);

  public ExperienceConverterContainer(int id, Inventory playerInventory, ExperienceConverterEntity host) {
    super(AExpMenus.EXPERIENCE_CONVERTER, id, playerInventory.player.getInventory(), host);
    this.entity = host;

    addSlot(new AppEngSlot(host.getInternalInventory(), 0), IN_SLOT);
    addSlot(new AppEngSlot(host.getInternalInventory(), 1), OUT_SLOT);

    createPlayerInventorySlots(playerInventory);
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
