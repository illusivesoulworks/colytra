package top.theillusivec4.colytra.loader.mixin;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.colytra.loader.common.MixinHooks;

@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbEntityMixin {

  @Inject(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "net/minecraft/enchantment/EnchantmentHelper.chooseEquipmentWith (Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/entity/LivingEntity;Ljava/util/function/Predicate;)Ljava/util/Map$Entry;"), cancellable = true)
  private void onPlayerCollision(PlayerEntity playerEntity, CallbackInfo cb) {
    @SuppressWarnings("ConstantConditions") ExperienceOrbEntity orb = (ExperienceOrbEntity) (Object) this;

    if (MixinHooks.checkColytraMending(playerEntity, orb)) {
      cb.cancel();
    }
  }
}