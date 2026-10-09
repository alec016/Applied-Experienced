package es.degrassi.appex.data.models;

import appeng.core.AppEng;
import appeng.datagen.providers.models.PartModelOutput;
import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.definition.AExpBlocks;
import es.degrassi.appex.definition.AExpItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;

public final class BlockModelProvider extends ModelSubProvider {
  private static final TextureSlot CELL = TextureSlot.create("cell");

  public BlockModelProvider(BlockModelGenerators blockModels, ItemModelGenerators itemModels, PartModelOutput partModels) {
    super(blockModels, itemModels, partModels);
  }

  @Override
  protected void register() {
    this.blockStateOutput.accept(createSimpleBlock(AExpBlocks.EXPERIENCE_CONVERTER,
        BlockModelGenerators.plainVariant(AExpBlocks.EXPERIENCE_CONVERTER.id().withPrefix("block/"))));
    simpleBlockAndItem(AExpBlocks.EXPERIENCE_ACCEPTOR);

    withParent(AExpBlocks.EXPERIENCE_CONVERTER.id().withPrefix("block/"))
        .create(AExpBlocks.EXPERIENCE_CONVERTER.asItem(), TextureMapping.particle(AExpBlocks.EXPERIENCE_CONVERTER.block()), modelOutput);

    AExpItems.getCells().forEach(cell -> {
      withParent(AppEng.makeId("block/drive_%s_item_cell".formatted(cell.get().getTier().namePrefix())), CELL)
          .create(
              AppliedExperienced.id("block/drive_%s_experience_cell".formatted(cell.get().getTier().namePrefix())),
              TextureMapping.particle(makeAEMaterial("block/drive/drive_cells"))
                .put(CELL, new Material(AppliedExperienced.id("block/drive/drive_cells"))),
              modelOutput
          );
    });
  }
}
