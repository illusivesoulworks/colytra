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

package top.theillusivec4.colytra.core.util;

import java.util.List;
import java.util.UUID;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import top.theillusivec4.caelus.api.CaelusApi;
import top.theillusivec4.colytra.core.Colytra;
import top.theillusivec4.colytra.core.base.ColytraConfig.ColytraMode;

public class ColytraHooks {

  public static final EntityAttributeModifier FLIGHT_MODIFIER = new EntityAttributeModifier(
      UUID.fromString("668bdbee-32b6-4c4b-bf6a-5a30f4d02e37"), "Flight modifier", 1.0d,
      EntityAttributeModifier.Operation.ADDITION);

  public static void updateColytra(PlayerEntity playerEntity) {
    ItemStack stack = playerEntity.getEquippedStack(EquipmentSlot.CHEST);
    EntityAttributeInstance attributeInstance = playerEntity
        .getAttributeInstance(CaelusApi.ELYTRA_FLIGHT);

    if (attributeInstance != null) {
      attributeInstance.removeModifier(FLIGHT_MODIFIER);

      if (ElytraTag.isUseable(stack, ElytraTag.getElytra(stack))) {
        attributeInstance.addTemporaryModifier(FLIGHT_MODIFIER);

        if (Colytra.getConfig().getColytraMode() != ColytraMode.PERFECT) {
          ItemStack elytraStack = ElytraTag.getElytra(stack);
          int ticksFlying = Colytra.getAccessor().getFlyingTicks(playerEntity);

          if ((ticksFlying + 1) % 20 != 0) {
            return;
          }
          ElytraTag.damageElytra(playerEntity, stack, elytraStack, 1);
        }
      }
    }
  }

  public static void appendColytraTooltip(ItemStack chestStack, List<Text> tooltip) {

    if (!ElytraTag.hasUpgrade(chestStack)) {
      return;
    }
    ItemStack elytraStack = ElytraTag.getElytra(chestStack);

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

    if (Colytra.getConfig().getColytraMode() == ColytraMode.NORMAL) {

      if (elytraStack.hasTag()) {
        int i = 0;
        CompoundTag tag = elytraStack.getTag();

        if (tag != null && tag.contains("HideFlags", 99)) {
          i = tag.getInt("HideFlags");
        }

        if ((i & 1) == 0) {
          ListTag nbttaglist = elytraStack.getEnchantments();

          for (int j = 0; j < nbttaglist.size(); ++j) {
            CompoundTag nbttagcompound = nbttaglist.getCompound(j);
            Colytra.getRegistryFinder().findEnchantment(nbttagcompound.getString("id")).ifPresent(
                enchantment -> tooltip.add(new LiteralText(" ")
                    .append(enchantment.getName(nbttagcompound.getInt("lvl")))));
          }
        }
      }

      if (ElytraTag.isUseable(chestStack, elytraStack)) {
        tooltip.add(new LiteralText(" ").append(new TranslatableText("item.durability",
            elytraStack.getMaxDamage() - elytraStack.getDamage(), elytraStack.getMaxDamage())));
      } else {
        tooltip.add(new LiteralText(" ")
            .append(new TranslatableText("tooltip.colytra.broken").formatted(Formatting.RED)));
      }
    }
  }
}
