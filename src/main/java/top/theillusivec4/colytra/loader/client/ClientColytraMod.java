package top.theillusivec4.colytra.loader.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import top.theillusivec4.caelus.api.event.RenderElytraCallback;
import top.theillusivec4.colytra.core.util.ElytraTag;
import top.theillusivec4.colytra.loader.network.ClientNetworkConnection;

public class ClientColytraMod implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    ClientNetworkConnection.setup();
    RenderElytraCallback.EVENT.register(((playerEntity, renderElytraInfo) -> {
      ItemStack stack = playerEntity.getEquippedStack(EquipmentSlot.CHEST);
      ItemStack elytraStack = ElytraTag.getElytra(stack);

      if (!elytraStack.isEmpty()) {
        renderElytraInfo.activateRender();

        if (elytraStack.hasGlint()) {
          renderElytraInfo.activateGlow();
        }
      }
    }));
  }
}
