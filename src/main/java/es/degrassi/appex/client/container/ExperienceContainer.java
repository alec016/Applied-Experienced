package es.degrassi.appex.client.container;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.slot.AppEngSlot;
import es.degrassi.appex.api.entity.IExperienceInvHost;
import lombok.Getter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ExperienceContainer<T extends IExperienceInvHost> extends AEBaseMenu {
  @GuiSync(2)
  public long xp = 0;
  @GuiSync(3)
  public long capacity = 0;
  @GuiSync(4)
  public boolean inverted = false;

  @Getter
  protected final T entity;

  protected static final SlotSemantic IN_SLOT = SlotSemantics.register("APEX_IN_SLOT", false);
  protected static final SlotSemantic OUT_SLOT = SlotSemantics.register("APEX_OUT_SLOT", false);

  public ExperienceContainer(MenuType<?> container, int id, Inventory playerInventory, T host) {
    super(container, id, playerInventory.player.getInventory(), host);
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
      this.inverted = entity.isInverted();
    }

    super.broadcastChanges();
  }
}
