package es.degrassi.appexp.item;

import appeng.items.AEBaseItem;
import net.minecraft.world.item.Rarity;

public class CreativeExperienceCellItem extends AEBaseItem {
  public CreativeExperienceCellItem(Properties properties) {
    super(properties.stacksTo(1).rarity(Rarity.EPIC));
  }
}
