/*
 * Copyright (c) 2018-2020 C4
 *
 * This file is part of Colytra, a mod made for Minecraft.
 *
 * Colytra is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Colytra is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Colytra.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.colytra.common;

import java.util.UUID;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import top.theillusivec4.caelus.api.CaelusAPI;

public class EventHandlerCommon {

  public static AttributeModifier FLIGHT_MODIFIER = new AttributeModifier(
      UUID.fromString("668bdbee-32b6-4c4b-bf6a-5a30f4d02e37"), "Flight modifier", 1.0d,
      AttributeModifier.Operation.ADDITION);

  private static void updateColytra(ItemStack chestStack, PlayerEntity player) {
    ItemStack elytraStack = ElytraNBT.getElytra(chestStack);

    if (elytraStack.isEmpty()) {
      return;
    }
    IAttributeInstance attributeInstance = player.getAttribute(CaelusAPI.ELYTRA_FLIGHT);

    if (!ElytraNBT.isUseable(chestStack, elytraStack)) {
      attributeInstance.removeModifier(FLIGHT_MODIFIER);
      return;
    } else if (!attributeInstance.hasModifier(FLIGHT_MODIFIER)) {
      attributeInstance.applyModifier(FLIGHT_MODIFIER);
    }
    Integer ticksFlying = ObfuscationReflectionHelper
        .getPrivateValue(LivingEntity.class, player, "field_184629_bo");

    if (ticksFlying == null || (ticksFlying + 1) % 20 != 0) {
      return;
    }
    ElytraNBT.damageElytra(player, chestStack, elytraStack, 1);
  }

  private static void handleColytraMending(ItemStack chestStack, PlayerXpEvent.PickupXp evt,
      PlayerEntity player) {
    ItemStack elytraStack = ElytraNBT.getElytra(chestStack);

    if (elytraStack.isEmpty() || elytraStack.getDamage() <= 0
        || EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, elytraStack) <= 0) {
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

  private static void handleColytraRepair(ItemStack chestStack, AnvilUpdateEvent evt) {
    ItemStack stack = ElytraNBT.getElytra(chestStack);

    if (stack.isEmpty()) {
      return;
    }
    ItemStack right = evt.getRight();
    int toRepair = stack.getDamage();

    if (right.getItem() != Items.PHANTOM_MEMBRANE || toRepair == 0) {
      return;
    }
    int membraneToUse = 0;

    while (toRepair > 0) {
      toRepair -= 108;
      membraneToUse++;
    }
    membraneToUse = Math.min(membraneToUse, right.getCount());
    int newDamage = Math.max(stack.getDamage() - membraneToUse * 108, 0);

    ItemStack output = chestStack.copy();
    ItemStack outputElytra = stack.copy();
    outputElytra.setDamage(newDamage);
    outputElytra.setRepairCost(stack.getRepairCost() * 2 + 1);
    ElytraNBT.setElytra(output, outputElytra);
    int xpCost = membraneToUse + chestStack.getRepairCost() + right.getRepairCost();
    String name = evt.getName();

    if (!name.isEmpty() && !name.equals(chestStack.getDisplayName().getString())) {
      output.setDisplayName(new StringTextComponent(name));
      xpCost++;
    }
    evt.setMaterialCost(membraneToUse);
    evt.setCost(xpCost);
    evt.setOutput(output);
  }

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

    if (ElytraNBT.isUseable(to, ElytraNBT.getElytra(to))) {
      attributeInstance.applyModifier(FLIGHT_MODIFIER);
    }
  }

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent evt) {

    if (evt.side != LogicalSide.SERVER || evt.phase != Phase.END) {
      return;
    }
    PlayerEntity player = evt.player;
    ItemStack stack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);

    if (ColytraConfig.getColytraMode() != ColytraConfig.ColytraMode.PERFECT) {
      updateColytra(stack, player);
    }
  }

  @SubscribeEvent
  public void onPlayerXPPickUp(PlayerXpEvent.PickupXp evt) {

    if (ColytraConfig.getColytraMode() != ColytraConfig.ColytraMode.NORMAL) {
      return;
    }
    PlayerEntity player = evt.getPlayer();

    if (player.world.isRemote) {
      return;
    }
    ItemStack stack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
    handleColytraMending(stack, evt, player);
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onColytraAnvil(AnvilUpdateEvent evt) {

    if (ColytraConfig.getColytraMode() != ColytraConfig.ColytraMode.NORMAL) {
      return;
    }
    ItemStack left = evt.getLeft();
    handleColytraRepair(left, evt);
  }
}
