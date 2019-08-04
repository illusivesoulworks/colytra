package top.theillusivec4.colytra.common;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.caelus.api.CaelusAPI;
import top.theillusivec4.colytra.common.capability.CapabilityElytra;
import top.theillusivec4.colytra.common.network.NetworkHandler;
import top.theillusivec4.colytra.common.network.SPacketSyncColytra;

import java.util.UUID;

public class EventHandlerCommon {

  private static AttributeModifier FLIGHT_MODIFIER =
      new AttributeModifier(UUID.fromString("668bdbee-32b6-4c4b-bf6a-5a30f4d02e37"),
                            "Flight modifier", 1.0d, AttributeModifier.Operation.ADDITION);

  @SubscribeEvent
  public void onLivingEquipmentChange(LivingEquipmentChangeEvent evt) {

    if (!(evt.getEntityLiving() instanceof PlayerEntity)) {
      return;
    }

    if (evt.getSlot() != EquipmentSlotType.CHEST) {
      return;
    }

    PlayerEntity playerEntity = (PlayerEntity) evt.getEntity();
    ItemStack to = evt.getTo();
    IAttributeInstance attributeInstance = playerEntity.getAttribute(CaelusAPI.ELYTRA_FLIGHT);
    attributeInstance.removeModifier(FLIGHT_MODIFIER);

    CapabilityElytra.getCapability(to).ifPresent(elytraHolder -> {

      if (elytraHolder.isUseable()) {
        attributeInstance.applyModifier(FLIGHT_MODIFIER);
      }
    });
  }

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent evt) {

    if (evt.side != LogicalSide.SERVER) {
      return;
    }

    if (evt.phase != TickEvent.Phase.END) {
      return;
    }

    PlayerEntity player = evt.player;
    ItemStack stack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);

    CapabilityElytra.getCapability(stack).ifPresent(elytraHolder -> {

      if (ColytraConfig.getColytraMode() != ColytraConfig.ColytraMode.PERFECT) {
        updateColytra(elytraHolder, player);
      }
    });
  }

  private static void updateColytra(CapabilityElytra.IElytra elytraHolder, PlayerEntity player) {

    Integer ticksFlying =
        ObfuscationReflectionHelper.getPrivateValue(LivingEntity.class, player, "field_184629_bo");

    if (ticksFlying == null) {
      return;
    }

    if ((ticksFlying + 1) % 20 != 0) {
      return;
    }

    elytraHolder.damageElytra(player, 1);

    if (player instanceof ServerPlayerEntity) {
      sendColytraSyncPacket(elytraHolder, player);
    }
  }

  @SubscribeEvent
  public void onPlayerXPPickUp(PlayerPickupXpEvent evt) {

    if (ColytraConfig.getColytraMode() != ColytraConfig.ColytraMode.NORMAL) {
      return;
    }

    if (evt.getEntityPlayer().world.isRemote) {
      return;
    }

    PlayerEntity player = evt.getEntityPlayer();
    ItemStack stack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);

    CapabilityElytra.getCapability(stack)
                    .ifPresent(elytraHolder -> handleColytraMending(elytraHolder, evt, player));
  }

  private static void handleColytraMending(CapabilityElytra.IElytra elytraHolder,
                                           PlayerPickupXpEvent evt, PlayerEntity player) {

    ItemStack elytraStack = elytraHolder.getElytra();

    if (elytraStack.isEmpty()) {
      return;
    }

    if (elytraStack.getDamage() <= 0) {
      return;
    }

    if (EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, elytraStack) <= 0) {
      return;
    }

    evt.setCanceled(true);
    ExperienceOrbEntity xpOrb = evt.getOrb();

    if (xpOrb.delayBeforeCanPickup == 0 && player.xpCooldown == 0) {
      player.xpCooldown = 2;
      player.onItemPickup(xpOrb, 1);
      int i = Math.min(xpToDurability(xpOrb.xpValue), elytraStack.getDamage());
      xpOrb.xpValue -= durabilityToXp(i);
      elytraStack.setDamage(elytraStack.getDamage() - i);

      if (player instanceof ServerPlayerEntity) {
        sendColytraSyncPacket(elytraHolder, player);
      }

      if (xpOrb.xpValue > 0) {
        player.giveExperiencePoints(xpOrb.xpValue);
      }

      xpOrb.remove();
    }
  }

  private static int durabilityToXp(int durability) {

    return durability / 2;
  }

  private static int xpToDurability(int xp) {

    return xp * 2;
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onColytraAnvil(AnvilUpdateEvent evt) {

    if (ColytraConfig.getColytraMode() != ColytraConfig.ColytraMode.NORMAL) {
      return;
    }

    ItemStack left = evt.getLeft();
    CapabilityElytra.getCapability(left)
                    .ifPresent(elytraHolder -> handleColytraRepair(elytraHolder, evt));
  }

  private static void handleColytraRepair(CapabilityElytra.IElytra elytraHolder,
                                          AnvilUpdateEvent evt) {

    ItemStack right = evt.getRight();
    ItemStack left = evt.getLeft();
    ItemStack stack = elytraHolder.getElytra();
    int toRepair = stack.getDamage();

    if (right.getItem() != Items.PHANTOM_MEMBRANE) {
      return;
    }

    if (toRepair == 0) {
      return;
    }

    int membraneToUse = 0;

    while (toRepair > 0) {
      toRepair -= 108;
      membraneToUse++;
    }
    membraneToUse = Math.min(membraneToUse, right.getCount());
    int newDamage = Math.max(stack.getDamage() - membraneToUse * 108, 0);

    ItemStack output = left.copy();
    ItemStack outputElytra = stack.copy();
    outputElytra.setDamage(newDamage);
    outputElytra.setRepairCost(stack.getRepairCost() * 2 + 1);

    CapabilityElytra.getCapability(output)
                    .ifPresent(outputElytraHolder -> outputElytraHolder.setElytra(outputElytra));

    int xpCost = membraneToUse + left.getRepairCost() + right.getRepairCost();
    String name = evt.getName();

    if (!name.isEmpty() && !name.equals(left.getDisplayName().getString())) {
      output.setDisplayName(new StringTextComponent(name));
      xpCost++;
    }

    evt.setMaterialCost(membraneToUse);
    evt.setCost(xpCost);
    evt.setOutput(output);
  }

  private static void sendColytraSyncPacket(CapabilityElytra.IElytra elytraHolder,
                                            PlayerEntity player) {

    NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                                 new SPacketSyncColytra(player.getEntityId(),
                                                        elytraHolder.getElytra()));
  }
}
