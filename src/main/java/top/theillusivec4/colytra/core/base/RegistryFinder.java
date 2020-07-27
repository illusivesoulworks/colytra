package top.theillusivec4.colytra.core.base;

import java.util.Optional;
import net.minecraft.enchantment.Enchantment;

public interface RegistryFinder {

  Optional<Enchantment> findEnchantment(String id);
}
