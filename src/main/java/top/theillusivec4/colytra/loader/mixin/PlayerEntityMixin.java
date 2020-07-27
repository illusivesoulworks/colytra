package top.theillusivec4.colytra.loader.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.colytra.loader.common.MixinHooks;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

  @Inject(at = @At("TAIL"), method = "tick")
  public void onTick(CallbackInfo cb) {
    @SuppressWarnings("ConstantConditions") PlayerEntity playerEntity = (PlayerEntity) (Object) this;
    MixinHooks.updateColytra(playerEntity);
  }
}
