package es.degrassi.appex.definition;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.core.definitions.ItemDefinition;
import appeng.items.parts.PartItem;
import es.degrassi.appex.AppliedExperienced;
import es.degrassi.appex.part.ExperienceAcceptorPart;
import es.degrassi.appex.part.ExperienceP2PTunnelPart;
import es.degrassi.appex.part.reporting.ExperienceAccessTerminalPart;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

import static es.degrassi.appex.definition.AExpItems.item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AExpParts {
  public static final ItemDefinition<PartItem<ExperienceAcceptorPart>> EXPERIENCE_ACCEPTOR = createPart(
      "ME Cable Experience Acceptor",
      AppliedExperienced.id("cable_experience_acceptor"),
      ExperienceAcceptorPart.class,
      ExperienceAcceptorPart::new
  );

  public static final ItemDefinition<PartItem<ExperienceP2PTunnelPart>> EXPERIENCE_P2P_TUNNEL = createPart(
      "Experience P2P Tunnel",
      AppliedExperienced.id("experience_p2p_tunnel"),
      ExperienceP2PTunnelPart.class,
      ExperienceP2PTunnelPart::new
  );

  public static final ItemDefinition<PartItem<ExperienceAccessTerminalPart>> EXPERIENCE_ACCESS_TERMINAL = createPart(
      "ME Experience Access Terminal",
      AppliedExperienced.id("experience_access_terminal"),
      ExperienceAccessTerminalPart.class,
      ExperienceAccessTerminalPart::new
  );

  private static <T extends IPart> ItemDefinition<PartItem<T>> createPart(
      String englishName,
      Identifier id,
      Class<T> partClass,
      Function<IPartItem<T>, T> factory) {
    return item(englishName, id, props -> new PartItem<>(props, partClass, factory));
  }

  // Used to control in which order static constructors are called
  public static void init() {
  }

}
