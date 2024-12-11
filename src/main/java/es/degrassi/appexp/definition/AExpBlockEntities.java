package es.degrassi.appexp.definition;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.core.definitions.BlockDefinition;
import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.appexp.block.entity.ExperienceAcceptorEntity;
import es.degrassi.appexp.block.entity.ExperienceConverterEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public final class AExpBlockEntities {
  private AExpBlockEntities() {
  }

  public static final DeferredRegister<BlockEntityType<?>> DR =
      DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AppliedExperienced.MODID);

  public static final Supplier<BlockEntityType<ExperienceConverterEntity>> EXPERIENCE_CONVERTER = DR.register(
      "experience_converter",
      () -> new BlockEntityType<>(
          ExperienceConverterEntity::new,
          Set.of(AExpBlocks.EXPERIENCE_CONVERTER.block()),
          null
      )
  );

  public static final Supplier<BlockEntityType<ExperienceAcceptorEntity>> EXPERIENCE_ACCEPTOR = create(
      "experience_acceptor",
      ExperienceAcceptorEntity.class,
      ExperienceAcceptorEntity::new,
      AExpBlocks.EXPERIENCE_ACCEPTOR);


  @SuppressWarnings("DataFlowIssue")
  private static <T extends AEBaseBlockEntity> Supplier<BlockEntityType<T>> create(
      String id,
      Class<T> entityClass,
      BlockEntityType.BlockEntitySupplier<T> supplier,
      BlockDefinition<? extends AEBaseEntityBlock<T>> block) {
    return DR.register(id, () -> {
      var type = BlockEntityType.Builder.of(supplier, block.block()).build(null);

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
