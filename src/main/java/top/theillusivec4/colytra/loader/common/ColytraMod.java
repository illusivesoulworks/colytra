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

package top.theillusivec4.colytra.loader.common;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.registry.Registry;
import top.theillusivec4.colytra.core.Colytra;
import top.theillusivec4.colytra.core.crafting.ElytraAttachmentRecipe;
import top.theillusivec4.colytra.core.crafting.ElytraDetachmentRecipe;
import top.theillusivec4.colytra.loader.common.impl.AccessorImpl;
import top.theillusivec4.colytra.loader.common.impl.RegistryFinderImpl;

public class ColytraMod implements ModInitializer {

  public static ConfigDataHolder config;

  private static final String ATTACH_ELYTRA = Colytra.MODID + ":elytra_attachment";
  private static final String DETACH_ELYTRA = Colytra.MODID + ":elytra_detachment";

  @Override
  public void onInitialize() {
    config = AutoConfig.register(ConfigDataHolder.class, JanksonConfigSerializer::new).getConfig();
    Colytra.setAccessor(new AccessorImpl());
    Colytra.setRegistryFinder(new RegistryFinderImpl());
    Registry.register(Registry.RECIPE_SERIALIZER, ATTACH_ELYTRA,
        ElytraAttachmentRecipe.CRAFTING_ATTACH_ELYTRA);
    Registry.register(Registry.RECIPE_SERIALIZER, DETACH_ELYTRA,
        ElytraDetachmentRecipe.CRAFTING_DETACH_ELYTRA);
  }
}
