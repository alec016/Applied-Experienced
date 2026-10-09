package es.degrassi.appex.definition;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.core.definitions.BlockDefinition;
import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.block.entity.ExperienceAcceptorEntity;
import es.degrassi.appex.block.entity.ExperienceConverterEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AExpBlockEntities {
  public static final DeferredRegister<BlockEntityType<?>> DR =
      DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AppliedExperienced.MODID);

  public static final Supplier<BlockEntityType<ExperienceConverterEntity>> EXPERIENCE_CONVERTER = DR.register(
      "experience_converter",
      () -> new BlockEntityType<>(
          ExperienceConverterEntity::new,
          Set.of(AExpBlocks.EXPERIENCE_CONVERTER.block())
      )
  );

  public static final Supplier<BlockEntityType<ExperienceAcceptorEntity>> EXPERIENCE_ACCEPTOR = create(
      "experience_acceptor",
      ExperienceAcceptorEntity.class,
      ExperienceAcceptorEntity::new,
      AExpBlocks.EXPERIENCE_ACCEPTOR
  );

  private static <T extends AEBaseBlockEntity> Supplier<BlockEntityType<T>> create(
      String id,
      Class<T> entityClass,
      BlockEntityType.BlockEntitySupplier<T> supplier,
      BlockDefinition<? extends AEBaseEntityBlock<T>> block) {
    return DR.register(id, () -> {
      var type = new BlockEntityType<>(
          supplier,
          Set.of(block.block())
      );

      BlockEntityTicker<T> clientTicker = null;
      BlockEntityTicker<T> serverTicker = null;

      if (ClientTickingBlockEntity.class.isAssignableFrom(entityClass)) {
        clientTicker = (level, pos, state, entity) -> ((ClientTickingBlockEntity) entity).clientTick();
      }

      if (ServerTickingBlockEntity.class.isAssignableFrom(entityClass)) {
        serverTicker = (level, pos, state, entity) -> ((ServerTickingBlockEntity) entity).serverTick();
      }

      block.block().setBlockEntity(entityClass, type, clientTicker, serverTicker);
      AEBaseBlockEntity.registerBlockEntityItem(type, block.asItem());
      return type;
    });
  }
}
