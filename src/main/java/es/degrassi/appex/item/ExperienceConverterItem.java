package es.degrassi.appex.item;

import es.degrassi.appex.block.ExperienceConverterBlock;
import es.degrassi.appex.definition.AExpComponents;
import es.degrassi.appex.definition.AExpText;
import es.degrassi.experiencelib.common.ExperienceLibRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class ExperienceConverterItem extends BlockItem {
  public ExperienceConverterItem(ExperienceConverterBlock block, Properties properties) {
    super(
        block,
        properties.component(ExperienceLibRegistration.EXPERIENCE_COMPONENT, 0L)
            .component(AExpComponents.IN_INV, ItemContainerContents.fromItems(List.of(ItemStack.EMPTY)))
            .component(AExpComponents.OUT_INV, ItemContainerContents.fromItems(List.of(ItemStack.EMPTY)))
    );
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
    super.appendHoverText(stack, context, display, builder, tooltipFlag);
    long amount = Optional.ofNullable(stack.get(ExperienceLibRegistration.EXPERIENCE_COMPONENT)).orElse(0L);
    ItemContainerContents inContents = Optional.ofNullable(stack.get(AExpComponents.IN_INV)).orElse(ItemContainerContents.fromItems(List.of(ItemStack.EMPTY)));
    ItemContainerContents outContents = Optional.ofNullable(stack.get(AExpComponents.OUT_INV)).orElse(ItemContainerContents.fromItems(List.of(ItemStack.EMPTY)));

    builder.accept(AExpText.CONVERTER_TOOLTIP.text(amount).withStyle(ChatFormatting.DARK_GRAY));
    ItemStack s = ItemStack.EMPTY;
    if (inContents.getSlots() > 0) {
      s = inContents.getStackInSlot(0);
    }
    builder.accept(AExpText.CONVERTER_INV.text(inContents.getSlots() > 0 ? s.getCount() + "x " + s.getHoverName().getString() : "Empty").withStyle(ChatFormatting.DARK_GRAY));
    if (outContents.getSlots() > 0) {
      s = outContents.getStackInSlot(0);
    }
    builder.accept(AExpText.CONVERTER_INV.text(outContents.getSlots() > 0 ? s.getCount() + "x " + s.getHoverName().getString() : "Empty").withStyle(ChatFormatting.DARK_GRAY));
  }
}
