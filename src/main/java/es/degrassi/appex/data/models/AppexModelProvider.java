package es.degrassi.appex.data.models;

import appeng.api.implementations.parts.ICablePart;
import appeng.api.parts.IPartItem;
import appeng.client.api.model.parts.ClientPart;
import appeng.client.api.model.parts.PartModel;
import appeng.datagen.providers.models.PartModelOutput;
import es.degrassi.appex.definition.AExpBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class AppexModelProvider extends ModelProvider implements DataProvider {
  private final PackOutput.PathProvider partOutput;
  @Nullable
  private AEPartModelCollector partModelCollector;

  public static Factory<DataProvider> create(String modId, ModelSubProviderFactory... subProviders) {
    var subProviderList = List.of(subProviders);
    return output -> new AppexModelProvider(output, modId, subProviderList);
  }

  // This matches the super-class constructor of ModelSubProvider
  @FunctionalInterface
  public interface ModelSubProviderFactory {
    ModelSubProvider create(BlockModelGenerators blockModels, ItemModelGenerators itemModels,
                            PartModelOutput partModels);
  }

  private final List<ModelSubProviderFactory> subProviders;

  public AppexModelProvider(PackOutput packOutput, String modid, List<ModelSubProviderFactory> subProviders) {
    super(packOutput, modid);
    this.subProviders = subProviders;
    this.partOutput = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "ae2/parts");
  }

  @Override
  protected Stream<? extends Holder<Block>> getKnownBlocks() {
    return super.getKnownBlocks().filter(block -> !block.is(AExpBlocks.EXPERIENCE_CONVERTER.id()));
  }

  @Override
  public CompletableFuture<?> run(CachedOutput output) {
    var partModelCollector = new AEPartModelCollector(this::getKnownItems);
    this.partModelCollector = partModelCollector;
    CompletableFuture<?> future;
    try {
      future = super.run(output);
    } finally {
      this.partModelCollector = null;
    }
    partModelCollector.finalizeAndValidate();
    return CompletableFuture.allOf(future, partModelCollector.save(output, partOutput));
  }

  @Override
  protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
    for (var subProvider : subProviders) {
      subProvider.create(blockModels, itemModels, partModelCollector).register();
    }
  }

  @Override
  public String getName() {
    return super.getName() + " " + getClass().getName();
  }

  public static final class AEPartModelCollector implements PartModelOutput {
    private final Map<IPartItem<?>, ClientPart> parts = new HashMap<>();
    private final Map<IPartItem<?>, IPartItem<?>> copies = new HashMap<>();
    private final Supplier<Stream<? extends Holder<Item>>> knownItems;

    public AEPartModelCollector(Supplier<Stream<? extends Holder<Item>>> knownItems) {
      this.knownItems = knownItems;
    }

    @Override
    public void accept(IPartItem<?> part, PartModel.Unbaked model) {
      this.register(part, new ClientPart(model, ClientPart.Properties.DEFAULT));
    }

    @Override
    public void accept(IPartItem<?> part, PartModel.Unbaked model, ClientPart.Properties properties) {
      this.register(part, new ClientPart(model, properties));
    }

    @Override
    public void copy(IPartItem<?> from, IPartItem<?> to) {
      this.copies.put(to, from);
    }

    public void register(IPartItem<?> part, ClientPart clientPart) {
      var existingPart = this.parts.put(part, clientPart);
      if (existingPart != null) {
        throw new IllegalStateException("Duplicate part model definition for " + IPartItem.getId(part));
      }
    }

    public void finalizeAndValidate() {
      this.copies.forEach((to, from) -> {
        var clientPart = this.parts.get(from);
        if (clientPart == null) {
          throw new IllegalStateException("Missing donor: " + from + " -> " + to);
        } else {
          this.register(to, clientPart);
        }
      });

      var missingPartModels = knownItems.get()
          .filter(holder -> holder.value() instanceof IPartItem<?> partItem && !isCable(partItem)
              && !this.parts.containsKey(partItem))
          .map(holder -> holder.unwrapKey().orElseThrow().identifier())
          .toList();
      if (!missingPartModels.isEmpty()) {
        throw new IllegalStateException("Missing part model definitions for: " + missingPartModels);
      }
    }

    private boolean isCable(IPartItem<?> partItem) {
      return ICablePart.class.isAssignableFrom(partItem.getPartClass());
    }

    public CompletableFuture<?> save(CachedOutput output, PackOutput.PathProvider pathProvider) {
      return DataProvider.saveAll(
          output, ClientPart.CODEC,
          partItem -> pathProvider.json(BuiltInRegistries.ITEM.getKey(partItem.asItem())), this.parts);
    }
  }
}
