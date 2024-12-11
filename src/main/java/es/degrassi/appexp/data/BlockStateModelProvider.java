package es.degrassi.appexp.data;

import appeng.core.AppEng;
import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.appexp.definition.AExpBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

class BlockStateModelProvider extends BlockStateProvider {
    BlockStateModelProvider(PackOutput output, ExistingFileHelper existing) {
        super(output, AppliedExperienced.MODID, existing);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(AExpBlocks.EXPERIENCE_ACCEPTOR.block(), cubeAll(AExpBlocks.EXPERIENCE_ACCEPTOR.block()));

        var experienceTankModel = models().getExistingFile(AExpBlocks.EXPERIENCE_CONVERTER.id());
        getVariantBuilder(AExpBlocks.EXPERIENCE_CONVERTER.block())
            .setModels(getVariantBuilder(AExpBlocks.EXPERIENCE_CONVERTER.block()).partialState(), ConfiguredModel.builder().modelFile(experienceTankModel).build());
        simpleBlockItem(AExpBlocks.EXPERIENCE_CONVERTER.block(), experienceTankModel);

        var experienceCell = "block/drive/drive_cells";
        models().getBuilder(experienceCell)
                .parent(new ModelFile.UncheckedModelFile(AppEng.makeId("block/drive/drive_cell")))
                .texture("cell", AppliedExperienced.id(experienceCell));
    }
}
