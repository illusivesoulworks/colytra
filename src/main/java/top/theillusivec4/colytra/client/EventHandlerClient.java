package top.theillusivec4.colytra.client;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.caelus.api.event.RenderCapeCheckEvent;
import top.theillusivec4.colytra.common.ColytraConfig;
import top.theillusivec4.colytra.common.capability.CapabilityElytra;

import java.util.List;

public class EventHandlerClient {

    @SubscribeEvent
    public void onRenderCape(RenderCapeCheckEvent evt) {

        CapabilityElytra.getCapability(evt.getEntityPlayer().getItemStackFromSlot(EntityEquipmentSlot.CHEST)).ifPresent(ielytra -> {

            if (!ielytra.getElytra().isEmpty()) {
                evt.setResult(Event.Result.DENY);
            }
        });
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent evt) {
        ItemStack itemstack = evt.getItemStack();
        List<ITextComponent> tooltip = evt.getToolTip();

        CapabilityElytra.getCapability(itemstack).ifPresent(ielytra -> {
            ItemStack elytraStack = ielytra.getElytra();

            if (!elytraStack.isEmpty()) {
                tooltip.add(new TextComponentString(""));
                tooltip.add(new TextComponentTranslation("item.minecraft.elytra").applyTextStyle(TextFormatting.AQUA));

                if (ColytraConfig.SERVER.colytraMode.get() == ColytraConfig.ColytraMode.NORMAL) {

                    if (elytraStack.hasTag()) {
                        int i = 0;
                        NBTTagCompound tag = elytraStack.getTag();
                        if (tag.contains("HideFlags", 99)) {
                            i = tag.getInt("HideFlags");
                        }
                        if ((i & 1) == 0) {
                            NBTTagList nbttaglist = elytraStack.getEnchantmentTagList();

                            for (int j = 0; j < nbttaglist.size(); ++j) {
                                NBTTagCompound nbttagcompound = nbttaglist.getCompound(j);
                                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryCreate(nbttagcompound.getString("id")));
                                if (enchantment != null) {
                                    tooltip.add(new TextComponentString(" ").appendSibling(enchantment.getDisplayName(nbttagcompound.getInt("lvl"))));
                                }
                            }
                        }
                    }

                    if (ielytra.isUseable()) {
                        tooltip.add(new TextComponentString(" ").appendSibling(new TextComponentTranslation("item.durability", elytraStack.getMaxDamage() - elytraStack.getDamage(), elytraStack.getMaxDamage())));
                    } else {
                        tooltip.add(new TextComponentString(" ").appendSibling(new TextComponentTranslation("tooltip.colytra.broken").applyTextStyle(TextFormatting.RED)));
                    }
                }
            }
        });
    }
}
