package es.degrassi.appexp.definition;

import es.degrassi.appexp.AppliedExperienced;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public abstract class AExpTags {
  private static ResourceLocation rl(String name, boolean isNeoForge) {
    return isNeoForge ? ResourceLocation.fromNamespaceAndPath("c", name) :
        AppliedExperienced.id(name);
  }

  private static TagKey<Block> blockTag(String name, boolean isNeoForge) {
    return BlockTags.create(rl(name, isNeoForge));
  }

  private static TagKey<Fluid> fluidTag(String name, boolean isNeoForge) {
    return FluidTags.create(rl(name, isNeoForge));
  }

  private static TagKey<Item> itemTag(String name, boolean isNeoForge) {
    return ItemTags.create(rl(name, isNeoForge));
  }

  private static class Tag<T> {
    private final TagKey<T> tag;
    protected Tag(TagKey<T> tag) {
      this.tag = tag;
    }

    public TagKey<T> get() {
      return tag;
    }
  }

  public static final class Blocks extends Tag<Block> {

    public Blocks(boolean isNeoForge, String name) {
      super(blockTag(name, isNeoForge));
    }

    public Blocks(String name) {
      this(false, name);
    }
  }

  public static final class Items extends Tag<Item> {

    public Items(boolean isNeoForge, String name) {
      super(itemTag(name, isNeoForge));
    }

    public Items(String name) {
      this(false, name);
    }
  }

  public static final class Fluids extends Tag<Fluid> {
    public static final TagKey<Fluid> EXPERIENCE = new Fluids(true, "experience").get();

    public Fluids(boolean isNeoForge, String name) {
      super(fluidTag(name, isNeoForge));
    }

    public Fluids(String name) {
      this(false, name);
    }
  }
}
