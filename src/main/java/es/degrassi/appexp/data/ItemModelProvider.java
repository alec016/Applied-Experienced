package es.degrassi.appexp.data;

import appeng.core.AppEng;
import appeng.core.definitions.ItemDefinition;
import appeng.items.storage.StorageTier;
import es.degrassi.appexp.definition.AExpItems;
import es.degrassi.appexp.AppliedExperienced;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemModelProvider extends net.neoforged.neoforge.client.model.generators.ItemModelProvider {
  public ItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
    super(output, AppliedExperienced.MODID, existingFileHelper);
  }

  @Override
  protected void registerModels() {
    var housing = AExpItems.EXPERIENCE_CELL_HOUSING;

    withExistingParent(housing.id().getPath(), mcLoc("item/generated"))
        .texture("layer0", AppliedExperienced.id("item/" + housing.id().getPath()));

    cell(AExpItems.EXPERIENCE_CELL_CREATIVE, "item/" + AExpItems.EXPERIENCE_CELL_CREATIVE.id().getPath());
    driveCell(AExpItems.EXPERIENCE_CELL_CREATIVE.id().getPath(), 10);
    AExpItems.getCells().forEach(cell -> {
      cell(cell, "item/" + cell.id().getPath());
      driveCell(cell.id().getPath(), offsetByTier(cell.get().getTier()));
    });
    AExpItems.getPortables().forEach(cell -> portableCell(cell, cell.get().getTier().namePrefix()));

    var tunnel = AppliedExperienced.id("part/p2p_tunnel_experience");
    withExistingParent("item/experience_p2p_tunnel", AppEng.makeId("item/p2p_tunnel_base"))
        .texture("type", tunnel);
    withExistingParent("part/experience_p2p_tunnel", AppEng.makeId("part/p2p/p2p_tunnel_base"))
        .texture("type", tunnel);

    withExistingParent("part/experience_acceptor", AppEng.makeId("part/energy_acceptor"))
        .texture("front", "block/experience_acceptor");
    withExistingParent("item/cable_experience_acceptor", AppEng.makeId("item/cable_energy_acceptor"))
        .texture("front", "block/experience_acceptor");
  }

  private int offsetByTier(StorageTier tier) {
    return switch (tier.bytes()) {
      case 1024 -> 0;
      case 4096 -> 2;
      case 16384 -> 4;
      case 65536 -> 6;
      case 262144 -> 8;
      default -> 12;
    };
  }

  private void cell(ItemDefinition<?> cell, String background) {
    withExistingParent(cell.id().getPath(), mcLoc("item/generated"))
        .texture("layer0", AppliedExperienced.id(background))
        .texture("layer1", AppEng.makeId("item/storage_cell_led"));
  }

  private void portableCell(ItemDefinition<?> portable, String tier) {
    withExistingParent(portable.id().getPath(), mcLoc("item/generated"))
        .texture("layer0", AppliedExperienced.id("item/portable_experience_cell_housing"))
        .texture("layer1", AppEng.makeId("item/portable_cell_led"))
        .texture("layer2", AppEng.makeId("item/portable_cell_screen"))
        .texture("layer3", AppEng.makeId("item/portable_cell_side_" + tier));
  }

  private void driveCell(String cell, int offset) {
    getBuilder("block/drive/cells/" + cell)
        .ao(false)
        .texture("cell", "block/drive/drive_cells")
        .texture("particle", "block/drive/drive_cells")
        .element()
        .to(6, 2, 2)
        .face(Direction.NORTH)
        .uvs(0, offset, 6, offset + 2)
        .end()
        .face(Direction.UP)
        .uvs(6, offset, 0, offset + 2)
        .end()
        .face(Direction.DOWN)
        .uvs(6, offset, 0, offset + 2)
        .end()
        .faces((dir, builder) -> builder.texture("#cell").cullface(Direction.NORTH).end())
        .end();
  }
}
