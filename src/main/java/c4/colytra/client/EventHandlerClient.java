/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Colytra.
 * Colytra is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.colytra.client;

import c4.colytra.common.config.ConfigHandler;
import c4.colytra.util.ClientUtil;
import c4.colytra.network.CPacketToggleColytra;
import c4.colytra.network.NetworkHandler;
import c4.colytra.proxy.CommonProxy;
import c4.colytra.util.ColytraUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import vazkii.quark.vanity.feature.DyableElytra;

import java.util.List;

public class EventHandlerClient {

    @SubscribeEvent
    public void onToggleColytra(InputEvent.KeyInputEvent e) {

        if (ClientUtil.toggleColytra.isPressed()) {
            ItemStack stack = Minecraft.getMinecraft().player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

            if (ColytraUtil.hasElytraUpgrade(stack)) {
                NBTTagCompound compound = stack.getSubCompound("Elytra Upgrade");

                if (compound == null) {
                    return;
                }
                int isActive = compound.getInteger("Active");
                NetworkHandler.INSTANCE.sendToServer(new CPacketToggleColytra(isActive));
            }
        }
    }

    @SubscribeEvent
    public void tooltipHandler(ItemTooltipEvent e) {
        ItemStack itemstack = e.getItemStack();

        if (ColytraUtil.hasElytraUpgrade(itemstack)) {
            NBTTagCompound compound = itemstack.getSubCompound("Elytra Upgrade");

            if (compound == null) {
                return;
            }
            boolean isActive = compound.getInteger("Active") == 1;
            int durability = compound.getInteger("Durability");
            List<String> tooltip = e.getToolTip();
            ITextComponent tip;
            Style style = new Style().setColor(TextFormatting.GRAY);

            if (isActive) {
                style = style.setColor(TextFormatting.AQUA);
            }

            tip = new TextComponentTranslation("item.elytra.name").setStyle(style);

            if (ConfigHandler.durabilityMode == ConfigHandler.DurabilityMode.NORMAL) {

                if (durability > 1) {
                    tooltip.add(tip.appendText(": " + durability + "/432").getFormattedText());
                } else {
                    tooltip.add(tip.appendText(": " + new TextComponentTranslation("tooltip.colytra.broken")).getFormattedText());
                }
            } else {
                tooltip.add(tip.getFormattedText());
            }

            if (CommonProxy.quarkLoaded && itemstack.getTagCompound() != null) {
                addElytraColorTooltip(e, itemstack.getTagCompound());
            }
        }
    }

    @Optional.Method(modid = "quark")
    private static void addElytraColorTooltip(ItemTooltipEvent evt, NBTTagCompound compound) {

        if (compound.hasKey(DyableElytra.TAG_ELYTRA_DYE)) {
            int color = compound.getInteger(DyableElytra.TAG_ELYTRA_DYE);
            EnumDyeColor dye = EnumDyeColor.byDyeDamage(color);

            if(dye != EnumDyeColor.WHITE) {
                evt.getToolTip().add(I18n.format("quark.dyedElytra", I18n.format("quark.dye." + dye.getUnlocalizedName())));
            }
        }
    }
}
