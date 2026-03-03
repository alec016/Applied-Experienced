package es.degrassi.appexp.api.integration.emi;

import appeng.api.integrations.emi.EmiStackConverters;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class AppexEmiPlugin implements EmiPlugin {
  @Override
  public void register(EmiRegistry registry) {
    EmiStackConverters.register(new EmiExperienceStackConverter());
  }
}
