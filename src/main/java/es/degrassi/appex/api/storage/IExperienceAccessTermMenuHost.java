package es.degrassi.appex.api.storage;

import appeng.api.networking.IGridNode;
import appeng.api.storage.ILinkStatus;
import appeng.api.storage.MEStorage;
import appeng.api.util.IConfigurableObject;
import es.degrassi.appex.api.entity.IExperienceInvHost;
import org.jetbrains.annotations.Nullable;

public interface IExperienceAccessTermMenuHost extends IExperienceInvHost {
  @Nullable
  IGridNode getGridNode();

  ILinkStatus getLinkStatus();
}
