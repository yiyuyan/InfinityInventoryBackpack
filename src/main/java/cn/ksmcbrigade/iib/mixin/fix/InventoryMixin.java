package cn.ksmcbrigade.iib.mixin.fix;

import cn.ksmcbrigade.iib.util.PlayerExInventoryDataAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public class InventoryMixin {
    @Shadow @Final public Player player;

    @Inject(method = "setItem",at = @At("HEAD"), cancellable = true)
    public void set(int p_35999_, ItemStack p_36000_, CallbackInfo ci){
        if(((PlayerExInventoryDataAccess) this.player).tickStop()) ci.cancel();
    }
}
