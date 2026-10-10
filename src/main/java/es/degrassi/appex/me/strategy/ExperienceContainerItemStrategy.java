package es.degrassi.appex.me.strategy;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;
import com.google.common.primitives.Ints;
import es.degrassi.appex.me.key.ExperienceKey;
import es.degrassi.experiencelib.api.ExperienceResource;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.experiencelib.common.ExperienceLibRegistration;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public class ExperienceContainerItemStrategy implements ContainerItemStrategy<ExperienceKey, IExperienceHandler> {
  @Override
  public @Nullable GenericStack getContainedStack(ItemStack stack) {
    if (stack.isEmpty()) return null;
    if (!stack.has(ExperienceLibRegistration.EXPERIENCE_COMPONENT)) return null;
    return new GenericStack(ExperienceKey.KEY, stack.get(ExperienceLibRegistration.EXPERIENCE_COMPONENT));
  }

  @Override
  public @org.jspecify.annotations.Nullable IExperienceHandler findCarriedContext(Player player, AbstractContainerMenu menu) {
    var itemAccess = ItemAccess.forPlayerCursor(player, menu);
    return itemAccess.getCapability(ExperienceLibCapabilities.EXPERIENCE.item());
  }

  @Override
  public @org.jspecify.annotations.Nullable IExperienceHandler findPlayerSlotContext(Player player, int slot) {
    var itemAccess = ItemAccess.forPlayerSlot(player, slot);
    return itemAccess.getCapability(ExperienceLibCapabilities.EXPERIENCE.item());
  }

  @Override
  public long extract(IExperienceHandler context, ExperienceKey what, long amount, Actionable mode) {
    try (var tx = Transaction.open(null)) {
      var extracted = context.extract(ExperienceResource.of(amount), Ints.saturatedCast(amount), tx);
      if (mode == Actionable.MODULATE) {
        tx.commit();
      }
      return extracted;
    }
  }

  @Override
  public long insert(IExperienceHandler context, ExperienceKey what, long amount, Actionable mode) {
    try (var tx = Transaction.open(null)) {
      var inserted = context.insert(ExperienceResource.of(amount), Ints.saturatedCast(amount), tx);
      if (mode == Actionable.MODULATE) {
        tx.commit();
      }
      return inserted;
    }
  }

  @Override
  public void playFillSound(Player player, ExperienceKey what) {

  }

  @Override
  public void playEmptySound(Player player, ExperienceKey what) {

  }

  @Override
  public @Nullable GenericStack getExtractableContent(IExperienceHandler context) {
    try (var tx = Transaction.open(null)) {
      var stack = ResourceHandlerUtil.extractFirst(context, r -> true, Integer.MAX_VALUE, tx);
      if (stack != null) {
        return new GenericStack(ExperienceKey.KEY, stack.amount());
      }
    }
    return null;
  }
}
