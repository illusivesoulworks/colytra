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
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import top.theillusivec4.caelus.api.CaelusApi;
import top.theillusivec4.colytra.server.ColytraServerConfig;

public class CommonEventHandler {

  public static final AttributeModifier FLIGHT_MODIFIER = new AttributeModifier(
      UUID.fromString("668bdbee-32b6-4c4b-bf6a-5a30f4d02e37"), "Flight modifier", 1.0d,
      AttributeModifier.Operation.ADDITION);

  private static void updateColytra(ItemStack chestStack, Player player) {
    ItemStack elytraStack = ElytraTag.getElytra(chestStack);
    int fallFlyingTicks = player.getFallFlyingTicks();

    if ((fallFlyingTicks + 1) % 20 != 0) {
      return;
    }
    ElytraTag.damageElytra(player, chestStack, elytraStack, 1);
  }

  private static void handleColytraMending(ItemStack chestStack, PlayerXpEvent.PickupXp evt,
                                           Player player) {
    ItemStack elytraStack = ElytraTag.getElytra(chestStack);

    if (elytraStack.isEmpty() || elytraStack.getDamageValue() <= 0
        || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, elytraStack) <= 0) {
      return;
    }
    ExperienceOrb xpOrb = evt.getOrb();

    if (player.takeXpDelay == 0) {
      int i = Math.min(xpToDurability(xpOrb.value), elytraStack.getDamageValue());

      if (i <= 0) {
        return;
      }
      evt.setCanceled(true);
      player.takeXpDelay = 2;
      player.take(xpOrb, 1);
      xpOrb.value -= durabilityToXp(i);
      elytraStack.setDamageValue(elytraStack.getDamageValue() - i);
      ElytraTag.setElytra(chestStack, elytraStack);

      if (xpOrb.value > 0) {
        player.giveExperiencePoints(xpOrb.value);
      }
      xpOrb.discard();
    }
  }

  private static int durabilityToXp(int durability) {
    return durability / 2;
  }

  private static int xpToDurability(int xp) {
    return xp * 2;
  }

  private static void handleColytraRepair(ItemStack chestStack, AnvilUpdateEvent evt) {
    ItemStack stack = ElytraTag.getElytra(chestStack);

    if (stack.isEmpty()) {
      return;
    }
    ItemStack right = evt.getRight();
    int toRepair = stack.getDamageValue();

    if (right.getItem() != Items.PHANTOM_MEMBRANE || toRepair == 0) {
      return;
    }
    int membraneToUse = 0;

    while (toRepair > 0) {
      toRepair -= 108;
      membraneToUse++;
    }
    membraneToUse = Math.min(membraneToUse, right.getCount());
    int newDamage = Math.max(stack.getDamageValue() - membraneToUse * 108, 0);

    ItemStack output = chestStack.copy();
    ItemStack outputElytra = stack.copy();
    outputElytra.setDamageValue(newDamage);
    outputElytra.setRepairCost(stack.getBaseRepairCost() * 2 + 1);
    ElytraTag.setElytra(output, outputElytra);
    int xpCost = membraneToUse + chestStack.getBaseRepairCost() + right.getBaseRepairCost();
    String name = evt.getName();

    if (name != null && !name.isEmpty() && !name.equals(chestStack.getHoverName().getString())) {
      output.setHoverName(new TextComponent(name));
      xpCost++;
    }
    evt.setMaterialCost(membraneToUse);
    evt.setCost(xpCost);
    evt.setOutput(output);
  }

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent evt) {

    if (evt.side != LogicalSide.SERVER || evt.phase != Phase.END) {
      return;
    }
    Player player = evt.player;
    ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
    AttributeInstance attributeInstance = player
        .getAttribute(CaelusApi.getInstance().getFlightAttribute());

    if (attributeInstance != null) {
      attributeInstance.removeModifier(FLIGHT_MODIFIER);

      if (ElytraTag.isUseable(stack, ElytraTag.getElytra(stack))) {
        attributeInstance.addTransientModifier(FLIGHT_MODIFIER);

        if (ColytraServerConfig.colytraMode != ColytraServerConfig.ColytraMode.PERFECT) {
          updateColytra(stack, player);
        }
      }
    }
  }

  @SubscribeEvent
  public void onPlayerXPPickUp(PlayerXpEvent.PickupXp evt) {

    if (ColytraServerConfig.colytraMode != ColytraServerConfig.ColytraMode.NORMAL) {
      return;
    }
    Player player = evt.getPlayer();

    if (player.level.isClientSide) {
      return;
    }
    ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
    handleColytraMending(stack, evt, player);
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onColytraAnvil(AnvilUpdateEvent evt) {

    if (ColytraServerConfig.colytraMode != ColytraServerConfig.ColytraMode.NORMAL) {
      return;
    }
    ItemStack left = evt.getLeft();
    handleColytraRepair(left, evt);
  }
}
