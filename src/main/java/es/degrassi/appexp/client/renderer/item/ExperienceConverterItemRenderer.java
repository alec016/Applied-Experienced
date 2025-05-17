package es.degrassi.appexp.client.renderer.item;

import appeng.client.render.cablebus.CubeBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.MatrixUtil;
import es.degrassi.appexp.definition.AExpBlocks;
import es.degrassi.appexp.definition.AExpComponents;
import es.degrassi.appexp.definition.AExpConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.neoforged.neoforge.client.ClientHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

import static es.degrassi.experiencelib.util.ExperienceUtils.EXPERIENCE;

@ParametersAreNonnullByDefault
public class ExperienceConverterItemRenderer extends BlockEntityWithoutLevelRenderer {
  private static final Minecraft mc = Minecraft.getInstance();

  public ExperienceConverterItemRenderer() {
    super(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
  }

  public static final ExperienceConverterItemRenderer RENDERER = new ExperienceConverterItemRenderer();

  @Override
  public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
    //Note: We don't need to register this as a reload listener as we don't have an in code model or make use of this
    // reload in any way
  }

  @Override
  public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
    var m = Minecraft.getInstance().getBlockRenderer()
        .getBlockModel(AExpBlocks.EXPERIENCE_CONVERTER.block().defaultBlockState());

    poseStack.pushPose();
    poseStack.translate(0.5, 0.5, 0.5);
    m = ClientHooks.handleCameraTransforms(poseStack, m, displayContext, false);
    poseStack.translate(-0.5F, -0.5F, -0.5F);
    boolean flag1;
    if (displayContext != ItemDisplayContext.GUI && !displayContext.firstPerson() && stack.getItem() instanceof BlockItem blockitem) {
      Block block = blockitem.getBlock();
      flag1 = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
    } else {
      flag1 = true;
    }

    for (var model : m.getRenderPasses(stack, flag1)) {
      for (var rendertype : model.getRenderTypes(stack, flag1)) {
        VertexConsumer vertexconsumer;
        if (hasAnimatedTexture(stack) && stack.hasFoil()) {
          PoseStack.Pose posestack$pose = poseStack.last().copy();
          if (displayContext == ItemDisplayContext.GUI) {
            MatrixUtil.mulComponentWise(posestack$pose.pose(), 0.5F);
          } else if (displayContext.firstPerson()) {
            MatrixUtil.mulComponentWise(posestack$pose.pose(), 0.75F);
          }

          vertexconsumer = ItemRenderer.getCompassFoilBuffer(buffer, rendertype, posestack$pose);
        } else if (flag1) {
          vertexconsumer = ItemRenderer.getFoilBufferDirect(buffer, rendertype, true, stack.hasFoil());
        } else {
          vertexconsumer = ItemRenderer.getFoilBuffer(buffer, rendertype, true, stack.hasFoil());
        }

        mc.getItemRenderer().renderModelLists(model, stack, light, overlay, poseStack, vertexconsumer);
      }
    }

    float percent = (float) stack.getOrDefault(AExpComponents.EXPERIENCE_AMOUNT, 0L)
        / AExpConfig.get().XP_CONVERTER_CAPACITY.get();

    if (percent <= 0f) {
      poseStack.popPose();
      return;
    }

    renderExperienceFill(percent, poseStack, buffer, light, overlay);
    poseStack.popPose();
  }

  private void renderExperienceFill(float percent, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
    var vertexConsumer = buffer.getBuffer(RenderType.translucentMovingBlock());
    var fill = Mth.lerp(Mth.clamp(percent, 0f, 1f), 2 / 16F, 12 / 16F);

    var builder = new CubeBuilder();
    float y2 = Math.min(fill * 16, Math.max(fill * 16 - 1/2048f, 0));

    builder.setTexture(EXPERIENCE.sprite());
    builder.addCube(1 + 1/2048f,  1/2048f, 1 + 1/2048f, 15 - 1/2048f, y2, 15 - 1/2048f);

    for (var bakedQuad : builder.getOutput()) {
      vertexConsumer.putBulkData(poseStack.last(), bakedQuad, 1f, 1f, 1f, 1f, light, overlay);
    }
  }

  private static boolean hasAnimatedTexture(ItemStack stack) {
    return stack.is(ItemTags.COMPASSES) || stack.is(Items.CLOCK);
  }
}
