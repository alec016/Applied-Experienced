package es.degrassi.appex.block.entity;

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
import com.google.common.primitives.Ints;
import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import es.degrassi.appex.api.entity.IExperienceInvHost;
import es.degrassi.appex.definition.AExpBlockEntities;
import es.degrassi.appex.definition.AExpBlocks;
import es.degrassi.appex.definition.AExpComponents;
import es.degrassi.appex.definition.AExpConfig;
import es.degrassi.appex.definition.AExpMenus;
import es.degrassi.appex.definition.AExpTags;
import es.degrassi.appex.util.inv.filter.ApexItemFilters;
import es.degrassi.experiencelib.api.ExperienceResource;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import es.degrassi.experiencelib.api.network.ISyncable;
import es.degrassi.experiencelib.api.network.ISyncableHandler;
import es.degrassi.experiencelib.api.network.ISyncableStuff;
import es.degrassi.experiencelib.api.network.syncable.BooleanSyncable;
import es.degrassi.experiencelib.common.ExperienceLibRegistration;
import es.degrassi.experiencelib.transfer.ItemAccessExperienceHandler;
import es.degrassi.experiencelib.transfer.SimpleExperienceHandler;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ExperienceConverterEntity extends BlockEntity implements ServerTickingBlockEntity, IActionHost, InternalInventoryHost, IExperienceInvHost,
    ISyncableHandler, ISyncableStuff {
  private static final String EXPERIENCE_TANK = "experienceTank";
  private static final String INVENTORY = "inv";
  private boolean setChangedQueued = false;
  @Getter
  private boolean isInverted = false;
  private boolean initialized = false;
  @Getter
  protected final List<ISyncable<?, ?>> stuffToSync = new ArrayList<>();
  private final AppEngInternalInventory inventoryIn;
  private final AppEngInternalInventory inventoryOut;
  @Getter
  private final CombinedInternalInventory internalInventory;
  @Getter
  private final SimpleExperienceHandler experienceTank = new SimpleExperienceHandler(AExpConfig.get().XP_CONVERTER_CAPACITY.get()) {
    @Override
    public void onExperienceChange(long prevAmount) {
      super.onExperienceChange(prevAmount);
      broadcastChanges();
      saveChanges();
    }
  };
  private final FluidStacksResourceHandler fluidHandler = new FluidStacksResourceHandler(1, 0) {
    @Override
    public boolean isValid(int index, FluidResource resource) {
      return resource.is(AExpTags.Fluids.EXPERIENCE);
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
      if (resource.is(AExpTags.Fluids.EXPERIENCE)) {
        long toInsert = amount / AExpConfig.get().XP_CONVERSION_RATE.get();
        try (Transaction subTrans = Transaction.open(transaction)) {
          long received = experienceTank.insert(0, toInsert, subTrans);
          if (received == 0) return 0;
          if (received < toInsert) return 0;
          subTrans.commit();
          return Ints.saturatedCast(received * AExpConfig.get().XP_CONVERSION_RATE.get());
        }
      }
      return 0;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
      if (resource.is(AExpTags.Fluids.EXPERIENCE)) {
        long toExtract = amount / AExpConfig.get().XP_CONVERSION_RATE.get();
        try (Transaction subTrans = Transaction.open(transaction)) {
          long extracted = experienceTank.extract(0, toExtract, subTrans);
          if (extracted == 0) return 0;
          if (extracted < toExtract) return 0;
          subTrans.commit();
          return Ints.saturatedCast(extracted * AExpConfig.get().XP_CONVERSION_RATE.get());
        }
      }
      return 0;
    }
  };

  public ExperienceConverterEntity(BlockPos pos, BlockState state) {
    super(AExpBlockEntities.EXPERIENCE_CONVERTER.get(), pos, state);
    this.inventoryIn = new AppEngInternalInventory(this, 1);
    this.inventoryIn.setFilter(ApexItemFilters.INSERT_ONLY_XP);
    this.inventoryOut = new AppEngInternalInventory(this, 1);
    this.inventoryOut.setFilter(ApexItemFilters.EXTRACT_ONLY_GLASS);
    this.internalInventory = new CombinedInternalInventory(inventoryIn, inventoryOut);
  }

  public int getLightEmission() {
    var fillState = getFillState();
    if (fillState == 0) return 0;
    return Mth.lerpInt(Mth.clamp(fillState, 0, 1), 1, 15);
  }

  public float getFillState() {
    return (float) getExperience() / getExperienceCapacity();
  }

  @Override
  public void onLoad() {
    super.onLoad();
    init();
  }

  private void init() {
    if (initialized) return;
    getStuffToSync().clear();
    experienceTank.getStuffToSync(stuffToSync::add);
    getStuffToSync(stuffToSync::add);
    initialized = true;
  }

  @Override
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
    container.accept(BooleanSyncable.create(this::isInverted, this::setInverted));
  }

  @Override
  public void serverTick() {
    if (!initialized) init();
    if (inventoryIn.isEmpty()) return;
    if (inventoryOut.getRedstoneSignal() == 15) return;
    AtomicBoolean doneSomething = new AtomicBoolean(false);
    internalInventory.forEach(stack -> {
      doneSomething.set(doneSomething.get() || (isInverted() ? insertToItem(stack) : extractFromItem(stack)));
    });
    if (doneSomething.get()) {
      broadcastChanges();
      saveChanges();
    }
  }

  public void setInverted(boolean inverted) {
    isInverted = inverted;
    var prevStackIn = inventoryIn.getStackInSlot(0);
    var prevStackOut = inventoryOut.getStackInSlot(0);
    inventoryIn.setFilter(isInverted ? ApexItemFilters.INSERT_ONLY_GLASS : ApexItemFilters.INSERT_ONLY_XP);
    inventoryOut.setFilter(isInverted ? ApexItemFilters.EXTRACT_ONLY_XP : ApexItemFilters.EXTRACT_ONLY_GLASS);
    inventoryIn.setItemDirect(0, prevStackOut);
    inventoryOut.setItemDirect(0, prevStackIn);
    broadcastChanges();
  }

  private boolean extractFromItem(ItemStack stack) {
    if (!stack.is(Items.EXPERIENCE_BOTTLE)) return false;
    if (stack.isEmpty()) return false;
    int amount = Math.min(AExpConfig.get().XP_CONVERTER_TICK_AMOUNT.get(), stack.count());
    var cap = stack.getCapability(ExperienceLibCapabilities.EXPERIENCE.item(), ItemAccess.forStack(stack.copyWithCount(1)));
    if (cap == null) return false;
    try (Transaction root = Transaction.open(null)) {
      long capExtract = cap.getCapacityAsLong(0, ExperienceResource.EMPTY) * amount;
      if (capExtract == 0) return false;
      long received = experienceTank.insert(0, capExtract, root);
      if (received == 0) return false;
      int capAmountDone = Ints.saturatedCast(received / cap.getCapacityAsLong(0, ExperienceResource.EMPTY));
      if (capAmountDone < 1) return false;
      ItemStack extracted = internalInventory.simulateSimilarRemove(capAmountDone, Items.EXPERIENCE_BOTTLE.getDefaultInstance(), FuzzyMode.IGNORE_ALL, null);
      if (extracted.isEmpty()) return false;
      ItemStack remaining = internalInventory.addItems(Items.GLASS_BOTTLE.getDefaultInstance().copyWithCount(extracted.count()), true);
      if (!remaining.isEmpty()) return false;
      internalInventory.removeItems(extracted.count(), extracted, null);
      internalInventory.addItems(Items.GLASS_BOTTLE.getDefaultInstance().copyWithCount(extracted.count()), false);
      root.commit();
      return true;
    }
  }

  private boolean insertToItem(ItemStack stack) {
    if (!stack.is(Items.GLASS_BOTTLE)) return false;
    if (stack.isEmpty()) return false;
    int amount = Math.min(AExpConfig.get().XP_CONVERTER_TICK_AMOUNT.get(), stack.count());
    var cap = stack.getCapability(ExperienceLibCapabilities.EXPERIENCE.item(), ItemAccess.forStack(stack.copyWithCount(1)));
    if (cap == null) return false;
    var capAmount = cap.getCapacityAsLong(0, ExperienceResource.EMPTY);
    try (Transaction root = Transaction.open(null)) {
      long capExtract = experienceTank.extract(0, capAmount * amount, root);
      if (capExtract == 0) return false;
      var extractedAmount = Ints.saturatedCast(capExtract / capAmount);
      if (extractedAmount < 1) return false;
      ItemStack extracted = internalInventory.simulateSimilarRemove(extractedAmount, Items.GLASS_BOTTLE.getDefaultInstance(), FuzzyMode.IGNORE_ALL, null);
      if (extracted.isEmpty()) return false;
      ItemStack remaining = internalInventory.addItems(Items.EXPERIENCE_BOTTLE.getDefaultInstance(), true);
      if (!remaining.isEmpty()) return false;
      internalInventory.removeItems(extracted.count(), extracted, null);
      internalInventory.addItems(Items.EXPERIENCE_BOTTLE.getDefaultInstance().copyWithCount(extracted.count()), false);
      root.commit();
      return true;
    }
  }

  @Override
  protected void collectImplicitComponents(DataComponentMap.Builder components) {
    super.collectImplicitComponents(components);
    components.set(AExpComponents.IN_INV, inventoryIn.toItemContainerContents());
    components.set(AExpComponents.OUT_INV, inventoryOut.toItemContainerContents());
    components.set(ExperienceLibRegistration.EXPERIENCE_COMPONENT, Mth.clamp(getExperience(), 0L, getExperienceCapacity()));
  }

  @Override
  protected void applyImplicitComponents(DataComponentGetter components) {
    super.applyImplicitComponents(components);
    getExperienceTank()
        .setExperience(0, Mth.clamp(components.getOrDefault(ExperienceLibRegistration.EXPERIENCE_COMPONENT, 0L), 0L, getExperienceCapacity()));
    inventoryIn
        .fromItemContainerContents(components.getOrDefault(AExpComponents.IN_INV, ItemContainerContents.fromItems(List.of(ItemStack.EMPTY))));
    inventoryOut
        .fromItemContainerContents(components.getOrDefault(AExpComponents.OUT_INV, ItemContainerContents.fromItems(List.of(ItemStack.EMPTY))));
  }

  public long getExperience() {
    return experienceTank.getAmountAsLong(0);
  }

  public long getExperienceCapacity() {
    return experienceTank.getCapacityAsLong(0, ExperienceResource.EMPTY);
  }

  @Override
  protected void saveAdditional(ValueOutput output) {
    super.saveAdditional(output);
    experienceTank.serialize(output.child(EXPERIENCE_TANK));
    var inv = getInternalInventory();
    if (inv != InternalInventory.empty()) {
      var opt = output.child(INVENTORY);
      for (int x = 0; x < inv.size(); x++) {
        var is = inv.getStackInSlot(x);
        opt.store("item" + x, ItemStack.OPTIONAL_CODEC, is);
      }
    }
    output.putBoolean("inverted", isInverted);
  }

  @Override
  protected void loadAdditional(ValueInput input) {
    super.loadAdditional(input);
    experienceTank.deserialize(input.childOrEmpty(EXPERIENCE_TANK));
    for (int i = 0; i < experienceTank.size(); i++) {
      experienceTank.setCapacity(i, AExpConfig.get().XP_CONVERTER_CAPACITY.get());
    }
    var inv = getInternalInventory();
    if (inv != InternalInventory.empty()) {
      input.child(INVENTORY).ifPresent(opt -> {
        for (int x = 0; x < inv.size(); x++) {
          inv.setItemDirect(x, opt.read("item" + x, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY));
        }
      });
    }
    this.isInverted = input.getBooleanOr("inverted", false);
    inventoryIn.setFilter(isInverted ? ApexItemFilters.INSERT_ONLY_GLASS : ApexItemFilters.INSERT_ONLY_XP);
    inventoryOut.setFilter(isInverted ? ApexItemFilters.EXTRACT_ONLY_XP : ApexItemFilters.EXTRACT_ONLY_GLASS);
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
    if (!getLevel().isClientSide()) {
      MenuOpener.open(AExpMenus.EXPERIENCE_CONVERTER, player, menuHostLocator);
    }
  }

  @Override
  public void saveChangedInventory(AppEngInternalInventory inv) {
    saveChanges();
  }

  public void saveChanges() {
    if (this.level == null) {
      return;
    }

    if (this.level.isClientSide()) {
      this.setChanged();
      requestModelDataUpdate();
    } else {
      this.level.blockEntityChanged(this.worldPosition);
      if (!this.setChangedQueued) {
        TickHandler.instance().addCallable(level, this::setChangedAtEndOfTick);
        this.setChangedQueued = true;
      }
    }
  }

  @Override
  public void onChangeInventory(AppEngInternalInventory inv, int slot) {
    this.setChanged();
  }

  private void setChangedAtEndOfTick(Level level) {
    this.setChanged();
    this.setChangedQueued = false;
  }

  @Override
  public boolean isClientSide() {
    Level level = getLevel();
    return level == null || level.isClientSide();
  }

  @Override
  public BlockEntity getEntity() {
    return this;
  }

  public static void registerCapability(final RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(
        ExperienceLibCapabilities.EXPERIENCE.block(),
        AExpBlockEntities.EXPERIENCE_CONVERTER.get(),
        (be, ctx) -> be.getExperienceTank()
    );
    event.registerItem(
        ExperienceLibCapabilities.EXPERIENCE.item(),
        (stack, access) -> {
          ItemAccessExperienceHandler handler = new ItemAccessExperienceHandler(Optional.ofNullable(access).orElse(ItemAccess.forStack(stack)), ExperienceLibRegistration.EXPERIENCE_COMPONENT.get(), Long.MAX_VALUE);
          handler.setExperience(0, stack.getOrDefault(ExperienceLibRegistration.EXPERIENCE_COMPONENT, 0L));
          return handler;
        },
        AExpBlocks.EXPERIENCE_CONVERTER.item().get()
    );
    event.registerBlockEntity(
        Capabilities.Item.BLOCK,
        AExpBlockEntities.EXPERIENCE_CONVERTER.get(),
        (be, ctx) -> be.internalInventory.toResourceHandler()
    );
    event.registerBlockEntity(
        Capabilities.Fluid.BLOCK,
        AExpBlockEntities.EXPERIENCE_CONVERTER.get(),
        (be, ctx) -> be.fluidHandler
    );
  }
}
