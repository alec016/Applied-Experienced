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
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
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
    public int getTanks() {
      return 1;
    }

    @Override
    public boolean canAcceptExperience(int tank, long experience) {
      for (var output : getOutputs()) {
        if (output.getOutputHandler().canAcceptLocalExperience(tank, experience)) {
          return true;
        }
      }

      return false;
    }

    @Override
    public boolean canProvideExperience(int tank, long experience) {
      return false;
    }

    @Override
    public long getMaxExtract(int tank) {
      return 0;
    }

    @Override
    public long getMaxReceive(int tank) {
      return getExperienceCapacity();
    }

    @Override
    public long getExperience() {
      return getOutputStream()
          .map(part -> part.getOutputHandler().getLocalExperience())
          .reduce(0L, Long::sum);
    }

    @Override
    public void setExperience(int tank, long stack) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setCapacity(int tank, long l) {
      throw new UnsupportedOperationException();
    }

    @Override
    public long getExperienceCapacity() {
      return getOutputStream()
          .map(part -> part.getOutputHandler().getLocalMaxExperience())
          .reduce(0L, Long::sum);
    }

    @Override
    public long receiveExperience(int tank, long stack, boolean simulate) {
      var outputs = getOutputStream()
          .filter(part -> part.getOutputHandler().canAcceptLocalExperience(tank, stack))
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
          .addExperienceRespectingBuffer(tank, forEach + (spill.getAndDecrement() > 0 ? 1 : 0), simulate)));

      return total.get();
    }

    @Override
    public long extractExperience(int tank, long amount, boolean simulate) {
      return 0;
    }

    @Override
    public long extractExperienceRecipe(int tank, long amount, boolean simulate) {
      return 0;
    }

    @Override
    public long receiveExperienceRecipe(int tank, long amount, boolean simulate) {
      var outputs = getOutputStream()
          .filter(part -> part.getOutputHandler().canAcceptLocalExperience(tank, amount))
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
          .addExperienceRespectingBuffer(tank, forEach + (spill.getAndDecrement() > 0 ? 1 : 0), simulate)));

      return total.get();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
      return new CompoundTag();
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {

    }
  }

  private class OutputExperienceHandler implements IExperienceHandler {
    private static final long MAX_BUFFER = 1_000;

    private long bufferExperience = 0;

    private boolean canAcceptLocalExperience(int tank, long source) {
      return getLocalExperience() + source < getLocalMaxExperience();
    }

    private long addExperienceRespectingBuffer(int tank, long amount, boolean simulate) {
      long experience = 0;

      try (var guard = getAdjacentCapability()) {
        var tile = guard.get();

        if (tile != null && !(tile instanceof NullExperienceHandler)) {
          experience += tile.receiveExperienceRecipe(tank, amount, simulate);
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
    public int getTanks() {
      return 1;
    }

    @Override
    public boolean canAcceptExperience(int tank, long experience) {
      return false;
    }

    @Override
    public boolean canProvideExperience(int tank, long experience) {
      return extractExperience(tank, experience, true) > 0;
    }

    @Override
    public long getMaxExtract(int tank) {
      try (var input = getInputCapability()) {
        var tile = input.get();
        return tile != emptyHandler ? tile.getMaxExtract(tank) : MAX_BUFFER;
      }
    }

    @Override
    public long getMaxReceive(int tank) {
      return 0;
    }

    @Override
    public long getExperience() {
      try (var input = getInputCapability()) {
        return input.get().getExperience() + bufferExperience;
      }
    }

    @Override
    public void setExperience(int tank, long stack) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setCapacity(int tank, long l) {
      throw new UnsupportedOperationException();
    }

    @Override
    public long getExperienceCapacity() {
      try (var input = getInputCapability()) {
        return input.get().getExperienceCapacity() + MAX_BUFFER;
      }
    }

    @Override
    public long receiveExperience(int tank, long stack, boolean actionable) {
      return 0;
    }

    @Override
    public long extractExperience(int tank, long amount, boolean simulate) {
      // use buffer first
      if (bufferExperience >= amount) {
        bufferExperience -= amount;
        return 0;
      } else {
        bufferExperience = 0;
      }

      try (var input = getInputCapability()) {
        var result = input.get().extractExperience(tank, amount, simulate);

        if (!simulate) {
          deductEnergyCost((double) result / ExperienceKeyType.TYPE.getAmountPerOperation(), PowerUnit.AE);
        }

        return result;
      }
    }

    @Override
    public long extractExperienceRecipe(int tank, long amount, boolean simulate) {
      if (bufferExperience >= amount) {
        bufferExperience -= amount;
        return 0;
      } else {
        bufferExperience = 0;
      }

      try (var input = getInputCapability()) {
        var result = input.get().extractExperienceRecipe(tank, amount, simulate);

        if (!simulate) {
          deductEnergyCost((double) result / ExperienceKeyType.TYPE.getAmountPerOperation(), PowerUnit.AE);
        }

        return result;
      }
    }

    @Override
    public long receiveExperienceRecipe(int tank, long amount, boolean simulate) {
      return 0;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
      return null;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {

    }
  }

  private static class NullExperienceHandler implements IExperienceHandler {
    @Override
    public int getTanks() {
      return 0;
    }

    @Override
    public boolean canAcceptExperience(int tank, long experience) {
      return false;
    }

    @Override
    public boolean canProvideExperience(int tank, long experience) {
      return false;
    }

    @Override
    public long getMaxExtract(int tank) {
      return 0;
    }

    @Override
    public long getMaxReceive(int tank) {
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
    public void setExperience(int tank, long experience) {

    }

    @Override
    public void setCapacity(int tank, long l) {

    }

    @Override
    public long receiveExperience(int tank, long experience, boolean simulate) {
      return 0;
    }

    @Override
    public long extractExperience(int tank, long experience, boolean simulate) {
      return 0;
    }

    @Override
    public long extractExperienceRecipe(int tank, long amount, boolean simulate) {
      return 0;
    }

    @Override
    public long receiveExperienceRecipe(int tank, long amount, boolean simulate) {
      return 0;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
      return new CompoundTag();
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {

    }
  }
}
