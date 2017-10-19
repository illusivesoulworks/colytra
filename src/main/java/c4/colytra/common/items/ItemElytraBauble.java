/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Colytra.
 * Colytra is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.colytra.common.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import c4.colytra.Colytra;
import c4.colytra.core.util.ColytraUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.ItemHandlerHelper;

@Optional.Interface(modid = "baubles", iface = "baubles.api.IBauble", striprefs = true)
public class ItemElytraBauble extends ItemElytra implements IBauble {

    public ItemElytraBauble() {
        super();
        this.setRegistryName("elytra_bauble");
        this.setUnlocalizedName(Colytra.MODID + ".elytra_bauble");
    }

    @Override
    public BaubleType getBaubleType(ItemStack var1) {
        return BaubleType.BODY;
    }

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
        player.playSound(SoundEvents.ITEM_ARMOR_EQIIP_ELYTRA, 1.0F, 1.0F);
    }

    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {

        if (player instanceof EntityPlayer) {

            ItemStack colytra = ColytraUtil.findColytraChest(player);

            if (colytra != ItemStack.EMPTY) {
                ItemStack ret = itemstack.copy();
                itemstack.shrink(1);
                ItemHandlerHelper.giveItemToPlayer((EntityPlayer) player, ret);
            }
        }
    }

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {

        ItemStack colytra = ColytraUtil.findColytraChest(player);

        return colytra == ItemStack.EMPTY;
    }
}
