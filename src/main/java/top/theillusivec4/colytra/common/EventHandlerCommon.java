package top.theillusivec4.colytra.common;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import top.theillusivec4.caelus.api.CaelusAPI;
import top.theillusivec4.colytra.common.capability.CapabilityElytra;
import top.theillusivec4.colytra.common.network.NetworkHandler;
import top.theillusivec4.colytra.common.network.SPacketSyncColytra;

import java.util.UUID;

public class EventHandlerCommon {

    private static AttributeModifier FLIGHT_MODIFIER = new AttributeModifier(UUID.fromString("668bdbee-32b6-4c4b-bf6a-5a30f4d02e37"),
            "Flight modifier", 1.0d, 0);

    @SubscribeEvent
    public void onLivingEquipmentChange(LivingEquipmentChangeEvent evt) {

        if (evt.getEntityLiving() instanceof EntityPlayer && evt.getSlot() == EntityEquipmentSlot.CHEST) {
            ItemStack to = evt.getTo();
            IAttributeInstance attributeInstance = evt.getEntityLiving().getAttribute(CaelusAPI.ELYTRA_FLIGHT);
            attributeInstance.removeModifier(FLIGHT_MODIFIER);
            CapabilityElytra.getCapability(to).ifPresent(ielytra -> {

                if (ielytra.isUseable()) {
                    attributeInstance.applyModifier(FLIGHT_MODIFIER);
                }
            });
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent evt) {

        if (evt.side == LogicalSide.SERVER && evt.phase == TickEvent.Phase.END) {
            EntityPlayer player = evt.player;
            ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            CapabilityElytra.getCapability(stack).ifPresent(ielytra -> {
                ColytraConfig.ColytraMode mode = ColytraConfig.SERVER.colytraMode.get();

                if (mode != ColytraConfig.ColytraMode.PERFECT) {
                    int ticksFlying = ObfuscationReflectionHelper.getPrivateValue(EntityLivingBase.class, player, "field_184629_bo");

                    if ((ticksFlying + 1) % 20 == 0) {
                        ielytra.damageElytra(player, 1);

                        if (player instanceof EntityPlayerMP) {
                            NetworkHandler.INSTANCE.sendTo(new SPacketSyncColytra(player.getEntityId(), ielytra.getElytra()),
                                    ((EntityPlayerMP) player).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                        }
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public void onPlayerXPPickUp(PlayerPickupXpEvent evt) {
        EntityPlayer player = evt.getEntityPlayer();

        if (!player.world.isRemote && ColytraConfig.SERVER.colytraMode.get() == ColytraConfig.ColytraMode.NORMAL) {
            CapabilityElytra.getCapability(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST)).ifPresent(ielytra -> {
                ItemStack elytraStack = ielytra.getElytra();

                if (!elytraStack.isEmpty() && elytraStack.getDamage() > 0 && EnchantmentHelper.getEnchantmentLevel(Enchantments.
                        MENDING, elytraStack) > 0) {
                    evt.setCanceled(true);
                    EntityXPOrb xpOrb = evt.getOrb();

                    if (xpOrb.delayBeforeCanPickup == 0 && player.xpCooldown == 0) {
                        player.xpCooldown = 2;
                        player.onItemPickup(xpOrb, 1);
                        int i = Math.min(xpToDurability(xpOrb.xpValue), elytraStack.getDamage());
                        xpOrb.xpValue -= durabilityToXp(i);
                        elytraStack.setDamage(elytraStack.getDamage() - i);

                        if (player instanceof EntityPlayerMP) {
                            NetworkHandler.INSTANCE.sendTo(new SPacketSyncColytra(player.getEntityId(), ielytra.getElytra()),
                                    ((EntityPlayerMP) player).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                        }

                        if (xpOrb.xpValue > 0) {
                            player.giveExperiencePoints(xpOrb.xpValue);
                        }
                        xpOrb.remove();
                    }
                }
            });
        }
    }

    private static int durabilityToXp(int durability) {
        return durability / 2;
    }

    private static int xpToDurability(int xp) {
        return xp * 2;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onColytraAnvil(AnvilUpdateEvent evt) {

        if (ColytraConfig.SERVER.colytraMode.get() != ColytraConfig.ColytraMode.NORMAL) {
            return;
        }
        ItemStack chest = evt.getLeft();
        CapabilityElytra.getCapability(chest).ifPresent(ielytra -> {
            ItemStack membrane = evt.getRight();

            if (membrane.getItem() == Items.PHANTOM_MEMBRANE) {
                ItemStack stack = ielytra.getElytra();
                int toRepair = stack.getDamage();

                if (toRepair == 0) {
                    return;
                }
                int membraneToUse = 0;

                while (toRepair > 0) {
                    toRepair -= 108;
                    membraneToUse++;
                }
                membraneToUse = Math.min(membraneToUse, membrane.getCount());
                int newDamage = Math.max(stack.getDamage() - membraneToUse * 108, 0);
                ItemStack output = chest.copy();
                ItemStack outputElytra = stack.copy();
                outputElytra.setDamage(newDamage);
                outputElytra.setRepairCost(stack.getRepairCost() * 2 + 1);
                CapabilityElytra.getCapability(output).ifPresent(ielytra1 -> ielytra1.setElytra(outputElytra));
                int xpCost = membraneToUse + chest.getRepairCost() + membrane.getRepairCost();
                String name = evt.getName();

                if (!name.isEmpty() && !name.equals(chest.getDisplayName().getString())) {
                    output.setDisplayName(new TextComponentString(name));
                    xpCost++;
                }
                evt.setMaterialCost(membraneToUse);
                evt.setCost(xpCost);
                evt.setOutput(output);
            }
        });
    }
}
