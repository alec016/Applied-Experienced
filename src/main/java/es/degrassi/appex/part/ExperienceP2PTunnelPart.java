package es.degrassi.appex.part;

import appeng.api.config.PowerUnit;
import appeng.api.parts.IPartItem;
import appeng.parts.p2p.CapabilityP2PTunnelPart;
import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import es.degrassi.experiencelib.api.ExperienceResource;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.experiencelib.transfer.EmptyExperienceHandler;
import es.degrassi.experiencelib.util.TransferPreconditions;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExperienceP2PTunnelPart extends CapabilityP2PTunnelPart<ExperienceP2PTunnelPart, IExperienceHandler> {

  public ExperienceP2PTunnelPart(IPartItem<?> partItem) {
    super(partItem, ExperienceLibCapabilities.EXPERIENCE.block());
    this.inputHandler = new InputExperienceHandler();
    this.outputHandler = new OutputExperienceHandler();
    this.emptyHandler = EmptyExperienceHandler.INSTANCE;
  }

  private class InputExperienceHandler implements IExperienceHandler {

    @Override
    public int size() {
      return 1;
    }

    @Override
    public long getMaxExtract(int tank) {
      return 0;
    }

    @Override
    public long getMaxInsert(int tank) {
      return getCapacityAsLong(tank, ExperienceResource.EMPTY);
    }

    @Override
    public long getAmountAsLong(int tank) {
      long tot = 0;
      for (var output : getOutputs()) {
        try (var capabilityGuard = output.getAdjacentCapability()) {
          var tempAmount = capabilityGuard.get().getAmountAsLong(tank);
          if (tempAmount == Long.MAX_VALUE) return Long.MAX_VALUE;
          if (Math.min(tot + tempAmount, Long.MAX_VALUE) == Long.MAX_VALUE) return Long.MAX_VALUE;
          tot += capabilityGuard.get().getAmountAsLong(tank);
        }
      }
      return tot;
    }

    @Override
    public void setExperience(int tank, long stack) {
    }

    @Override
    public void setCapacity(int tank, long l) {
    }

    @Override
    public long getCapacityAsLong(int tank, ExperienceResource resource) {
      long tot = 0;
      for (var output : getOutputs()) {
        try (var capabilityGuard = output.getAdjacentCapability()) {
          var tempCapacity = capabilityGuard.get().getCapacityAsLong(tank, ExperienceResource.EMPTY);
          if (tempCapacity == Long.MAX_VALUE) return Long.MAX_VALUE;
          if (Math.min(tot + tempCapacity, Long.MAX_VALUE) == Long.MAX_VALUE) return Long.MAX_VALUE;
          tot += tempCapacity;
        }
      }
      return tot;
    }

    @Override
    public long insert(int tank, long stack, TransactionContext tx) {
      return insertRecipe(tank, stack, tx);
    }

    @Override
    public long extract(int tank, long amount, TransactionContext tx) {
      return 0;
    }

    @Override
    public long extractRecipe(int tank, long amount, TransactionContext tx) {
      return 0;
    }

    @Override
    public long insertRecipe(int tank, long maxAmount, TransactionContext tx) {
      TransferPreconditions.checkNonNegative(maxAmount);
      long total = 0;

      long outputTunnels = getOutputs().size();

      if (outputTunnels == 0 || maxAmount == 0) return 0;

      long amountPerOutput = maxAmount / outputTunnels;
      long overflow = amountPerOutput == 0 ? maxAmount : maxAmount % amountPerOutput;

      for (var target : getOutputs()) {
        try (var capabilityGuard = target.getAdjacentCapability()) {
          var output = capabilityGuard.get();
          long toSend = amountPerOutput + overflow;

          long received = output.insert(tank, toSend, tx);

          overflow = toSend - received;
          total += received;
        }
      }

      deductEnergyCost(total, PowerUnit.FE, tx);

      return total;
    }
  }

  private class OutputExperienceHandler implements IExperienceHandler {
    @Override
    public int size() {
      return 1;
    }

    @Override
    public long getMaxExtract(int tank) {
      try (var input = getInputCapability()) {
        return input.get().getMaxExtract(tank);
      }
    }

    @Override
    public long getMaxInsert(int tank) {
      return 0;
    }

    @Override
    public long getAmountAsLong(int tank) {
      try (var input = getInputCapability()) {
        return input.get().getAmountAsLong(tank);
      }
    }

    @Override
    public void setExperience(int tank, long stack) {
    }

    @Override
    public void setCapacity(int tank, long l) {
    }

    @Override
    public long getCapacityAsLong(int tank, ExperienceResource resource) {
      try (var input = getInputCapability()) {
        return input.get().getCapacityAsLong(tank, resource) ;
      }
    }

    @Override
    public long insert(int tank, long stack, TransactionContext actionable) {
      return 0;
    }

    @Override
    public long extract(int tank, long amount, TransactionContext tx) {
      return extractRecipe(tank, amount, tx);
    }

    @Override
    public long extractRecipe(int tank, long amount, TransactionContext tx) {
      try (var input = getInputCapability()) {
        long extracted = input.get().extractRecipe(tank, amount, tx);
        deductEnergyCost(extracted, PowerUnit.FE, tx);
        return extracted;
      }
    }

    @Override
    public long insertRecipe(int tank, long amount, TransactionContext tx) {
      return 0;
    }
  }
}
