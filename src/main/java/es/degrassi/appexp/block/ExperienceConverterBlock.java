package es.degrassi.appexp.block;

import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.menu.locator.MenuLocators;
import es.degrassi.appexp.block.entity.ExperienceConverterEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExperienceConverterBlock extends Block implements EntityBlock {
  public static final IntegerProperty light = IntegerProperty.create("light", 0, 15);
  private static final StateArgumentPredicate<EntityType<?>> spawnPredicate =
      (state, level, pos, type) ->
          state.isFaceSturdy(level, pos, Direction.UP) &&
              state.getBlock() instanceof ExperienceConverterBlock machineBlock &&
              machineBlock.getLightEmission(state, level, pos) < 14;

  public ExperienceConverterBlock() {
    super(Properties.of()
        .mapColor(MapColor.METAL)
        .sound(SoundType.METAL)
        .dynamicShape()
        .noOcclusion()
        .requiresCorrectToolForDrops()
        .strength(5.0f, 6.0f)
        .lightLevel(state -> state.getValue(light))
        .isValidSpawn(spawnPredicate)
    );
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return super.getStateForPlacement(context).setValue(light, 0);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(light);
  }

  @Override
  protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    if (level.isClientSide) return ItemInteractionResult.sidedSuccess(true);
    if (level.getBlockEntity(pos) instanceof ExperienceConverterEntity entity) {
      if (stack.is(Items.GLASS_BOTTLE)) {
        long extracted = entity.getExperienceTank().extractExperience(7, true);
        if (extracted == 7) {
          entity.getExperienceTank().extractExperience(7, false);
          stack.consume(1, player);
          player.getInventory().placeItemBackInInventory(new ItemStack(Items.EXPERIENCE_BOTTLE));
          return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.FAIL;
      } else if (stack.is(Items.EXPERIENCE_BOTTLE)) {
        long inserted = entity.getExperienceTank().receiveExperience(7, true);
        if (inserted == 7) {
          entity.getExperienceTank().receiveExperience(7, false);
          stack.consume(1, player);
          player.getInventory().placeItemBackInInventory(new ItemStack(Items.GLASS_BOTTLE));
          return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.FAIL;
      }
    }
    return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
  }

  @Override
  protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return makeShape();
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {

    BlockEntity te = level.getBlockEntity(pos);
    if (te instanceof ExperienceConverterEntity machine) {
      machine.openMenu(player, MenuLocators.forBlockEntity(te));
      return InteractionResult.sidedSuccess(level.isClientSide);
    }
    return super.useWithoutItem(state, level, pos, player, hitResult);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new ExperienceConverterEntity(pos, state);
  }

  public VoxelShape makeShape() {
    VoxelShape shape = Shapes.empty();
    shape = Shapes.join(shape, Shapes.create(0.0625, 0, 0.0625, 0.9375, 0.75, 0.9375), BooleanOp.OR);
    shape = Shapes.join(shape, Shapes.create(0.375, 0.875, 0.375, 0.625, 1, 0.625), BooleanOp.OR);
    shape = Shapes.join(shape, Shapes.create(0.9375, 0.75, 0.9375, 0.0625, 0, 0.0625), BooleanOp.OR);
    shape = Shapes.join(shape, Shapes.create(0.4375, 0.75, 0.4375, 0.5625, 0.875, 0.5625), BooleanOp.OR);
    shape = Shapes.join(shape, Shapes.create(0.375, 0.75, 0.375, 0.625, 0.875, 0.625), BooleanOp.OR);

    return shape;
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
    return (l, pos, s, be) -> {
      if (!l.isClientSide() && be instanceof ServerTickingBlockEntity tank) tank.serverTick();
      if (l.isClientSide() && be instanceof ClientTickingBlockEntity tank) tank.clientTick();
    };
  }

  public int getExpDrop(BlockState state, LevelAccessor level, BlockPos pos, @Nullable BlockEntity blockEntity,
                        @Nullable Entity breaker, ItemStack tool) {
    if (blockEntity instanceof ExperienceConverterEntity tank) {
      return (int) tank.getExperience();
    }
    return super.getExpDrop(state, level, pos, blockEntity, breaker, tool);
  }
}
