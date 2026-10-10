package es.degrassi.appex.network.client;

import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.client.Amount;
import es.degrassi.appex.client.container.ExperienceAccessTermMenu;
import es.degrassi.appex.util.inv.MEExperienceUtils;
import es.degrassi.experiencelib.api.codec.EnumCodec;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.NonNull;

public record CExperienceButtonClickedInPartPacket(int containerId, Amount amount, boolean extraction) implements CustomPacketPayload {
  public static final Type<CExperienceButtonClickedInPartPacket> TYPE = new Type<>(AppliedExperienced.id("experience_button_clicked_in_part"));
  private static final NamedCodec<CExperienceButtonClickedInPartPacket> codec = NamedCodec.record(instance -> instance.group(
      NamedCodec.INT.fieldOf("containerId").forGetter(CExperienceButtonClickedInPartPacket::containerId),
      EnumCodec.of(Amount.class).fieldOf("amount").forGetter(CExperienceButtonClickedInPartPacket::amount),
      NamedCodec.BOOL.fieldOf("extraction").forGetter(CExperienceButtonClickedInPartPacket::extraction)
  ).apply(instance, CExperienceButtonClickedInPartPacket::new), "CExperienceButtonClickedInPartPacket");
  public static final StreamCodec<RegistryFriendlyByteBuf, CExperienceButtonClickedInPartPacket> CODEC = codec.streamCodec();

  @Override
  public @NonNull Type<CExperienceButtonClickedInPartPacket> type() {
    return TYPE;
  }

  public static void handle(CExperienceButtonClickedInPartPacket packet, IPayloadContext context) {
    if (context.player() instanceof ServerPlayer player) {
      if (player.containerMenu.containerId == packet.containerId) {
        if (player.containerMenu instanceof ExperienceAccessTermMenu menu) {
          var delegating = menu.getEntity().getInventory();
          if (packet.amount.isAll()) MEExperienceUtils.addAllLevelToPlayer(delegating, packet.extraction, player);
          else MEExperienceUtils.addLevelToPlayer(delegating, packet.amount.getAmount(packet.extraction), player);
        }
      }
    }
  }
}
