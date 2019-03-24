/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Colytra mod for Minecraft.
 * Colytra is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Colytra
 */

package c4.colytra.common.items;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import c4.colytra.util.ColytraUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

@Optional.Interface(modid = "baubles", iface = "baubles.api.IBauble", striprefs = true)
public class ItemElytraBauble extends ItemElytra implements IBauble {

    public ItemElytraBauble() {
        super();
        this.setRegistryName("elytra_bauble");
        this.setTranslationKey("elytra");
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {

        if(!world.isRemote) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            ItemStack stack = player.getHeldItem(hand);

            for(int i = 0; i < baubles.getSlots(); i++) {

                if(baubles.getStackInSlot(i).isEmpty() && baubles.isItemValidForSlot(i, stack, player)) {
                    baubles.setStackInSlot(i, stack.copy());

                    if(!player.isCreative()) {
                        stack.setCount(0);
                    }
                    this.onEquipped(player.getHeldItem(hand), player);
                    break;
                }
            }
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
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
            ItemStack colytra = ColytraUtil.wornColytra(player);

            if (colytra != ItemStack.EMPTY) {
                ItemStack ret = itemstack.copy();
                itemstack.shrink(1);
                ItemHandlerHelper.giveItemToPlayer((EntityPlayer) player, ret);
            }
        }
    }

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        ItemStack colytra = ColytraUtil.wornColytra(player);
        return colytra == ItemStack.EMPTY;
    }

    @Override
    public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }
}
