package es.degrassi.appexp.part;

import appeng.api.config.PowerUnit;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.p2p.CapabilityP2PTunnelPart;
import appeng.parts.p2p.P2PModels;
import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.appexp.me.key.ExperienceKeyType;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ExperienceP2PTunnelPart extends CapabilityP2PTunnelPart<ExperienceP2PTunnelPart, IExperienceHandler> {

  private static final P2PModels MODELS = new P2PModels(AppliedExperienced.id("part/experience_p2p_tunnel"));
  private static final IExperienceHandler NULL_EXPERIENCE_HANDLER = new NullExperienceHandler();

  public ExperienceP2PTunnelPart(IPartItem<?> partItem) {
    super(partItem, ExperienceLibCapabilities.EXPERIENCE.block());
    inputHandler = new InputExperienceHandler();
    outputHandler = new OutputExperienceHandler();
    emptyHandler = NULL_EXPERIENCE_HANDLER;
  }

  @PartModels
  public static List<IPartModel> getModels() {
    return MODELS.getModels();
  }

  @Override
  public IPartModel getStaticModels() {
    return MODELS.getModel(this.isPowered(), this.isActive());
  }

  private OutputExperienceHandler getOutputHandler() {
    return (OutputExperienceHandler) outputHandler;
  }

  @Override
  public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
    super.writeToNBT(data, registries);
    data.putLong("experience", getOutputHandler().bufferExperience);
  }

  @Override
  public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
    super.readFromNBT(data, registries);
    getOutputHandler().bufferExperience = data.getLong("experience");
  }

  private class InputExperienceHandler implements IExperienceHandler {

    @Override
    public boolean canAcceptExperience(long experience) {
      for (var output : getOutputs()) {
        if (output.getOutputHandler().canAcceptLocalExperience(experience)) {
          return true;
        }
      }

      return false;
    }

    @Override
    public boolean canProvideExperience(long experience) {
      return false;
    }

    @Override
    public long getMaxExtract() {
      return 0;
    }

    @Override
    public long getMaxReceive() {
      return getExperienceCapacity();
    }

    @Override
    public long getExperience() {
      return getOutputStream()
          .map(part -> part.getOutputHandler().getLocalExperience())
          .reduce(0L, Long::sum);
    }

    @Override
    public void setExperience(long stack) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setCapacity(long l) {
      throw new UnsupportedOperationException();
    }

    @Override
    public long getExperienceCapacity() {
      return getOutputStream()
          .map(part -> part.getOutputHandler().getLocalMaxExperience())
          .reduce(0L, Long::sum);
    }

    @Override
    public long receiveExperience(long stack, boolean simulate) {
      var outputs = getOutputStream()
          .filter(part -> part.getOutputHandler().canAcceptLocalExperience(stack))
          .toList();

      if (outputs.isEmpty()) {
        return 0;
      }

      if (!simulate) {
        deductEnergyCost((double) stack / ExperienceKeyType.TYPE.getAmountPerOperation(), PowerUnit.AE);
      }

      var forEach = stack / outputs.size();
      var spill = new AtomicLong(stack % outputs.size());
      var total = new AtomicLong(0);

      outputs.forEach(output -> total.addAndGet(output.getOutputHandler()
          .addExperienceRespectingBuffer(forEach + (spill.getAndDecrement() > 0 ? 1 : 0), simulate)));

      return total.get();
    }

    @Override
    public long extractExperience(long amount, boolean simulate) {
      return 0;
    }

    @Override
    public long extractExperienceRecipe(long amount, boolean simulate) {
      return 0;
    }

    @Override
    public long receiveExperienceRecipe(long amount, boolean simulate) {
      var outputs = getOutputStream()
          .filter(part -> part.getOutputHandler().canAcceptLocalExperience(amount))
          .toList();

      if (outputs.isEmpty()) {
        return 0;
      }

      if (!simulate) {
        deductEnergyCost((double) amount / ExperienceKeyType.TYPE.getAmountPerOperation(), PowerUnit.AE);
      }

      var forEach = amount / outputs.size();
      var spill = new AtomicLong(amount % outputs.size());
      var total = new AtomicLong(0);

      outputs.forEach(output -> total.addAndGet(output.getOutputHandler()
          .addExperienceRespectingBuffer(forEach + (spill.getAndDecrement() > 0 ? 1 : 0), simulate)));

      return total.get();
    }
  }

  private class OutputExperienceHandler implements IExperienceHandler {
    private static final long MAX_BUFFER = 1_000;

    private long bufferExperience = 0;

    private boolean canAcceptLocalExperience(long source) {
      return getLocalExperience() + source < getLocalMaxExperience();
    }

    private long addExperienceRespectingBuffer(long amount, boolean simulate) {
      long experience = 0;

      try (var guard = getAdjacentCapability()) {
        var tile = guard.get();

        if (tile != null && !(tile instanceof NullExperienceHandler)) {
          experience += tile.receiveExperienceRecipe(amount, simulate);
          amount = 0;
        }
      }

      // add to buffer only if no machine to add to
      bufferExperience += amount;

      if (bufferExperience > MAX_BUFFER) {
        bufferExperience = MAX_BUFFER;
      }

      experience += bufferExperience;

      return experience;
    }

    private long getLocalExperience() {
      try (var guard = getAdjacentCapability()) {
        return bufferExperience + guard.get().getExperience();
      }
    }

    private long getLocalMaxExperience() {
      try (var guard = getAdjacentCapability()) {
        return MAX_BUFFER + guard.get().getExperience();
      }
    }

    @Override
    public boolean canAcceptExperience(long experience) {
      return false;
    }

    @Override
    public boolean canProvideExperience(long experience) {
      return extractExperience(experience, true) > 0;
    }

    @Override
    public long getMaxExtract() {
      try (var input = getInputCapability()) {
        var tile = input.get();
        return tile != emptyHandler ? tile.getMaxExtract() : MAX_BUFFER;
      }
    }

    @Override
    public long getMaxReceive() {
      return 0;
    }

    @Override
    public long getExperience() {
      try (var input = getInputCapability()) {
        return input.get().getExperience() + bufferExperience;
      }
    }

    @Override
    public void setExperience(long stack) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setCapacity(long l) {
      throw new UnsupportedOperationException();
    }

    @Override
    public long getExperienceCapacity() {
      try (var input = getInputCapability()) {
        return input.get().getExperienceCapacity() + MAX_BUFFER;
      }
    }

    @Override
    public long receiveExperience(long stack, boolean actionable) {
      return 0;
    }

    @Override
    public long extractExperience(long amount, boolean simulate) {
      // use buffer first
      if (bufferExperience >= amount) {
        bufferExperience -= amount;
        return 0;
      } else {
        bufferExperience = 0;
      }

      try (var input = getInputCapability()) {
        var result = input.get().extractExperience(amount, simulate);

        if (!simulate) {
          deductEnergyCost((double) result / ExperienceKeyType.TYPE.getAmountPerOperation(), PowerUnit.AE);
        }

        return result;
      }
    }

    @Override
    public long extractExperienceRecipe(long amount, boolean simulate) {
      if (bufferExperience >= amount) {
        bufferExperience -= amount;
        return 0;
      } else {
        bufferExperience = 0;
      }

      try (var input = getInputCapability()) {
        var result = input.get().extractExperienceRecipe(amount, simulate);

        if (!simulate) {
          deductEnergyCost((double) result / ExperienceKeyType.TYPE.getAmountPerOperation(), PowerUnit.AE);
        }

        return result;
      }
    }

    @Override
    public long receiveExperienceRecipe(long amount, boolean simulate) {
      return 0;
    }
  }

  private static class NullExperienceHandler implements IExperienceHandler {
    @Override
    public boolean canAcceptExperience(long experience) {
      return false;
    }

    @Override
    public boolean canProvideExperience(long experience) {
      return false;
    }

    @Override
    public long getMaxExtract() {
      return 0;
    }

    @Override
    public long getMaxReceive() {
      return 0;
    }

    @Override
    public long getExperience() {
      return 0;
    }

    @Override
    public long getExperienceCapacity() {
      return 0;
    }

    @Override
    public void setExperience(long experience) {

    }

    @Override
    public void setCapacity(long l) {

    }

    @Override
    public long receiveExperience(long experience, boolean simulate) {
      return 0;
    }

    @Override
    public long extractExperience(long experience, boolean simulate) {
      return 0;
    }

    @Override
    public long extractExperienceRecipe(long amount, boolean simulate) {
      return 0;
    }

    @Override
    public long receiveExperienceRecipe(long amount, boolean simulate) {
      return 0;
    }
  }
}
