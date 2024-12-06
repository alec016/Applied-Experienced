package es.degrassi.appexp.item;

import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.core.AEConfig;
import appeng.core.localization.Tooltips;
import appeng.items.storage.StorageCellTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExperienceCellHandler implements ICellHandler {
  public static final ExperienceCellHandler INSTANCE = new ExperienceCellHandler();

  private ExperienceCellHandler() {
  }

  @Override
  public boolean isCell(ItemStack is) {
    return is != null && is.getItem() instanceof IExperienceCellItem;
  }

  @Override
  public @Nullable ExperienceCellInventory getCellInventory(ItemStack is, @Nullable ISaveProvider container) {
    return isCell(is) ? new ExperienceCellInventory((IExperienceCellItem) is.getItem(), is, container) : null;
  }

  public void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
    var handler = getCellInventory(is, null);

    if (handler != null) {
      lines.add(Tooltips.bytesUsed(handler.getUsedBytes(), handler.getTotalBytes()));
    }
  }

  public Optional<TooltipComponent> getTooltipImage(ItemStack is) {
    var handler = getCellInventory(is, null);
    if (handler == null) return Optional.empty();

    var upgrades = new ArrayList<ItemStack>();

    if (AEConfig.instance().isTooltipShowCellUpgrades()) {
      handler.getUpgrades().forEach(upgrades::add);
    }
    var content = new ArrayList<GenericStack>();
    KeyCounter counter = new KeyCounter();
    handler.getAvailableStacks(counter);
    if (!counter.isEmpty())
      counter.forEach(entry -> content.add(new GenericStack(entry.getKey(), entry.getLongValue())));

    return Optional.of(new StorageCellTooltipComponent(upgrades, content, false, true));
  }
}
