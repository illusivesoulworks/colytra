package top.theillusivec4.colytra.integration;

import com.gildedgames.aether.common.item.accessories.cape.CapeItem;
import top.theillusivec4.caelus.api.RenderElytraEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class AetherIntegration {

  public static void checkCape(RenderElytraEvent evt) {
    CuriosApi.getCuriosHelper().findEquippedCurio((item) -> item.getItem() instanceof CapeItem, evt.getPlayer()).ifPresent(triple ->
            CuriosApi.getCuriosHelper().getCuriosHandler(evt.getPlayer()).ifPresent(handler -> handler.getStacksHandler(triple.getLeft()).ifPresent(stacksHandler -> {
                CapeItem cape = (CapeItem) triple.getRight().getItem();
                if (cape.getCapeTexture() != null && stacksHandler.getRenders().get(triple.getMiddle())) {
                    evt.setResourceLocation(cape.getCapeTexture());
                }
            })));
  }
}
