package es.degrassi.appex.definition;

import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.block.ExperienceAcceptorBlock;
import es.degrassi.appex.block.ExperienceConverterBlock;
import es.degrassi.appex.item.ExperienceConverterItem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AExpBlocks {

  public static final DeferredRegister.Blocks DR = DeferredRegister.createBlocks(AppliedExperienced.MODID);

  private static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();

  public static List<BlockDefinition<?>> getBlocks() {
    return Collections.unmodifiableList(BLOCKS);
  }

  public static final BlockDefinition<ExperienceConverterBlock> EXPERIENCE_CONVERTER =
      blockWithItem(
          "Experience Tank",
          "experience_converter",
          ExperienceConverterBlock::new,
          ExperienceConverterItem::new
      );

  public static final BlockDefinition<ExperienceAcceptorBlock> EXPERIENCE_ACCEPTOR =
      blockWithItem(
          "ME Experience Acceptor",
          "experience_acceptor",
          ExperienceAcceptorBlock::new,
          BlockItem::new
      );

  private static <T extends Block> BlockDefinition<T> blockWithItem(
      String englishName,
      String id,
      Function<BlockBehaviour.Properties, T> blockSupplier,
      BiFunction<T, Item.Properties, BlockItem> itemFunction
  )  {
    var block = DR.registerBlock(id, blockSupplier);
    var item = AExpItems.DR.registerItem(id, p -> itemFunction.apply(block.get(), p));
    var definition = new BlockDefinition<>(englishName, block, new ItemDefinition<>(englishName, item));
    BLOCKS.add(definition);
    return definition;
  }
}
