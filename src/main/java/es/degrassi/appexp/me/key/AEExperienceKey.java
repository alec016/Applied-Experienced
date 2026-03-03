package es.degrassi.appexp.me.key;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.experiencelib.api.xei.ExperienceKey;
import es.degrassi.experiencelib.api.xei.ExperienceStack;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

@Getter
public class AEExperienceKey extends AEKey {
  public static final AEExperienceKey KEY = new AEExperienceKey();
  public static final long MAX_EXPERIENCE = 10_000;

  private static final ResourceLocation ID = AppliedExperienced.id("experience");
  private static final ExperienceKey PK = ExperienceStack.EMPTY.getKey();

  private AEExperienceKey() {}

  @Override
  public AEKeyType getType() {
    return ExperienceKeyType.TYPE;
  }

  @Override
  public AEKey dropSecondary() {
    return this;
  }

  @Override
  public CompoundTag toTag(HolderLookup.Provider registries) {
    return new CompoundTag();
  }

  @Override
  public Object getPrimaryKey() {
    return PK;
  }

  @Override
  public ResourceLocation getId() {
    return ID;
  }

  @Override
  public void writeToPacket(RegistryFriendlyByteBuf data) {
  }

  @Override
  protected Component computeDisplayName() {
    return Component.translatable("text.appex.experience");
  }

  @Override
  public void addDrops(long amount, List<ItemStack> drops, Level level, BlockPos pos) {

  }

  public ExperienceStack getStack() {
    return ExperienceStack.EMPTY.copyWithAmount(1);
  }

  @Override
  public boolean hasComponents() {
    return false;
  }
}
