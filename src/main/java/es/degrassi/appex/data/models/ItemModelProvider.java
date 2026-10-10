package es.degrassi.appex.data.models;

import appeng.client.item.PortableCellColorTintSource;
import appeng.client.item.StorageCellStateTintSource;
import appeng.core.AppEng;
import appeng.core.definitions.ItemDefinition;
import appeng.datagen.providers.models.PartModelOutput;
import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.client.renderer.item.ExperienceConverterItemModel;
import es.degrassi.appex.definition.AExpBlocks;
import es.degrassi.appex.definition.AExpItems;
import es.degrassi.appex.definition.AExpParts;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;

public class ItemModelProvider extends ModelSubProvider {
  public ItemModelProvider(BlockModelGenerators blockModels, ItemModelGenerators itemModels, PartModelOutput partModels) {
    super(blockModels, itemModels, partModels);
  }

  private static final TextureSlot P2P_TYPE = TextureSlot.create("type");
  private static final TextureSlot FRONT = TextureSlot.create("front");
  private static final TextureSlot FRONT_BRIGHT = TextureSlot.create("front_bright");
  private static final TextureSlot FRONT_MEDIUM = TextureSlot.create("front_medium");
  private static final TextureSlot FRONT_DARK = TextureSlot.create("front_dark");

  @Override
  protected void register() {
    builtInItemModel(AExpBlocks.EXPERIENCE_CONVERTER.item(), new ExperienceConverterItemModel.Unbaked());
    flatSingleLayer(AExpItems.EXPERIENCE_CELL_HOUSING, "item/%s".formatted(AExpItems.EXPERIENCE_CELL_HOUSING.id().getPath()));
    AExpItems.getCells().forEach(cell -> {
      storageCell(cell, "item/experience_storage_cell_" + cell.asItem().getTier().namePrefix());
    });
    AExpItems.getPortables().forEach(cell -> {
      portableCell(cell, "experience", cell.asItem().getTier().namePrefix());
    });
    withParent(AppEng.makeId("item/cable_energy_acceptor"), FRONT)
        .create(AExpParts.EXPERIENCE_ACCEPTOR.asItem(),
            TextureMapping.particle(AExpBlocks.EXPERIENCE_ACCEPTOR.block())
                .put(FRONT, makeMaterial("block/experience_acceptor")), modelOutput);
    withParent(AppEng.makeId("item/display_base"), FRONT, FRONT_BRIGHT, FRONT_MEDIUM, FRONT_DARK)
        .create(AExpParts.EXPERIENCE_ACCESS_TERMINAL.asItem(),
            TextureMapping.particle(new Material(AExpParts.EXPERIENCE_ACCESS_TERMINAL.id().withPrefix("item")))
                .put(FRONT, makeMaterial("part/experience_access_terminal"))
                .put(FRONT_BRIGHT, makeMaterial("part/experience_access_terminal_bright"))
                .put(FRONT_MEDIUM, makeMaterial("part/experience_access_terminal_medium"))
                .put(FRONT_DARK, makeMaterial("part/experience_access_terminal_dark")),
            modelOutput);
    flatSingleLayer(AExpItems.EXPERIENCE_TERMINAL, "item/wireless_experience_access_terminal");
  }

  private void flatSingleLayer(ItemLike item, String texture) {
    var model = ModelTemplates.FLAT_ITEM.create(item.asItem(), TextureMapping.layer0(new Material(makeId(texture))),
        itemModels.modelOutput);
    itemModels.itemModelOutput.accept(item.asItem(), ItemModelUtils.plainModel(model));
  }

  private void builtInItemModel(ItemLike item, ItemModel.Unbaked unbaked) {
    blockModels.itemModelOutput.accept(item.asItem(), unbaked);
  }

  private void storageCell(ItemDefinition<?> item, String background) {
    var model = itemModels.generateLayeredItem(
        item.asItem(),
        new Material(makeId(background)),
        new Material(makeAEId("item/storage_cell_led")));
    itemModels.itemModelOutput.accept(item.asItem(), ItemModelUtils.tintedModel(
        model,
        new Constant(-1),
        new StorageCellStateTintSource()));
  }

  public static final TextureSlot LAYER3 = TextureSlot.create("layer3");
  private static final ModelTemplate FOUR_LAYERED_ITEM = ModelTemplates.createItem("generated", TextureSlot.LAYER0,
      TextureSlot.LAYER1, TextureSlot.LAYER2, LAYER3);

  private void portableCell(ItemDefinition<?> item, String housingType, String tier) {

    var model = FOUR_LAYERED_ITEM.create(
        item.asItem(),
        TextureMapping.layered(
                new Material(makeId("item/portable_cell_%s_housing".formatted(housingType))),
                new Material(makeAEId("item/portable_cell_led")),
                new Material(makeAEId("item/portable_cell_screen")))
            .put(LAYER3, new Material(makeAEId("item/portable_cell_side_%s".formatted(tier)))),
        itemModels.modelOutput);
    itemModels.itemModelOutput.accept(item.asItem(), ItemModelUtils.tintedModel(
        model,
        new Constant(-1),
        new StorageCellStateTintSource(),
        new PortableCellColorTintSource()));
  }

  private static Identifier makeAEId(String id) {
    return id.contains(":") ? Identifier.parse(id) : AppEng.makeId(id);
  }

  private static Identifier makeId(String id) {
    return id.contains(":") ? Identifier.parse(id) : AppliedExperienced.id(id);
  }
}
