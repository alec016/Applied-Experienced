package es.degrassi.appexp.item;

import es.degrassi.appexp.block.ExperienceConverterBlock;
import es.degrassi.appexp.definition.AExpComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Optional;

public class ExperienceConverterItem extends BlockItem {
  public ExperienceConverterItem(ExperienceConverterBlock block, Properties properties) {
    super(
        block,
        properties.component(AExpComponents.EXPERIENCE_AMOUNT, 0L)
    );
  }

  @Override
  public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    long amount = Optional.ofNullable(stack.get(AExpComponents.EXPERIENCE_AMOUNT)).orElse(0L);

    tooltipComponents.add(
        Component.translatable(
            "appex.item.converter.tooltip",
            amount
        )
    );
  }
}
