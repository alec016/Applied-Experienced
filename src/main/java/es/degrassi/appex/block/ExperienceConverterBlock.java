package es.degrassi.appex.block;

import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.menu.locator.MenuLocators;
import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import es.degrassi.appex.block.entity.ExperienceConverterEntity;
import es.degrassi.experiencelib.common.ExperienceLibRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExperienceConverterBlock extends Block implements EntityBlock {
  public ExperienceConverterBlock(Properties props) {
    super(props
        .mapColor(MapColor.NONE)
        .sound(SoundType.GLASS)
        .dynamicShape()
        .noOcclusion()
        .requiresCorrectToolForDrops()
        .strength(5f, 6f)
    );
  }

  @Override
  public boolean hasDynamicLightEmission(BlockState state) {
    return true;
  }

  @Override
  public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
    if (level.getBlockEntity(pos) instanceof ExperienceConverterEntity entity) {
      return entity.getLightEmission();
    }
    return super.getLightEmission(state, level, pos);
  }

  @Override
  protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                        InteractionHand hand, BlockHitResult hitResult) {
    if (level.isClientSide()) return InteractionResult.SUCCESS;
    if (level.getBlockEntity(pos) instanceof ExperienceConverterEntity entity) {
      try (Transaction root = Transaction.openRoot()) {
        if (stack.is(Items.GLASS_BOTTLE)) {
          try (Transaction sub = Transaction.open(root)) {
            long extracted = entity.getExperienceTank().extract(0, 7, sub);
            if (extracted == 7) {
              stack.consume(1, player);
              player.getInventory().placeItemBackInInventory(new ItemStack(Items.EXPERIENCE_BOTTLE));
              sub.commit();
              return InteractionResult.CONSUME;
            }
            return InteractionResult.FAIL;
          }
        } else if (stack.is(Items.EXPERIENCE_BOTTLE)) {
          try (Transaction sub = Transaction.open(root)) {
            long inserted = entity.getExperienceTank().insert(0, 7, sub);
            if (inserted == 7) {
              stack.consume(1, player);
              player.getInventory().placeItemBackInInventory(new ItemStack(Items.GLASS_BOTTLE));
              sub.commit();
              return InteractionResult.CONSUME;
            }
            return InteractionResult.FAIL;
          }
        }
      }
    }
    return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
  }

  @Override
  protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return makeShape();
  }

  @Override
  public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity by, ItemStack itemStack) {
    if (level.getBlockEntity(pos) instanceof ExperienceConverterEntity entity) {
      entity.getExperienceTank().setExperience(0, itemStack.getOrDefault(ExperienceLibRegistration.EXPERIENCE_COMPONENT, 0L));
    }
  }

  @Override
  public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
    ItemStack stack = new ItemStack(this);
    if (!includeData) return stack;
    if (level.getBlockEntity(pos) instanceof ExperienceConverterEntity entity) {
      stack.set(ExperienceLibRegistration.EXPERIENCE_COMPONENT, entity.getExperience());
    }
    return stack;
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
    BlockEntity te = level.getBlockEntity(pos);
    if (te instanceof ExperienceConverterEntity machine) {
      machine.openMenu(player, MenuLocators.forBlockEntity(te));
      return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }
    return super.useWithoutItem(state, level, pos, player, hitResult);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new ExperienceConverterEntity(blockPos, blockState);
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
}
