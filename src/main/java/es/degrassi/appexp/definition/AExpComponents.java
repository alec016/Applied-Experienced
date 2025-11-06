package es.degrassi.appexp.definition;

import com.mojang.serialization.Codec;
import es.degrassi.appexp.AppliedExperienced;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

public final class AExpComponents {
  private AExpComponents() {
  }

  public static final DeferredRegister<DataComponentType<?>> DR =
      DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, AppliedExperienced.MODID);

  public static final DataComponentType<Long> EXPERIENCE_AMOUNT = register(
      "experience_amount", builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG)
  );
  public static final DataComponentType<ItemContainerContents> IN_INV = register("in_inv",
      builder -> builder.persistent(ItemContainerContents.CODEC)
          .networkSynchronized(ItemContainerContents.STREAM_CODEC));

  public static final DataComponentType<ItemContainerContents> OUT_INV = register("out_inv",
      builder -> builder.persistent(ItemContainerContents.CODEC)
          .networkSynchronized(ItemContainerContents.STREAM_CODEC));

  public static final DataComponentType<Long> EXPERIENCE_CAPACITY = register(
      "experience_capacity", builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG)
  );

  private static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
    var builder = DataComponentType.<T>builder();
    customizer.accept(builder);
    var componentType = builder.build();
    DR.register(name, () -> componentType);
    return componentType;
  }

  public static void initialize(IEventBus bus) {
    DR.register(bus);
  }
}
