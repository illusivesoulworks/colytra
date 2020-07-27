package top.theillusivec4.colytra.loader.common.impl;

import java.util.Optional;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import top.theillusivec4.colytra.core.base.RegistryFinder;

public class RegistryFinderImpl implements RegistryFinder {

  @Override
  public Optional<Enchantment> findEnchantment(String id) {
    return Registry.ENCHANTMENT.getOrEmpty(new Identifier(id));
  }
}
