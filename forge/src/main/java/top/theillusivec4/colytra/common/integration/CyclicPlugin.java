package top.theillusivec4.colytra.common.integration;

import com.lothrazar.cyclic.data.Const;
import com.lothrazar.cyclic.util.UtilNBT;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

public class CyclicPlugin {

  protected static final String NBT_USES = "launchuses";

  public static void setup() {
    MinecraftForge.EVENT_BUS.addListener(CyclicPlugin::onLivingUpdate);
  }

  private static void onLivingUpdate(final LivingEvent.LivingUpdateEvent event) {

    if (event.getEntity() instanceof Player player) {
      ItemStack armorStack = player.getItemBySlot(EquipmentSlot.CHEST);

      if (armorStack.isEmpty()) {
        return;
      }

      if ((!player.hasImpulse || player.isOnGround()) &&
          armorStack.getOrCreateTag().getInt(NBT_USES) > 0) {
        UtilNBT.setItemStackNBTVal(armorStack, NBT_USES, 0);
      }
    }
  }
}
