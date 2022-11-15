package top.theillusivec4.colytra.common.integration;

import com.lothrazar.cyclic.data.Const;
import com.lothrazar.cyclic.net.PacketPlayerFalldamage;
import com.lothrazar.cyclic.registry.EnchantRegistry;
import com.lothrazar.cyclic.registry.PacketRegistry;
import com.lothrazar.cyclic.util.EntityUtil;
import com.lothrazar.cyclic.util.ParticleUtil;
import com.lothrazar.cyclic.util.TagDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import top.theillusivec4.colytra.common.ElytraTag;

public class CyclicClientPlugin {

  protected static final int COOLDOWN = 7 * Const.TPS;
  protected static final float POWER = 1.07F;
  protected static final int ROTATIONPITCH = 68;

  public static void setup() {
    MinecraftForge.EVENT_BUS.addListener(CyclicClientPlugin::onKeyInput);
  }

  private static void onKeyInput(final InputEvent.KeyInputEvent evt) {
    Player player = Minecraft.getInstance().player;

    if (player == null || player.getVehicle() instanceof Boat) {
      return;
    }
    ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);

    if (EnchantmentHelper.getEnchantments(feet).containsKey(EnchantRegistry.LAUNCH)) {
      return;
    }
    ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
    ItemStack elytra = ElytraTag.getElytra(chest);
    ItemStack enchanted = ItemStack.EMPTY;

    if (EnchantmentHelper.getEnchantments(elytra).containsKey(EnchantRegistry.LAUNCH)) {
      enchanted = elytra;
    } else if (EnchantmentHelper.getEnchantments(chest).containsKey(EnchantRegistry.LAUNCH)) {
      enchanted = chest;
    }

    if (enchanted.isEmpty() || player.isCrouching()) {
      return;
    }

    if (player.getCooldowns().isOnCooldown(chest.getItem())) {
      return;
    }
    if (Minecraft.getInstance().options.keyJump.isDown() && player.getY() < player.yOld &&
        player.hasImpulse && !player.isInWater()) {
      int level = EnchantmentHelper.getEnchantments(enchanted).get(EnchantRegistry.LAUNCH);
      int uses = elytra.getOrCreateTag().getInt(CyclicPlugin.NBT_USES);
      player.fallDistance = 0;
      float angle = (player.getDeltaMovement().x == 0 && player.getDeltaMovement().z == 0) ? 90 :
          ROTATIONPITCH;
      EntityUtil.launch(player, angle, POWER);
      ParticleUtil.spawnParticle(player.getCommandSenderWorld(), ParticleTypes.CRIT,
          player.blockPosition(), 7);
      uses++;

      if (uses >= level) {

        if (!chest.isEmpty()) {
          EntityUtil.setCooldownItem(player, chest.getItem(), COOLDOWN);
        }
        uses = 0;
      }
      TagDataUtil.setItemStackNBTVal(feet, CyclicPlugin.NBT_USES, uses);
      player.fallDistance = 0;
      PacketRegistry.INSTANCE.sendToServer(new PacketPlayerFalldamage());
    }
  }
}
