package top.theillusivec4.colytra.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import top.theillusivec4.colytra.common.util.ColytraNbt;

public class ColytraFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>>
    extends ElytraFeatureRenderer<T, M> {

  private static final Identifier SKIN = new Identifier("textures/entity/elytra.png");
  private final ElytraEntityModel<T> elytra;

  public ColytraFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader loader) {
    super(context, loader);
    this.elytra = new ElytraEntityModel<>(loader.getModelPart(EntityModelLayers.ELYTRA));
  }

  @Override
  public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i,
                     T livingEntity, float f, float g, float h, float j, float k, float l) {
    ItemStack stack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
    ItemStack elytraStack = ColytraNbt.getElytra(stack);

    if (!elytraStack.isEmpty()) {
      Identifier identifier4;
      if (livingEntity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
        if (abstractClientPlayerEntity.canRenderElytraTexture() &&
            abstractClientPlayerEntity.getElytraTexture() != null) {
          identifier4 = abstractClientPlayerEntity.getElytraTexture();
        } else if (abstractClientPlayerEntity.canRenderCapeTexture() &&
            abstractClientPlayerEntity.getCapeTexture() != null &&
            abstractClientPlayerEntity.isPartVisible(
                PlayerModelPart.CAPE)) {
          identifier4 = abstractClientPlayerEntity.getCapeTexture();
        } else {
          identifier4 = SKIN;
        }
      } else {
        identifier4 = SKIN;
      }
      matrixStack.push();
      matrixStack.translate(0.0D, 0.0D, 0.125D);
      this.getContextModel().copyStateTo(this.elytra);
      this.elytra.setAngles(livingEntity, f, g, j, k, l);
      VertexConsumer vertexConsumer = ItemRenderer
          .getArmorGlintConsumer(vertexConsumerProvider,
              RenderLayer.getArmorCutoutNoCull(identifier4), false, elytraStack.hasGlint());
      this.elytra
          .render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F,
              1.0F);
      matrixStack.pop();
    }
  }
}
