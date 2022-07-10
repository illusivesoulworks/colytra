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

package com.illusivesoulworks.colytra.mixin;

import com.illusivesoulworks.colytra.common.CommonEvents;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public class MixinExperienceOrb {

  @Shadow
  private int value;

  @SuppressWarnings("ConstantConditions")
  @Inject(at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "net/minecraft/world/entity/player/Player.takeXpDelay:I"), method = "playerTouch", cancellable = true)
  private void colytra$playerTouch(Player player, CallbackInfo ci) {

    if (CommonEvents.repairColytraWithXp((ExperienceOrb) (Object) this, player,
        (val) -> this.value = val)) {
      ci.cancel();
    }
  }
}
