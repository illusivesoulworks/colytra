package top.theillusivec4.colytra.client;

import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.caelus.api.event.RenderElytraEvent;
import top.theillusivec4.colytra.common.ColytraConfig;
import top.theillusivec4.colytra.common.capability.CapabilityElytra;

public class EventHandlerClient {

  private static void getColytraToolip(CapabilityElytra.IElytra elytraHolder,
      List<ITextComponent> tooltip) {

    ItemStack elytraStack = elytraHolder.getElytra();

    if (elytraStack.isEmpty()) {
      return;
    }

    tooltip.add(new StringTextComponent(""));
    tooltip.add(
        new TranslationTextComponent("item.minecraft.elytra").applyTextStyle(TextFormatting.AQUA));

    if (ColytraConfig.getColytraMode() == ColytraConfig.ColytraMode.NORMAL) {

      if (elytraStack.hasTag()) {
        int i = 0;
        CompoundNBT tag = elytraStack.getTag();

        if (tag != null && tag.contains("HideFlags", 99)) {
          i = tag.getInt("HideFlags");
        }

        if ((i & 1) == 0) {
          ListNBT nbttaglist = elytraStack.getEnchantmentTagList();

          for (int j = 0; j < nbttaglist.size(); ++j) {
            CompoundNBT nbttagcompound = nbttaglist.getCompound(j);
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(
                ResourceLocation.tryCreate(nbttagcompound.getString("id")));

            if (enchantment != null) {
              tooltip.add(new StringTextComponent(" ").appendSibling(
                  enchantment.getDisplayName(nbttagcompound.getInt("lvl"))));
            }
          }
        }
      }

      if (elytraHolder.isUseable()) {
        tooltip.add(new StringTextComponent(" ").appendSibling(
            new TranslationTextComponent("item.durability",
                elytraStack.getMaxDamage() - elytraStack.getDamage(),
                elytraStack.getMaxDamage())));
      } else {
        tooltip.add(new StringTextComponent(" ").appendSibling(
            new TranslationTextComponent("tooltip.colytra.broken").applyTextStyle(
                TextFormatting.RED)));
      }
    }
  }

  @SubscribeEvent
  public void onRenderElytra(RenderElytraEvent evt) {

    ItemStack stack = evt.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.CHEST);

    CapabilityElytra.getCapability(stack).ifPresent(ielytra -> {
      ItemStack elytraStack = ielytra.getElytra();

      if (!elytraStack.isEmpty()) {
        evt.setRenderElytra(true);
      }

      if (elytraStack.isEnchanted()) {
        evt.setRenderEnchantmentGlow(true);
      }
    });
  }

  @SubscribeEvent
  public void onItemTooltip(ItemTooltipEvent evt) {

    ItemStack itemstack = evt.getItemStack();
    List<ITextComponent> tooltip = evt.getToolTip();

    CapabilityElytra.getCapability(itemstack).ifPresent(elytraHolder -> {
      getColytraToolip(elytraHolder, tooltip);
    });
  }
}
