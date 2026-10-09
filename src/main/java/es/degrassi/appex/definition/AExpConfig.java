package es.degrassi.appex.definition;

import lombok.Getter;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

public final class AExpConfig {
  private static final AExpConfig INSTANCE;
  @Getter
  private static final ModConfigSpec spec;

  static {
    Pair<AExpConfig, ModConfigSpec> pair = new Builder().configure(AExpConfig::new);
    INSTANCE = pair.getLeft();
    spec = pair.getRight();
  }

  public final ConfigValue<Long> XP_CONVERTER_CAPACITY;
  public final ConfigValue<Integer> XP_CONVERSION_RATE;
  public final ConfigValue<Integer> XP_CONVERTER_TICK_AMOUNT;

  public AExpConfig(Builder builder) {
    builder.push("Experience Converter");
    this.XP_CONVERTER_CAPACITY = builder
        .comment("Defines de Experience Converter capacity in XP Points")
        .defineInRange("capacity", Integer.MAX_VALUE, 1L, Integer.MAX_VALUE);
    this.XP_CONVERSION_RATE = builder
        .comment("Defines the amount(mB) necessary per XP point")
        .defineInRange("rate", 250, 1, Integer.MAX_VALUE, Integer.class);
    this.XP_CONVERTER_TICK_AMOUNT = builder
        .comment("Defines the amount of items to extract each tick at max")
        .defineInRange("amount", 1, 1, Integer.MAX_VALUE);
    builder.pop();
  }

  public static AExpConfig get() {
    return INSTANCE;
  }
}
