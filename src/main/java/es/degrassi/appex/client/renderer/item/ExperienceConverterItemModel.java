package es.degrassi.appex.client.renderer.item;

import appeng.client.render.CubeBuilder;
import appeng.client.render.ItemBaseModelWrapper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.serialization.MapCodec;
import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.definition.AExpConfig;
import es.degrassi.appex.item.ExperienceConverterItem;
import es.degrassi.experiencelib.common.ExperienceLibRegistration;
import es.degrassi.experiencelib.util.ClientExperienceUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class ExperienceConverterItemModel implements ItemModel {
  private static final Identifier MODEL_BASE = AppliedExperienced.id("item/experience_converter");
  private static final Identifier MODEL_BASE_BLOCK = AppliedExperienced.id("block/experience_converter");

  private final ItemBaseModelWrapper baseModel;
  private final ResolvedModel model;
  private final ModelBaker baker;
  private final ItemModel missingItemModel;

  public ExperienceConverterItemModel(ItemBaseModelWrapper baseModel,
                                      ResolvedModel model,
                                      ModelBaker baker,
                                      ItemModel missingItemModel) {
    this.baseModel = baseModel;
    this.model = model;
    this.baker = baker;
    this.missingItemModel = missingItemModel;
  }

  @Override
  public void update(ItemStackRenderState itemStackRenderState, ItemStack itemStack, ItemModelResolver itemModelResolver,
                     ItemDisplayContext itemDisplayContext, @Nullable ClientLevel clientLevel, @Nullable ItemOwner itemOwner, int seed) {
    if (!(itemStack.getItem() instanceof ExperienceConverterItem item)) {
      missingItemModel.update(itemStackRenderState, itemStack, itemModelResolver, itemDisplayContext, clientLevel, itemOwner, seed);
      return;
    }

    var xpLayer = itemStackRenderState.newLayer();
    var baseLayer = itemStackRenderState.newLayer();
    var xpSpecial = new ExperienceConverterSpecialRender();
    var argument = xpSpecial.extractArgument(itemStack);
    xpLayer.setupSpecialModel(xpSpecial, argument);
    xpLayer.setExtents(baseModel.extents());
    if (argument != null)
      itemStackRenderState.appendModelIdentityElement(argument);
    baseLayer.setupSpecialModel(new NoDataSpecialModelRenderer() {
      @Override
      public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                         int lightCoords, int overlayCoords, boolean hasFoil, final int outlineColor) {
        var qi = new QuadInstance();
        qi.setOverlayCoords(overlayCoords);
        qi.setLightCoords(lightCoords);
        qi.setColor(0xffffffff);
        submitNodeCollector.submitCustomGeometry(
            poseStack,
            RenderTypes.translucentMovingBlock(),
            (pose, buffer) -> model.bakeTopGeometry(
                model.getTopTextureSlots(),
                baker,
                BlockModelRotation.IDENTITY
            ).getAll().forEach(baked -> buffer.putBakedQuad(pose, baked, qi))
        );
      }

      @Override
      public void getExtents(Consumer<Vector3fc> extents) {
        for (var point : baseModel.extents().get()) {
          extents.accept(point);
        }
      }
    }, null);
    baseLayer.setExtents(baseModel.extents());
    baseModel.renderProperties().applyToLayer(baseLayer, itemDisplayContext);
    baseModel.renderProperties().applyToLayer(xpLayer, itemDisplayContext);
    itemStackRenderState.appendModelIdentityElement(this);
  }

  public class ExperienceConverterSpecialRender implements SpecialModelRenderer<Float> {
    @Override
    public void submit(Float amount, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                       int lightCoords, int overlayCoords, boolean hasFoil, final int outlineColor) {
      if (amount == null || amount <= 0) return;
      var qi = new QuadInstance();
      qi.setOverlayCoords(overlayCoords);
      qi.setLightCoords(lightCoords);
      qi.setColor(0xffffffff);
      var fill = Mth.lerp(amount, 2 / 16F, 12 / 16F);
      float y2 = Math.clamp(fill * 16 - 1 / 2048f, 0, fill * 16);

      submitNodeCollector.submitCustomGeometry(
          poseStack,
          RenderTypes.entityCutout(ClientExperienceUtils.EXPERIENCE.sprite().atlasLocation()),
          (pose, buffer) -> {
            var builder = new CubeBuilder(quad -> buffer.putBakedQuad(pose, quad, qi));
            builder.setTexture(ClientExperienceUtils.EXPERIENCE.sprite());
            builder.addCube(1 + 1/2048f,  1/2048f, 1 + 1/2048f, 15 - 1/2048f, y2, 15 - 1/2048f);
          }
      );
    }

    @Override
    public void getExtents(Consumer<Vector3fc> extents) {
      for (var point : baseModel.extents().get()) {
        extents.accept(point);
      }
    }

    @Override
    public @Nullable Float extractArgument(ItemStack itemStack) {
      if (!(itemStack.getItem() instanceof ExperienceConverterItem)) return null;
      if (!itemStack.has(ExperienceLibRegistration.EXPERIENCE_COMPONENT.get())) return null;
      float amount = itemStack.getOrDefault(ExperienceLibRegistration.EXPERIENCE_COMPONENT.get(), 0L);
      return amount / AExpConfig.get().XP_CONVERTER_CAPACITY.get();
    }
  }

  public record Unbaked() implements ItemModel.Unbaked {
    public static final Identifier ID = AppliedExperienced.id("xp_converter");

    public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

    @Override
    public void resolveDependencies(Resolver resolver) {
      resolver.markDependency(MODEL_BASE);
    }

    @Override
    public ItemModel bake(BakingContext bakingContext, Matrix4fc matrix4fc) {
      return new ExperienceConverterItemModel(
          ItemBaseModelWrapper.bake(bakingContext.blockModelBaker(), MODEL_BASE, matrix4fc),
          bakingContext.blockModelBaker().getModel(MODEL_BASE_BLOCK),
          bakingContext.blockModelBaker(),
          bakingContext.missingItemModel()
      );
    }

    @Override
    public MapCodec<Unbaked> type() {
      return MAP_CODEC;
    }
  }
}
