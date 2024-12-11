package es.degrassi.appexp.me.cell;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.core.definitions.AEItems;
import es.degrassi.appexp.api.item.IExperienceCellItem;
import es.degrassi.appexp.me.key.ExperienceKey;
import es.degrassi.appexp.me.key.ExperienceKeyType;
import es.degrassi.appexp.definition.AExpComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class ExperienceCellInventory implements StorageCell {
  private final IExperienceCellItem cell;
  private final ItemStack stack;
  private final ISaveProvider container;

  private long amount;
  private boolean isPersisted = true;

  public ExperienceCellInventory(IExperienceCellItem cell, ItemStack stack, ISaveProvider container) {
    this.cell = cell;
    this.stack = stack;
    this.container = container;

    this.amount = stack.getOrDefault(AExpComponents.EXPERIENCE_CELL_AMOUNT, 0L);
  }

  public long getTotalBytes() {
    return cell.getTotalBytes();
  }

  public long getUsedBytes() {
    var amountPerByte = ExperienceKeyType.TYPE.getAmountPerByte();
    return (amount + amountPerByte - 1) / amountPerByte;
  }

  public long getMaxAmount() {
    return cell.getTotalBytes() * ExperienceKeyType.TYPE.getAmountPerByte();
  }

  @Override
  public CellState getStatus() {
    if (amount == 0) {
      return CellState.EMPTY;
    }

    if (amount == getMaxAmount()) {
      return CellState.FULL;
    }

    if (amount > getMaxAmount() / 2) {
      return CellState.TYPES_FULL;
    }

    return CellState.NOT_EMPTY;
  }

  @Override
  public double getIdleDrain() {
    return cell.getIdleDrain();
  }

  public IUpgradeInventory getUpgrades() {
    return cell.getUpgrades(stack);
  }

  private void saveChanges() {
    isPersisted = false;

    if (container != null) {
      container.saveChanges();
    } else {
      // if there is no ISaveProvider, store to NBT immediately
      persist();
    }
  }

  @Override
  public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
    if (amount == 0 || !(what instanceof ExperienceKey)) {
      return 0;
    }

    var inserted = Math.min(amount, Math.max(0, getMaxAmount() - this.amount));

    if (mode == Actionable.MODULATE) {
      this.amount += inserted;
      saveChanges();
    }

    return getUpgrades().isInstalled(AEItems.VOID_CARD) ? amount : inserted;
  }

  @Override
  public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
    var extractAmount = Math.min(Integer.MAX_VALUE, amount);
    var currentAmount = this.amount;

    if (this.amount > 0 && Objects.equals(ExperienceKey.KEY, what)) {
      if (mode == Actionable.MODULATE) {
        this.amount = Math.max(0, this.amount - extractAmount);
        saveChanges();
      }

      return Math.min(extractAmount, currentAmount);
    }

    return 0;
  }

  @Override
  public void persist() {
    if (isPersisted) {
      return;
    }

    if (amount < 0) {
      stack.remove(AExpComponents.EXPERIENCE_CELL_AMOUNT);
    } else {
      stack.set(AExpComponents.EXPERIENCE_CELL_AMOUNT, amount);
    }

    isPersisted = true;
  }

  @Override
  public void getAvailableStacks(KeyCounter out) {
    if (amount > 0) {
      out.add(ExperienceKey.KEY, amount);
    }
  }

  @Override
  public Component getDescription() {
    return stack.getHoverName();
  }
}
