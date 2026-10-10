package es.degrassi.appex.util.inv;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.MEStorage;
import com.google.common.primitives.Ints;
import es.degrassi.appex.me.key.ExperienceKey;
import es.degrassi.experiencelib.util.ExperienceUtils;
import net.minecraft.world.entity.player.Player;

public class MEExperienceUtils {
  public static void addLevelToPlayer(MEStorage storage, int levelDiff, Player player) {
    int requestedLevel = player.experienceLevel + levelDiff;
    requestedLevel = Math.max(requestedLevel, 0);
    long playerXp = ExperienceUtils.getPlayerTotalXp(player);
    long requestedXp = ExperienceUtils.getXpFromLevel(requestedLevel) - playerXp;
    long awardXp = levelDiff > 0
        ? Math.min(storage.getAvailableStacks().get(ExperienceKey.KEY), requestedXp)
        : -Math.min(Math.abs(requestedXp), storage.insert(ExperienceKey.KEY, Math.abs(requestedXp), Actionable.SIMULATE, IActionSource.ofPlayer(player)));
    if (awardXp == 0) return;
    awardXP(storage, Ints.saturatedCast(awardXp), player);
  }

  public static void addAllLevelToPlayer(MEStorage storage, boolean give, Player player) {
    long awardXp;
    if (give) {
      awardXp = storage.extract(ExperienceKey.KEY, Long.MAX_VALUE, Actionable.SIMULATE, IActionSource.ofPlayer(player));
    } else {
      awardXp = -Math.min(ExperienceUtils.getPlayerTotalXp(player), storage.insert(ExperienceKey.KEY, Long.MAX_VALUE, Actionable.SIMULATE, IActionSource.ofPlayer(player)));
    }
    if (awardXp == 0) return;
    awardXP(storage, Ints.saturatedCast(awardXp), player);
  }

  public static void awardXP(MEStorage handler, int xpOrigin, Player player) {
    if (xpOrigin > 0) {
      handler.extract(ExperienceKey.KEY, xpOrigin, Actionable.MODULATE, IActionSource.ofPlayer(player));
    } else {
      handler.insert(ExperienceKey.KEY, Math.abs(xpOrigin), Actionable.MODULATE, IActionSource.ofPlayer(player));
    }
    player.giveExperiencePoints(xpOrigin);
  }
}
