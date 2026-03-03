package es.degrassi.appexp.api.integration.jei;

import es.degrassi.appexp.AppliedExperienced;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;

import java.util.Objects;

@JeiPlugin
public class AppexJeiPlugin implements IModPlugin {

  public AppexJeiPlugin() {
    if (isModLoaded("ae2jeiintegration")) {
      AE2JeiIntegrationHelper.register();
    }
  }

  public ResourceLocation getPluginUid() {
    return AppliedExperienced.id("jei_plugin");
  }

  private static boolean isModLoaded(String modId) {
    if (ModList.get() == null) {
      var list = LoadingModList.get().getMods().stream().map(ModInfo::getModId);
      Objects.requireNonNull(modId);
      return list.anyMatch(modId::equals);
    } else {
      return ModList.get().isLoaded(modId);
    }
  }
}
