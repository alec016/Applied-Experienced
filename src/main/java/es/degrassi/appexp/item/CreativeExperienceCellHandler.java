package es.degrassi.appexp.item;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import net.minecraft.world.item.ItemStack;

public class CreativeExperienceCellHandler implements ICellHandler {
  public static final CreativeExperienceCellHandler INSTANCE = new CreativeExperienceCellHandler();

  private CreativeExperienceCellHandler() {}

  @Override
  public boolean isCell(ItemStack is) {
    return is != null && is.getItem() instanceof CreativeExperienceCellItem;
  }

  @Override
  public StorageCell getCellInventory(ItemStack is, ISaveProvider host) {
    return isCell(is) ? new CreativeExperienceCellInventory(is) : null;
  }
}
