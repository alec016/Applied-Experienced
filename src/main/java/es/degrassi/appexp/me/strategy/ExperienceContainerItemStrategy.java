package es.degrassi.appexp.me.strategy;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;
import com.google.common.base.Preconditions;
import es.degrassi.appexp.me.key.ExperienceKey;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class ExperienceContainerItemStrategy implements ContainerItemStrategy<ExperienceKey, ExperienceContainerItemStrategy.Context> {
  private long getExperience(ItemStack stack) {
    Preconditions.checkArgument(isExperienced(stack), "Stack must have Experience capability");
    return stack.getCapability(ExperienceLibCapabilities.EXPERIENCE.item()).getExperience();
  }

  private long getCapacity(ItemStack stack) {
    Preconditions.checkArgument(isExperienced(stack), "Stack must have Experience capability");
    return stack.getCapability(ExperienceLibCapabilities.EXPERIENCE.item()).getExperienceCapacity();
  }

  private boolean isExperienced(ItemStack stack) {
    return stack.getCapability(ExperienceLibCapabilities.EXPERIENCE.item()) != null;
  }

  private boolean isCreative(ItemStack stack) {
    return isExperienced(stack) && getExperience(stack) >= Integer.MAX_VALUE;
  }

  private void changeExperience(long amount, ItemStack stack) {
    Preconditions.checkArgument(isExperienced(stack), "Stack must have Experience capability");
    if (isCreative(stack)) return;
    stack.getCapability(ExperienceLibCapabilities.EXPERIENCE.item()).setExperience(Math.min(ExperienceKey.MAX_EXPERIENCE,
        Math.max(getExperience(stack) + amount, 0)));
  }

  @Override
  public @Nullable GenericStack getContainedStack(ItemStack stack) {
    if (stack.isEmpty()) {
      return null;
    }

    var handler = stack.getCapability(ExperienceLibCapabilities.EXPERIENCE.item());

    if (handler != null) {
      return new GenericStack(ExperienceKey.KEY, handler.getExperience());
    }

    return null;
  }

  @Override
  public @Nullable Context findCarriedContext(Player player, AbstractContainerMenu menu) {
    var carried = menu.getCarried();

    if (carried.getCapability(ExperienceLibCapabilities.EXPERIENCE.item()) != null) {
      return new CarriedContext(player, menu);
    }

    return null;
  }

  @Override
  public @Nullable Context findPlayerSlotContext(Player player, int slot) {
    var carried = player.getInventory().getItem(slot);

    if (carried.getCapability(ExperienceLibCapabilities.EXPERIENCE.item()) != null) {
      return new PlayerInvContext(player, slot);
    }

    return null;
  }

  @Override
  public long extract(Context context, ExperienceKey what, long amount, Actionable mode) {
    var stackCopy = context.getStack().copy();
    stackCopy.setCount(1);
    var extracted = stackCopy.is(Items.EXPERIENCE_BOTTLE) ? getExperience(stackCopy) : Math.min(amount, getExperience(stackCopy));
    if (extracted > 0 && mode == Actionable.MODULATE) {
      if (stackCopy.is(Items.EXPERIENCE_BOTTLE))
        stackCopy = new ItemStack(Items.GLASS_BOTTLE);
      else
        changeExperience(-extracted, stackCopy);
      context.getStack().shrink(1);
      context.addOverflow(stackCopy);
    }
    return extracted;
  }

  @Override
  public long insert(Context context, ExperienceKey what, long amount, Actionable mode) {
    var stackCopy = context.getStack().copy();
    stackCopy.setCount(1);
    var inserted = stackCopy.is(Items.GLASS_BOTTLE) ? getCapacity(stackCopy) : Math.min(amount, ExperienceKey.MAX_EXPERIENCE - getExperience(stackCopy));
    if (inserted > 0 && mode == Actionable.MODULATE) {
      if (stackCopy.is(Items.GLASS_BOTTLE)) {
        stackCopy = new ItemStack(Items.EXPERIENCE_BOTTLE);
      } else {
        changeExperience(inserted, stackCopy);
      }
      context.getStack().shrink(1);
      context.addOverflow(stackCopy);
    }
    return inserted;
  }

  @Override
  public void playFillSound(Player player, ExperienceKey what) {
  }

  @Override
  public void playEmptySound(Player player, ExperienceKey what) {
  }

  @Override
  public @Nullable GenericStack getExtractableContent(Context context) {
    return getContainedStack(context.getStack());
  }

  public interface Context {
    ItemStack getStack();

    void addOverflow(ItemStack stack);
  }

  private record CarriedContext(Player player, AbstractContainerMenu menu) implements Context {
    @Override
    public ItemStack getStack() {
      return menu.getCarried();
    }

    @Override
    public void addOverflow(ItemStack stack) {
      if (menu.getCarried().isEmpty()) {
        menu.setCarried(stack);
      } else {
        player.getInventory().placeItemBackInInventory(stack);
      }
    }
  }

  private record PlayerInvContext(Player player, int slot) implements Context {
    @Override
    public ItemStack getStack() {
      return player.getInventory().getItem(slot);
    }

    @Override
    public void addOverflow(ItemStack stack) {
      player.getInventory().placeItemBackInInventory(stack);
    }
  }
}
