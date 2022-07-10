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

package com.illusivesoulworks.colytra.common;

import java.util.UUID;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import org.apache.commons.lang3.tuple.Triple;
import top.theillusivec4.caelus.api.CaelusApi;

public class CommonEventsListener {

  private static final AttributeModifier FLIGHT_MODIFIER =
      new AttributeModifier(UUID.fromString("668bdbee-32b6-4c4b-bf6a-5a30f4d02e37"),
          "Flight modifier", 1.0d, AttributeModifier.Operation.ADDITION);

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onColytraAnvil(final AnvilUpdateEvent evt) {
    Triple<ItemStack, Integer, Integer> result =
        CommonEvents.repairColytra(evt.getLeft(), evt.getRight(), evt.getName());

    if (!result.getLeft().isEmpty()) {
      evt.setOutput(result.getLeft());
      evt.setMaterialCost(result.getMiddle());
      evt.setCost(result.getRight());
    }
  }

  @SubscribeEvent
  public void onPlayerTick(final TickEvent.PlayerTickEvent evt) {

    if (evt.side != LogicalSide.SERVER || evt.phase != TickEvent.Phase.END) {
      return;
    }
    Player player = evt.player;
    AttributeInstance attributeInstance = player
        .getAttribute(CaelusApi.getInstance().getFlightAttribute());

    if (attributeInstance != null) {
      attributeInstance.removeModifier(FLIGHT_MODIFIER);

      if (CommonEvents.updateColytra(player, true)) {
        attributeInstance.addTransientModifier(FLIGHT_MODIFIER);
      }
    }
  }

  @SubscribeEvent
  public void onPlayerXPPickUp(final PlayerXpEvent.PickupXp evt) {
    ExperienceOrb orb = evt.getOrb();

    if (CommonEvents.repairColytraWithXp(orb, evt.getPlayer(), (val) -> orb.value = val)) {
      evt.setCanceled(true);
    }
  }
}
