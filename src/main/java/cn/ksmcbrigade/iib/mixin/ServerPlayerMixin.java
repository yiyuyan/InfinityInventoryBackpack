package cn.ksmcbrigade.iib.mixin;

import cn.ksmcbrigade.iib.Config;
import cn.ksmcbrigade.iib.InfinityInventoryBackpack;
import cn.ksmcbrigade.iib.network.ClientBoundCallbackNetworkMessage;
import cn.ksmcbrigade.iib.network.ClientBoundNetworkMessage;
import cn.ksmcbrigade.iib.util.PlayerExInventoryDataAccess;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    @Shadow public ServerGamePacketListenerImpl connection;

    public ServerPlayerMixin(Level p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_) {
        super(p_250508_, p_250289_, p_251702_, p_252153_);
    }


    @Inject(method = {"doCloseContainer","closeContainer"},at = @At("HEAD"))
    public void close(CallbackInfo ci){
        if(this.containerMenu.slots.size()>=54 && this.containerMenu.slots.get(53).getItem().getItem() instanceof ArrowItem && this.containerMenu.slots.get(53).getItem().getHoverName().getString().contains("IB")){
            //InfinityInventoryBackpack.CHANNEL.sendTo(new ClientBoundCallbackNetworkMessage(-114,this.containerMenu.getItems()),this.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            if(!InfinityInventoryBackpack.check){
                return;
            }
            InfinityInventoryBackpack.CHANNEL.sendTo(new ClientBoundNetworkMessage(0),this.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            InfinityInventoryBackpack.LOGGER.info("Closed the infinity backpack.");
        }
        InfinityInventoryBackpack.check = true;
    }

    @Inject(method = "tick",at = @At("HEAD"))
    public void tick(CallbackInfo ci){
        if(this.tickCount%10!=0 || ((PlayerExInventoryDataAccess)this).tickStop())return;
        if(!this.hasContainerOpen() && !Config.SAVE_CURRENT_INV_INDEX.get()){
            InfinityInventoryBackpack.CHANNEL.sendTo(new ClientBoundNetworkMessage(0),this.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
        if(this.containerMenu!=null && this.containerMenu.slots.size()>=54 && this.containerMenu.slots.get(53).getItem().getItem() instanceof ArrowItem && this.containerMenu.slots.get(53).getItem().getHoverName().getString().contains("IB]")){
            InfinityInventoryBackpack.CHANNEL.sendTo(new ClientBoundCallbackNetworkMessage(-114,this.containerMenu.getItems()),this.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}
