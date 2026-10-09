package es.degrassi.appex.client.renderer.entity;

import appeng.client.render.CubeBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import es.degrassi.appex.block.entity.ExperienceConverterEntity;
import es.degrassi.experiencelib.util.ClientExperienceUtils;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class ExperienceConverterEntityRenderer implements BlockEntityRenderer<ExperienceConverterEntity, ExperienceConverterEntityRenderState> {
  public ExperienceConverterEntityRenderer(BlockEntityRendererProvider.Context context) {}

  @Override
  public ExperienceConverterEntityRenderState createRenderState() {
    return new ExperienceConverterEntityRenderState();
  }

  @Override
  public void extractRenderState(ExperienceConverterEntity blockEntity, ExperienceConverterEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
    BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
    state.setEntity(blockEntity);
  }

  @Override
  public void submit(ExperienceConverterEntityRenderState state, PoseStack poseStack,
                     SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
    if (state.getFillState() <= 0) return;
    poseStack.pushPose();
    submitNodeCollector.submitCustomGeometry(
        poseStack,
        RenderTypes.entityCutout(ClientExperienceUtils.EXPERIENCE.sprite().atlasLocation()),
        (pose, buffer) -> {
          var amount = state.getFillState();
          var quadInstance = new QuadInstance();
          quadInstance.setLightCoords(state.lightCoords);
          var fill = Mth.lerp(Mth.clamp(amount, 0, 1), 2 / 16F, 12 / 16F);
          float y2 = Math.clamp(fill * 16 - 1 / 2048f, 0, fill * 16);
          var builder = new CubeBuilder(quad -> buffer.putBakedQuad(pose, quad, quadInstance));
          builder.setTexture(ClientExperienceUtils.EXPERIENCE.sprite());
          builder.addCube(1 + 1/2048f,  1/2048f, 1 + 1/2048f, 15 - 1/2048f, y2, 15 - 1/2048f);
        }
    );
    poseStack.popPose();
  }
}
