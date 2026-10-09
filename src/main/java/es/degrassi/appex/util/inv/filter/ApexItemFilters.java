package es.degrassi.appex.util.inv.filter;

import appeng.api.inventories.InternalInventory;
import appeng.util.inv.filter.IAEItemFilter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApexItemFilters {
  public static final IAEItemFilter INSERT_ONLY_XP = new InsertOnlyFilter(Items.EXPERIENCE_BOTTLE);
  public static final IAEItemFilter EXTRACT_ONLY_GLASS = new ExtractOnlyFilter(Items.GLASS_BOTTLE);
  public static final IAEItemFilter EXTRACT_ONLY_XP = new ExtractOnlyFilter(Items.EXPERIENCE_BOTTLE);
  public static final IAEItemFilter INSERT_ONLY_GLASS = new InsertOnlyFilter(Items.GLASS_BOTTLE);

  private record InsertOnlyFilter(Predicate<ItemStack> filter) implements IAEItemFilter {
    private InsertOnlyFilter(Item filter) {
        this(stack -> stack.is(filter));
      }

    @Override
    public boolean allowExtract(InternalInventory inv, int slot, int amount) {
      return filter.test(inv.getStackInSlot(slot));
    }

    @Override
    public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
      return filter.test(stack);
    }
  }

  private record ExtractOnlyFilter(Predicate<ItemStack> filter) implements IAEItemFilter {
    private ExtractOnlyFilter(Item filter) {
        this(stack -> stack.is(filter));
      }

    @Override
    public boolean allowExtract(InternalInventory inv, int slot, int amount) {
      return filter.test(inv.getStackInSlot(slot));
    }

    @Override
    public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
        return filter.test(stack);
      }
  }

}
