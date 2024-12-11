package es.degrassi.appexp.api.item;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.cells.ICellWorkbenchItem;
import net.minecraft.world.item.ItemStack;

public interface IExperienceCellItem extends ICellWorkbenchItem {
  long getTotalBytes();

  double getIdleDrain();

  @Override
  default FuzzyMode getFuzzyMode(ItemStack is) {
    return null;
  }

  @Override
  default void setFuzzyMode(ItemStack is, FuzzyMode mode) {}
}
