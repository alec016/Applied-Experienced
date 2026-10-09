package es.degrassi.appex.block;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseEntityBlock;
import es.degrassi.appex.block.entity.ExperienceAcceptorEntity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class ExperienceAcceptorBlock extends AEBaseEntityBlock<ExperienceAcceptorEntity> {
  public ExperienceAcceptorBlock(Properties props) {
    super(AEBaseBlock.defaultProps(props, MapColor.METAL, SoundType.METAL));
  }
}
