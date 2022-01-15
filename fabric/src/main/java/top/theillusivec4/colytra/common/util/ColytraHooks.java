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
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
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
import top.theillusivec4.colytra.common.config.ColytraConfig;

public class ColytraHooks {

  public static boolean updateColytra(LivingEntity entity, boolean tickElytra) {
    ItemStack stack = entity.getEquippedStack(EquipmentSlot.CHEST);
    ItemStack elytraStack = ColytraNbt.getElytra(stack);

    if (!elytraStack.isEmpty() && ColytraNbt.isUseable(stack, elytraStack)) {

      if (ColytraConfig.colytraMode != ColytraConfig.ColytraMode.PERFECT && tickElytra) {
        int roll = entity.getRoll();
        int i = roll + 1;

        if (!entity.world.isClient() && i % 10 == 0) {
          int j = i / 10;

          if (j % 2 == 0) {
            ColytraNbt.damageElytra(entity, stack, elytraStack, 1);
          }
          entity.emitGameEvent(GameEvent.ELYTRA_FREE_FALL);
        }
      }
      return true;
    }
    return false;
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

      if (elytraStack.hasNbt()) {
        int i = 0;
        NbtCompound tag = elytraStack.getNbt();

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
