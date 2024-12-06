package es.degrassi.appexp.api.capability;

import es.degrassi.appexp.AppliedExperienced;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

public class AExpCapabilities {
  private AExpCapabilities() {}

  public static final CapSet<IExperienceHandler> EXPERIENCE = new CapSet<>(rl("experience_handler"), IExperienceHandler.class);

  public record CapSet<T>(BlockCapability<T, @Nullable Direction> block, ItemCapability<T, Void> item) {
    public CapSet(ResourceLocation name, Class<T> handlerClass) {
      this(BlockCapability.createSided(name, handlerClass), ItemCapability.createVoid(name, handlerClass));
    }
  }

  private static ResourceLocation rl(String path) {
    return ResourceLocation.fromNamespaceAndPath(AppliedExperienced.MODID, path);
  }
}
