package es.degrassi.appexp.mixin;

import appeng.api.stacks.GenericStack;
import appeng.util.ConfigInventory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ConfigInventory.class, remap = false)
public abstract class ConfigInventoryMixin {
  @Unique
  private GenericStack appex$toStock;

  @Shadow
  @Nullable
  public abstract GenericStack getStack(int slot);

  @Inject(method = "setStack", at = @At("HEAD"))
  private void rememberStack(int slot, GenericStack stack, CallbackInfo ci) {
    appex$toStock = stack;
  }

  @ModifyVariable(method = "setStack", at = @At(value = "STORE", ordinal = 1), argsOnly = true)
  private GenericStack handleEmptyContainer(GenericStack stack, int slot) {
    return getStack(slot) == null && appex$toStock != null ? new GenericStack(appex$toStock.what(), 1) : null;
  }
}
