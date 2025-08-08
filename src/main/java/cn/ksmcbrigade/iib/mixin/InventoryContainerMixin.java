package cn.ksmcbrigade.iib.mixin;

import cn.ksmcbrigade.iib.InfinityInventoryBackpack;
import cn.ksmcbrigade.iib.client.ClientTemps;
import cn.ksmcbrigade.iib.network.NetworkMessage;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryContainerMixin extends EffectRenderingInventoryScreen<InventoryMenu> implements RecipeUpdateListener {
    public InventoryContainerMixin(InventoryMenu p_98701_, Inventory p_98702_, Component p_98703_) {
        super(p_98701_, p_98702_, p_98703_);
    }

    @Inject(method = "init",at = @At("TAIL"))
    public void init(CallbackInfo ci){
        this.addRenderableWidget(Button.builder(Component.literal("IB"),(button)-> {
            ClientTemps.nextIndex--;
            if(ClientTemps.nextIndex<0) ClientTemps.nextIndex = 0;
            InfinityInventoryBackpack.CHANNEL.sendToServer(new NetworkMessage(ClientTemps.nextIndex));
        }).size(15,15).pos(this.leftPos+this.imageWidth-10-5-5,this.topPos+60+5).build());
    }
}
