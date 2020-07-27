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

package top.theillusivec4.colytra.loader.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.colytra.loader.common.MixinHooks;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {

  @Inject(at = @At("HEAD"), method = "updateResult()V", cancellable = true)
  public void updateResult(CallbackInfo cb) {
    @SuppressWarnings("ConstantConditions") AnvilScreenHandler screenHandler = (AnvilScreenHandler) (Object) this;

    if (MixinHooks.checkColytraRepair(screenHandler)) {
      cb.cancel();
    }
  }
}
