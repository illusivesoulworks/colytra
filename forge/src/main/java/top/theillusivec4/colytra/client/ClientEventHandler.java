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

package top.theillusivec4.colytra.client;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.colytra.common.ElytraTag;
import top.theillusivec4.colytra.server.ColytraServerConfig;

public class ClientEventHandler {

  private static void getColytraTooltip(ItemStack chestStack, List<Component> tooltip) {

    if (!ElytraTag.hasUpgrade(chestStack)) {
      return;
    }
    ItemStack elytraStack = ElytraTag.getElytra(chestStack);

    if (elytraStack.isEmpty()) {
      return;
    }
    tooltip.add(new TextComponent(""));

    if (elytraStack.hasCustomHoverName()) {
      Component display = elytraStack.getHoverName();

      if (display instanceof MutableComponent) {
        ((MutableComponent) display).withStyle(ChatFormatting.AQUA)
            .withStyle(ChatFormatting.ITALIC);
      }
      tooltip.add(display);
    } else {
      tooltip.add(
          new TranslatableComponent("item.minecraft.elytra").withStyle(ChatFormatting.AQUA));
    }

    if (ColytraServerConfig.colytraMode == ColytraServerConfig.ColytraMode.NORMAL) {

      if (elytraStack.hasTag()) {
        int i = 0;
        CompoundTag tag = elytraStack.getTag();

        if (tag != null && tag.contains("HideFlags", 99)) {
          i = tag.getInt("HideFlags");
        }

        if ((i & 1) == 0) {
          ListTag nbttaglist = elytraStack.getEnchantmentTags();

          for (int j = 0; j < nbttaglist.size(); ++j) {
            CompoundTag nbttagcompound = nbttaglist.getCompound(j);
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS
                .getValue(ResourceLocation.tryParse(nbttagcompound.getString("id")));

            if (enchantment != null) {
              tooltip.add(new TextComponent(" ")
                  .append(enchantment.getFullname(nbttagcompound.getInt("lvl"))));
            }
          }
        }
      }

      if (ElytraTag.isUseable(chestStack, elytraStack)) {
        tooltip.add(new TextComponent(" ").append(
            (new TranslatableComponent("item.durability",
                elytraStack.getMaxDamage() - elytraStack.getDamageValue(),
                elytraStack.getMaxDamage()))));
      } else {
        tooltip.add(new TextComponent(" ").append(
            new TranslatableComponent("tooltip.colytra.broken").withStyle(ChatFormatting.RED)));
      }
    }
  }

  @SubscribeEvent
  public void itemTooltip(ItemTooltipEvent evt) {
    ItemStack itemstack = evt.getItemStack();
    List<Component> tooltip = evt.getToolTip();
    getColytraTooltip(itemstack, tooltip);
  }
}
