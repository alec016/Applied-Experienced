package es.degrassi.appex.item;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.upgrades.Upgrades;
import appeng.items.storage.StorageTier;
import appeng.items.tools.powered.AbstractPortableCell;
import es.degrassi.appex.api.item.IExperienceCellItem;
import es.degrassi.appex.definition.AExpMenus;
import es.degrassi.appex.me.cell.ExperienceCellHandler;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@Getter
public class ExperiencePortableCellItem extends AbstractPortableCell implements IExperienceCellItem {
  private final StorageTier tier;

  public ExperiencePortableCellItem(StorageTier tier, Properties props, int defaultColor) {
    super(AExpMenus.PORTABLE_EXPERIENCE_CELL_TYPE, props, defaultColor);
    this.tier = tier;
  }

  @Override
  public void appendHoverText(
      ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag advancedTooltips) {
    super.appendHoverText(stack, context, display, lines, advancedTooltips);
    ExperienceCellHandler.INSTANCE.addCellInformationToTooltip(stack, lines);
  }

  @NotNull
  @Override
  public Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
    return ExperienceCellHandler.INSTANCE.getTooltipImage(stack);
  }

  @Override
  public Identifier getRecipeId() {
    return Objects.requireNonNull(getRegistryName());
  }

  @Override
  public double getChargeRate(ItemStack stack) {
    return 80D * (Upgrades.getEnergyCardMultiplier(getUpgrades(stack)) + 1);
  }

  @Override
  public long getTotalBytes() {
    return 50 * (long) Math.pow(4, tier.index() - 1);
  }

  @Override
  public double getIdleDrain() {
    return tier.idleDrain();
  }

  @Override
  public IUpgradeInventory getUpgrades(ItemStack is) {
    return UpgradeInventories.forItem(is, 3, this::onUpgradesChanged);
  }
}
