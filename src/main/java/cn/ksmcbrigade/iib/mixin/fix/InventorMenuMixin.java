package cn.ksmcbrigade.iib.mixin.fix;

import cn.ksmcbrigade.iib.util.PlayerExInventoryDataAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(InventoryMenu.class)
public abstract class InventorMenuMixin extends RecipeBookMenu<CraftingContainer> {
    @Shadow @Final private Player owner;

    public InventorMenuMixin(MenuType<?> p_40115_, int p_40116_) {
        super(p_40115_, p_40116_);
    }

    @Unique
    @Override
    public void setItem(int p_182407_, int p_182408_, @NotNull ItemStack p_182409_) {
        if(!((PlayerExInventoryDataAccess) this.owner).tickStop()){
            super.setItem(p_182407_,p_182408_,p_182409_);
        }
    }
}
