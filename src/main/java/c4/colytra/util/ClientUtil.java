/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Colytra mod for Minecraft.
 * Colytra is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Colytra
 */

package c4.colytra.util;

import c4.colytra.common.config.ConfigHandler;
import c4.colytra.common.items.ItemElytraBauble;
import c4.colytra.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Optional;
import org.lwjgl.input.Keyboard;
import vazkii.quark.vanity.feature.DyableElytra;

public class ClientUtil {

    public static KeyBinding toggleColytra;

    public static void init() {
        toggleColytra = new KeyBinding("key.colytra.toggle.desc", Keyboard.KEY_G, "key.colytra.category");
        ClientRegistry.registerKeyBinding(toggleColytra);
        if (CommonProxy.quarkLoaded && CommonProxy.baublesLoaded && !ConfigHandler.baubles.disableBauble) {
            initElytraBaubleColors();
        }
    }

    public static boolean shouldRenderColytra(ItemStack stack) {

        if (stack.getItem() instanceof ItemElytraBauble) {
            return !stack.hasTagCompound() || stack.getTagCompound().hasKey("Active") && stack.getTagCompound().getInteger("Active") == 1;
        }
        return !(stack.hasTagCompound() && stack.getTagCompound().hasKey("Elytra Upgrade") && stack.getSubCompound("Elytra Upgrade").getInteger("Active") == 0);
    }

    @Optional.Method(modid = "quark")
    private static void initElytraBaubleColors() {

        Minecraft mc = Minecraft.getMinecraft();
        mc.getItemColors().registerItemColorHandler((ItemStack stack, int tintIndex) -> {

                int color = -1;
                if (stack.hasTagCompound() && stack.getTagCompound().hasKey(DyableElytra.TAG_ELYTRA_DYE)) {
                    color = stack.getTagCompound().getInteger(DyableElytra.TAG_ELYTRA_DYE);
                }
                if(color == -1 || color == 15)
                    return -1;

            return ItemDye.DYE_COLORS[color];

        }, CommonProxy.elytraBauble);
    }
}
