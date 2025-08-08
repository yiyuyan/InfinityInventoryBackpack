package cn.ksmcbrigade.iib.mixin;

import cn.ksmcbrigade.iib.InfinityInventoryBackpack;
import cn.ksmcbrigade.iib.network.ClientBoundCallbackNetworkMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Slot.class)
public abstract class SlotMixin {
    @Shadow public abstract ItemStack getItem();

    @Shadow public abstract void setChanged();

    @Shadow @Final public Container container;

    @Inject(method = {"mayPickup"},at = @At("RETURN"),cancellable = true)
    public void cannotMove(Player p_40228_, CallbackInfoReturnable<Boolean> cir){
        if(this.getItem().getHoverName().getString().contains("IB]") && this.getItem().getItem() instanceof ArrowItem){
            this.getItem().setHoverName(Component.literal(this.getItem().getHoverName().getString().replace("[IB]","[CIB]")));
            this.setChanged();
            this.container.setChanged();
            InfinityInventoryBackpack.CHANNEL.send(PacketDistributor.ALL.noArg(),new ClientBoundCallbackNetworkMessage(InfinityInventoryBackpack.serverIndex+(this.getItem().getHoverName().getString().contains("Next")?1:-1), List.of()));
            cir.setReturnValue(false);
        }
    }
}
