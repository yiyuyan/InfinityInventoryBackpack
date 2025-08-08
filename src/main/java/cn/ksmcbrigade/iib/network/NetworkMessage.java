package cn.ksmcbrigade.iib.network;

import net.minecraft.network.FriendlyByteBuf;

public record NetworkMessage(int index) {
    public static void encode(NetworkMessage message,FriendlyByteBuf buf){
        buf.writeInt(message.index);
    }

    public static NetworkMessage decode(FriendlyByteBuf friendlyByteBuf){
        return new NetworkMessage(friendlyByteBuf.readInt());
    }
}
