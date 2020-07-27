package top.theillusivec4.colytra.loader.mixin;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.colytra.loader.common.MixinHooks;

@Mixin(ItemStack.class)
public class ItemStackMixin {

  @SuppressWarnings("ConstantConditions")
  @Inject(at = @At("RETURN"), method = "getTooltip", cancellable = true)
  public void getTooltip(CallbackInfoReturnable<List<Text>> cb) {
    MixinHooks.appendColytraTooltip((ItemStack) (Object) this, cb.getReturnValue());
  }
}