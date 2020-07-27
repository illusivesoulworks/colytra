package top.theillusivec4.colytra.loader.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.colytra.loader.common.MixinHooks;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {

  @Inject(at = @At("HEAD"), method = "updateResult()V", cancellable = true)
  public void updateResult(CallbackInfo cb) {
    @SuppressWarnings("ConstantConditions") AnvilScreenHandler screenHandler = (AnvilScreenHandler) (Object) this;

    if (MixinHooks.checkColytraRepair(screenHandler)) {
      cb.cancel();
    }
  }
}
