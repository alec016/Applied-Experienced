package es.degrassi.appex.item;

import appeng.api.storage.StorageCells;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.localization.PlayerMessages;
import appeng.items.AEBaseItem;
import appeng.items.storage.StorageTier;
import appeng.recipes.game.StorageCellDisassemblyRecipe;
import es.degrassi.appex.api.item.IExperienceCellItem;
import es.degrassi.appex.me.cell.ExperienceCellHandler;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Consumer;

@Getter
public class ExperienceStorageCell extends AEBaseItem implements IExperienceCellItem {
  private final StorageTier tier;

  public ExperienceStorageCell(Properties properties, StorageTier tier) {
    super(properties);
    this.tier = tier;
  }

  @Override
  public IUpgradeInventory getUpgrades(ItemStack stack) {
    return UpgradeInventories.forItem(stack, 1);
  }

  @Override
  public long getTotalBytes() {
    return tier.bytes();
  }

  @Override
  public double getIdleDrain() {
    return tier.idleDrain();
  }

  @ParametersAreNonnullByDefault
  @NotNull
  @Override
  public InteractionResult use(Level level, Player player, InteractionHand hand) {
    var inHand = player.getItemInHand(hand);
    disassemble(inHand, level, player);
    return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
  }

  @NotNull
  @Override
  public InteractionResult onItemUseFirst(@NotNull ItemStack stack, UseOnContext context) {
    return disassemble(stack, context.getLevel(), context.getPlayer())
        ? context.getLevel().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER
        : InteractionResult.PASS;
  }

  private boolean disassemble(ItemStack stack, Level level, Player player) {
    if (player != null && player.isShiftKeyDown()) {
      if (level.isClientSide()) return false;

      var disassembledStacks = StorageCellDisassemblyRecipe.getDisassemblyResult((ServerLevel) level, stack.getItem());
      if (disassembledStacks.isEmpty()) return false;

      var playerInv = player.getInventory();
      var cellInv = StorageCells.getCellInventory(stack, null);

      if (cellInv != null && playerInv.getSelectedItem() == stack) {
        if (cellInv.getAvailableStacks().isEmpty()) {
          playerInv.setItem(playerInv.getSelectedSlot(), ItemStack.EMPTY);

          for (var upgrade : getUpgrades(stack)) {
            playerInv.placeItemBackInInventory(upgrade);
          }

          for (var disassembled : disassembledStacks) {
            playerInv.placeItemBackInInventory(disassembled);
          }

          return true;
        } else {
          player.sendOverlayMessage(PlayerMessages.OnlyEmptyCellsCanBeDisassembled.text());
        }
      }
    }

    return false;
  }

  @ParametersAreNonnullByDefault
  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag advTooltips) {
    ExperienceCellHandler.INSTANCE.addCellInformationToTooltip(stack, lines);
  }

  @NotNull
  @Override
  public Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
    return ExperienceCellHandler.INSTANCE.getTooltipImage(stack);
  }
}
