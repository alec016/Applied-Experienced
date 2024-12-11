package es.degrassi.appexp.me.cell;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.StorageCell;
import es.degrassi.appexp.me.key.ExperienceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public record CreativeExperienceCellInventory(ItemStack i) implements StorageCell {
  @Override
  public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
    return what instanceof ExperienceKey ? amount : 0;
  }

  @Override
  public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
    return what instanceof ExperienceKey ? amount : 0;
  }

  @Override
  public void getAvailableStacks(KeyCounter out) {
    out.add(ExperienceKey.KEY, Integer.MAX_VALUE);
  }
  @Override
  public CellState getStatus() {
    return CellState.NOT_EMPTY;
  }

  @Override
  public double getIdleDrain() {
    return 0;
  }

  @Override
  public void persist() {

  }

  @Override
  public Component getDescription() {
    return i.getDisplayName();
  }
}
