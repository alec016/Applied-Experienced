package es.degrassi.appexp.definition;

import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.appexp.block.ExperienceAcceptorBlock;
import es.degrassi.appexp.block.ExperienceConverterBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class AExpBlocks {
  private AExpBlocks() {
  }

  public static final DeferredRegister.Blocks DR = DeferredRegister.createBlocks(AppliedExperienced.MODID);

  private static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();

  public static List<BlockDefinition<?>> getBlocks() {
    return Collections.unmodifiableList(BLOCKS);
  }

  public static final BlockDefinition<ExperienceConverterBlock> EXPERIENCE_CONVERTER =
      block("Experience Tank", "experience_converter", ExperienceConverterBlock::new);

  public static final BlockDefinition<ExperienceAcceptorBlock> EXPERIENCE_ACCEPTOR =
      block("ME Experience Acceptor", "experience_acceptor", ExperienceAcceptorBlock::new);

  private static <T extends Block> BlockDefinition<T> block(
      String englishName, String id, Supplier<T> blockSupplier) {
    var block = DR.register(id, blockSupplier);
    var item = AExpItems.DR.registerItem(id, p -> new BlockItem(block.get(), p));
    var definition = new BlockDefinition<>(englishName, block, new ItemDefinition<>(englishName, item));
    BLOCKS.add(definition);
    return definition;
  }
}
