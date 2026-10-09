package es.degrassi.appex.client;

import appeng.client.api.AEKeyRenderer;
import appeng.client.gui.style.Blitter;
import appeng.util.Platform;
import com.mojang.blaze3d.vertex.PoseStack;
import es.degrassi.appex.me.key.ExperienceKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static es.degrassi.experiencelib.util.ClientExperienceUtils.EXPERIENCE;

public final class AExpStackRenderer implements AEKeyRenderer<ExperienceKey, AExpStackRenderer.RenderState> {
  @Override
  public void drawInGui(Minecraft minecraft, GuiGraphicsExtractor guiGraphics, int x, int y, ExperienceKey what) {
    Blitter.sprite(EXPERIENCE.sprite())
        .blending(false)
        .dest(x, y, 16, 16)
        .blit(guiGraphics);
  }

  @Override
  public Class<RenderState> stateClass() {
    return RenderState.class;
  }

  @Override
  public RenderState createState() {
    return new RenderState();
  }

  @Override
  public void extract(RenderState state, ExperienceKey what, @Nullable Level level, int seed) {

  }

  @Override
  public void submit(PoseStack poseStack, RenderState state, SubmitNodeCollector nodes, int lightCoords) {
    var sprite = EXPERIENCE.sprite();
    var color = 0xffffff;

    poseStack.pushPose();
    // Push it out of the block face a bit to avoid z-fighting
    poseStack.translate(0, 0, 0.01f);

    // y is flipped here
    var x0 = -1 / 2;
    var y0 = 1 / 2;
    var x1 = 1 / 2;
    var y1 = -1 / 2;

    nodes.submitCustomGeometry(poseStack, RenderTypes.entitySolid(sprite.atlasLocation()), (pose, buffer) -> {
      buffer.addVertex(pose, x0, y1, 0)
          .setColor(color)
          .setUv(sprite.getU0(), sprite.getV1())
          .setOverlay(OverlayTexture.NO_OVERLAY)
          .setLight(lightCoords)
          .setNormal(0, 0, 1);
      buffer.addVertex(pose, x1, y1, 0)
          .setColor(color)
          .setUv(sprite.getU1(), sprite.getV1())
          .setOverlay(OverlayTexture.NO_OVERLAY)
          .setLight(lightCoords)
          .setNormal(0, 0, 1);
      buffer.addVertex(pose, x1, y0, 0)
          .setColor(color)
          .setUv(sprite.getU1(), sprite.getV0())
          .setOverlay(OverlayTexture.NO_OVERLAY)
          .setLight(lightCoords)
          .setNormal(0, 0, 1);
      buffer.addVertex(pose, x0, y0, 0)
          .setColor(color)
          .setUv(sprite.getU0(), sprite.getV0())
          .setOverlay(OverlayTexture.NO_OVERLAY)
          .setLight(lightCoords)
          .setNormal(0, 0, 1);
    });

    poseStack.popPose();
  }

  @Override
  public List<Component> getTooltip(ExperienceKey stack) {
    var tooltip = new ArrayList<Component>();
    tooltip.add(stack.getDisplayName());

    // Heuristic: If the last line doesn't include the modname, add it ourselves
    var modName = Platform.formatModName(stack.getModId());
    if (!tooltip.getLast().getString().equals(modName)) {
      tooltip.add(Component.literal(modName));
    }

    return tooltip;
  }

  public static final class RenderState {}
}
