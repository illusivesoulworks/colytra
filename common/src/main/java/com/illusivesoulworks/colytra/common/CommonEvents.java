/*
 * Copyright (C) 2017-2022 Illusive Soulworks
 *
 * Colytra is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Colytra is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Colytra. If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.colytra.common;

import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.gameevent.GameEvent;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;

public class CommonEvents {

  public static final AttributeModifier FLIGHT_MODIFIER =
      new AttributeModifier(UUID.fromString("668bdbee-32b6-4c4b-bf6a-5a30f4d02e37"),
          "Flight modifier", 1.0d, AttributeModifier.Operation.ADDITION);

  public static boolean updateColytra(LivingEntity livingEntity, boolean tickElytra) {
    ItemStack stack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
    ItemStack elytraStack = ElytraTag.getElytra(stack);

    if (!elytraStack.isEmpty() && ElytraTag.isUseable(stack, elytraStack)) {

      if (ColytraConfig.SERVER.fusionType.get() != ColytraConfig.FusionType.PERFECT &&
          tickElytra) {
        int fallFlyingTicks = livingEntity.getFallFlyingTicks();
        int i = fallFlyingTicks + 1;

        if (!livingEntity.level().isClientSide() && i % 10 == 0) {
          int j = i / 10;

          if (j % 2 == 0) {
            ElytraTag.damageElytra(livingEntity, stack, elytraStack, 1);
          }
          livingEntity.gameEvent(GameEvent.ELYTRA_GLIDE);
        }
      }
      return true;
    }
    return false;
  }

  public static boolean repairColytraWithXp(ExperienceOrb experienceOrb, Player player,
                                            Consumer<Integer> xpSetter) {

    if (ColytraConfig.SERVER.fusionType.get() != ColytraConfig.FusionType.NORMAL ||
        player.level().isClientSide()) {
      return false;
    }
    ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
    ItemStack elytraStack = ElytraTag.getElytra(chestStack);

    if (elytraStack.isEmpty() || elytraStack.getDamageValue() <= 0
        || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, elytraStack) <= 0) {
      return false;
    }
    int i = Math.min(xpToDurability(experienceOrb.getValue()), elytraStack.getDamageValue());

    if (i <= 0) {
      return false;
    }
    player.takeXpDelay = 2;
    player.take(experienceOrb, 1);
    xpSetter.accept(experienceOrb.getValue() - durabilityToXp(i));
    elytraStack.setDamageValue(elytraStack.getDamageValue() - i);
    ElytraTag.setElytra(chestStack, elytraStack);

    if (experienceOrb.getValue() > 0) {
      player.giveExperiencePoints(experienceOrb.getValue());
    }
    experienceOrb.discard();
    return true;
  }

  private static int durabilityToXp(int durability) {
    return durability / 2;
  }

  private static int xpToDurability(int xp) {
    return xp * 2;
  }

  private static final Triple<ItemStack, Integer, Integer> EMPTY_RESULT =
      new ImmutableTriple<>(ItemStack.EMPTY, 0, 0);

  public static Triple<ItemStack, Integer, Integer> repairColytra(ItemStack input,
                                                                  ItemStack ingredient,
                                                                  String name) {

    if (ColytraConfig.SERVER.fusionType.get() != ColytraConfig.FusionType.NORMAL) {
      return EMPTY_RESULT;
    }
    ItemStack stack = ElytraTag.getElytra(input);

    if (stack.isEmpty()) {
      return EMPTY_RESULT;
    }
    int toRepair = stack.getDamageValue();

    if (ingredient.getItem() != Items.PHANTOM_MEMBRANE || toRepair == 0) {
      return EMPTY_RESULT;
    }
    int membraneToUse = 0;

    while (toRepair > 0) {
      toRepair -= 108;
      membraneToUse++;
    }
    membraneToUse = Math.min(membraneToUse, ingredient.getCount());
    int newDamage = Math.max(stack.getDamageValue() - membraneToUse * 108, 0);

    ItemStack output = input.copy();
    ItemStack outputElytra = stack.copy();
    outputElytra.setDamageValue(newDamage);
    outputElytra.setRepairCost(stack.getBaseRepairCost() * 2 + 1);
    ElytraTag.setElytra(output, outputElytra);
    int xpCost = membraneToUse + input.getBaseRepairCost() + stack.getBaseRepairCost();

    if (name != null && !name.isEmpty() && !name.equals(input.getHoverName().getString())) {
      output.setHoverName(Component.literal(name));
      xpCost++;
    }
    return new MutableTriple<>(output, membraneToUse, xpCost);
  }
}
