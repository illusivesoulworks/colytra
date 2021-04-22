package top.theillusivec4.colytra.integration;

import com.gildedgames.aether.common.item.accessories.cape.CapeItem;
import top.theillusivec4.caelus.api.RenderElytraEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class AetherIntegration {

  public static void checkCape(RenderElytraEvent evt) {
    CuriosApi.getCuriosHelper()
        .findEquippedCurio(stack -> stack.getItem() instanceof CapeItem, evt.getPlayer()).ifPresent(
        triple -> evt
            .setResourceLocation(((CapeItem) triple.getRight().getItem()).getCapeTexture()));
  }
}
