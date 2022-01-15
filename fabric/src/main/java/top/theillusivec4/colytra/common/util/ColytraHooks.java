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

package top.theillusivec4.colytra.common.util;

import java.util.List;
import java.util.UUID;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;
import top.theillusivec4.caelus.api.CaelusApi;
import top.theillusivec4.colytra.common.config.ColytraConfig;
import top.theillusivec4.colytra.mixin.AccessorLivingEntity;

public class ColytraHooks {

  public static final EntityAttributeModifier FLIGHT_MODIFIER = new EntityAttributeModifier(
      UUID.fromString("668bdbee-32b6-4c4b-bf6a-5a30f4d02e37"), "Flight modifier", 1.0d,
      EntityAttributeModifier.Operation.ADDITION);

  public static void updateColytra(PlayerEntity playerEntity) {
    ItemStack stack = playerEntity.getEquippedStack(EquipmentSlot.CHEST);
    EntityAttributeInstance attributeInstance = playerEntity
        .getAttributeInstance(CaelusApi.getInstance().getFlightAttribute());

    if (attributeInstance != null) {
      attributeInstance.removeModifier(FLIGHT_MODIFIER);

      if (ColytraNbt.isUseable(stack, ColytraNbt.getElytra(stack))) {
        attributeInstance.addTemporaryModifier(FLIGHT_MODIFIER);

        if (ColytraConfig.colytraMode != ColytraConfig.ColytraMode.PERFECT) {
          ItemStack elytraStack = ColytraNbt.getElytra(stack);
          int roll = ((AccessorLivingEntity) playerEntity).getRoll();
          int i = roll + 1;

          if (!playerEntity.world.isClient() && i % 10 == 0) {
            int j = i / 10;

            if (j % 2 == 0) {
              ColytraNbt.damageElytra(playerEntity, stack, elytraStack, 1);
            }
            playerEntity.emitGameEvent(GameEvent.ELYTRA_FREE_FALL);
          }

        }
      }
    }
  }

  public static void appendColytraTooltip(ItemStack chestStack, List<Text> tooltip) {

    if (!ColytraNbt.hasUpgrade(chestStack)) {
      return;
    }
    ItemStack elytraStack = ColytraNbt.getElytra(chestStack);

    if (elytraStack.isEmpty()) {
      return;
    }
    tooltip.add(new LiteralText(""));

    if (elytraStack.hasCustomName()) {
      Text display = elytraStack.getName();

      if (display instanceof MutableText) {
        ((MutableText) display).formatted(Formatting.AQUA, Formatting.ITALIC);
      }
      tooltip.add(display);
    } else {
      tooltip.add(new TranslatableText("item.minecraft.elytra").formatted(Formatting.AQUA));
    }

    if (ColytraConfig.colytraMode == ColytraConfig.ColytraMode.NORMAL) {

      if (elytraStack.hasTag()) {
        int i = 0;
        NbtCompound tag = elytraStack.getTag();

        if (tag != null && tag.contains("HideFlags", 99)) {
          i = tag.getInt("HideFlags");
        }

        if ((i & 1) == 0) {
          NbtList nbttaglist = elytraStack.getEnchantments();

          for (int j = 0; j < nbttaglist.size(); ++j) {
            NbtCompound nbttagcompound = nbttaglist.getCompound(j);
            Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(nbttagcompound.getString("id")))
                .ifPresent(enchantment -> tooltip.add(new LiteralText(" ")
                    .append(enchantment.getName(nbttagcompound.getInt("lvl")))));
          }
        }
      }

      if (ColytraNbt.isUseable(chestStack, elytraStack)) {
        tooltip.add(new LiteralText(" ").append(new TranslatableText("item.durability",
            elytraStack.getMaxDamage() - elytraStack.getDamage(), elytraStack.getMaxDamage())));
      } else {
        tooltip.add(new LiteralText(" ")
            .append(new TranslatableText("tooltip.colytra.broken").formatted(Formatting.RED)));
      }
    }
  }
}
