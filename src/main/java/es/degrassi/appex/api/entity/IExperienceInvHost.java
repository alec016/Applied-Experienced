package es.degrassi.appex.api.entity;

import appeng.api.storage.MEStorage;
import appeng.util.inv.CombinedInternalInventory;

public interface IExperienceInvHost {
  // Returns null if it's attached or linked to an ME System
  default CombinedInternalInventory getInternalInventory() {
    return null;
  }

  // Returns null if it's not attached or linked to an ME System
  default MEStorage getInventory() {
    return null;
  }

  long getExperience();

  long getExperienceCapacity();

  default boolean isInverted() {
    return false;
  }
}
