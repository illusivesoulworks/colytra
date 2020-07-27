package top.theillusivec4.colytra.loader.common.impl;

import net.minecraft.entity.LivingEntity;
import top.theillusivec4.colytra.core.base.Accessor;
import top.theillusivec4.colytra.loader.mixin.LivingEntityAccessor;

public class AccessorImpl implements Accessor {

  @Override
  public int getFlyingTicks(LivingEntity livingEntity) {
    return ((LivingEntityAccessor) livingEntity).getRoll();
  }
}
