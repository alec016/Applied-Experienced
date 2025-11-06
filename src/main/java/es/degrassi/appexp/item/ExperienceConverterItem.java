package es.degrassi.appexp.item;

import es.degrassi.appexp.block.ExperienceConverterBlock;
import es.degrassi.appexp.definition.AExpComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.List;
import java.util.Optional;

public class ExperienceConverterItem extends BlockItem {
  public ExperienceConverterItem(ExperienceConverterBlock block, Properties properties) {
    super(
        block,
        properties.component(AExpComponents.EXPERIENCE_AMOUNT, 0L)
            .component(AExpComponents.IN_INV, ItemContainerContents.fromItems(List.of(ItemStack.EMPTY)))
            .component(AExpComponents.OUT_INV, ItemContainerContents.fromItems(List.of(ItemStack.EMPTY)))
    );
  }

  @Override
  public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    long amount = Optional.ofNullable(stack.get(AExpComponents.EXPERIENCE_AMOUNT)).orElse(0L);
    ItemContainerContents inContents = Optional.ofNullable(stack.get(AExpComponents.IN_INV)).orElse(ItemContainerContents.fromItems(List.of(ItemStack.EMPTY)));
    ItemContainerContents outContents = Optional.ofNullable(stack.get(AExpComponents.OUT_INV)).orElse(ItemContainerContents.fromItems(List.of(ItemStack.EMPTY)));

    tooltipComponents.add(
        Component.translatable(
            "appex.item.converter.tooltip",
            amount
        )
    );
    ItemStack s = ItemStack.EMPTY;
    if (inContents.getSlots() > 0) {
      s = inContents.getStackInSlot(0);
    }
    tooltipComponents.add(
        Component.translatable("appex.item.converter.inv.in",
            inContents.getSlots() > 0 ? s.getCount() + "x " + s.getHoverName().getString() : "Empty")
    );
    if (outContents.getSlots() > 0) {
      s = outContents.getStackInSlot(0);
    }
    tooltipComponents.add(
        Component.translatable("appex.item.converter.inv.out",
            outContents.getSlots() > 0 ? s.getCount() + "x " + s.getHoverName().getString() : "Empty")
    );
  }
}
