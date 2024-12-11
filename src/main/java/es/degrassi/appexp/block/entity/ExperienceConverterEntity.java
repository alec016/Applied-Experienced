package es.degrassi.appexp.block.entity;

import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import es.degrassi.appexp.block.ExperienceConverterBlock;
import es.degrassi.appexp.definition.AExpBlockEntities;
import es.degrassi.appexp.definition.AExpConfig;
import es.degrassi.appexp.definition.AExpMenus;
import es.degrassi.appexp.definition.AExpTags;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import es.degrassi.experiencelib.impl.capability.BasicExperienceTank;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExperienceConverterEntity extends BlockEntity implements ServerTickingBlockEntity, IActionHost {
  @Getter
  private final BasicExperienceTank experienceTank = new BasicExperienceTank(
      AExpConfig.get().XP_CONVERTER_CAPACITY.get(),
      () -> {
        setChanged();
        BlockState state = getBlockState().setValue(ExperienceConverterBlock.light,
            Mth.lerpInt(Mth.clamp(((float) getExperience()) / getExperienceCapacity(), 0, 1), 0, 15));
        level.setBlockAndUpdate(getBlockPos(), state);
      }
  );
  private final FluidTank fluidTank = new FluidTank(0, e -> e.is(AExpTags.Fluids.EXPERIENCE)) {
    @Override
    public int fill(FluidStack resource, FluidAction action) {
      if (resource.is(AExpTags.Fluids.EXPERIENCE)) {
        long toInsert = resource.getAmount() / AExpConfig.get().XP_CONVERSION_RATE.get();
        long received = experienceTank.receiveExperience(toInsert, true);
        if (received >= toInsert) {
          experienceTank.receiveExperience(received, action.simulate());
          return (int) received * AExpConfig.get().XP_CONVERSION_RATE.get();
        }
      }
      return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
      if (resource.is(AExpTags.Fluids.EXPERIENCE)) {
        long toExtract = resource.getAmount() / AExpConfig.get().XP_CONVERSION_RATE.get();
        long extracted = experienceTank.extractExperience(toExtract, true);
        if (extracted >= toExtract) {
          experienceTank.extractExperience(extracted, action.simulate());
          return resource.copyWithAmount((int) extracted * AExpConfig.get().XP_CONVERSION_RATE.get());
        }
      }
      return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
      return drain(getFluid().copyWithAmount(maxDrain), action);
    }
  };

  public static void registerCapability(final RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(
        ExperienceLibCapabilities.EXPERIENCE.block(),
        AExpBlockEntities.EXPERIENCE_CONVERTER.get(),
        (be, ctx) -> be.experienceTank
    );
    event.registerBlockEntity(
        Capabilities.FluidHandler.BLOCK,
        AExpBlockEntities.EXPERIENCE_CONVERTER.get(),
        (be, ctx) -> be.fluidTank
    );
  }

  public float getFillState() {
    return this.getExperience() / (float) this.getExperienceCapacity();
  }

  public ExperienceConverterEntity(BlockPos pos, BlockState blockState) {
    super(AExpBlockEntities.EXPERIENCE_CONVERTER.get(), pos, blockState);
  }

  @Override
  public void serverTick() {
  }

  public long getExperience() {
    return experienceTank.getExperience();
  }

  public long getExperienceCapacity() {
    return experienceTank.getExperienceCapacity();
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tag.put("experienceTank", experienceTank.serializeNBT(registries));
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    experienceTank.deserializeNBT(registries, tag.get("experienceTank"));
  }

  @Override
  public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
    loadAdditional(tag, lookupProvider);
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    CompoundTag tag = new CompoundTag();
    saveAdditional(tag, registries);
    return tag;
  }

  @Override
  public final ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public @Nullable IGridNode getActionableNode() {
    return null;
  }

  public void openMenu(Player player, MenuHostLocator menuHostLocator) {
    if (!getLevel().isClientSide) {
      MenuOpener.open(AExpMenus.EXPERIENCE_CONVERTER, player, menuHostLocator);
    }
  }
}
