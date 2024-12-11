package es.degrassi.appexp.data;

import appeng.core.AppEng;
import appeng.core.definitions.ItemDefinition;
import appeng.items.storage.StorageTier;
import es.degrassi.appexp.definition.AExpItems;
import es.degrassi.appexp.AppliedExperienced;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemModelProvider extends net.neoforged.neoforge.client.model.generators.ItemModelProvider {

  private static final ResourceLocation P2P_TUNNEL_BASE_ITEM = AppEng.makeId("item/p2p_tunnel_base");
  private static final ResourceLocation P2P_TUNNEL_BASE_PART = AppEng.makeId("part/p2p/p2p_tunnel_base");
  private static final ResourceLocation STORAGE_CELL_LED = AppEng.makeId("item/storage_cell_led");
  private static final ResourceLocation PORTABLE_CELL_LED = AppEng.makeId("item/portable_cell_led");
  private static final ResourceLocation PORTABLE_CELL_FIELD = AppEng.makeId("item/portable_cell_screen");
  private static final ResourceLocation P2P_TUNNEL = AppliedExperienced.id("part/p2p_tunnel_experience");
  private static final ResourceLocation ENERGY_ACCEPTOR = AppEng.makeId("part/energy_acceptor");
  private static final ResourceLocation CABLE_ENERGY_ACCEPTOR = AppEng.makeId("item/cable_energy_acceptor");

  public ItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
    super(output, AppliedExperienced.MODID, existingFileHelper);

    existingFileHelper.trackGenerated(P2P_TUNNEL_BASE_ITEM, MODEL);
    existingFileHelper.trackGenerated(P2P_TUNNEL_BASE_PART, MODEL);
    existingFileHelper.trackGenerated(ENERGY_ACCEPTOR, MODEL);
    existingFileHelper.trackGenerated(CABLE_ENERGY_ACCEPTOR, MODEL);
    existingFileHelper.trackGenerated(STORAGE_CELL_LED, TEXTURE);
    existingFileHelper.trackGenerated(PORTABLE_CELL_LED, TEXTURE);
    existingFileHelper.trackGenerated(PORTABLE_CELL_FIELD, TEXTURE);
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
    AExpItems.getPortables().forEach(cell -> portableCell(cell, "item/" + cell.id().getPath()));

    withExistingParent("item/experience_p2p_tunnel", P2P_TUNNEL_BASE_ITEM)
        .texture("type", P2P_TUNNEL);
    withExistingParent("part/experience_p2p_tunnel", P2P_TUNNEL_BASE_PART)
        .texture("type", P2P_TUNNEL);

    withExistingParent("part/experience_acceptor", ENERGY_ACCEPTOR)
        .texture("front", "block/experience_acceptor");
    withExistingParent("item/cable_experience_acceptor", CABLE_ENERGY_ACCEPTOR)
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
        .texture("layer1", STORAGE_CELL_LED);
  }

  private void portableCell(ItemDefinition<?> portable, String background) {
    withExistingParent(portable.id().getPath(), mcLoc("item/generated"))
        .texture("layer0", AppliedExperienced.id("item/portable_experience_cell_housing"))
        .texture("layer1", PORTABLE_CELL_LED)
        .texture("layer2", PORTABLE_CELL_FIELD)
        .texture("layer3", AppliedExperienced.id(background));
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
