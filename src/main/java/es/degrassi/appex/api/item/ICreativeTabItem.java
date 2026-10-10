package es.degrassi.appex.api.item;

import net.minecraft.world.item.CreativeModeTab;

public interface ICreativeTabItem {
  void addToMainCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output);
}
