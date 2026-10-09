package es.degrassi.appex.network.client;

import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.block.entity.ExperienceConverterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CInvertExperienceButtonClickedPacket(BlockPos pos) implements CustomPacketPayload {
  public static final Type<CInvertExperienceButtonClickedPacket> TYPE = new Type<>(AppliedExperienced.id("experience_invert_button_clicked"));

  public static final StreamCodec<RegistryFriendlyByteBuf, CInvertExperienceButtonClickedPacket> CODEC = new StreamCodec<>() {
    @Override
    public CInvertExperienceButtonClickedPacket decode(RegistryFriendlyByteBuf buffer) {
      return new CInvertExperienceButtonClickedPacket(buffer.readBlockPos());
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buffer, CInvertExperienceButtonClickedPacket value) {
      buffer.writeBlockPos(value.pos);
    }
  };

  @Override
  public Type<CInvertExperienceButtonClickedPacket> type() {
    return TYPE;
  }

  public static void handle(CInvertExperienceButtonClickedPacket packet, IPayloadContext context) {
    if (context.player() instanceof ServerPlayer player) {
      if (player.level().getBlockEntity(packet.pos) instanceof ExperienceConverterEntity entity) {
        entity.setInverted(!entity.isInverted());
      }
    }
  }
}
