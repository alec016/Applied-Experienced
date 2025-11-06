package es.degrassi.appexp.block.entity;

import appeng.api.config.FuzzyMode;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.hooks.ticking.TickHandler;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import es.degrassi.appexp.block.ExperienceConverterBlock;
import es.degrassi.appexp.definition.AExpBlockEntities;
import es.degrassi.appexp.definition.AExpBlocks;
import es.degrassi.appexp.definition.AExpComponents;
import es.degrassi.appexp.definition.AExpConfig;
import es.degrassi.appexp.definition.AExpMenus;
import es.degrassi.appexp.definition.AExpTags;
import es.degrassi.appexp.util.inv.filter.ApexItemFilters;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.experiencelib.impl.capability.BasicExperienceHandler;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExperienceConverterEntity extends BlockEntity implements ServerTickingBlockEntity, IActionHost, InternalInventoryHost {
  @Getter
  private final BasicExperienceHandler experienceTank = new BasicExperienceHandler(
      1,
      AExpConfig.get().XP_CONVERTER_CAPACITY.get(),
      () -> {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, level.registryAccess());
        BlockState state = getBlockState().setValue(ExperienceConverterBlock.light,
            Mth.lerpInt(Mth.clamp(((float) getExperience()) / getExperienceCapacity(), 0, 1), 0, 15));
        level.setBlockAndUpdate(getBlockPos(), state);
        if (level.getBlockEntity(getBlockPos()) instanceof ExperienceConverterEntity entity) {
          entity.loadAdditional(tag, level.registryAccess());
          entity.setChanged();
          return;
        }
        setChanged();
      }
  );

  private final AppEngInternalInventory inventoryIn;
  private final AppEngInternalInventory inventoryOut;

  @Getter
  private final CombinedInternalInventory internalInventory;

  private final FluidTank fluidTank = new FluidTank(0, e -> e.is(AExpTags.Fluids.EXPERIENCE)) {
    @Override
    public int fill(FluidStack resource, FluidAction action) {
      if (resource.is(AExpTags.Fluids.EXPERIENCE)) {
        long toInsert = resource.getAmount() / AExpConfig.get().XP_CONVERSION_RATE.get();
        long received = experienceTank.receiveExperience(0, toInsert, true);
        if (received >= toInsert) {
          experienceTank.receiveExperience(0, received, action.simulate());
          return (int) received * AExpConfig.get().XP_CONVERSION_RATE.get();
        }
      }
      return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
      if (resource.is(AExpTags.Fluids.EXPERIENCE)) {
        long toExtract = resource.getAmount() / AExpConfig.get().XP_CONVERSION_RATE.get();
        long extracted = experienceTank.extractExperience(0, toExtract, true);
        if (extracted >= toExtract) {
          experienceTank.extractExperience(0, extracted, action.simulate());
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
    event.registerItem(
        ExperienceLibCapabilities.EXPERIENCE.item(),
        (item, v) -> {
          IExperienceHandler handler = new BasicExperienceHandler(1, Long.MAX_VALUE, null);
          handler.setExperience(0, Optional.ofNullable(item.get(AExpComponents.EXPERIENCE_AMOUNT)).orElse(0L));
          return handler;
        },
        AExpBlocks.EXPERIENCE_CONVERTER.item().get()
    );
    event.registerBlockEntity(
        Capabilities.ItemHandler.BLOCK,
        AExpBlockEntities.EXPERIENCE_CONVERTER.get(),
        (be, ctx) -> be.internalInventory.toItemHandler()
    );
  }

  public float getFillState() {
    return this.getExperience() / (float) this.getExperienceCapacity();
  }

  private boolean setChangedQueued = false;

  public ExperienceConverterEntity(BlockPos pos, BlockState blockState) {
    super(AExpBlockEntities.EXPERIENCE_CONVERTER.get(), pos, blockState);
    this.inventoryIn = new AppEngInternalInventory(this, 1);
    this.inventoryIn.setFilter(ApexItemFilters.INSERT_ONLY);
    this.inventoryOut = new AppEngInternalInventory(this, 1);
    this.inventoryOut.setFilter(ApexItemFilters.EXTRACT_ONLY);
    this.internalInventory = new CombinedInternalInventory(inventoryIn, inventoryOut);
  }

  @Override
  public void serverTick() {
    internalInventory.forEach(stack -> {
      if (!stack.is(Items.EXPERIENCE_BOTTLE)) return;
      int amount = stack.getCount();
      if (amount < 1) return;
      long received = experienceTank.receiveExperience(0, AExpConfig.get().XP_BOTTLE_XP_AMOUNT.get(), true);
      ItemStack extracted = internalInventory.simulateSimilarRemove(1, Items.EXPERIENCE_BOTTLE.getDefaultInstance(), FuzzyMode.IGNORE_ALL, null);
      ItemStack remaining = internalInventory.addItems(Items.GLASS_BOTTLE.getDefaultInstance(), true);
      if (received < AExpConfig.get().XP_BOTTLE_XP_AMOUNT.get()) return;
      if (extracted.isEmpty()) return;
      if (!remaining.isEmpty()) return;
      experienceTank.receiveExperience(0, AExpConfig.get().XP_BOTTLE_XP_AMOUNT.get(), false);
      internalInventory.removeItems(1, extracted, null);
      internalInventory.addItems(Items.GLASS_BOTTLE.getDefaultInstance(), false);
    });
    setChanged();
  }

  @Override
  public void saveToItem(ItemStack stack, HolderLookup.Provider registries) {
    super.saveToItem(stack, registries);
    stack.set(AExpComponents.EXPERIENCE_AMOUNT, getExperience());
    stack.set(AExpComponents.IN_INV, inventoryIn.toItemContainerContents());
    stack.set(AExpComponents.OUT_INV, inventoryOut.toItemContainerContents());
  }

  @Override
  protected void collectImplicitComponents(DataComponentMap.Builder components) {
    components.set(AExpComponents.EXPERIENCE_AMOUNT, getExperience());
    components.set(AExpComponents.IN_INV, inventoryIn.toItemContainerContents());
    components.set(AExpComponents.OUT_INV, inventoryOut.toItemContainerContents());
  }

  protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
    getExperienceTank().setExperience(0, componentInput.getOrDefault(AExpComponents.EXPERIENCE_AMOUNT, 0L));
    inventoryIn.fromItemContainerContents(componentInput.getOrDefault(AExpComponents.IN_INV, ItemContainerContents.fromItems(List.of(ItemStack.EMPTY))));
    inventoryOut.fromItemContainerContents(componentInput.getOrDefault(AExpComponents.OUT_INV, ItemContainerContents.fromItems(List.of(ItemStack.EMPTY))));
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
    var inv = this.getInternalInventory();
    if (inv != InternalInventory.empty()) {
      final CompoundTag opt = new CompoundTag();
      for (int x = 0; x < inv.size(); x++) {
        var is = inv.getStackInSlot(x);
        opt.put("item" + x, is.saveOptional(registries));
      }
      tag.put("inv", opt);
    }
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    experienceTank.deserializeNBT(registries, tag.getCompound("experienceTank"));
    var inv = this.getInternalInventory();
    if (inv != InternalInventory.empty()) {
      var opt = tag.getCompound("inv");
      for (int x = 0; x < inv.size(); x++) {
        var item = opt.getCompound("item" + x);
        inv.setItemDirect(x, ItemStack.parseOptional(registries, item));
      }
    }
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

  @Override
  public void saveChangedInventory(AppEngInternalInventory inv) {
    this.saveChanges();
  }

  public void saveChanges() {
    if (this.level == null) {
      return;
    }

    if (this.level.isClientSide) {
      this.setChanged();
    } else {
      this.level.blockEntityChanged(this.worldPosition);
      if (!this.setChangedQueued) {
        TickHandler.instance().addCallable(null, this::setChangedAtEndOfTick);
        this.setChangedQueued = true;
      }
    }
  }

  @Override
  public void onChangeInventory(AppEngInternalInventory inv, int slot) {
    this.setChanged();
  }

  private void setChangedAtEndOfTick(@NotNull Level level) {
    this.setChanged();
    this.setChangedQueued = false;
  }

  @Override
  public boolean isClientSide() {
    Level level = getLevel();
    return level == null || level.isClientSide();
  }
}
