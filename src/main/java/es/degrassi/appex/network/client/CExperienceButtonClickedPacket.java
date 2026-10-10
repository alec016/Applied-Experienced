package es.degrassi.appex.network.client;

import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.block.entity.ExperienceConverterEntity;
import es.degrassi.appex.client.Amount;
import es.degrassi.experiencelib.util.ExperienceUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public record CExperienceButtonClickedPacket(BlockPos entityPos, Amount amount,
                                             boolean extraction) implements CustomPacketPayload {
  public static final Type<CExperienceButtonClickedPacket> TYPE = new Type<>(AppliedExperienced.id("experience_button_clicked"));

  public static final StreamCodec<RegistryFriendlyByteBuf, CExperienceButtonClickedPacket> CODEC = new StreamCodec<>() {
    @Override
    public CExperienceButtonClickedPacket decode(RegistryFriendlyByteBuf buffer) {
      return new CExperienceButtonClickedPacket(buffer.readBlockPos(), buffer.readEnum(Amount.class), buffer.readBoolean());
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buffer, CExperienceButtonClickedPacket value) {
      buffer.writeBlockPos(value.entityPos);
      buffer.writeEnum(value.amount);
      buffer.writeBoolean(value.extraction);
    }
  };

  @Override
  public Type<CExperienceButtonClickedPacket> type() {
    return TYPE;
  }

  public static void handle(CExperienceButtonClickedPacket packet, IPayloadContext context) {
    if (context.player() instanceof ServerPlayer player) {
      if (player.level().getBlockEntity(packet.entityPos) instanceof ExperienceConverterEntity entity) {
        var delegating = entity.getExperienceTank();
        try (Transaction root = Transaction.openRoot()) {
          if (packet.amount.isAll()) ExperienceUtils.addAllLevelToPlayer(delegating, packet.extraction, player, root);
          else ExperienceUtils.addLevelToPlayer(delegating, packet.amount.getAmount(packet.extraction), player, root);
          root.commit();
        }
      }
    }
  }
}
