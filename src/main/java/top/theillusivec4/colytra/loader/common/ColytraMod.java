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

  private static final String ATTACH_ELYTRA = "elytra_attachment";
  private static final String DETACH_ELYTRA = "elytra_detachment";

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
