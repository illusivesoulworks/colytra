/*
 * Copyright (C) 2017-2022 Illusive Soulworks
 *
 * Colytra is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Colytra is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Colytra. If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.colytra.client;

import com.illusivesoulworks.colytra.common.ElytraTag;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nonnull;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;

public class ColytraLayer<T extends LivingEntity, M extends EntityModel<T>>
    extends RenderLayer<T, M> {

  private static final ResourceLocation WINGS_LOCATION =
      new ResourceLocation("minecraft:textures/entity/elytra.png");

  private final ElytraModel<T> elytraModel;

  public ColytraLayer(RenderLayerParent<T, M> layerParent, EntityModelSet modelSet) {
    super(layerParent);
    this.elytraModel = new ElytraModel<>(modelSet.bakeLayer(ModelLayers.ELYTRA));
  }

  public void render(@Nonnull PoseStack pMatrixStack, @Nonnull MultiBufferSource pBuffer,
                     int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount,
                     float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
    ItemStack stack = pLivingEntity.getItemBySlot(EquipmentSlot.CHEST);
    ItemStack elytraStack = ElytraTag.getElytra(stack);

    if (!elytraStack.isEmpty()) {
      ResourceLocation resourcelocation;

      if (pLivingEntity instanceof AbstractClientPlayer abstractclientplayer) {

        if (abstractclientplayer.isElytraLoaded() &&
            abstractclientplayer.getElytraTextureLocation() != null) {
          resourcelocation = abstractclientplayer.getElytraTextureLocation();
        } else if (abstractclientplayer.isCapeLoaded() &&
            abstractclientplayer.getCloakTextureLocation() != null &&
            abstractclientplayer.isModelPartShown(PlayerModelPart.CAPE)) {
          resourcelocation = abstractclientplayer.getCloakTextureLocation();
        } else {
          resourcelocation = WINGS_LOCATION;
        }
      } else {
        resourcelocation = WINGS_LOCATION;
      }
      pMatrixStack.pushPose();
      pMatrixStack.translate(0.0D, 0.0D, 0.125D);
      this.getParentModel().copyPropertiesTo(this.elytraModel);
      this.elytraModel.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks,
          pNetHeadYaw, pHeadPitch);
      VertexConsumer vertexconsumer =
          ItemRenderer.getArmorFoilBuffer(pBuffer, RenderType.armorCutoutNoCull(resourcelocation),
              false, elytraStack.isEnchanted());
      this.elytraModel.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight,
          OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
      pMatrixStack.popPose();
    }
  }
}
