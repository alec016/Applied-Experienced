package es.degrassi.appexp.me.key;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKeyTypes;
import com.mojang.serialization.MapCodec;
import es.degrassi.appexp.definition.AExpText;
import es.degrassi.appexp.AppliedExperienced;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.Nullable;

public class ExperienceKeyType extends AEKeyType {
  public static final AEKeyType TYPE = new ExperienceKeyType();
  private static final MapCodec<AEExperienceKey> MAP_CODEC = MapCodec.unit(AEExperienceKey.KEY);

  public static void register(RegisterEvent event) {
    if (event.getRegistryKey().equals(Registries.ITEM)) {
      AEKeyTypes.register(TYPE);
    }
  }

  private ExperienceKeyType() {
    super(AppliedExperienced.id("experience"), AEExperienceKey.class, AExpText.EXPERIENCE.formatted());
  }

  public MapCodec<? extends AEKey> codec() {
    return MAP_CODEC;
  }

  public int getAmountPerOperation() {
    return 128;
  }

  public int getAmountPerByte() {
    return 128;
  }

  @Override
  public @Nullable AEKey readFromPacket(RegistryFriendlyByteBuf input) {
    return AEExperienceKey.KEY;
  }

  @Override
  public @Nullable String getUnitSymbol() {
    return "XP";
  }
}
