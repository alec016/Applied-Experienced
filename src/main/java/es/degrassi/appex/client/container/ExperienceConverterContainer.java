package es.degrassi.appex.client.container;

import es.degrassi.appex.block.entity.ExperienceConverterEntity;
import es.degrassi.appex.definition.AExpMenus;
import net.minecraft.world.entity.player.Inventory;

public class ExperienceConverterContainer extends ExperienceContainer<ExperienceConverterEntity> {
  public ExperienceConverterContainer(int id, Inventory playerInventory, ExperienceConverterEntity host) {
    super(AExpMenus.EXPERIENCE_CONVERTER, id, playerInventory.player.getInventory(), host);
  }
}
