package es.degrassi.appex.definition;

import es.degrassi.appex.AppliedExperienced;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public abstract class AExpTags {
  private static final Set<Tag<?>> tags = new HashSet<>();

  public static void addLocations(BiConsumer<TagKey<?>, String> consumer) {
    tags.parallelStream().filter(Tag::isNotNeoforge).forEach(tag -> consumer.accept(tag.get(), tag.getEnglishName()));
  }

  private static Identifier rl(String name, boolean isNeoForge) {
    return isNeoForge ? Identifier.fromNamespaceAndPath("c", name) : AppliedExperienced.id(name);
  }

  private static TagKey<Block> blockTag(String name, boolean isNeoForge) {
    return blockTag(rl(name, isNeoForge));
  }

  private static TagKey<Block> blockTag(Identifier id) {
    return BlockTags.create(id);
  }

  private static TagKey<Fluid> fluidTag(String name, boolean isNeoForge) {
    return fluidTag(rl(name, isNeoForge));
  }

  private static TagKey<Fluid> fluidTag(Identifier id) {
    return FluidTags.create(id);
  }

  private static TagKey<Item> itemTag(String name, boolean isNeoForge) {
    return itemTag(rl(name, isNeoForge));
  }

  private static TagKey<Item> itemTag(Identifier id) {
    return ItemTags.create(id);
  }

  private static class Tag<T> {
    private final TagKey<T> tag;
    @Getter
    @Nullable
    private final String englishName;
    @Getter(AccessLevel.PRIVATE)
    private final boolean neoforge;

    protected Tag(TagKey<T> tag, @Nullable String englishName, boolean neoforge) {
      this.tag = tag;
      this.englishName = englishName;
      this.neoforge = neoforge;
      tags.add(this);
    }

    public TagKey<T> get() {
      return tag;
    }

    private boolean isNotNeoforge() {
      return !neoforge;
    }
  }

  public static final class Blocks extends Tag<Block> {
    public Blocks(String name) {
      this(true, name, null);
    }

    public Blocks(boolean isNeoForge, String name, @Nullable String englishName) {
      super(blockTag(name, isNeoForge), englishName, isNeoForge);
    }

    public Blocks(String name, String englishName) {
      this(false, name, englishName);
    }
  }

  public static final class Items extends Tag<Item> {
    public static final TagKey<Item> CURIO = new Items(Identifier.parse("curios:curio")).get();
    public static TagKey<Item> XP_P2P = new Items("p2p_attunements/experience_p2p_tunnel", "Experience P2P attunement").get();
    public Items(String name) {
      this(true, name, null);
    }

    public Items(Identifier id) {
      super(itemTag(id), null, true);
    }

    public Items(boolean isNeoForge, String name, String englishName) {
      super(itemTag(name, isNeoForge), englishName, isNeoForge);
    }

    public Items(String name, String englishName) {
      this(false, name, englishName);
    }
  }

  public static final class Fluids extends Tag<Fluid> {
    public static final TagKey<Fluid> EXPERIENCE = new Fluids("experience").get();

    public Fluids(String name, String englishName) {
      this(false, name, englishName);
    }

    public Fluids(boolean isNeoForge, String name, String englishName) {
      super(fluidTag(name, isNeoForge), englishName, isNeoForge);
    }

    public Fluids(String name) {
      this(true, name, null);
    }
  }
}
