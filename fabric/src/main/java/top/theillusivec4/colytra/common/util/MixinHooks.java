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
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import top.theillusivec4.colytra.common.config.ColytraConfig;
import top.theillusivec4.colytra.common.network.ColytraPackets;
import top.theillusivec4.colytra.mixin.AccessorAnvilScreenHandler;
import top.theillusivec4.colytra.mixin.AccessorExperienceOrbEntity;
import top.theillusivec4.colytra.mixin.AccessorForgingScreenHandler;

public class MixinHooks {

  public static void syncConfig(ServerPlayerEntity playerEntity) {
    ServerPlayNetworking.send(playerEntity, ColytraPackets.SYNC_CONFIG,
        ColytraPackets.writeConfigPacket());
  }

  public static void syncAllConfigs(MinecraftServer server) {
    PacketByteBuf buf = ColytraPackets.writeConfigPacket();
    server.getPlayerManager().getPlayerList()
        .forEach(player -> ServerPlayNetworking.send(player, ColytraPackets.SYNC_CONFIG, buf));
  }

  public static void appendColytraTooltip(ItemStack stack, List<Text> tooltip) {
    ColytraHooks.appendColytraTooltip(stack, tooltip);
  }

  public static boolean repairColytra(AnvilScreenHandler anvil) {

    if (ColytraConfig.colytraMode != ColytraConfig.ColytraMode.NORMAL) {
      return false;
    }
    AccessorForgingScreenHandler forgingAccessor = (AccessorForgingScreenHandler) anvil;
    AccessorAnvilScreenHandler anvilAccessor = (AccessorAnvilScreenHandler) anvil;
    ItemStack chestStack = forgingAccessor.getInput().getStack(0);

    if (chestStack.isEmpty()) {
      return false;
    }
    ItemStack stack = ColytraNbt.getElytra(chestStack);

    if (stack.isEmpty()) {
      return false;
    }
    ItemStack right = ((AccessorForgingScreenHandler) anvil).getInput().getStack(1);
    int toRepair = stack.getDamage();

    if (right.getItem() != Items.PHANTOM_MEMBRANE || toRepair == 0) {
      return false;
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
    ColytraNbt.setElytra(output, outputElytra);
    int xpCost = membraneToUse + chestStack.getRepairCost() + right.getRepairCost();

    if (StringUtils.isBlank(anvilAccessor.getNewItemName())) {
      if (stack.hasCustomName()) {
        xpCost += 1;
        stack.removeCustomName();
      }
    } else if (!anvilAccessor.getNewItemName().equals(stack.getName().getString())) {
      xpCost += 1;
      output.setCustomName(new LiteralText(anvilAccessor.getNewItemName()));
    }
    anvilAccessor.setRepairItemUsage(membraneToUse);
    anvilAccessor.getLevelCost().set(xpCost);
    forgingAccessor.getOutput().setStack(0, output);
    anvil.sendContentUpdates();
    return true;
  }

  public static void repairColytra(PlayerEntity playerEntity, ExperienceOrbEntity orb) {

    if (ColytraConfig.colytraMode != ColytraConfig.ColytraMode.NORMAL) {
      return;
    }
    ItemStack chestStack = playerEntity.getEquippedStack(EquipmentSlot.CHEST);
    ItemStack elytraStack = ColytraNbt.getElytra(chestStack);

    if (!elytraStack.isEmpty() && EnchantmentHelper.getLevel(Enchantments.MENDING, elytraStack) > 0
        && elytraStack.isDamaged()) {
      int amount = orb.getExperienceAmount();
      int toRepair = Math.min(amount * 2, elytraStack.getDamage());
      ((AccessorExperienceOrbEntity) orb).setAmount(amount - toRepair / 2);
      elytraStack.setDamage(elytraStack.getDamage() - toRepair);
    }
  }
}
