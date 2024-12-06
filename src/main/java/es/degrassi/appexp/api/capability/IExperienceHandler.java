package es.degrassi.appexp.api.capability;

import appeng.api.config.Actionable;

public interface IExperienceHandler {
  boolean canAcceptExperience(long experience);

  boolean canProvideExperience(long experience);

  long getMaxExtract();

  long getMaxReceive();

  default boolean canExtract() {
    return this.canProvideExperience(1);
  }

  default boolean canReceive() {
    return this.canAcceptExperience(1);
  }

  long getExperience();

  long getExperienceCapacity();

  default long getMaxExperienceCapacity() {
    return this.getExperienceCapacity();
  }

  void setExperience(long experience);

  long receiveExperience(long experience, boolean simulate);

  long extractExperience(long experience, boolean simulate);
}
