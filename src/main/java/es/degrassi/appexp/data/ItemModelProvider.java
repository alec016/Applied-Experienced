package es.degrassi.appexp.data;

import appeng.core.AppEng;
import appeng.core.definitions.ItemDefinition;
import es.degrassi.appexp.AEItems;
import es.degrassi.appexp.AppliedExperienced;
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

  public ItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
    super(output, AppliedExperienced.MODID, existingFileHelper);

    existingFileHelper.trackGenerated(P2P_TUNNEL_BASE_ITEM, MODEL);
    existingFileHelper.trackGenerated(P2P_TUNNEL_BASE_PART, MODEL);
    existingFileHelper.trackGenerated(STORAGE_CELL_LED, TEXTURE);
    existingFileHelper.trackGenerated(PORTABLE_CELL_LED, TEXTURE);
    existingFileHelper.trackGenerated(PORTABLE_CELL_FIELD, TEXTURE);
  }

  @Override
  protected void registerModels() {
    var housing = AEItems.EXPERIENCE_CELL_HOUSING;

    withExistingParent(housing.id().getPath(), mcLoc("item/generated"))
        .texture("layer0", AppliedExperienced.id("item/" + housing.id().getPath()));

    cell(AEItems.EXPERIENCE_CELL_CREATIVE, "item/" + AEItems.EXPERIENCE_CELL_CREATIVE.id().getPath());
    AEItems.getCells().forEach(cell -> cell(cell, "item/" + cell.id().getPath()));
    AEItems.getPortables().forEach(cell -> portableCell(cell, "item/" + cell.id().getPath()));

    withExistingParent("item/experience_p2p_tunnel", P2P_TUNNEL_BASE_ITEM)
        .texture("type", P2P_TUNNEL);
    withExistingParent("part/experience_p2p_tunnel", P2P_TUNNEL_BASE_PART)
        .texture("type", P2P_TUNNEL);
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
}
