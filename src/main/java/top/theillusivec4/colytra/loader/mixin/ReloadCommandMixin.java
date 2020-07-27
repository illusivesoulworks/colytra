package top.theillusivec4.colytra.loader.mixin;

import java.util.Collection;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.colytra.loader.common.MixinHooks;

@Mixin(ReloadCommand.class)
public class ReloadCommandMixin {

  @Inject(at = @At("HEAD"), method = "method_29480")
  private static void reload(Collection<String> collection, ServerCommandSource serverCommandSource,
      CallbackInfo cb) {
    MixinHooks.syncAllConfigs(serverCommandSource.getMinecraftServer());
  }
}
