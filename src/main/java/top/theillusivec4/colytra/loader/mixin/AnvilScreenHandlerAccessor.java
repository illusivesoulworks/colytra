package top.theillusivec4.colytra.loader.mixin;

import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AnvilScreenHandler.class)
public interface AnvilScreenHandlerAccessor {

  @Accessor
  void setRepairItemUsage(int repairItemUsage);

  @Accessor
  Property getLevelCost();

  @Accessor
  String getNewItemName();
}
