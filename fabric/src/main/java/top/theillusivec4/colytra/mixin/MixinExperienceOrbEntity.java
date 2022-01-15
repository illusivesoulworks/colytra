/*
 * Copyright (c) 2018-2020 C4
 *
 * This file is part of Colytra, a mod made for Minecraft.
 *
 * Colytra is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Colytra is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Colytra.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.colytra.mixin;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.colytra.common.util.MixinHooks;

@Mixin(ExperienceOrbEntity.class)
public class MixinExperienceOrbEntity {

  @Inject(at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerEntity.sendPickup(Lnet/minecraft/entity/Entity;I)V"), method = "onPlayerCollision")
  private void colytra$onPlayerCollision(PlayerEntity playerEntity, CallbackInfo cb) {
    @SuppressWarnings("ConstantConditions") ExperienceOrbEntity orb =
        (ExperienceOrbEntity) (Object) this;
    MixinHooks.repairColytra(playerEntity, orb);
  }
}