package cn.ksmcbrigade.iib.client;

import cn.ksmcbrigade.iib.InfinityInventoryBackpack;
import cn.ksmcbrigade.iib.network.ClientBoundCallbackNetworkMessage;
import cn.ksmcbrigade.iib.network.NetworkMessage;
import cn.ksmcbrigade.iib.network.SaveMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientTemps {
    public static int nextIndex = 0;

    @OnlyIn(Dist.CLIENT)
    public static void handleCallback(ClientBoundCallbackNetworkMessage msg){
        Minecraft MC = Minecraft.getInstance();
        if(MC.player!=null && MC.player.hasContainerOpen() && MC.screen instanceof ContainerScreen containerScreen
                && MC.player.containerMenu.getItems().size()>=54
        && (MC.player.containerMenu.getItems().get(53).getHoverName().getString().contains("IB]") ||
                MC.player.containerMenu.getItems().get(45).getHoverName().getString().contains("IB]"))){
            if(containerScreen.getTitle().getString().startsWith("InfinityBackpack#")){
                int current = Integer.parseInt(containerScreen.getTitle().getString().replace("InfinityBackpack#",""));
                if(msg.index()==-114){
                    //InfinityInventoryBackpack.LOGGER.info("Sending a save message.");
                    InfinityInventoryBackpack.CHANNEL.sendToServer(new SaveMessage(current,msg.stacks()));
                    return;
                }
                if(msg.index()-1>=0){
                    ClientTemps.nextIndex = msg.index();
                    InfinityInventoryBackpack.CHANNEL.sendToServer(new NetworkMessage(msg.index()-1));
                }
            }
        }
    }
}
