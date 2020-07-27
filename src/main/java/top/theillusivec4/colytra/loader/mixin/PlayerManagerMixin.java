package top.theillusivec4.colytra.loader.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.colytra.loader.common.MixinHooks;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

  @Inject(at = @At("TAIL"), method = "onPlayerConnect")
  public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player,
      CallbackInfo cb) {
    MixinHooks.syncConfig(player);
  }
}
