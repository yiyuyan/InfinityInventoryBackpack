package cn.ksmcbrigade.iib.network;

import net.minecraft.network.FriendlyByteBuf;

public record ClientBoundNetworkMessage(int index) {
    public static void encode(ClientBoundNetworkMessage message, FriendlyByteBuf buf){
        buf.writeInt(message.index);
    }

    public static ClientBoundNetworkMessage decode(FriendlyByteBuf friendlyByteBuf){
        return new ClientBoundNetworkMessage(friendlyByteBuf.readInt());
    }
}
