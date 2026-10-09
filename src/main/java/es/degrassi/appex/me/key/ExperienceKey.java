package es.degrassi.appex.me.key;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.definition.AExpText;
import es.degrassi.experiencelib.api.ExperienceStack;
import es.degrassi.experiencelib.common.definition.ExperienceLibText;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;

@Getter
public class ExperienceKey extends AEKey {
  public static final ExperienceKey KEY = new ExperienceKey();
  public static final long MAX_EXPERIENCE = 10_000;

  private static final Identifier ID = AppliedExperienced.id("experience");

  private ExperienceKey() {}

  @Override
  public AEKeyType getType() {
    return ExperienceKeyType.TYPE;
  }

  @Override
  public AEKey dropSecondary() {
    return this;
  }

  @Override
  public void toTag(ValueOutput output) {

  }

  @Override
  public Object getPrimaryKey() {
    return new ExperienceStack(1);
  }

  @Override
  public Identifier getId() {
    return ID;
  }

  @Override
  public void writeToPacket(RegistryFriendlyByteBuf data) {
  }

  @Override
  protected Component computeDisplayName() {
    return ExperienceLibText.experience.text();
  }

  @Override
  public void addDrops(long amount, List<ItemStack> drops, Level level, BlockPos pos) {

  }

  @Override
  public boolean hasComponents() {
    return false;
  }
}
