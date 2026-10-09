package es.degrassi.appex.data.models;

import appeng.api.util.AEColor;
import appeng.api.util.AEColorVariant;
import appeng.client.api.model.parts.StaticPartModel;
import appeng.client.model.P2PFrequencyPartModel;
import appeng.client.model.StatusIndicatorPartModel;
import appeng.client.render.AEColorItemTintSource;
import appeng.core.definitions.ItemDefinition;
import appeng.datagen.providers.models.PartModelOutput;
import appeng.items.parts.ColoredPartItem;
import appeng.items.parts.PartItem;
import com.google.common.collect.Maps;
import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.definition.AExpBlocks;
import es.degrassi.appex.definition.AExpParts;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

import static appeng.core.AppEng.makeId;

public class PartModelProvider extends ModelSubProvider {

  public static final TextureSlot BASE = TextureSlot.create("base");
  public static final TextureSlot TYPE = TextureSlot.create("type");
  private static final Map<ItemDefinition<?>, Boolean> usesPartAsItem = Maps.newHashMap();

  public PartModelProvider(BlockModelGenerators blockModels, ItemModelGenerators itemModels, PartModelOutput partModels) {
    super(blockModels, itemModels, partModels);
  }

  @Override
  protected void register() {
    registerP2PTunnels();

    partModels.staticModel(AExpParts.EXPERIENCE_ACCEPTOR, AppliedExperienced.id("part/experience_acceptor"));
    customPartItemModel(AExpParts.EXPERIENCE_ACCEPTOR.asItem());
    createPart("ae2:energy_acceptor", TextureSlot.FRONT).create(
        AExpBlocks.EXPERIENCE_ACCEPTOR.id().withPrefix("part/"),
        new TextureMapping()
            .put(TextureSlot.FRONT, makeMaterial(AExpBlocks.EXPERIENCE_ACCEPTOR.id().withPrefix("block/").getPath())),
        modelOutput
    );
  }

  private void customPartItemModel(PartItem<?> item) {
    var color = AEColor.TRANSPARENT;
    if (item instanceof ColoredPartItem<?> coloredPartItem) {
      color = coloredPartItem.getColor();
    }

    itemModels.itemModelOutput.accept(
        item.asItem(),
        ItemModelUtils.tintedModel(
            ModelLocationUtils.getModelLocation(item),
            new Constant(-1),
            new AEColorItemTintSource(color, AEColorVariant.DARK),
            new AEColorItemTintSource(color, AEColorVariant.MEDIUM),
            new AEColorItemTintSource(color, AEColorVariant.BRIGHT),
            new AEColorItemTintSource(color, AEColorVariant.MEDIUM_BRIGHT)));
  }

  private ModelTemplate createPart(String id, TextureSlot...requiredSlots) {
    return createPart(id, null, requiredSlots);
  }

  private ModelTemplate createPart(String id, @Nullable String suffix, TextureSlot...requiredSlots) {
    return new ModelTemplate(Optional.of(Identifier.parse(id).withPrefix("part/")), Optional.ofNullable(suffix), requiredSlots);
  }

  private void registerP2PTunnels() {
    Map<ItemDefinition<?>, String> tunnels = Map.of(
        AExpParts.EXPERIENCE_P2P_TUNNEL, "part/p2p_tunnel_experience"
    );

    for (var entry : tunnels.entrySet()) {
      var model = ModelTemplates.createItem("ae2:p2p_tunnel_base", TYPE).create(
          entry.getKey().asItem(),
          new TextureMapping().put(TYPE, makeMaterial(entry.getValue())),
          modelOutput);
      var usePartAsItemModel = usesPartAsItem.getOrDefault(entry.getKey(), false);
      if (usePartAsItemModel) usePartModelAsItemModel(entry.getKey());
      else blockModels.registerSimpleItemModel(entry.getKey().asItem(), model);

      createPart("ae2:p2p_tunnel_base", TYPE).create(
          entry.getKey().id().withPrefix("part/"),
          new TextureMapping().put(TYPE, makeMaterial(entry.getValue())),
          modelOutput
      );

      partModels.composite(
          entry.getKey(),
          new StaticPartModel.Unbaked(entry.getKey().id().withPrefix("part/")),
          new P2PFrequencyPartModel.Unbaked(),
          partStatusIndicator(makeId("part/p2p_tunnel_status")));

    }
  }

  private void usePartModelAsItemModel(ItemDefinition<?> partItem) {
    blockModels.registerSimpleItemModel(partItem.asItem(), partItem.id().withPrefix("part/"));
  }

  private static StatusIndicatorPartModel.Unbaked partStatusIndicator(Identifier baseModel) {
    return new StatusIndicatorPartModel.Unbaked(
        baseModel.withSuffix("_has_channel"),
        baseModel.withSuffix("_on"),
        baseModel.withSuffix("_off"));
  }
}
