package es.degrassi.appexp.client.renderer.entity;

import appeng.client.render.cablebus.CubeBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import es.degrassi.appexp.block.entity.ExperienceConverterEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;

import javax.annotation.ParametersAreNonnullByDefault;

import static es.degrassi.experiencelib.util.ExperienceUtils.EXPERIENCE;

@ParametersAreNonnullByDefault
public class ExperienceConverterEntityRenderer implements BlockEntityRenderer<ExperienceConverterEntity> {
  public ExperienceConverterEntityRenderer(BlockEntityRendererProvider.Context context) {}

  @Override
  public void render(ExperienceConverterEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
    var amount = blockEntity.getFillState();

    if (amount <= 0) {
      return;
    }

    var vertexConsumer = buffer.getBuffer(RenderType.translucentMovingBlock());
    var fill = Mth.lerp(Mth.clamp(amount, 0, 1), 2 / 16F, 12 / 16F);

    var builder = new CubeBuilder();
    float y2 = Math.min(fill * 16, Math.max(fill * 16 - 1/2048f, 0));
    builder.setTexture(EXPERIENCE.sprite());
    builder.addCube(1 + 1/2048f,  1/2048f, 1 +  + 1/2048f, 15 - 1/2048f, y2, 15 - 1/2048f);

    for (var bakedQuad : builder.getOutput()) {
      vertexConsumer.putBulkData(poseStack.last(), bakedQuad, 1, 1, 1, 1, packedLight, packedOverlay);
    }
  }
}
