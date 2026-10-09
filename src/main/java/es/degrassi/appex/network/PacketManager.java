package es.degrassi.appex.network;

import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.network.client.CExperienceButtonClickedPacket;
import es.degrassi.appex.network.client.CInvertExperienceButtonClickedPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = AppliedExperienced.MODID)
public class PacketManager {
  @SubscribeEvent
  public static void register(final RegisterPayloadHandlersEvent event) {
    final PayloadRegistrar registrar = event.registrar(AppliedExperienced.MODID);
    // TO CLIENT

    // TO SERVER
    registrar.playToServer(CExperienceButtonClickedPacket.TYPE, CExperienceButtonClickedPacket.CODEC, CExperienceButtonClickedPacket::handle);
    registrar.playToServer(CInvertExperienceButtonClickedPacket.TYPE, CInvertExperienceButtonClickedPacket.CODEC, CInvertExperienceButtonClickedPacket::handle);
  }
}
