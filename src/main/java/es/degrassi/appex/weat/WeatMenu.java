package es.degrassi.appex.weat;

import appeng.api.storage.ITerminalHost;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.RestrictedInputSlot;
import de.mari_023.ae2wtlib.api.gui.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.api.terminal.ItemWUT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.client.container.ExperienceAccessTermMenu;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class WeatMenu extends ExperienceAccessTermMenu {
  public static final Identifier ID = AppliedExperienced.id("wireless_experience_access_terminal");
  public static final MenuType<WeatMenu> TYPE = MenuTypeBuilder.create(WeatMenu::new, WeatMenuHost.class).build(ID);
  private final WeatMenuHost weatMenuHost;

  public WeatMenu(int id, Inventory ip, WeatMenuHost anchor) {
    super(TYPE, id, ip, anchor, true);
    this.weatMenuHost = anchor;
    IUpgradeInventory upgrades = this.weatMenuHost.getUpgrades();

    for (int i = 0; i < upgrades.size(); ++i) {
      RestrictedInputSlot slot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.UPGRADES, upgrades, i);
      slot.setNotDraggable();
      this.addSlot(slot, SlotSemantics.UPGRADE);
    }

    this.addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.QE_SINGULARITY, this.weatMenuHost.getSubInventory(WTMenuHost.INV_SINGULARITY), 0), AE2wtlibSlotSemantics.SINGULARITY);
  }

  public boolean isWUT() {
    return this.weatMenuHost.getItemStack().getItem() instanceof ItemWUT;
  }

  public ITerminalHost getHost() {
    return this.weatMenuHost;
  }
}
