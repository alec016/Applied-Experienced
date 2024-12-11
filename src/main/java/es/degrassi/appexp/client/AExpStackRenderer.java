package es.degrassi.appexp.client;

import appeng.api.client.AEKeyRenderHandler;
import appeng.client.gui.style.Blitter;
import com.mojang.blaze3d.vertex.PoseStack;
import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.appexp.me.key.ExperienceKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;

public final class AExpStackRenderer implements AEKeyRenderHandler<ExperienceKey> {
  public static final Material EXPERIENCE = new Material(
      InventoryMenu.BLOCK_ATLAS, AppliedExperienced.id("block/experience"));

  @Override
  public void drawInGui(Minecraft minecraft, GuiGraphics guiGraphics, int x, int y, ExperienceKey what) {

    Blitter.sprite(EXPERIENCE.sprite())
        .blending(false)
        .dest(x, y, 16, 16)
        .blit(guiGraphics);
  }

  @Override
  public void drawOnBlockFace(PoseStack poseStack, MultiBufferSource buffers, ExperienceKey what, float scale, int combinedLight, Level level) {
    var sprite = EXPERIENCE.sprite();
    var color = 0xffffff;

    poseStack.pushPose();
    // Push it out of the block face a bit to avoid z-fighting
    poseStack.translate(0, 0, 0.01f);

    var buffer = buffers.getBuffer(RenderType.solid());

    // In comparison to items, make it _slightly_ smaller because item icons
    // usually don't extend to the full size.
    scale -= 0.05f;

    // y is flipped here
    var x0 = -scale / 2;
    var y0 = scale / 2;
    var x1 = scale / 2;
    var y1 = -scale / 2;

    var transform = poseStack.last().pose();
    buffer.addVertex(transform, x0, y1, 0)
        .setColor(color)
        .setUv(sprite.getU0(), sprite.getV1())
        .setOverlay(OverlayTexture.NO_OVERLAY)
        .setLight(combinedLight)
        .setNormal(0, 0, 1);
    buffer.addVertex(transform, x1, y1, 0)
        .setColor(color)
        .setUv(sprite.getU1(), sprite.getV1())
        .setOverlay(OverlayTexture.NO_OVERLAY)
        .setLight(combinedLight)
        .setNormal(0, 0, 1);
    buffer.addVertex(transform, x1, y0, 0)
        .setColor(color)
        .setUv(sprite.getU1(), sprite.getV0())
        .setOverlay(OverlayTexture.NO_OVERLAY)
        .setLight(combinedLight)
        .setNormal(0, 0, 1);
    buffer.addVertex(transform, x0, y0, 0)
        .setColor(color)
        .setUv(sprite.getU0(), sprite.getV0())
        .setOverlay(OverlayTexture.NO_OVERLAY)
        .setLight(combinedLight)
        .setNormal(0, 0, 1);
    poseStack.popPose();
  }

  @Override
  public Component getDisplayName(ExperienceKey stack) {
    return stack.getDisplayName();
  }
}
