package es.degrassi.appex.client.renderer.entity;

import es.degrassi.appex.block.entity.ExperienceConverterEntity;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

@Setter
public class ExperienceConverterEntityRenderState extends BlockEntityRenderState {
  private ExperienceConverterEntity entity;
  public float getFillState() {
    if (entity == null) return 0;
    return entity.getFillState();
  }
}
