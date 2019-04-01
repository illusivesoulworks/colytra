package top.theillusivec4.colytra.client;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.ModelElytra;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.colytra.common.ColytraConfig;
import top.theillusivec4.colytra.common.capability.CapabilityElytra;

import javax.annotation.Nonnull;

public class LayerColytra implements LayerRenderer<EntityLivingBase> {

    private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");
    protected final RenderLivingBase<?> renderPlayer;
    private final ModelElytra modelElytra = new ModelElytra();

    public LayerColytra(RenderLivingBase<?> renderPlayer) {
        this.renderPlayer = renderPlayer;
    }

    @Override
    public void render(@Nonnull EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        CapabilityElytra.getCapability(itemstack).ifPresent(ielytra -> {
            ItemStack elytraStack = ielytra.getElytra();

            if (!elytraStack.isEmpty()) {
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                if (entitylivingbaseIn instanceof AbstractClientPlayer) {
                    AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer)entitylivingbaseIn;

                    if (abstractclientplayer.isPlayerInfoSet() && abstractclientplayer.getLocationElytra() != null) {
                        this.renderPlayer.bindTexture(abstractclientplayer.getLocationElytra());
                    } else if (abstractclientplayer.hasPlayerInfo() && abstractclientplayer.getLocationCape() != null && abstractclientplayer.isWearing(EnumPlayerModelParts.CAPE)) {
                        this.renderPlayer.bindTexture(abstractclientplayer.getLocationCape());
                    } else {
                        this.renderPlayer.bindTexture(TEXTURE_ELYTRA);
                    }
                } else {
                    this.renderPlayer.bindTexture(TEXTURE_ELYTRA);
                }
                GlStateManager.pushMatrix();
                GlStateManager.translatef(0.0F, 0.0F, 0.125F);
                this.modelElytra.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entitylivingbaseIn);
                this.modelElytra.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                boolean renderEnchanted = ColytraConfig.SERVER.colytraMode.get() == ColytraConfig.ColytraMode.NORMAL ? elytraStack.isEnchanted() : itemstack.isEnchanted();

                if (renderEnchanted) {
                    LayerArmorBase.renderEnchantedGlint(this.renderPlayer, entitylivingbaseIn, this.modelElytra, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
                }
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        });
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
