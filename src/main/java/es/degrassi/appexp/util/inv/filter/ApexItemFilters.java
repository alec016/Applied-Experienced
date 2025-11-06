package es.degrassi.appexp.util.inv.filter;

import appeng.api.inventories.InternalInventory;
import appeng.util.inv.filter.IAEItemFilter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Predicate;

public class ApexItemFilters {
  public static final IAEItemFilter INSERT_ONLY = new ApexItemFilters.InsertOnlyFilter(Items.EXPERIENCE_BOTTLE);
  public static final IAEItemFilter EXTRACT_ONLY = new ApexItemFilters.ExtractOnlyFilter(Items.GLASS_BOTTLE);

  private ApexItemFilters() {
  }

  private static class InsertOnlyFilter implements IAEItemFilter {
    private final Predicate<ItemStack> filter;
    protected InsertOnlyFilter(Item filter) {
      this.filter = stack -> stack.is(filter);
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

  private static class ExtractOnlyFilter implements IAEItemFilter {
    private final Predicate<ItemStack> filter;
    protected ExtractOnlyFilter(Item filter) {
      this.filter = stack -> stack.is(filter);
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
