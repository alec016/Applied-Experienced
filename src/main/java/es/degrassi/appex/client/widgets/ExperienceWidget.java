package es.degrassi.appex.client.widgets;

import appeng.client.gui.widgets.ITooltip;
import com.google.common.collect.Lists;
import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import es.degrassi.appex.client.container.ExperienceConverterContainer;
import es.degrassi.appex.definition.AExpText;
import es.degrassi.experiencelib.util.ExperienceUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExperienceWidget extends AbstractWidget implements ITooltip {
  private final int screenWidth;
  private final ExperienceConverterContainer menu;

  public ExperienceWidget(int screenWidth, ExperienceConverterContainer menu) {
    super(8, 0, 16, 16, Component.empty());
    this.screenWidth = screenWidth;
    this.menu = menu;
  }

  @Override
  public void playDownSound(SoundManager handler) {
  }

  @Override
  protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
    if (this.visible) {
      this.width = screenWidth - 16;
      long level = ExperienceUtils.getLevelFromXp(menu.xp);
      String levels = "" + level;
      int xPos = this.getX() + this.width / 2 - Minecraft.getInstance().font.width(levels) / 2;
      graphics.text(Minecraft.getInstance().font, levels, xPos, this.getY(), 0x80FF20, true);
      graphics.fill(this.getX(), this.getY() + 9, this.getX() + this.width, this.getY() + 12, 0xFF000000);
      long xpDiff = menu.xp - ExperienceUtils.getXpFromLevel(level);
      if (xpDiff > 0) {
        double percent = (double) xpDiff / ExperienceUtils.getXpNeededForNextLevel(level);
        graphics.fill(this.getX() + 1, this.getY() + 10, this.getX() + 1 + Math.max((int) Math.ceil(this.width * percent) - 2, 0), this.getY() + 11, 0xFF80FF20);
      }
    }
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
  }

  @Override
  public Rect2i getTooltipArea() {
    return new Rect2i(
        getX(),
        getY(),
        screenWidth - 16,
        getHeight());
  }

  @Override
  public boolean isTooltipAreaVisible() {
    return visible;
  }

  @Override
  public List<Component> getTooltipMessage() {
    String literal = ExperienceUtils.format(menu.xp);
    String capacityLiteral = ExperienceUtils.format(menu.capacity) + "XP";
    String level = ExperienceUtils.getLevelFromXp(menu.xp) + "";
    String capacityLevel = ExperienceUtils.getLevelFromXp(menu.capacity) + " levels";
    return Lists.newArrayList(
        AExpText.EXPERIENCE_WIDGET.text(literal, capacityLiteral).withStyle(ChatFormatting.GRAY),
        AExpText.EXPERIENCE_WIDGET.text(level, capacityLevel).withStyle(ChatFormatting.GRAY)
    );
  }
}
