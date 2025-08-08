package cn.ksmcbrigade.iib.mixin;

import cn.ksmcbrigade.iib.InfinityInventoryBackpack;
import cn.ksmcbrigade.iib.client.ClientTemps;
import cn.ksmcbrigade.iib.network.NetworkMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryContainerMixin extends EffectRenderingInventoryScreen<InventoryMenu> implements RecipeUpdateListener {
    @Unique
    private Button infinityinventorybackpack$button;

    public CreativeInventoryContainerMixin(InventoryMenu p_98701_, Inventory p_98702_, Component p_98703_) {
        super(p_98701_, p_98702_, p_98703_);
    }

    @Inject(method = "init",at = @At("TAIL"))
    public void init(CallbackInfo ci){
        infinityinventorybackpack$button = Button.builder(Component.literal("IB"),(button)-> {
            ClientTemps.nextIndex--;
            if(ClientTemps.nextIndex<0) ClientTemps.nextIndex = 0;
            InfinityInventoryBackpack.CHANNEL.sendToServer(new NetworkMessage(ClientTemps.nextIndex));
        }).size(15,15).pos(this.leftPos+this.imageWidth-10-5-5,this.topPos+60+5).build();
        infinityinventorybackpack$button.active = false;
        this.addWidget(infinityinventorybackpack$button);
    }

    @Inject(method = "renderBg",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;renderEntityInInventoryFollowsMouse(Lnet/minecraft/client/gui/GuiGraphics;IIIFFLnet/minecraft/world/entity/LivingEntity;)V"))
    public void renderButton(GuiGraphics p_282663_, float p_282504_, int p_282089_, int p_282249_, CallbackInfo ci){
        infinityinventorybackpack$button.active = true;
        infinityinventorybackpack$button.render(p_282663_,p_282089_,p_282249_,p_282504_);
    }
}
